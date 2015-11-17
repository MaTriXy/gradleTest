package org.ysb33r.gradle.gradletest.internal
//import org.gradle.testkit.runner.GradleRunner
//import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class TestKitTestRunner extends Specification {

    static final def generator = { String version, File projectDir, def args ->
        org.gradle.testkit.runner.GradleRunner.create()
            .withGradleVersion(version)
            .withProjectDir(projectDir)
            .withArguments(args + 'runGradleTest')
            .build()
    }

    File buildFile

    def beforeSetup() {
        // copy all files
    }

    @Unroll
    def "can execute hello world task with Gradle version #gradleVersion"() {
//        given:
//        buildFile << """
//            task helloWorld {
//                doLast {
//                    logger.quiet 'Hello world!'
//                }
//            }
//        """

        when:
        def result =  generator gradleVersion, testProject, args
//        def result = GradleRunner.create()
//            .withGradleVersion(gradleVersion)
//            .withProjectDir(testProjectDir.root)
//            .withArguments()
//            .build()

        then:
        result.output.contains('Hello world!')
        result.task(":helloWorld").outcome == SUCCESS

        where:
        [ gradleVersion, testProject ] << ( ['2.6', '2.7'].collect { item -> ['build/gradleTest/2.6/test1'].collect { [item,it] }}  )
//        gradleVersion << ['2.6', '2.7'] // Read from predetermined versions file
//        testProject << ['build/gradleTest/2.6/test1']

    }
}