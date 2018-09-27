pipeline {
    agent {
      label "jenkins-maven"
    }
    environment {
      ORG               = 'acm-workshop'
      APP_NAME          = 'carts'
      CHARTMUSEUM_CREDS = credentials('jenkins-x-chartmuseum')
    }
    stages {
      stage('CI Build and push snapshot') {
        when {
          branch 'PR-*'
        }
        environment {
          PREVIEW_VERSION = "0.0.0-SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER"
          PREVIEW_NAMESPACE = "$APP_NAME-$BRANCH_NAME".toLowerCase()
          HELM_RELEASE = "$PREVIEW_NAMESPACE".toLowerCase()
        }
        steps {
          container('maven') {
            sh "mvn versions:set -DnewVersion=$PREVIEW_VERSION"
            sh "mvn install"
            sh 'export VERSION=$PREVIEW_VERSION && skaffold build -f skaffold.yaml'


            sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:$PREVIEW_VERSION"
          }

          dir ('./charts/preview') {
           container('maven') {
             sh "make preview"
             sh "jx preview --app $APP_NAME --dir ../.."
           }
          }
        }
      }
      stage('Build Release') {
        when {
          branch 'master'
        }
        steps {
          container('maven') {
            // ensure we're not on a detached head
            sh "git checkout master"
            sh "git config --global credential.helper store"

            sh "jx step git credentials"
            // so we can retrieve the version in later steps
            sh "echo \$(jx-release-version) > VERSION"
            //sh "mvn versions:set -DnewVersion=\$(cat VERSION)"
          }
          dir ('./charts/carts') {
            container('maven') {
              sh "make tag"
            }
          }
          container('maven') {
            sh "mvn -DskipTests package"  
            sh "./scripts/build.jb.sh"
            //sh 'mvn clean deploy'

            sh 'export VERSION=`cat VERSION` && skaffold build -f skaffold.yaml'

            sh "jx step post build --image $DOCKER_REGISTRY/$ORG/$APP_NAME:\$(cat VERSION)"
          }
        }
      }
      stage('Promote to Staging') {
        when {
          branch 'master'
        }
        steps {
          dir ('./charts/carts') {
            container('maven') {
              sh 'jx step changelog --version v\$(cat ../../VERSION)'

              // release the helm chart
              sh 'jx step helm release'

              // promote to staging
              sh 'jx promote -b --env staging --timeout 1h --version \$(cat ../../VERSION) $APP_NAME'
            }
          }
        }
      }
     stage('DT Deploy Event') {
       agent {
         label "jenkins-dtcli"
       }
       steps {              
         container('dtcli') {
           checkout scm

           sh "python3 /dtcli/dtcli.py config apitoken ${DT_API_TOKEN} tenanthost ${DT_TENANT_URL}"
           sh "python3 /dtcli/dtcli.py monspec pushdeploy monspec/${APP_NAME}_monspec.json monspec/${APP_NAME}_pipelineinfo.json ${APP_NAME}/Staging JenkinsBuild_${BUILD_NUMBER} ${BUILD_NUMBER}"
         }
       }
     }
     stage('Health Check Staging') {
       steps {
         build job: "${env.ORG}/jmeter-as-container/master", 
           parameters: [
             string(name: 'BUILD_JMETER', value: 'no'), 
             string(name: 'SCRIPT_NAME', value: 'basiccheck.jmx'), 
             string(name: 'SERVER_URL', value: "${env.APP_NAME}.jx-staging.35.233.15.115.nip.io"),
             string(name: 'SERVER_PORT', value: '80'),
             string(name: 'CHECK_PATH', value: '/health'),
             string(name: 'VUCount', value: '1'),
             string(name: 'LoopCount', value: '1'),
             string(name: 'DT_LTN', value: "HealthCheck_${BUILD_NUMBER}"),
             string(name: 'FUNC_VALIDATION', value: 'yes'),
             string(name: 'AVG_RT_VALIDATION', value: '0')
           ]
       }
     }
     stage('Functional Check Staging') {
       steps {
         build job: "${env.ORG}/jmeter-as-container/master", 
           parameters: [
             string(name: 'BUILD_JMETER', value: 'no'), 
             string(name: 'SCRIPT_NAME', value: 'cart_load.jmx'), 
             string(name: 'SERVER_URL', value: "${env.APP_NAME}.jx-staging.35.233.15.115.nip.io"),
             string(name: 'SERVER_PORT', value: '80'),
             string(name: 'CHECK_PATH', value: '/health'),
             string(name: 'VUCount', value: '1'),
             string(name: 'LoopCount', value: '1'),
             string(name: 'DT_LTN', value: "FuncCheck_${BUILD_NUMBER}"),
             string(name: 'FUNC_VALIDATION', value: 'yes'),
             string(name: 'AVG_RT_VALIDATION', value: '0')
           ]
       }
     }
     stage('Performance Check Staging') {
       steps {
         build job: "${env.ORG}/jmeter-as-container/master", 
           parameters: [
             string(name: 'BUILD_JMETER', value: 'no'), 
             string(name: 'SCRIPT_NAME', value: 'cart_load.jmx'), 
             string(name: 'SERVER_URL', value: "${env.APP_NAME}.jx-staging.35.233.15.115.nip.io"),
             string(name: 'SERVER_PORT', value: '80'),
             string(name: 'CHECK_PATH', value: '/health'),
             string(name: 'VUCount', value: '10'),
             string(name: 'LoopCount', value: '250'),
             string(name: 'DT_LTN', value: "PerfCheck_${BUILD_NUMBER}"),
             string(name: 'FUNC_VALIDATION', value: 'no'),
             string(name: 'AVG_RT_VALIDATION', value: '250')
           ]
       }
     }
    }
    post {
        always {
            cleanWs()
        }
        failure {
            input """Pipeline failed. 
We will keep the build pod around to help you diagnose any failures. 

Select Proceed or Abort to terminate the build pod"""
        }
    }
  }
