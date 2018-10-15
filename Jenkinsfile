pipeline {
  agent {
    label 'maven'
  }
  environment {
    APP_NAME = "carts"
    ARTEFACT_ID = "sockshop/" + "${env.APP_NAME}"
    FROM_FILE = readFile 'version'
    VERSION = "${env.FROM_FILE}" + "-${env.BUILD_ID}"
    /*VERSION = "${env.VERSION_FROM_FILE}-${env.BUILD_ID}"*/
    TAG = "10.31.240.247:5000/library/${env.ARTEFACT_ID}"
    TAG_DEV = "${env.TAG}" + ":dev"
    TAG_STAGING = "${env.TAG}" + ":staging"
  }
  stages {
    stage('Maven build') {
      steps {
        checkout scm
        container('maven') {
          sh 'mvn -B clean package'
        }
      }
    }
    stage('Docker build') {
      steps {
        container('docker') {
          sh "docker build -t ${env.TAG_DEV} ."
        }
      }
    }
    stage('Docker push to registry tagged :dev'){
      steps {
        container('docker') {
          sh "docker push ${env.TAG_DEV}"
        }
      }
    }
    stage('Deploy to dev namespace') {
      steps {
        container('kubectl') {
          sh "kubectl -n dev apply -f manifest/carts.yml"
        }
      }
    }
    stage('Run health check in dev') {
      steps {
        sleep 30

        build job: "jmeter-tests",
          parameters: [
            string(name: 'SCRIPT_NAME', value: 'basiccheck.jmx'),
            string(name: 'SERVER_URL', value: "${env.APP_NAME}.dev"),
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
    stage('Run functional check in dev') {
      steps {
        build job: "jmeter-tests",
          parameters: [
            string(name: 'SCRIPT_NAME', value: "${env.APP_NAME}_load.jmx"),
            string(name: 'SERVER_URL', value: "${env.APP_NAME}.dev"),
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
    stage('Mark artifact for staging namespace') {
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
