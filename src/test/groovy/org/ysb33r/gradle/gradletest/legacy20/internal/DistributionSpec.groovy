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
package org.ysb33r.gradle.gradletest.legacy20.internal

import spock.lang.Specification


class DistributionSpec extends Specification {

    def "Converting a list of Distribution to a Set"() {
        given:
        List<DistributionInternal> list = [
            new DistributionInternal('2.2',new File('loc1')),
            new DistributionInternal('2.2',new File('loc2'))
        ]
        Set<DistributionInternal> set = list as Set

        expect:
        set.size() == 1
        set[0].location.name == 'loc1'

    }
}