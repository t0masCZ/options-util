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
import org.junit.BeforeClass;
import org.junit.Test;
import org.plukh.options.OptionsFactory;
import org.plukh.options.ParseException;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.*;

public class AbstractOptionTest {
    private AbstractOption integerOption;
    private AbstractOption booleanOption;
    private AbstractOption stringOption;

    @BeforeClass
    public static void setUpClass() {
        //Make sure that OptionsFactory class is loaded, so that its static initializers will have a chance to run
        OptionsFactory.init();
    }

    @Before
    public void setUp() throws Exception {
        integerOption = AbstractOption.getOptionForClass(Integer.class);
        booleanOption = AbstractOption.getOptionForClass(Boolean.class);
        stringOption = AbstractOption.getOptionForClass(String.class);
    }

    @Test
    public void assertEachOptionIsOfAppropriateClass() {
        assertTrue("Option for class Integer should be instantiated as IntegerOption", integerOption instanceof IntegerOption);
        assertTrue("Option for class Boolean should be instantiated as BooleanOption", booleanOption instanceof BooleanOption);
        assertTrue("Option for class String should be instantiated as StringOption", stringOption instanceof StringOption);
    }

    @Test
    public void stringAssignmentShouldReturnAppropriateValue() {
        integerOption.setStringValue("1");
        assertEquals("Assigning String value to IntegerOption should return correct integer value", 1, integerOption.getValue());
        stringOption.setStringValue("abc");
        assertEquals("Assigning String value to StringOption should return correct String value", "abc", stringOption.getValue());
        booleanOption.setStringValue("true");
        assertEquals("Assigning String value to BooleanOption should return correct Boolean value", true, booleanOption.getValue());
    }

    @Test
    public void valueAssignmentShouldReturnAppropriateString() {
        integerOption.setValue(1);
        assertEquals("Assigning integer value to IntegerOption should return correct String representation", "1",
                integerOption.getStringValue());
        booleanOption.setValue(true);
        assertEquals("Assigning boolean value to BooleanOption should return correct String representation", "true",
                booleanOption.getStringValue());
        stringOption.setValue("abc");
        assertEquals("Assigning string value to StringOption should return currect String representation", "abc",
                stringOption.getStringValue());
    }

    @Test
    public void testTryStringToValueConversion() {
        integerOption.setStringValue("1");
        assertTrue(integerOption.tryStringToValueConversion());
        integerOption.setStringValue("aaa");
        assertFalse(integerOption.tryStringToValueConversion());
    }

    @Test
    public void stringToValueConversionsShouldBeCached() {
        final List<String> conversionInvocations = new LinkedList<String>();

        AbstractOption option = new IntegerOption() {
            public Object convertStringToValue(String s) throws ParseException {
                conversionInvocations.add("convertStringToValue");
                return super.convertStringToValue(s);
            }
        };

        option.setStringValue("1");
        option.getValue();
        option.getValue();

        assertEquals("Value must be cached after string to value conversion", 1, conversionInvocations.size());
    }

    @Test
    public void valueToStringConversionsShouldBeCached() {
        final List<String> conversionInvocations = new LinkedList<String>();

        AbstractOption option = new IntegerOption() {
            public String convertValueToString(Object o) {
                conversionInvocations.add("convertValueToString");
                return super.convertValueToString(o);
            }
        };

        option.setValue(1);
        option.getStringValue();
        option.getStringValue();

        assertEquals("String value must be cached after value to string conversion", 1, conversionInvocations.size());
    }

    @Test
    public void settingDefaultValueShouldUpdateActualValueIfNoValueBeenSetYet() {
        integerOption.setDefaultValue("10");
        assertEquals("Default value should update actual value if no values been set yet", 10, integerOption.getValue());

        stringOption.setValue("abc");
        stringOption.setDefaultValue("10");
        assertEquals("Default value should not change actual value if one been set", "abc", stringOption.getValue());
    }

    @Test
    public void testResetToDefaultValue() {
        integerOption.setDefaultValue("1");
        integerOption.setValue(5);
        assertEquals(5, integerOption.getValue());
        integerOption.resetToDefaultValue();
        assertEquals(1, integerOption.getValue());
    }

}
