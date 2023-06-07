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
        def filteredOutput = sh(
            script: 'cat trivy_output.json | jq ".Results[].Vulnerabilities[] | ["CVE-ID: " + .VulnerabilityID, "Severity: "+ .Severity, "Package: "+ .PkgName, "Title: "+  .Title]"',
            returnStdout: true
        )
        echo filteredOutput

        if (filteredOutput.contains('HIGH')) {
               slackSend (color: '#FF0000', message: "Critical vulnerability was found in ${env.JOB_NAME} build ${env.BUILD_NUMBER} (${env.BUILD_URL})")
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
