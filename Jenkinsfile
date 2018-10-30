pipeline {
  agent {
    label 'maven'
  }
  environment {
    APP_NAME = "carts"
  }
  stages {
    stage('Performance Check') {
      steps {
        checkout scm

        recordDynatraceSession(
          envId: 'Dynatrace Tenant',
          testCase: 'loadtest',
          tagMatchRules: [
            [
              meTypes: [
                [meType: 'SERVICE']
              ],
              tags: [
                [context: 'CONTEXTLESS', key: 'app', value: "${env.APP_NAME}"],
                [context: 'CONTEXTLESS', key: 'environment', value: 'dev']
              ]
            ]
          ]
        ) 
        {
          build job: "jmeter-tests",
            parameters: [
              string(name: 'SCRIPT_NAME', value: "${env.APP_NAME}_load.jmx"),
              string(name: 'SERVER_URL', value: "${env.APP_NAME}.dev"),
              string(name: 'SERVER_PORT', value: '80'),
              string(name: 'CHECK_PATH', value: '/health'),
              string(name: 'VUCount', value: '10'),
              string(name: 'LoopCount', value: '250'),
              string(name: 'DT_LTN', value: "PerfCheck_${BUILD_NUMBER}"),
              string(name: 'FUNC_VALIDATION', value: 'no'),
              string(name: 'AVG_RT_VALIDATION', value: '250')
            ]
        }
        // Now we use the Performance Signature Plugin to pull in Dynatrace Metrics based on the spec file
        perfSigDynatraceReports(
          envId: 'Dynatrace Tenant', 
          nonFunctionalFailure: 1, 
          specFile: "/monspec/${env.APP_NAME}_perfsig.json"
        ) 
      }
    }
  }
}
