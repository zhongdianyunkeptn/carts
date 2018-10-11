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
    containerTemplate(name: 'jnlp', image: 'jenkins/jnlp-slave:3.10-1-alpine'),
    containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'docker', image: 'docker:stable', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.12.1', command: 'cat', ttyEnabled: true)
  ], 
  volumes: [
    persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo', readOnly: false),
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
  ]
)
{
  node(label) {
    stage('build') {
      checkout scm

      def version = readFile('version')    
      env.ORG = "test"
      env.DOCKER_ID = "carts"
      env.VERSION = version + "-${env.BUILD_ID}"

      container('maven') {
        sh 'mvn -B clean package'
      }
    }
    stage('docker') {
      container('docker') {
        sh "cat /etc/resolv.conf"
        sh "docker build -t 10.31.240.247:5000/library/${env.ORG}/${env.DOCKER_ID}:${env.BUILD_NUMBER} ."
        sh "docker push 10.31.240.247:5000/library/${env.ORG}/${env.DOCKER_ID}:${env.BUILD_NUMBER}"
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