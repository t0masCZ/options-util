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
import org.plukh.options.ParseException;

import static junit.framework.Assert.assertEquals;

public class NumberOptionTest {
    private NumberOption option;

    @Before
    public void setUp() {
        option = new NumberOption(int.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onlyIntegerShouldBeAssignable() {
        option.setValue("thisShouldNotWork");
    }

    @Test
    public void testIntegerToStringConversion() {
        assertEquals("Converting integer to string", "1", option.convertValueToString(1));
    }

    @Test
    public void testStringToIntegerConversion() {
        assertEquals("Converting string to integer", 1, option.convertStringToValue("1"));
    }

    @Test(expected = ParseException.class)
    public void conversionErrorShouldThrowAnException() {
        option.convertStringToValue("thisShouldNotWork");
    }
}
