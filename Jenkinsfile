node {
  stage 'Clone'
  checkout scm

  stage 'Build, Test and Deploy'
  // def mvnHome = tool 'M3'
  // sh "${mvnHome}/bin/mvn -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
  // try {
  //   wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig:usethesource-maven-settings', targetLocation: 'settings.xml', variable: '']]]) {
  //     def mvnHome = tool 'M3'
  //     sh "${mvnHome}/bin/mvn -s settings.xml -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
  //   }
  // } finally {
  //   sh 'rm -f settings.xml'
  // }
  // wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig:usethesource-maven-settings', variable: 'SETTINGS_PATH']]]) {
  //   def mvnHome = tool 'M3'
  //   sh "${mvnHome}/bin/mvn -s ${env.SETTINGS_PATH} -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
  // }
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -s /var/jenkins_home/usethesource-maven-settings.xml -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"

  stage 'Archive'
  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
