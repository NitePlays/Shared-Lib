def call(IMAGE_NAME, BUILD_NUMBER, TYPE) {
    script {
        sh "trivy image --format json ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} > vuln.json"
        def vulnerabilities = readFile file: 'vuln.json'
        
            if (vulnerabilities_contain == 'CRITICAL') {
                error "Critical vulnerability found"
            }
        }
    }
}
