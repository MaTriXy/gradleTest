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
package org.ysb33r.gradle.gradletest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.Test
import org.ysb33r.gradle.gradletest.internal.AvailableDistributionsInternal
import org.ysb33r.gradle.gradletest.internal.DistributionInternal
import org.ysb33r.gradle.gradletest.internal.GradleTestDownloader

/**
 * @author Schalk W. Cronjé
 */
class GradleTestPlugin implements Plugin<Project> {
    void apply(Project project) {

        project.with {

            // Force the JAVA plugin to be applied. You need at least it to build
            // a plugin anyway
            apply plugin : 'java'

            configurations.maybeCreate  Names.CONFIGURATION
            configurations.maybeCreate  Names.DEFAULT_TASK

            def gradleExt = extensions.create Names.EXTENSION, GradleTestExtension, project
            def downloader = tasks.create Names.DOWNLOADER_TASK, GradleTestDownloader
            tasks.create Names.DEFAULT_TASK, GradleTest

            def cleaner = tasks.create Names.CLEAN_DOWNLOADER_TASK, Delete
            cleaner.description = 'Remove downloaded distributions from build directory'

            // When downloader fininshed we want the extension to have the new distributions added
            gradle.addListener( createUpdateListener() )

            addGradleTestCompilation(project)

            afterEvaluate { Project p ->

                // Get list of required versions
                Set<String> versions = GradleTest.findAllRequiredVersions(p)

                // Add a dependency for all Gradle tasks on the downloader tasks
                // Add a configuration on the jar task, so that it's output becomes
                // part of the gradleTask configuration
                tasks.withType(GradleTest) { t ->
                    t.dependsOn Names.DOWNLOADER_TASK
                    p.dependencies.add(t.name,p.tasks.jar.outputs.files)
                }

                // Search for local distributions
                Set<Distribution> dists = DistributionInternal.searchLocal(p,gradleExt) as Set
                Set<String> hasVersions = dists.collect { it.version } as Set<String>

                GradleTestDownloader.updateVersions(downloader,versions-hasVersions)

                // Adds the local distributions to available distributions
                if(gradleExt.distributions == null) {
                    gradleExt.distributions = new AvailableDistributionsInternal()
                }

                gradleExt.distributions.addDistributions dists

                tasks.getByName(Names.CLEAN_DOWNLOADER_TASK).configure {
                    description
                    delete tasks.getByName(Names.DOWNLOADER_TASK).outputDir
                }
            }
        }
    }

    // Hacked together with Vladimír Orany
    // Vincenzo Forchi
    void addGradleTestCompilation(Project project) {
        project.with {
            configurations {
                gradleTestCompile {
                    extendsFrom testCompile
                }
                gradleTestRuntime {
                    extendsFrom gradleTestCompile, testRuntime
                }
            }

            sourceSets {
                gradleTestRunner2 {
                    java.srcDir "${buildDir}/gradleTest/generated"
//                    groovy.srcDir "${buildDir}/gradleTest/generated"
//                    resources.srcDir file("src/integrationTest/resources")
                    compileClasspath = sourceSets.main.output +  configurations.gradleTestCompile
                    runtimeClasspath = output + compileClasspath + configurations.gradleTestRuntime
                }
            }

            def runner = tasks.create('gradleTestRunner',Test)
            runner.configure {
                dependsOn 'jar'
                testClassesDir = sourceSets.gradleTestRunner2.output.classesDir
                classpath = sourceSets.gradleTestRunner2.runtimeClasspath
                mustRunAfter 'test'
            }

        }
    }
    /** Creates a a listener which will update the Gradle Distribution Extension with the location of
     * downloaded (and unpacked) distributions
     *
     * @return An instance of a {@code TaskExecutionListeneer}
     */
    static TaskExecutionListener createUpdateListener() {
        new TaskExecutionListener() {
            @Override
            void beforeExecute(Task task) {}

            @Override
            void afterExecute(Task task, TaskState state) {
                if(task instanceof GradleTestDownloader && state.didWork && !state.skipped) {
                    GradleTestExtension ext = task.project.extensions.getByName(Names.EXTENSION)
                    GradleTestDownloader downloader = task as GradleTestDownloader
                    if(ext.distributions == null) {
                        ext.distributions = new AvailableDistributionsInternal()
                    }
                    (ext.distributions as AvailableDistributionsInternal).addDistributions downloader.downloaded
                }
            }
        }
    }

}
