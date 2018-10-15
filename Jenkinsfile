pipeline {
  agent {
    label 'maven'
  }
  environment {
    ARTEFACT_ID = "sockshop/carts"
    FROM_FILE = readFile 'version'
    VERSION = "${env.FROM_FILE}" + "-${env.BUILD_ID}"
    /*VERSION = "${env.VERSION_FROM_FILE}-${env.BUILD_ID}"*/
    TAG = "10.31.240.247:5000/library/${env.ARTEFACT_ID}"
    TAG_DEV = "${env.TAG}" + ":dev"
    TAG_STAGING = "${env.TAG}" + ":staging"
  }
  stages {
    stage('maven build') {
      steps {
        checkout scm
        container('maven') {
          sh 'mvn -B clean package'
        }
      }
    }
    stage('docker build') {
      steps {
        container('docker') {
          sh "docker build -t ${env.TAG_DEV} ."
        }
      }
    }
    stage('docker push'){
      steps {
        container('docker') {
          sh "docker push ${env.TAG_DEV}"
        }
      }
    }
    stage('deploy to dev') {
      steps {
        container('kubectl') {
          sh "kubectl -n dev apply -f manifest/carts.yml"
        }
      }
    }
    stage('run tests in dev') {
      steps {
        sleep 30

        build job: "jmeter-tests/master",
          parameters: [
            string(name: 'SCRIPT_NAME', value: 'basiccheck.jmx'),
            string(name: 'SERVER_URL', value: "carts.dev"),
            string(name: 'SERVER_PORT', value: '80'),
            string(name: 'CHECK_PATH', value: '/health'),
            string(name: 'VUCount', value: '1'),
            string(name: 'LoopCount', value: '1'),
            string(name: 'DT_LTN', value: "HealthCheck_${BUILD_NUMBER}"),
            string(name: 'FUNC_VALIDATION', value: 'yes'),
            string(name: 'AVG_RT_VALIDATION', value: '0'),
            string(name: 'RETRY_ON_ERROR', value: 'yes')
          ]
      }
    }
    stage('mark for staging') {
      steps {
        container('docker'){
          sh "docker tag ${env.TAG_DEV} ${env.TAG_STAGING}"
        }
      }
    }
    stage('deploy to staging') {
      steps {
        echo "update sockshop deployment yaml for staging -> github webhook triggers deployment to staging"
        echo "apply sockshop deployment yaml to staging environment"
        echo "istio blue/green deployment that rolls back when failure rate increases?"
        echo "at least in production it should be like that!"
      }
    }
  }
}
