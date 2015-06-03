import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


class GradleTestRunnerTest {

    @Test
    public void gradleOneTestAgainstOneVersion() {
        Project project = ProjectBuilder.builder().build();
        Closure cl = new Closure(project) {
            @Override
            public Object call(Object... args) {
                return super.call(args);
            }
        }
        project.javaexec()
      fail("NOT IMPLEMENTED YET");
    }
}