def call(String FE_DOCKER_IMAGE, String BE_DOCKER_IMAGE, String AWS_ACCESS_KEY_ID, String AWS_SECRET_ACCESS_KEY, String REG, String ECRREG) {
      withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws3', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
        script {
          def dockerCompose = """
            version: "3.8"
            services:
              fesong:
                image: ${FE_DOCKER_IMAGE}
                ports:
                  - 3000:3000
              besong:
                image: ${BE_DOCKER_IMAGE}
                ports:
                - 3002:3002 """

          writeFile file: 'docker-compose.yaml', text: dockerCompose

          dir('terraform') {
            sh "AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} terraform init"
            sh """
              AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} terraform apply \
              -auto-approve \
              -input=false \
              -var "aws_access=aws configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}" \
              -var "aws_secret=aws configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}" \
              -var "docker_login=aws ecr get-login-password --region ${REG} | sudo docker login --username AWS --password-stdin ${ECRREG}" \
              -var "fe_cmd=sudo docker pull ${FE_DOCKER_IMAGE}" \
              -var "be_cmd=sudo docker pull ${BE_DOCKER_IMAGE}" \
              -var "docker_compose=sudo docker-compose up -d"
            """
          }
        }
      }
}
