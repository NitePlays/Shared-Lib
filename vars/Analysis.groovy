def call(scannerHome, SonarQube) {
        withSonarQubeEnv('${SonarQube}') {
                sh "${scannerHome}/bin/sonar-scanner"
        }
}
