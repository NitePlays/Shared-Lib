def call(TYPE, BUILD_NUMBER) {
                sh 'cd ../archives'
                sh 'tar -czf ${TYPE}_${BUILD_NUMBER}.tar.gz --exclude=node_modules --exclude=README.md --directory=../${TYPE}Song .'
}
