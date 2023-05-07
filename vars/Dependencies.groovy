def call() {
       stage('Build') {
          steps {
              sh 'npm install'
          }
      }
}
