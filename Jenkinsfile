/**
 * This pipeline will execute a simple maven build, using a Persistent Volume Claim to store the local Maven repository
 *
 * A PersistentVolumeClaim needs to be created ahead of time with the definition in maven-with-cache-pvc.yml
 *
 * NOTE that typically writable volumes can only be attached to one Pod at a time, so you can't execute
 * two concurrent jobs with this pipeline. Or change readOnly: true after the first run
 */

def label = "maven-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
  containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat')
  ], volumes: [
  persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false)
  ]) {

  node(label) {
    stage('Build a Maven project') {
      git 'https://github.com/dynatrace-innovationlab/carts.git'
      container('maven') {
          sh 'mvn -B clean package'
      }
    }
  }
}
/*
pipeline {
  agent any

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
}*/