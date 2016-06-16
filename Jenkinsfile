node {
  stage 'Clone'
  checkout scm

  stage 'Build, Test and Deploy'
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
  // try {
  //   wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig:usethesource-maven-settings', targetLocation: 'settings.xml', variable: '']]]) {
  //     def mvnHome = tool 'M3'
  //     sh "${mvnHome}/bin/mvn -s settings.xml -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
  //   }
  // } finally {
  //   sh 'rm -f settings.xml'
  // }

  stage 'Archive'
  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
