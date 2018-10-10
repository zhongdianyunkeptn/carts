pipeline {
  environment {
    ORG="test"
    DOCKER_ID="carts"
    VERSION="0.1.0-${env.BUILD_NUMBER}"
  }
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
    timeout(time: 60, unit: 'MINUTES')
  }
  stages {
    stage('build') {
      steps {
        checkout scm
        container('maven') {
          sh "mvn versions:set -DnewVersion=${env.VERSION}"
          sh "mvn package"
        }
      }
    }
    stage('docker') {
      steps {
        sh "docker build --pull -t ${env.DOCKER_REGISTRY_URL}/library/${env.ORG}/${env.DOCKER_ID}:${env.BUILD_NUMBER} ."
      }
    }
  }
  post {
    always {
      cleanWs()
    }
  }
}