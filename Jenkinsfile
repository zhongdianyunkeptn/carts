def label = "maven-${UUID.randomUUID().toString()}"

podTemplate(
  label: label, 
  containers: [
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
    stage('maven build') {
      checkout scm

      def version = readFile('version') 
      env.ORG = "sockshop"
      env.DOCKER_ID = "carts"
      env.VERSION = version + "-${env.BUILD_ID}"
      env.TAG = "10.31.240.247:5000/library/${env.ORG}/${env.DOCKER_ID}"
      env.TAG_UNSTABLE = TAG + ":unstable"
      env.TAG_STABLE = TAG + ":stable"

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
