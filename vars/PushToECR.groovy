def call(ECR_REGISTRY, IMAGE_NAME, DOCKER_IMAGE, TYPE) {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws3']]) {
                    sh "docker login -u AWS -p \$(aws ecr get-login-password) ${ECR_REGISTRY}"
                    sh "docker tag ${IMAGE_NAME}:${TYPE}_${BUILD_NUMBER} ${DOCKER_IMAGE}"
                    sh "docker push ${DOCKER_IMAGE}"
        }
}
