/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.test.quickstarts;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class DeploymentProbe {
    public static final String BUNDLE_NAME_KEY = "org.switchyard.karaf.test.bundleName";
    @Inject
    private BundleContext bundleContext;
    
    @Test
    public void testDeployment() {
        Assert.assertNotNull(bundleContext);
        String bundleName = System.getProperty(BUNDLE_NAME_KEY);
        Bundle bundle = null;
        for (Bundle aux : bundleContext.getBundles()) {
            if (bundleName.equals(aux.getSymbolicName())) {
                bundle = aux;
                break;
            }
        }
        Assert.assertNotNull(bundle);
        Assert.assertEquals("Bundle ACTIVE", Bundle.ACTIVE, bundle.getState());
    }
}