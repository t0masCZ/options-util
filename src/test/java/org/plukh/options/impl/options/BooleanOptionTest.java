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

import static junit.framework.Assert.*;

public class BooleanOptionTest {

    private BooleanOption option;

    @Before
    public void setUp() {
        option = new BooleanOption("key");
    }

    @Test(expected = IllegalArgumentException.class)
    public void onlyBooleanIsAssignable() {
        option.setValue("thisshouldfail");
    }

    @Test
    public void testStringToBooleanConversion() {
        assertTrue("Converting string literal 'true'", (Boolean) option.convertStringToValue("true"));
        assertTrue("Converting string literal 'yes'", (Boolean) option.convertStringToValue("yes"));
        assertTrue("Converting string literal '1'", (Boolean) option.convertStringToValue("1"));

        assertFalse("Converting string literal 'false'", (Boolean) option.convertStringToValue("false"));
        assertFalse("Converting string literal 'no'", (Boolean) option.convertStringToValue("no"));
        assertFalse("Converting string literal '0'", (Boolean) option.convertStringToValue("0"));
    }

    @Test
    public void testBooleanToStringConversion() {
        assertEquals("Converting 'true' to string", "true", option.convertValueToString(true));
        assertEquals("Converting 'false' to string", "false", option.convertValueToString(false));
    }

    @Test(expected = ParseException.class)
    public void conversionErrorShouldThrowAnException() {
        option.convertStringToValue("thisfails");
    }
}

