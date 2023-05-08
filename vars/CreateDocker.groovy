def call(IMAGE_NAME, BUILD_NUMBER, TYPE) {
      sh "docker build -t ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ."
}
