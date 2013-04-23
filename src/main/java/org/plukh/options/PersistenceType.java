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

package org.plukh.options;

/**
 * Enumeration used by the {@link Persistence @Persistence} annotation to indicate type of persistence implementation to employ.
 */
public enum PersistenceType {
    /**
     * Use transient persistence provider. Transient provider ignores load/save requests.
     */
    TRANSIENT,

    /**
     * Use properties persistence provider. Options will be saved to and loaded from a {@code .properties} file on the
     * local filesystem.
     */
    PROPERTIES_FILE,

    /**
     * Use XML persistence provider. Options will be saved to and loaded from an XML file on the local filesystem.
     */
    XML_FILE, //TODO: Implement

    /**
     * Use custom persistence provider. Application must provide an implementation of {@link PersistenceProvider}
     * interface to save and load options.
     */
    CUSTOM
}
