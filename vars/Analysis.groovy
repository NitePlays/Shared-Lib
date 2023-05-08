def call(scannerHome) {
        stage('Sonarqube analysis') {
              steps {
                withSonarQubeEnv('SonarScan') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }
}
