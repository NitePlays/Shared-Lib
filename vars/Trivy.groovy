def call(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                script {
                    def vulnerabilities = sh(
                        script: 'trivy image --format json ${IMAGE_NAME}:FE_${BUILD_NUMBER}',
                        returnStdout: true
                    )
                    echo vulnerabilities
                    
                    if (vulnerabilities.contains('CRITICAL')) {
                        error "Critical vulnerability found"
                    }
                }
}

