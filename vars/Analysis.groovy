def call(scannerHome) {
        withSonarQubeEnv('SonarScan') {
                sh "${scannerHome}/bin/sonar-scanner"
        }
}
