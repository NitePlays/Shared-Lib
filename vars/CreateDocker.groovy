def CreateDocker(String IMAGE_NAME, String BUILD_NUMBER, String TYPE) {
  stage('Create Docker Image') {
              steps {
                sh "docker build -t ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ."
            }
        }
}
