package org.switchyard.test.quickstarts;

import static org.ops4j.pax.exam.CoreOptions.maven;
//import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
//import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFileExtend;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import java.io.File;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.junit.PaxExamServer;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public abstract class AbstractQuickstartTest {
    @Inject
    private BundleContext bundleContext;
    
    @Rule
    public PaxExamServer exam = new PaxExamServer();
    
    @Configuration
    public Option[] config() {
        return new Option[] {
            karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").versionAsInProject())
            .karafVersion(MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf")).name("Apache Karaf").unpackDirectory(new File("target/karaf")).useDeployFolder(false),
            keepRuntimeFolder(),
            logLevel(LogLevel.DEBUG),
            editConfigurationFileExtend("etc/config.properties", "org.osgi.framework.system.packages.extra", "sun.misc"),
            editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.jboss.org/nexus/content/repositories/snapshots@snapshots@noreleases@id=jboss-snapshot"),
            editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.jboss.org/nexus/content/repositories/fs-releases@id=fusesource.release"),
            features("mvn:org.switchyard.karaf/switchyard/2.0.0-SNAPSHOT/xml/features", getFeatureName()),
        };
    }

    @Test
    public void testDeployment() throws Exception {
        Assert.assertNotNull(bundleContext);
        Bundle bundle = null;
        for (Bundle aux : bundleContext.getBundles()) {
            if (getBundleName().equals(aux.getSymbolicName())) {
                bundle = aux;
                break;
            }
        }
        Assert.assertNotNull(bundle);
        Assert.assertEquals("Bundle ACTIVE", Bundle.ACTIVE, bundle.getState());
    }

    public abstract String getBundleName();
    public abstract String getFeatureName();
}
