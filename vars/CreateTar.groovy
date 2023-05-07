def CreateTar() {
          stage('Create Tar Archive') {
              steps {
                sh 'tar -czf ../workspace.tar.gz --exclude=node_modules --exclude=README.md --directory=.. workspace'
                sh 'mkdir -p ../archives'
                sh 'mv ../workspace.tar.gz ../archives'
              }
            }
}
