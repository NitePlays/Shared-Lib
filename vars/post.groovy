def Failure(env.JOB_NAME, env.BUILD_NUMBER, env.BUILD_URL) {
    failure {
        slackSend color: 'danger',
            message: "Build failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER} (${env.BUILD_URL})"
    }
}

def Always(TYPE, BUILD_NUMBER) {
        always {
          dir('../archives') {
            archiveArtifacts("${TYPE}_${BUILD_NUMBER}.tar.gz")
            }
        }
}
