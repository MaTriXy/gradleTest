package org.ysb33r.gradle.gradletest

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.ysb33r.gradle.gradletest.internal.GradleTestIntegrationSpecification
import spock.lang.Specification


/**
 * Run `gradle integrationTestRepo` before running this from IDE.
 */
class CompileIntegrationSpec extends Specification {

    static final List TESTNAMES = ['alpha','beta','gamma']
    static final File GRADLETESTREPO = new File(System.getProperty('GRADLETESTREPO') ?: 'build/integrationTest/repo').absoluteFile

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    @Delegate Project project

    void setup() {
        project = ProjectBuilder.builder().withProjectDir(testProjectDir.root).build()

        File srcDir = new File(project.projectDir,'src/gradleTest')
        srcDir.mkdirs()
        TESTNAMES.each {
            File testDir = new File(srcDir,it)
            testDir.mkdirs()
            new File(testDir,'build.gradle').text = ''
        }
    }

    def "Compiling generated source code"() {
        given: "A GradleTest project"
        configure(project) {
            apply plugin : 'org.ysb33r.gradletest'

            repositories {
                flatDir {
                    dirs GRADLETESTREPO
                }
            }

            gradleTest {
                versions '2.1','2.2'
            }

            evaluate()
        }

        when: "The generated source is compiled"
        Task generatorTask = tasks.getByName('gradleTestGenerator')
        Task compileTask = tasks.getByName('compileGradleTestGroovy')
        SourceSet sourceSet = project.sourceSets.getByName('gradleTest')
        File classesDir = sourceSet.output.classesDir
        generatorTask.execute()
        compileTask.execute()


        then: "Generator task to have executed"
        generatorTask.didWork

        and: "Expected classes in output directory"
        new File(classesDir,"gradleTest/tests/AlphaCompatibilitySpec.class").exists()

    }
}