def call(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                script {
                    def trivy_output = sh(script: "trivy image --severity CRITICAL --vuln-type --no-progress ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER}", returnStdout: true)
                    if (trivy_output.contains('CRITICAL')) {
                        error('Critical vulnerabilities found in Docker image.')
                    }
                }
}
