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
    stage('build') {
      checkout scm

      def version = readFile('version')'   
      env.ORG = "sockshop"
      env.DOCKER_ID = "carts"
      env.VERSION = version + "-${env.BUILD_ID}"

      container('maven') {
        sh 'mvn -B clean package'
      }
    }
    stage('docker build and push') {
      container('docker') {
        sh "docker build -t 10.31.240.247:5000/library/${env.ORG}/${env.DOCKER_ID}:${env.VERSION} ."
        sh "docker push 10.31.240.247:5000/library/${env.ORG}/${env.DOCKER_ID}:${env.VERSION}"
      }
    }
  }
}
