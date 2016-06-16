node {
  stage 'Clone'
  checkout scm

  stage 'Build, Test and Deploy'
  try {
    wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'org.jenkinsci.plugins.configfiles.maven.GlobalMavenSettingsConfig', targetLocation: 'globalSettings.xml', variable: '']]]) {
      def mvnHome = tool 'M3'
      sh "${mvnHome}/bin/mvn -gs globalSettings.xml -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
    }
  } finally {
    sh 'rm -f globalSettings.xml'
  }

  stage 'Archive'
  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
