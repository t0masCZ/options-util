/*
 * Copyright 2012-2014 by Victor Denisov (vdenisov@plukh.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.plukh.options.impl.persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.plukh.options.*;
import org.plukh.options.impl.OptionsProxyHandler;
import org.plukh.options.interfaces.TestOptions;

import java.io.*;
import java.lang.reflect.Proxy;
import java.util.Properties;

import static org.junit.Assert.*;

public class PropertiesPersistenceProviderTestSaveLoad {
    private static File tempDir;
    private PropertiesPersistenceProvider provider;

    @BeforeClass
    public static void setUpClass() throws IOException {
        tempDir = TestUtils.createTempDir("ppptest");
    }

    @Before
    public void setUp() {
        provider = new PropertiesPersistenceProvider();
        TestUtils.cleanDir(tempDir);
    }

    @AfterClass
    public static void tearDownClass() {
        TestUtils.cleanDir(tempDir);
        //noinspection ResultOfMethodCallIgnored
        tempDir.delete();
    }

    @Test
    public void simpleSaveToExistingDirectory() throws IOException, OptionsException, ProviderConfigurationException {
        Options options = setupOptions(TestOptions.class);
        options.configurePersistenceProvider(new FileConfig(tempDir.getAbsolutePath(), "test.properties"));
        options.save(false);

        File optionsFile = new File(tempDir, "test.properties");

        assertTrue(optionsFile.exists());
        assertTrue(optionsFile.length() > 0);
        assertFalse(new File(optionsFile.getAbsolutePath() + ".bak").exists());
    }

    @Test
    public void saveWithBackup() throws ProviderConfigurationException, OptionsException {
        Options options = setupOptions(TestOptions.class);
        options.configurePersistenceProvider(new FileConfig(tempDir.getAbsolutePath(), "test.properties", true));

        options.save(false);
        options.save(false);

        File optionsFile = new File(tempDir, "test.properties");
        File backupFile = new File(optionsFile.getAbsolutePath() + ".bak");

        assertTrue(optionsFile.exists());
        assertTrue(optionsFile.length() > 0);
        assertTrue(backupFile.exists());
        assertTrue(backupFile.length() == optionsFile.length());
    }

    @Test
    public void saveWithMultipleDirectories() throws OptionsException, ProviderConfigurationException {
        Options options = setupOptions(TestOptions.class);
        File optionsFile = new File(tempDir.getAbsolutePath() + File.separator + "testdir", "test.properties");

        options.configurePersistenceProvider(new FileConfig(optionsFile.getParent(), "test.properties"));
        options.save(false);

        assertTrue(optionsFile.exists());
        assertTrue(optionsFile.length() > 0);
    }

    @Test
    public void testSimpleLoad() throws OptionsException, IOException, ProviderConfigurationException {
        TestOptions options = (TestOptions) setupOptions(TestOptions.class);
        options.configurePersistenceProvider(new FileConfig(tempDir.getAbsolutePath(), "test.properties"));

        createOptionsFile();

        final boolean result = options.load(false);

        assertTrue(result);
        assertEquals(1, options.getInt());
        assertEquals(2, options.getInt2());
        assertEquals(true, options.getBoolean());
        assertEquals("1", options.getString());
        assertEquals(3, options.getUnnamedOption());
    }

    @Test
    public void loadingNonExistentFileShouldNotResetOptionValuesToDefault() throws OptionsException, ProviderConfigurationException {
        TestOptions options = (TestOptions) setupOptions(TestOptions.class);
        options.configurePersistenceProvider(new FileConfig("aaabbb", "aaabbbb"));

        options.setInt(100);
        options.setString("abcdefg");

        final boolean result = options.load(false);

        assertFalse(result);
        assertEquals(100, options.getInt());
        assertEquals("abcdefg", options.getString());
    }

    private void createOptionsFile() throws IOException {
        Properties props = new Properties();

        props.setProperty("int", "1");
        props.setProperty("int2", "2");
        props.setProperty("boolean", "true");
        props.setProperty("option.string", "1");
        props.setProperty("unnamedOption", "3");

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(new File(tempDir, "test.properties")));
            props.store(out, null);
        } finally {
            if (out != null) out.close();
        }
    }

    private Options setupOptions(Class<? extends TestOptions> optionsClass) throws OptionsException {
        Options options = OptionsFactory.getOptionsInstance(optionsClass);
        OptionsProxyHandler handler = (OptionsProxyHandler) Proxy.getInvocationHandler(options);
        handler.setPersistenceProvider(provider);

        return options;
    }
}
