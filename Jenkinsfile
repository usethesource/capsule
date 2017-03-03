node {
  def mvnHome = tool 'M3'
  env.JAVA_HOME="${tool 'jdk-oracle-8'}"
  env.PATH="${env.JAVA_HOME}/bin:${mvnHome}/bin:${env.PATH}"

  stage 'Clone'
  checkout scm

  // stage 'Build, Test and Deploy'
  // wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig:usethesource-maven-settings', variable: 'SETTINGS_PATH']]]) {
  //   def mvnHome = tool 'M3'
  //   sh "${mvnHome}/bin/mvn -s ${env.SETTINGS_PATH} -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"
  // }
  // sh "${mvnHome}/bin/mvn -s /var/jenkins_home/usethesource-maven-settings.xml -Dmaven.repo.local=/var/jenkins_home/repo -B clean install deploy"

  stage 'Build and Test'
  sh "mvn -B clean install"

  stage 'Deploy'
  sh "mvn -s ${env.HOME}/usethesource-maven-settings.xml -B deploy -DskipTests"

  stage 'Archive'
  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
