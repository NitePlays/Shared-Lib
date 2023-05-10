def call(PAT, NAME) {
       cleanWs()
       sh 'git clone -b feature-ricards https://${PAT}@github.com/SpaceTech-project/SpaceTech-${NAME}.git .'
}
