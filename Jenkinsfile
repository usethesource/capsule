node {
  git url: 'https://github.com/usethesource/capsule.git'
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn clean install"
  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
  step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
}
