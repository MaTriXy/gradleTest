/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronje 2015
 *
 * This software is licensed under the Apache License 2.0
 * See http://www.apache.org/licenses/LICENSE-2.0 for license details
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * ============================================================================
 */
package org.ysb33r.gradle.gradletest.internal

import groovy.transform.TupleConstructor
import org.gradle.api.Project
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.process.ExecResult
import org.ysb33r.gradle.gradletest.CompatibilityTestResult
import org.ysb33r.gradle.gradletest.GradleTestDescriptor

/**
 * @author Schalk W. Cronjé
 */
@TupleConstructor
class TestRunner  {

    Project project
    File gradleLocationDir
    File testProjectDir
    File initScript
    String version
    String testName
    long startTime
    long endTime

    ExecResult execResult = null

    ExecResult run() {

        File settings = new File(testProjectDir,'settings.gradle')

        if(!settings.exists()) {
            settings.text = ''
        }

        project.logger.info "Running ${testName} against Gradle ${version}"

        startTime = System.currentTimeMillis()

        try {
            execResult = project.javaexec {
                ignoreExitValue = true
                classpath "${gradleLocationDir}/lib/gradle-launcher-${version}.jar"
                workingDir testProjectDir.absoluteFile

                main 'org.gradle.launcher.GradleMain'
                args '--project-cache-dir',"${testProjectDir}/.gradle"
                args '--gradle-user-home',  "${testProjectDir}/../home"
                args '--no-daemon'
                args '--full-stacktrace'
                args '--info'
                args '--init-script',initScript.absolutePath

                if(project.gradle.startParameter.offline) {
                    args '--offline'
                }

                args 'runGradleTest'

                systemProperties 'org.gradle.appname' : this.class.name

                // Capture standardOutput and errorOutput
                // errorOutput = new ByteArrayOutputStream()
                // standardOutput = new ByteArrayOutputStream()
                // systemProperties & environment in a later release
            }
        } finally {
            endTime = System.currentTimeMillis()
        }
    }

    /** Returns this test as a {@code TestDescriptor} so that Gradle test infrastructure can absorb it
     *
     * @return An object implementing the {@code org.gradle.api.tasks.testing.TestDescriptor} interface.
     */
    TestDescriptor getTestDescriptor() {
        new GradleTestDescriptor( name:testName, gradleVersion: version )
    }

    /** Return the results of this test as a {@code TestResult} so that Gradle test infrastucture can absorb it.
     *
     * @return An object implementing the {@code org.gradle.api.tasks.testing.TestResult} interface.
     * If called before the test was executed, acts as if the test was skipped.
     */
    TestResult getTestResult() {
        if(execResult == null) {
            return null
        }

        def result
        switch (execResult.exitValue) {
            case null:
                result = TestResult.ResultType.FAILURE
                break
            case 0:
                result = TestResult.ResultType.SUCCESS
                break
            default:
                result = TestResult.ResultType.SKIPPED
        }

        new CompatibilityTestResult(
            testName : testName,
            gradleVersion : ver,
            resultType: result,
            startTime : startTime,
            endTime: endTime
        )
    }
 }
