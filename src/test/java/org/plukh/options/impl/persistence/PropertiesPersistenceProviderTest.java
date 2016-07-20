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

import org.junit.Before;
import org.junit.Test;
import org.plukh.options.*;
import org.plukh.options.impl.persistence.interfaces.DefaultValueTestOptions;
import org.plukh.options.impl.persistence.interfaces.ReadOnlyTestOptions;
import org.plukh.options.impl.persistence.interfaces.TransientOptionTestOptions;
import org.plukh.options.interfaces.TestOptions;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.*;

public class PropertiesPersistenceProviderTest {
    private PropertiesPersistenceProvider provider;
    private String homeDir;

    @Before
    public void setUp() {
        TestUtils.resetOptionsFactory();
        provider = new PropertiesPersistenceProvider();
        homeDir = System.getProperty("user.home");
    }

    @Test(expected = ProviderConfigurationException.class)
    public void wrongConfigClassShouldThrowException() throws ProviderConfigurationException {
        FakeConfig pc = new FakeConfig();
        provider.configure(pc);
    }

    @Test
    public void testDefaultInitialization() throws ProviderConfigurationException {
        provider.init(TestOptions.class);

        //Assert class is configured properly
        assertEquals(TestOptions.class, provider.getOptionsClass());

        //Assert proper default file name
        assertEquals(TestOptions.class.getName() + ".properties", provider.getFilename());

        //Assert proper default path
        assertEquals(homeDir, provider.getPath());

        //Backups should be disabled
        assertFalse(provider.isBackupOnSave());
    }

    @Test
    public void absolutePathWithFilenameAndExtensionShouldBeUsedAsIs() throws ProviderConfigurationException {
        final String path = File.listRoots()[0].getAbsolutePath();
        final String filename = "test.properties";
        final String expectedPath = new File(path, filename).getAbsolutePath();

        testPathConfig(path, filename, expectedPath);
    }

    @Test
    public void relativePathWithFilenameAndExtensionShouldBeResolvedAgainstUserHomeDir() throws ProviderConfigurationException {
        final String path = "test";
        final String filename = "test.properties";
        final String expectedPath = new File(homeDir + File.separator + path, filename).getAbsolutePath();

        testPathConfig(path, filename, expectedPath);
    }

    @Test
    public void pathWithoutFilenameShouldUseDefaultFilename() throws ProviderConfigurationException {
        final String path = "test";
        final String expectedPath = new File(homeDir + File.separator + path, TestOptions.class.getName() + ".properties").getAbsolutePath();

        testPathConfig(path, null, expectedPath);
    }

    @Test
    public void filenameWithoutPathShouldBeResolvedAgainstUserHomeDir() throws ProviderConfigurationException {
        final String filename = "test.properties";
        final String expectedPath = new File(homeDir, filename).getAbsolutePath();

        testPathConfig(null, filename, expectedPath);
    }

    @Test
    public void ifExtensionIsMissingDefaultShouldBeAdded() throws ProviderConfigurationException {
        final String filename = "test";
        final String expectedPath = new File(homeDir, filename + ".properties").getAbsolutePath();

        testPathConfig(null, filename, expectedPath);
    }

    @Test
    public void testOptionLoad() throws OptionsException, IOException {
        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);

        InputStream in = getClass().getResourceAsStream("properties/DefaultValueTestOptions.properties");

        options.loadFromStream(in, true);

