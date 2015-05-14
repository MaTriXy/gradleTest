package org.ysb33r.gradle.gradletest

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.buildcomparison.gradle.GradleBuildInvocationSpec
import org.gradle.process.ExecResult
import org.gradle.util.CollectionUtils

/**
 * @author Schalk W. Cronjé
 */
class GradleInvocation implements GradleBuildInvocationSpec {

    String gradleVersion
    File projectDir
    Project initiatingProject

    List<String> arguments = []
    List<String> tasks = []

    GradleInvocation( final Map<String,File> distributionsMap ) {
        assert distributions != null
        distributions = distributionsMap
    }

    /**
     * Sets the “root” directory of the build.
     *
     * This should not be the project directory of child project in a multi project build.
     * It should always be the root of the multiproject build.
     *
     * The value is interpreted as a file as per {@link org.gradle.api.Project#file(Object)}.
     *
     * @param pd The “root” directory of the build.
     */
    @Override
    void setProjectDir(Object pd) {
        switch(projectDir) {
            case File:
                projectDir = pd
                break
            case String:
                projectDir = new File(pd)
                break
            default:
                projectDir = new File(pd.toString())
        }
    }

    /**
     * Sets the tasks to execute.
     *
     * @param tasks The tasks to execute.
     */
    @Override
    void setTasks(Iterable<String> tasks) {
        tasks.clear()
        tasks.addAll(tasks)
    }

    /**
     * The command line arguments (excluding tasks) to invoke the build with.
     *
     * @return The command line arguments (excluding tasks) to invoke the build with.
     */
    @Override
    List<String> getArguments() {
        this.arguments
    }

    /**
     * Sets the command line arguments (excluding tasks) to invoke the build with.
     * Will always have a fixed set of initial arguments.
     * @param arguments The command line arguments (excluding tasks) to invoke the build with.
     */
    @Override
    void setArguments(Iterable<String> args) {
        extraArgs.clear()
        extraArgs.addAll args
    }

    void arguments(Object... args) {
        this.arguments.addAll(CollectionUtils.stringize(args as List))
    }

    ExecResult run() {

        assert initiatingProject != null

        File testProjectDir = projectDir.absoluteFile
        File location = distributions[gradleVersion]

        if(location == null ) {
            throw new GradleException( "A distribution for '${gradleVersion}' is not available.")
        }

        initiatingProject.javaexec {
            ignoreExitValue = true
            classpath "${location.absolutePath}/lib/gradle-launcher-${gradleVersion}.jar"
            workingDir testProjectDir
            systemProperties 'org.gradle.appname' : 'gradleTest'

            main 'org.gradle.launcher.GradleMain'
            args owner.getArguments()
            args owner.getTasks()

            // Capture standardOutput and errorOutput
            // errorOutput = new ByteArrayOutputStream()
            // standardOutput = new ByteArrayOutputStream()
            // systemProperties & environment in a later release
        }

    }

    GradleInvocation call(@DelegatesTo(GradleInvocation) Closure c) {
        def newc=c.clone()
        newc.delegate=this
        newc.call()
        this
    }

    private Map<String,File> distributions

}
