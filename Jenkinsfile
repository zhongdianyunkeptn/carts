pipeline {
  agent {
    label 'maven'
  }
  environment {
    ARTEFACT_ID = "sockshop/carts"
    VERSION = readFile('version') + "-${env.BUILD_ID}"
    /*VERSION = "${env.VERSION_FROM_FILE}-${env.BUILD_ID}"*/
    TAG = "10.31.240.247:5000/${env.ARTEFACT_ID}"
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
          sh "kubectl -n staging apply -f manifest/carts.yml"
        }
      }
    }
    stage('run tests in dev') {
      steps {
        echo "running tests"
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
      }
    }
  }
}
