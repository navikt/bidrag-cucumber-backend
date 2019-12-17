node {
   def repo = "navikt"
   def sourceapp = "bidrag-cucumber-backend"

    stage("#1: Checkout code") {
        println("[INFO] Clean workspace")
        cleanWs()

        println("[INFO] Checkout ${sourceapp}")
        withCredentials([usernamePassword(credentialsId: 'jenkinsPipeline', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
                sh(script: "git clone https://${USERNAME}:${PASSWORD}@github.com/${repo}/${sourceapp}.git .")
                sh(script: "git checkout ${BRANCH}", returnStatus:true)
            }
        }
    }

    stage("#2 Cucumber tests for backend") {
        println("[INFO] Run cucumber tests for backend")

        withCredentials([
                usernamePassword(credentialsId: 'j104364', usernameVariable: 'USERNAME', passwordVariable: 'USER_AUTH'),
                usernamePassword(credentialsId: TestUserID, usernameVariable: 'TEST_USER', passwordVariable: 'TEST_PASS'),
                usernamePassword(credentialsId: PipUserID, usernameVariable: 'PIP_USER', passwordVariable: 'PIP_AUTH')
            ]) {
            try {
                sh(script: "echo $PIP_USER")
                sh(script:"docker run --rm -v '${env.WORKSPACE}':/usr/src/mymaven -w /usr/src/mymaven " +
                          "-v $JENKINS_HOME/.m2:/root/.m2 maven:3.6.1-jdk-12 " +
                          "mvn clean test ${CucumberTag}" +
                          "  -DENVIRONMENT=${NaisEnvironment}" +
                          "  -DUSERNAME=${USERNAME} -DUSER_AUTH=${USER_AUTH}" +
                          "  -DTEST_USER=${TEST_USER} -DTEST_AUTH=${TEST_PASS}" +
                          "  -DPIP_USER=${PIP_USER} -DPIP_AUTH=${PIP_AUTH}"
                )
            } catch (err) { println("SOMETHING FISHY HAPPENED: " + err) } // Failures should not terminate the pipeline
        }
    }

    stage("#3 Create cucumber report") {
        println("[INFO] Create cucumber reports")
        sh(script:"docker run --rm -v '${env.WORKSPACE}':/usr/src/mymaven -w /usr/src/mymaven " +
                  "-v $JENKINS_HOME/.m2:/root/.m2 maven:3.6.1-jdk-12 " +
                  "mvn cluecumber-report:reporting"
        )

        cucumber buildStatus: 'UNSTABLE', fileIncludePattern:'**/cucumber.json'
    }
}
