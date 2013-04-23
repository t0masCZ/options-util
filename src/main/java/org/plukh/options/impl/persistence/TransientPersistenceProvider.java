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

import org.plukh.options.OptionsException;
import org.plukh.options.PersistenceConfig;
import org.plukh.options.PersistenceProvider;
import org.plukh.options.ProviderConfigurationException;
import org.plukh.options.impl.options.AbstractOption;

import java.util.Collection;

/**
 * {@code TransientPersistenceProvider} ignores save, load and configure calls. This is the default provider for
 * application-specifc options interfaces without {@link org.plukh.options.Persistence @Persistence} annotation.
 */
public class TransientPersistenceProvider implements PersistenceProvider {
    @Override
    public void configure(PersistenceConfig configuration) throws ProviderConfigurationException {
    }

    @Override
    public void save(Collection<AbstractOption> options, boolean nonDefaultOnly) throws OptionsException {
    }

    @Override
    public boolean load(Collection<AbstractOption> options, boolean suppressConversionErrors) throws OptionsException {
        return true;
    }
}
