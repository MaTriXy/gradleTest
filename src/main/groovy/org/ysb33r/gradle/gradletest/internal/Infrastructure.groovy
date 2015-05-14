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

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.WorkResult
import org.ysb33r.gradle.gradletest.GradleInvocation

/** Utiity class to aid in building the files structure for compatibility testing
 *
 * @author Schalk W. Cronjé
 */
class Infrastructure {
    /** Creates the file layout. It takes a property map as aparaneter with the following required non-null-value properties:
     *
     *  @li name - Name of the the test task (usually {@code gradleTest}).
     *  @li project - THe project that these tests are being built for
     *  @li tests - A list of tests to be executed
     *  @li invocations - A closure which returns a new GradleInvocation every time it is called
     *  @li sourceDir - Source dreictory where tests are copied from
     *  @li initScript - URI of the initscript that will be used
     *  @li versions - A list of versions that the compatibility tests will be executed against
     *
     * @param settings Property map
     * @return A list of TestRunner object which can be executed
     */
    static List<TestRunner> create( Map settings ) {

        final Project project = settings.project
        final List<String> tests = settings.tests
        final Closure invocationFactory = settings.invocations
        final String name = settings.name
        final File src = settings.sourceDir
        final Object initScript = settings.initScript
        final Set<String> versions = settings.versions


        assert project != null
        assert tests != null
        assert invocationFactory != null
        assert name != null
        assert src != null
        assert initScript != null
        assert versions != null

        final File baseDir = new File(project.buildDir,name)
        final File initGradle = new File(baseDir,"init.gradle" )
        final File repo = new File( baseDir,"repo" )
        final Logger logger = project.logger

        List<TestRunner> testRunners = []
        WorkResult wr

        logger.debug "Infrastructure: Copying initscript from '${initScript}'"
        wr = project.copy {
            from initScript
            into initGradle.parentFile
            rename { 'init.gradle' }
            // TODO: Fix this replacement
//            filter { line ->
//                line.replaceAll('%%GROUP%%',project.group).
//                    replaceAll('%%MODULE%%',project.tasks.jar.baseName).
//                    replaceAll('%%VERSION%%',project.version)
//            }
        }

        assert wr.didWork

        versions.each { ver ->
            File dest = new File(baseDir,ver)
            logger.debug "Infrastructure: Copying project files from '${src}' to '${dest}'"
            wr = project.copy {
                from src, {
                    include '**'
                }
                into dest
                filter { line ->
                    line.replaceAll('%%GROUP%%',project.group).
                    replaceAll('%%MODULE%%',project.tasks.jar.baseName).
                    replaceAll('%%VERSION%%',project.version)
                }

            }
            assert wr.didWork

            tests.each { test ->
                GradleInvocation gradle = invocations()

                gradle {
                    gradleVersion = ver
                    projectDir = new File(dest,test)
                }

                testRunners+= new TestRunner(
                    gradleInvocation : gradle,
                    testName : test,
                    version : ver,
                    initScript : initGradle
                )

                logger.debug "Infrastructure: Created test runner for '${ver}:${test}'"
            }
        }

        logger.debug "Infrastructure: Copying ${project.configurations.getByName(name).files} to '${repo}'"
        project.copy {
            from project.configurations.getByName(name).files
            into repo
        }

        testRunners
    }
}
