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

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskState
import org.gradle.util.GradleVersion
import org.ysb33r.gradle.gradletest.internal.TestSet
import org.ysb33r.gradle.gradletest.legacy20.LegacyGradleTestPlugin
import org.ysb33r.gradle.gradletest.legacy20.internal.AvailableDistributionsInternal
import org.ysb33r.gradle.gradletest.legacy20.internal.DistributionInternal
import org.ysb33r.gradle.gradletest.legacy20.internal.GradleTestDownloader
import org.ysb33r.gradle.gradletest.legacy20.DeprecatingGradleTestExtension
import org.ysb33r.gradle.gradletest.legacy20.Distribution
import org.ysb33r.gradle.gradletest.legacy20.GradleTestExtension

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class GradleTestPlugin implements Plugin<Project> {

    /** Applies a specific plugin depending whether the runnign version if Gradle 2.13+ or and earlier
     * version.
     */
    void apply(Project project) {
        if(GradleVersion.current() < GradleVersion.version('2.13')) {
            project.apply plugin : LegacyGradleTestPlugin
        } else {
            applyTestKit(project)
        }
    }

    /** Applies the appropriate structures when Gradle is 2.13+
     *
     * @param project
     */
    private void applyTestKit(Project project) {
        project.with {
//            apply plugin : 'java-gradle-plugin'
            apply plugin : 'groovy'
            extensions.create Names.EXTENSION, DeprecatingGradleTestExtension, project
        }
        addTestKitTriggers(project)
        TestSet.addTestSet(project,Names.DEFAULT_TASK)
    }

    /** Adds the appropriate task triggers.
     */
    @CompileDynamic
    private void addTestKitTriggers(Project project) {
        project.ext {
            additionalGradleTestSet = { String name ->
                TestSet.addTestSet(project, name)
            }
        }
    }

}
