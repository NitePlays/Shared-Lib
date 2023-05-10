def call(TYPE, BUILD_NUMBER) {
    dir('../archives') {
        sh "sudo tar -czf ${TYPE}_${BUILD_NUMBER}.tar.gz --exclude=node_modules --exclude=README.md --directory=../${TYPE}Song ."
    }
}
