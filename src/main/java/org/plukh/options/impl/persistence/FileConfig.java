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

import org.plukh.options.PersistenceConfig;

/**
 * Configuration class for {@link PropertiesPersistenceProvider}. Allows to specify path to and filename of the options
 * file and whether the provider should back up existing file before overwriting it with a new one.
 */
public class FileConfig extends PersistenceConfig {
    private String path;
    private String filename;
    private boolean backupOnSave;

    public FileConfig(String path, String filename) {
        super(null);
        this.path = path;
        this.filename = filename;
    }

    public FileConfig(String path, String filename, boolean backupOnSave) {
        super(null);
        this.path = path;
        this.filename = filename;
        this.backupOnSave = backupOnSave;
    }

    /**
     * Returns path to options file.
     * @return path to options file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set path to options file.
     * @param path path to options file.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns filename of the options file.
     * @return filename of the options file.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets filename of the options file.
     * @param filename filename of the options file.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns {@code true} if options file should be backed up before being overwritten.
     * @return {@code true} if options file should be backed up before being overwritten.
     */
    public boolean isBackupOnSave() {
        return backupOnSave;
    }

    /**
     * Set to {@code true} to ask persistence provider to backup options file before saving.
     * @param backupOnSave {@code true} if options file should be backed up before being overwritten.
     */
    public void setBackupOnSave(boolean backupOnSave) {
        this.backupOnSave = backupOnSave;
    }
}
