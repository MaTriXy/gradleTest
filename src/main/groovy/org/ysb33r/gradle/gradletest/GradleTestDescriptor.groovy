
package org.ysb33r.gradle.gradletest

import groovy.transform.TupleConstructor
import org.gradle.api.tasks.testing.TestDescriptor

@TupleConstructor
class GradleTestDescriptor implements TestDescriptor {

    String name
    String gradleVersion

//    /**
//     * Returns the name of the test.  Not guaranteed to be unique.
//     *
//     * @return The test name
//     */
//    @Override
//    String getName() {
//        return null
//    }

    /**
     * Returns the test class name for this test, if any.
     *
     * @return The class name. May return null.
     */
    @Override
    String getClassName() {
        'gradle-' + gradleVersion
    }

    /**
     *  gradleTest cases are not composites.
     *
     * @return Always false
     */
    @Override
    boolean isComposite() { false }

    /**
     * gradleTest cases do not have parents
     *
     * @return Always null.
     */
    @Override
    TestDescriptor getParent() { null }
}
//
//import groovy.transform.TupleConstructor
//import org.gradle.api.tasks.testing.TestDescriptor
//
///**
// * @author Schalk W. Cronjé
// */
//@TupleConstructor
//class GradleTestDescriptor implements TestDescriptor {
//
//    final String testName
//    final String gradleVersion
//
//    /**
//     * Returns the name of the test.  Not guaranteed to be unique.
//     *
//     * @return The test name
//     */
//    @Override
//    String getName() {
//        testName
//    }
//
//    /**
//     * Returns the test class name for this test, if any.
//     *
//     * @return The class name. May return null.
//     */
//    @Override
//    String getClassName() {
//        'gradle-' + gradleVersion
//    }
//
//    /**
//     *  gradleTest cases are not composites.
//     *
//     * @return Always false
//     */
//    @Override
//    boolean isComposite() { false }
//
//    /**
//     * gradleTest cases do not have parents
//     *
//     * @return Always null.
//     */
//    @Override
//    TestDescriptor getParent() { null }
//}
