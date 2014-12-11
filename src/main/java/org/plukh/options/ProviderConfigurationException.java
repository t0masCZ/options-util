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

package org.plukh.options;

/**
 * This exception is thrown by the {@link Options#configurePersistenceProvider(PersistenceConfig)} method if the information
 * passed to the provider is incomplete (missing required options), incorrect or otherwise can not be applied (if,
 * for example, configuration object is of the wrong class).
 */
public class ProviderConfigurationException extends Exception {
    public ProviderConfigurationException(String message) {
        super(message);
    }

    public ProviderConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
