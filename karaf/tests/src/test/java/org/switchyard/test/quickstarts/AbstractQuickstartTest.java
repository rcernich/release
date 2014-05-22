package org.switchyard.test.quickstarts;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;

import org.junit.AfterClass;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.TestProbeProvider;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.PaxExamRuntime;

public abstract class AbstractQuickstartTest {

    protected static TestContainer testContainer;

    @AfterClass
    public static void afterClass() throws Exception {
        if (testContainer != null) {
            final TestContainer tc = testContainer;
            testContainer = null;
            tc.stop();
        }
    }
    protected static void startTestContainer(String featureName, String bundleName) throws Exception {
        final ExamSystem system = PaxExamRuntime.createTestSystem(config(featureName, bundleName));
        final TestProbeBuilder probeBuilder = system.createProbe();
        probeBuilder.addTest(DeploymentProbe.class, "testDeployment");
        testContainer = PaxExamRuntime.createContainer(system);
        try {
            final TestProbeProvider probeProvider = probeBuilder.build();
            testContainer.start();
            testContainer.installProbe(probeProvider.getStream());
            Thread.sleep(5000);
            for (TestAddress test : probeProvider.getTests()) {
                testContainer.call(test);
            }
        } catch (Exception e) {
            final TestContainer tc = testContainer;
            testContainer = null;
            tc.stop();
            throw e;
        }
    }

    private static Option[] config(String featureName, String bundleName) {
        return new Option[] {
            karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").versionAsInProject())
            .karafVersion(MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf")).name("Apache Karaf").unpackDirectory(new File("target/karaf")).useDeployFolder(false),
            keepRuntimeFolder(),
            logLevel(LogLevel.INFO),
            configureConsole().ignoreLocalConsole(),
            editConfigurationFileExtend("etc/config.properties", "org.osgi.framework.system.packages.extra", "sun.misc"),
            editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.jboss.org/nexus/content/repositories/snapshots@snapshots@noreleases@id=jboss-snapshot"),
            editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.jboss.org/nexus/content/repositories/fs-releases@id=fusesource.release"),
            editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.switchyard", "DEBUG"),
            editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.ops4j.pax.exam", "DEBUG"),
            features("mvn:org.switchyard.karaf/switchyard/2.0.0-SNAPSHOT/xml/features", featureName),
            systemProperty(DeploymentProbe.BUNDLE_NAME_KEY).value(bundleName)
        };
    }

    public abstract String getBundleName();
    public abstract String getFeatureName();
}
