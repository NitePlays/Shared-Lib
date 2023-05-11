def Clone(PAT, NAME) {
       cleanWs()
       sh 'git clone -b feature-ricards https://${PAT}@github.com/SpaceTech-project/SpaceTech-${NAME}.git .'
}

def Build() {
       sh 'npm run build'
}

def Dependencies() {
              sh 'npm install'
}

def Analysis(scannerHome) {
                withSonarQubeEnv('SonarScan') {
                    sh "${scannerHome}/bin/sonar-scanner"
        }
}

def QualityGate() {
                waitForQualityGate abortPipeline: true
}

def CreateDocker(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                sh "docker build -t ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ."
}

def Trivy(IMAGE_NAME, BUILD_NUMBER, TYPE) {
                script {
                    def trivy_output = sh(script: "trivy image --severity CRITICAL --no-progress ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER}", returnStdout: true)
                    if (trivy_output.contains('CRITICAL')) {
                        error('Critical vulnerabilities found in Docker image.')
                    }
        }
}

def PushToECR(ECR_REGISTRY, IMAGE_NAME, DOCKER_IMAGE, TYPE) {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws3']]) {
                    sh "docker login -u AWS -p \$(aws ecr get-login-password) ${ECR_REGISTRY}"
                    sh "docker tag ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ${DOCKER_IMAGE}"
                    sh "docker push ${DOCKER_IMAGE}"
        }
}

def CreateTar() {
                sh 'tar -czf ../workspace.tar.gz --exclude=node_modules --exclude=README.md --directory=.. workspace'
                sh 'mkdir -p ../archives'
                sh 'mv ../workspace.tar.gz ../archives'
}
