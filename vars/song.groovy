def Clone(PAT, NAME) {
       cleanWs()
       sh 'git clone -b feature-task13 https://${PAT}@github.com/SpaceTech-project/SpaceTech-${NAME}.git .'
}

def Build() {
       sh 'npm run build'
}

def Dependencies() {
              sh 'npm install'
}

def Analysis(scannerHome, SonarQube) {
        withSonarQubeEnv("${SonarQube}") {
                sh "${scannerHome}/bin/sonar-scanner"
        }
}

def QualityGate() {
                waitForQualityGate abortPipeline: true
}

def CreateDocker(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                sh "docker buildx build -t ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ."
}

def Trivy(IMAGE_NAME, BUILD_NUMBER, TYPE) {
    script {
        sh "trivy image --format json ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} > trivy_output.json"

        def trivyJson = sh(returnStdout: true, script: 'cat trivy_output.json').trim()
        
        def filteredJson = sh(returnStdout: true, script: "echo '${trivyJson}' | jq '.[].Vulnerabilities[] | {Severity: .Severity, Package: .PkgName, Vulnerability: .VulnerabilityID}'")
        echo(filteredJson)

        if (filteredJson.contains('CRITICAL')) {
            error "Critical vulnerability found"
        }
    }
}




def PushToECR(ECR_REGISTRY, IMAGE_NAME, DOCKER_IMG, TYPE) {
               withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS']]) {
                    sh "aws ecr-public get-login-password --region us-east-1 |docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                    sh "docker tag ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ${DOCKER_IMG}"
                    sh "docker push ${DOCKER_IMG}"
       }
}

def CreateTar(TYPE, BUILD_NUMBER) {
    dir('../archives') {
        sh "tar -czf ${TYPE}_${BUILD_NUMBER}.tar.gz --exclude=node_modules --exclude=README.md --directory=../${NAME}-Build ."
    }
}
