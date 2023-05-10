def call(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                script {
                    def trivy_output = sh(script: "trivy image --format json ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} | grep "Severity"")
                    if (trivy_output.contains('CRITICAL')) {
                        error('Critical vulnerabilities found in Docker image.')
                    }
                }
}
