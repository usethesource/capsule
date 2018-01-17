node {
  try {
      stage('Clone'){
          checkout scm
      }

      withMaven(maven: 'M3') {
          stage('Build and Test') {
              sh "mvn clean install jacoco:report"
          }

          stage('Report Code Coverage') {
              sh "                           curl https://codecov.io/bash | bash -s - -K -X gcov -t 5f64115f-81e9-4128-b761-e23ce5e20f95"
              sh "cd capsule-core         && curl https://codecov.io/bash | bash -s - -K -X gcov -t 5f64115f-81e9-4128-b761-e23ce5e20f95"
              sh "cd capsule-experimental && curl https://codecov.io/bash | bash -s - -K -X gcov -t 5f64115f-81e9-4128-b761-e23ce5e20f95"
              sh "cd capsule-veritas      && curl https://codecov.io/bash | bash -s - -K -X gcov -t 5f64115f-81e9-4128-b761-e23ce5e20f95"
          }

          stage('Deploy') {
              sh "mvn deploy -DskipTests"
          }
      }

      stage('Archive') {
          step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
      }
      if (currentBuild.previousBuild.result == "FAILURE") { 
          slackSend (color: '#5cb85c', message: "BUILD BACK TO NORMAL: <${env.BUILD_URL}|${env.JOB_NAME} [${env.BUILD_NUMBER}]>")
      }
  } catch (e) {
    slackSend (color: '#d9534f', message: "FAILED: <${env.BUILD_URL}|${env.JOB_NAME} [${env.BUILD_NUMBER}]>")
    throw e
  }
}
