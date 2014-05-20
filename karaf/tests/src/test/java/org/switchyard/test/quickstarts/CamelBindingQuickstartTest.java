package org.switchyard.test.quickstarts;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class CamelBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-binding";
    private static String featureName = "switchyard-quickstart-camel-binding";

    private static String SOURCE_FILE = "../../test-classes/test.txt";
    private static String DEST_FILE = "target/input/test.txt";

    @Test
    public void testFeatures() throws Exception {
        File srcFile = new File(SOURCE_FILE);
        File destFile = new File(DEST_FILE);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        srcFile.renameTo(destFile);

        assertTrue(destFile.exists());
        // Wait a spell so that the file component polls and picks up the file

        // File should have been picked up
        while (destFile.exists()) {
            Thread.sleep(50);
        }
        assertFalse(destFile.exists());
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getFeatureName() {
        return featureName;
    }
}
