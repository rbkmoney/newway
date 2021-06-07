#!groovy
build('newway', 'java-maven') {
    checkoutRepo()
    loadBuildUtils()

    def javaServicePipeline
    runStage('load JavaService pipeline') {
        javaServicePipeline = load("build_utils/jenkins_lib/pipeJavaServiceInsideDocker.groovy")
    }

    def serviceName = env.REPO_NAME
    def mvnArgs = '-DjvmArgs="-Xmx256m"'

    def serviceImage = "c0612d6052ac049496b72a23a04acb142035f249"
    def buildImage = "365786e22351183f6fa694febdcb42141f7fdb80"

    javaServicePipeline(serviceName, serviceImage, buildImage, mvnArgs)
}
