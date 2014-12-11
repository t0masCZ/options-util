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

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class DateOptionTest {
    private DateOption option;

    @Before
    public void setUp() {
        option = new DateOption("key");
    }

    @Test(expected = IllegalArgumentException.class)
    public void onlyDateShouldBeAssignable() {
        option.setValue("thisshouldnotwork");
    }

    @Test
    public void testDateToStringConversion() {
        Calendar cal = Calendar.getInstance();
        cal.set(1974, Calendar.JANUARY, 31, 22, 0, 0);
        cal.set(Calendar.MILLISECOND, 111);

        assertEquals("Converting date to string", "1974-01-31 22:00:00.111", option.convertValueToString(cal.getTime()));
    }

    @Test
    public void testStringToIntegerConversion() {
        Calendar cal = Calendar.getInstance();
        cal.set(1974, Calendar.JANUARY, 31, 22, 0, 0);
        cal.set(Calendar.MILLISECOND, 111);

        assertEquals("Converting string to integer", cal.getTime(), option.convertStringToValue("1974-01-31 22:00:00.111"));
    }

    @Test(expected = ParseException.class)
    public void conversionErrorShouldThrowAnException() {
        option.convertStringToValue("aaaa");
    }

    @Test
    public void nullOrEmptyShouldConvertAsNullBothWays() {
        assertNull("Converting null to value", option.convertStringToValue(null));
        assertNull("Converting empty string to value", option.convertStringToValue(""));
        assertNull("Converting null to string", option.convertValueToString(null));
    }
}