        assertEquals(5, options.getValue());
    }

    @Test
    public void optionWithDefaultValueShouldNotBeSavedWhenSaveNonDefaultOnlyIsTrue() throws OptionsException,
            IOException {

        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);
        assertEquals(1, options.getValue());

        saveOptionsAndCompareResult(options, true, "properties/EmptyProperties.properties");
    }

    @Test
    public void optionWithNonDefaultValueShouldBeSavedRegardless() throws OptionsException, IOException {
        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);
        assertEquals(1, options.getValue());
        options.setValue(5);

        saveOptionsAndCompareResult(options, true, "properties/DefaultValueTestOptions.properties");
        saveOptionsAndCompareResult(options, false, "properties/DefaultValueTestOptions.properties");
    }

    @Test
    public void optionWithDefaultValueShouldBeSavedWhenSaveNonDefaultOnlyIsFalse() throws OptionsException, IOException {
        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);
        assertEquals(1, options.getValue());

        saveOptionsAndCompareResult(options, false, "properties/DefaultValueSavedTestOptions.properties");
    }

    @Test
    public void transientOptionShouldNotBeSaved() throws OptionsException, IOException {
        TransientOptionTestOptions options = OptionsFactory.getOptionsInstance(TransientOptionTestOptions.class);
        options.setTransientOption(10);

        saveOptionsAndCompareResult(options, false, "properties/EmptyProperties.properties");
    }

    @Test
    public void transientOptionShouldNotBeLoaded() throws OptionsException {
        TransientOptionTestOptions options = OptionsFactory.getOptionsInstance(TransientOptionTestOptions.class);
        options.setTransientOption(10);
        loadOptionsFromProperties(options, false, "properties/TransientOptionTestOptions.properties");
        assertEquals(10, options.getTransientOption());
    }

    @Test
    public void readOnlyOptionShouldBeSavedLikeNormalOption() throws IOException, OptionsException {
        ReadOnlyTestOptions options = OptionsFactory.getOptionsInstance(ReadOnlyTestOptions.class);
        saveOptionsAndCompareResult(options, false, "properties/ReadOnlySavedTestOptions.properties");
        saveOptionsAndCompareResult(options, true, "properties/EmptyProperties.properties");
    }

    @Test
    public void readOnlyOptionShouldBeLoadedLikeNormalOption() throws OptionsException {
        ReadOnlyTestOptions options = OptionsFactory.getOptionsInstance(ReadOnlyTestOptions.class);
        loadOptionsFromProperties(options, false, "properties/ReadOnlyTestOptions.properties");
        assertEquals(5, options.getReadOnlyOption());
    }

    @Test
    public void conversionErrorShouldThrowAnExceptionIfNotSuppressed() throws OptionsException {
        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);
        try {
            loadOptionsFromProperties(options, false, "properties/DefaultValueConversionErrorTestOptions.properties");
            fail("Expected conversion exception not thrown");
        } catch (ConversionException e) {
            //Assert that value is not changed and that property name is present
            assertEquals(1, options.getValue());
            assertTrue("Exception message should contain property name", e.getMessage().contains("default.value"));
        }
    }

    @Test
    public void conversionErrorShouldNotThrowAnExceptionIfSuppressed() throws OptionsException {
        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);
        options.setValue(10);
        try {
            loadOptionsFromProperties(options, true, "properties/DefaultValueConversionErrorTestOptions.properties");
            //Check that value is reset to default
            assertEquals(1, options.getValue());
        } catch (ConversionException e) {
            fail("Conversion exception should not be thrown when suppressed");
        }
    }

    @Test
    public void optionsMissingFromTheStoreShouldBeResetToDefaultOnLoad() throws OptionsException {
        DefaultValueTestOptions options = OptionsFactory.getOptionsInstance(DefaultValueTestOptions.class);
        options.setValue(10);
        loadOptionsFromProperties(options, false, "properties/EmptyProperties.properties");
        assertEquals(1, options.getValue());
    }

    private void testPathConfig(String path, String filename, String expectedPath) throws ProviderConfigurationException {
        FileConfig fc = new FileConfig(path, filename);
        provider.init(TestOptions.class);
        provider.configure(fc);
        assertEquals(expectedPath, provider.getOptionsFile().getAbsolutePath());
    }

    private void saveOptionsAndCompareResult(PersistenceOptions options, boolean nonDefaultOnly, String expectedPropsFileName)
            throws OptionsException, IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in;

        options.saveToStream(out, nonDefaultOnly);
        in = new ByteArrayInputStream(out.toByteArray());

        Properties actualProps = new Properties();
        actualProps.load(in);
        Properties expectedProps = new Properties();
        expectedProps.load(getClass().getResourceAsStream(expectedPropsFileName));

        TestUtils.assertMapsMatch(expectedProps, actualProps);
    }

    private void loadOptionsFromProperties(PersistenceOptions options, boolean suppressConversionErrors, String expectedPropsFileName)
            throws OptionsException {

        InputStream in = getClass().getResourceAsStream(expectedPropsFileName);
        options.loadFromStream(in, suppressConversionErrors);
    }

}
