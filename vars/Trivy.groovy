def Trivy(String IMAGE_NAME, String BUILD_NUMBER, String TYPE) {
        stage('Trivy Scan') {
              steps {
                script {
                    def trivy_output = sh(script: "trivy image --severity CRITICAL --no-progress ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER}", returnStdout: true)
                    if (trivy_output.contains('CRITICAL')) {
                        error('Critical vulnerabilities found in Docker image.')
                    }
                }
            }
        }
}
