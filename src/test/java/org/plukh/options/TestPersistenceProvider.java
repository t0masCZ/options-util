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

import org.plukh.options.impl.options.AbstractOption;

import java.util.Collection;

public class TestPersistenceProvider implements PersistenceProvider {
    @Override
    public void init(Class<? extends Options> classForPersisting) {
    }

    @Override
    public void configure(PersistenceConfig configuration) {
        //Do nothing
    }

    @Override
    public void save(Collection<AbstractOption> options, boolean nonDefaultOnly) throws OptionsException {
        //Do nothing
    }

    @Override
    public boolean load(Collection<AbstractOption> options, boolean suppressConversionErrors) throws OptionsException {
        //Do nothing
        return true;
    }
}
