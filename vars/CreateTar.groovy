def call(TYPE, BUILD_NUMBER) {
                sh 'tar -czf ${TYPE}_${BUILD_NUMBER}.tar.gz --exclude=node_modules --exclude=README.md .'
                sh 'mv ${TYPE}_${BUILD_NUMBER}.tar.gz ../archives'
}
