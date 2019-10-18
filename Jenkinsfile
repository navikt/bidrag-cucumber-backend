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

        println("[INFO] cucumber options")
        sh(script: "export CUCUMBER_OPTIONS=-Dcucumber.options='--tags \"@bidrag-cucumber or @bidrag-dokument\"'")
    }

    stage("#2 Cucumber tests with kotlin") {
        println("[INFO] Run cucumber tests with kotlin")

        withCredentials([
                usernamePassword(credentialsId: 'j104364', usernameVariable: 'USERNAME', passwordVariable: 'USER_AUTH'),
                usernamePassword(credentialsId: TestUserID, usernameVariable: 'TEST_USER', passwordVariable: 'TEST_PASS')
            ]) {
            try {
                sh(script:"docker run --rm -v '${env.WORKSPACE}':/usr/src/mymaven -w /usr/src/mymaven " +
                          "-v $JENKINS_HOME/.m2:/root/.m2 maven:3.6.1-jdk-12 " +
                          "mvn clean test $CUCUMBER_OPTIONS" +
                          "  -DENVIRONMENT=${NaisEnvironment}" +
                          "  -DUSERNAME=${USERNAME} -DUSER_AUTH=${USER_AUTH}" +
                          "  -DTEST_USER=${TEST_USER} -DTEST_AUTH=${TEST_PASS}"
                )
            } catch (err) { println(err) } // Test failures should not terminate the pipeline
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
