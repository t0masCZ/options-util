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

package org.plukh.options.impl.options;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StringOptionTest {
    private StringOption option;

    @Before
    public void setUp() {
        option = new StringOption("key");
    }

    //We can only assign objects of class String to StringOption
    @Test(expected = IllegalArgumentException.class)
    public void onlyStringIsAssignable() {
        option.setValue(true);
    }

    //Conversion should not alter string
    @Test
    public void conversionShouldNotAlterString() {
        assertEquals("Converting string to value", "test", option.convertStringToValue("test"));
        assertEquals("Converting value to string", "test", option.convertValueToString("test"));
    }

}
