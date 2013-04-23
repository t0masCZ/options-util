/*
 *
 *  * Copyright 2012, 2013 by Victor Denisov (vdenisov@plukh.org).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.plukh.options.impl.persistence;

import org.plukh.options.*;
import org.plukh.options.impl.options.AbstractOption;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * {@code PropertiesPersistenceProvider} uses {@code .properties} text files on a file system as a persistent backing
 * store for application-specific options. Each application-specific interface must use a different file; otherwise,
 * this provider's behavior is unpredictable and can result in properties file getting corrupted.
 * <p/>
 * <b>Configuration:</b>
 * <p/>
 * Applications must use {@link FileConfig} class or {@link org.plukh.options.PersistenceConfig} class to configure properties persistence
 * provider - the latter can be used if application is satisfied with default path and filename of the options file.
 * Path information in the configuration class will be used as follows:
 *<ul>
 *     <li>if path is null or empty, persistence provider will set it to the user's home directory;</li>
 *     <li>if filename is null or empty, persistence provider will set it to a default file name;</li>
 *     <li>if the path is absolute, it will be used as is; if it's relative, it will be resolved
 *     relative to the user's home directory;</li>
 *     <li>if the file name doesn't have an extension, a default one will be used.</li>
 * </ul>
 * <p/>
 * NOTE: Properties file format used by this class is substantially different from the one specified by {@code java.util.Properties}:
 * <ul>
 *  <li>UTF-8 encoding is used ({@code Properties} use Latin1 encoding with Unicode escapes for non-Latin1 characters);</li>
 *  <li>"=", ":" and other special characters are not escaped (though specific option classes may choose to escape some
 *  characters in their encoding);</li>
 *  <li>only "=" can be used as key/value separator;</li>
 *  <li>lines cannot be splitted by using "\" as the last non-whitespace character on a line;</li>
 *  <li>whitespace handling is somewhat more lax; in particular, both keys and values will be trimmed before conversion.</li>
 * </ul>
 * <p/>
 * <b>Default configuration:</b>
 * <p/>
 * By default, the following configuration options are in effect for {@code PropertiesPersistenceProvider}:
 * <ul>
 *     <li>path to options file: current user's home directory (value of {@code
 *     System.getProperty("user.home")};</li>
 *     <li>options filename: fully-qualified class name of the interface to which this instance of persistence provider
 *     is attached, combined with {@code .properties} extension (for example,
 *     {@code com.mycompany.myproject.MyOptions.properties});</li>
 *     <li>backup of options file is disabled.</li>
 * </ul>
 */
public class PropertiesPersistenceProvider implements StreamPersistenceProvider {
    private Class<? extends Options> optionsClass;
    private String path;
    private String filename;
    private boolean backupOnSave;

    private File optionsFile;

    public PropertiesPersistenceProvider() {
    }

    @Override
    public void configure(PersistenceConfig configuration) throws ProviderConfigurationException {
        //Options class is always available
        optionsClass = configuration.getOptionsClass();

        //Configure file-related parameters
        configureFileConfig(configuration);

        //Set up path using defaults, if necessary
        setupOptionsFile();
    }

    private void configureFileConfig(PersistenceConfig configuration) throws ProviderConfigurationException {
        if (configuration instanceof FileConfig) {
            FileConfig fileConfig = (FileConfig) configuration;
            path = fileConfig.getPath();
            filename = fileConfig.getFilename();
            backupOnSave = fileConfig.isBackupOnSave();
        } else {
            if (!configuration.getClass().equals(PersistenceConfig.class))
                throw new ProviderConfigurationException("Use instances of PersistenceConfig or FileConfig to" +
                    "configure PropertiesPersistenceProvider");
            path = null;
            filename = null;
            backupOnSave = false;
            optionsFile = null;
        }
    }

    @Override
    public void save(Collection<AbstractOption> options, boolean nonDefaultOnly) throws OptionsException {
        //See if the target file already exists
        if (optionsFile.exists() && optionsFile.isFile()) {
            if (backupOnSave) {
                try {
                    FileUtils.copyFile(optionsFile, new File(optionsFile.getAbsolutePath() + ".bak"));
                } catch (IOException e) {
                    throw new OptionsException("Error making backup copy of options file: " +
                            optionsFile.getAbsolutePath(), e);
                }
            }
        } else {
            //See if the target directory already exists
            if (!optionsFile.getParentFile().isDirectory()) {
                //See if the file object with the correct name exists, but is not a directory
                if (optionsFile.getParentFile().exists())
                    throw new OptionsException("Target file exists, but is not a directory: " + optionsFile.getParent());
                //Create a target directory
                if (!optionsFile.getParentFile().mkdirs()) throw new OptionsException("Unable to create target directory");
            }
        }

        //Create an output stream
        OutputStream out;
        try {
            out = new FileOutputStream(optionsFile);
        } catch (FileNotFoundException e) {
            throw new OptionsException("Unable to open options file " + optionsFile.getAbsolutePath() + " for writing", e);
        }

        //Save options
        saveToStream(out, options, nonDefaultOnly);
    }

