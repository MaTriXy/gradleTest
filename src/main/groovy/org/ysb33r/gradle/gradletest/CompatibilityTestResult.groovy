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

import groovy.transform.Immutable
import org.gradle.api.tasks.testing.TestResult

/**
 * @author Schalk W. Cronjé
 *
 */
@Immutable
class CompatibilityTestResult implements TestResult {

    String testName
    String gradleVersion
    TestResult.ResultType resultType
    long startTime
    long endTime

    final long testCount = 1

    /**
     * If the test failed with an exception, this will be the exception.  Some test frameworks do not fail without an
     * exception (JUnit), so in those cases this method will never return null.
     *
     * @return The exception, if any, logged for this test.  If none, a null is returned.
     */

    @Override
    Throwable getException() {
        return null
    }

    /**
     * If the test failed with any exceptions, this will contain the exceptions.  Some test frameworks do not fail
     * without an exception (JUnit), so in those cases this method will never return null.
     *
     * @return The exceptions, if any, logged for this test.  If none, an empty list is returned.
     */
    @Override
    List<Throwable> getExceptions() {
         [] as List<Throwable>
    }

    @Override
    long getSuccessfulTestCount() {
        resultType  == TestResult.ResultType.SUCCESS ? 1 : 0
    }

    @Override
    long getFailedTestCount() {
        resultType  == TestResult.ResultType.FAILED ? 1 : 0
    }

    @Override
    long getSkippedTestCount() {
        resultType  == TestResult.ResultType.SKIPPED ? 1 : 0
    }

    boolean getPassed() {
        resultType == TestResult.ResultType.SUCCESS
    }
}