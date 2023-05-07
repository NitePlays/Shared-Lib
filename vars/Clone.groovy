def call(PAT) {
       cleanWs()
       sh 'git clone -b feature-ricards https://${PAT}@github.com/SpaceTech-project/SpaceTech-Frontend.git .'
}
