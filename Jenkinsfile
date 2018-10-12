def currentVersion = readFile('version')

pipeline {
  agent {
    label 'maven'
  }
  environment {
    ORG = "sockshop"
    DOCKER_ID = "carts"
    VERSION = currentVersion + "-${env.BUILD_ID}"
    TAG = "10.31.240.247:5000/library/${env.ORG}/${env.DOCKER_ID}"
    TAG_UNSTABLE = ${env.TAG} + ":unstable"
    TAG_STABLE = ${env.TAG} + ":stable"
  }
  stages {
    stage('maven build') {
      checkout scm
      container('maven') {
        sh 'mvn -B clean package'
      }
    }
    stage('docker build') {
      container('docker') {
        sh "docker build -t ${env.TAG_UNSTABLE} ."
      }
    }
    stage('docker push'){
      container('docker') {
        sh "docker push ${env.TAG_UNSTABLE}"
      }
    }
    stage('deploy to dev') {
      container('kubectl') {
        sh "kubectl -n staging apply -f manifest/carts.yml"
      }
    }
    stage('run tests in dev') {
      echo "running tests"
    }
    stage('mark for staging') {
      container('docker'){
        sh "docker tag ${env.TAG_UNSTABLE} ${env.TAG_STABLE}"
      }
    }
  }
}
