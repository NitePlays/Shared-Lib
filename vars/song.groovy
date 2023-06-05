def Clone(PAT, NAME) {
       cleanWs()
       sh 'git clone -b feature-ricards-marians https://${PAT}@github.com/SpaceTech-project/SpaceTech-${NAME}.git .'
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
                sh "docker buildx build -t ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} --platform linux/amd64 ."
}

def Trivy(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                script {
                    def vulnerabilities = sh(
                           script: 'trivy image --format json ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER}',
                        returnStdout: true
                    )
                    echo vulnerabilities
                    
                    if (vulnerabilities.contains('CRITICAL')) {
                        error "Critical vulnerability found"
                    }
                }
}

def PushToECR(ECR_REGISTRY, IMAGE_NAME, DOCKER_IMG, TYPE) {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'AWS']]) {
                    sh "docker login -u AWS -p \$(aws ecr get-login-password --region eu-central-1) ${ECR_REGISTRY}"
                    sh "docker tag ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ${DOCKER_IMG}"
                    sh "docker push ${DOCKER_IMG}"
        }
}

def CreateTar(TYPE, BUILD_NUMBER) {
    dir('../archives') {
        sh "sudo tar -czf ${TYPE}_${BUILD_NUMBER}.tar.gz --exclude=node_modules --exclude=README.md --directory=../${TYPE}Song ."
    }
}