    public void saveToStream(OutputStream out, Collection<AbstractOption> options, boolean nonDefaultOnly)
            throws OptionsException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF8")));

        try {
            writer.write("# [" + optionsClass.getSimpleName() + "]" + ", modified on " + new Date());
            writer.newLine();

            for (AbstractOption option : options) {
                //Transient options should be skipped outright
                if (option.isTransient()) continue;

                final String value = option.getStringValue();

                //If non-default only flag is true, and option has a default value set, see if we should persist it
                if (nonDefaultOnly && option.isDefaultValueSet()) {
                    final String defaultValue = option.getDefaultValue();
                    if (defaultValue == null && value == null) continue;
                    if (defaultValue == null) continue;
                    if (defaultValue.equals(value)) continue;
                }

                writer.write(option.getKey() + "=" + (value == null ? "" : value));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new OptionsException("Error saving options to file: " + optionsFile.getAbsolutePath(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                //noinspection ThrowFromFinallyBlock
                throw new OptionsException("Error closing options file: " + optionsFile.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public boolean load(Collection<AbstractOption> options, boolean suppressConversionErrors) throws OptionsException {
        InputStream in;
        try {
            in = new FileInputStream(optionsFile);
            loadFromStream(in, options, suppressConversionErrors);
        } catch (FileNotFoundException e) {
            return false;
        }

        return true;
    }

    public void loadFromStream(InputStream in, Collection<AbstractOption> options, boolean suppressConversionErrors)
            throws OptionsException {
        //Read properties from file and store in a map
        final Map<String, String> properties = loadPropertiesFromStream(in);

        //Try string to value conversion, collection conversion errors
        final Collection<String> conversionErrors = tryConvertingStringsToValues(options, properties);

        //If there are conversion errors and they are not supressed, throw an exception
        if (!conversionErrors.isEmpty() && !suppressConversionErrors)
            throw new ConversionException("Error converting option values from options file: " +
                    optionsFile.getAbsolutePath() + ": " + conversionErrors, conversionErrors);

        //We know there will be no further errors; assign string values to options
        assignStringValues(options, properties);
    }

    private void assignStringValues(Collection<AbstractOption> options, Map<String, String> properties) {
        for (AbstractOption option : options) {
            //Skip transient options
            if (option.isTransient()) continue;

            final String key = option.getKey();
            if (properties.containsKey(key)) option.setStringValue(properties.get(key));
            else option.resetToDefaultValue();
        }
    }

    private Collection<String> tryConvertingStringsToValues(Collection<AbstractOption> options,
                                                            Map<String, String> properties) {

        final Collection<String> conversionErrors = new LinkedList<String>();

        for (AbstractOption option : options) {
            final String key = option.getKey();
            if (properties.containsKey(key)) {
                try {
                    option.convertStringToValue(properties.get(key));
                } catch (ParseException e) {
                    conversionErrors.add(key);
                    properties.remove(key);
                }
            }
        }

        return conversionErrors;
    }

    //TODO: write tests for UTF8

    private Map<String, String> loadPropertiesFromStream(InputStream in) throws OptionsException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF8")));

        final Map<String, String> properties = new HashMap<String, String>();

        try {
            String s;
            while ((s = reader.readLine()) != null) {
                s = s.trim();

                //Ignore empty strings and comments
                if (s.isEmpty() || s.startsWith("#")) continue;

                final int p = s.indexOf('=');
                if (p < 0) continue; //no equal sign - no need to parse further

                final String key = s.substring(0, p).trim();
                final String value = s.substring(p + 1, s.length()).trim();

                //Store the key-value pair
                properties.put(key, value);
            }
        } catch (IOException e) {
            throw new OptionsException("Error reading options file: " + optionsFile.getAbsolutePath(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //noinspection ThrowFromFinallyBlock
                throw new OptionsException("Error closing options file reader: " + optionsFile.getAbsolutePath(), e);
            }
        }
        return properties;
    }

    private void setupOptionsFile() {
        if (path == null) path = "";
        if (filename == null) filename = "";

        //Make sure that filename is *really* filename
        filename = new File(filename).getName();

        //If path is empty, use default directory
        if (path.isEmpty()) {
            path = System.getProperty("user.home");
        }

        //If filename is empty, use default filename
        if (filename.isEmpty()) filename = optionsClass.getName() + ".properties";

        //If filename lacks an extension, use default extension
        if (filename.lastIndexOf(".") == -1) filename += ".properties";

        //If the path provided is not absolute, prepend it with user's home directory (which is absolute)
        if (!new File(path).isAbsolute()) path = new File(System.getProperty("user.home"), path).getAbsolutePath();

        optionsFile = new File(path, filename);
    }

    public Class<? extends Options> getOptionsClass() {
        return optionsClass;
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

    public File getOptionsFile() {
        return optionsFile;
    }

    public boolean isBackupOnSave() {
        return backupOnSave;
    }
}
