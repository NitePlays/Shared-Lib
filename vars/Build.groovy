def Clone(String PAT) {
       sh 'git clone -b feature-ricards https://${PAT}@github.com/SpaceTech-project/SpaceTech-Frontend.git .'
}

def Build() {
       sh 'npm run build'
}

def Dependencies() {
       stage('Build') {
          steps {
              sh 'npm install'
          }
      }
}

def Analysis(Tool scannerHome) {
        stage('Sonarqube analysis') {
              steps {
                withSonarQubeEnv('SonarScan') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }
}

def QualityGate() {
          stage('SonarQube quality gate') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }
}

def CreateDocker(String IMAGE_NAME, String BUILD_NUMBER, String TYPE) {
  stage('Create Docker Image') {
              steps {
                sh "docker build -t ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ."
            }
        }
}

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

def PushToECR(String ECR_REGISTRY, String IMAGE_NAME, String DOCKER_IMAGE, String TYPE) {
          stage('Push to ECR') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws3']]) {
                    sh "docker login -u AWS -p \$(aws ecr get-login-password) ${ECR_REGISTRY}"
                    sh "docker tag ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ${DOCKER_IMAGE}"
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }
}

def CreateTar() {
          stage('Create Tar Archive') {
              steps {
                sh 'tar -czf ../workspace.tar.gz --exclude=node_modules --exclude=README.md --directory=.. workspace'
                sh 'mkdir -p ../archives'
                sh 'mv ../workspace.tar.gz ../archives'
              }
            }
}
