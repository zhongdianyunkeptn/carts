/**
 * This pipeline will execute a simple maven build, using a Persistent Volume Claim to store the local Maven repository
 *
 * A PersistentVolumeClaim needs to be created ahead of time with the definition in maven-with-cache-pvc.yml
 *
 * NOTE that typically writable volumes can only be attached to one Pod at a time, so you can't execute
 * two concurrent jobs with this pipeline. Or change readOnly: true after the first run
 */

def label = "maven-${UUID.randomUUID().toString()}"

podTemplate(
  label: label, 
  containers: [
    containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'jnlp', image: 'jenkins/jnlp-slave:3.10-1-alpine'),
    containerTemplate(name: 'docker', image: 'docker:stable', ttyEnabled: true, command: 'cat')
  ], 
  volumes: [
    persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false),
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
  ]
)
{
  env.ORG = "test"
  env.DOCKER_ID = "carts"
  env.VERSION = "0.1.0-${env.BUILD_ID}"

  node(label) {
    stage('build') {
      checkout scm
      container('maven') {
        sh 'mvn -B clean package'
      }
    }
    stage('docker') {
      container('docker') {
        echo "ORG=${env.ORG}"
        echo "DOCKER_ID=${env.DOCKER_ID}"
        echo "BUILD_ID=${env.BUILD_ID}"
        echo "JENKINS_URL=${env.JENKINS_URL}"
        sh "docker build -t ${env.ORG}/${env.DOCKER_ID} ."
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