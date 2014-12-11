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

import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestUtilsTest {
    private static class ArrayTest {
        private String[][] array;
        private boolean ignoreCommentsEmptyLines;

        private ArrayTest(String[][] array, boolean ignoreCommentsEmptyLines) {
            this.array = array;
            this.ignoreCommentsEmptyLines = ignoreCommentsEmptyLines;
        }

        public String[][] getArray() {
            return array;
        }

        public boolean isIgnoreCommentsEmptyLines() {
            return ignoreCommentsEmptyLines;
        }

        @Override
        public String toString() {
            return "ArrayTest{" +
                    "array=" + (array == null ? null : "{" + Arrays.asList(array[0]) + ", " + Arrays.asList(array[1]) + "}") +
                    ", ignoreCommentsEmptyLines=" + ignoreCommentsEmptyLines +
                    '}';
        }
    }

    private static final String[][] EQUAL_ARRAY = {{"abc", "def"}, {"abc", "def"}};
    private static final String[][] EQUAL_ARRAY_COMMENT = {{"abc", "#def", "ghi"}, {"abc", "ghi"}};
    private static final String[][] EQUAL_ARRAY_COMMENT2 = {{"abc", "ghi"}, {"abc", "#def", "ghi"}};
    private static final String[][] EQUAL_ARRAY_EMPTY_LINE = {{"abc", "", "def"}, {"abc", "def"}};
    private static final String[][] EQUAL_ARRAY_EMPTY_LINE2 = {{"abc", "def"}, {"abc", "", "def"}};
    private static final String[][] EQUAL_ARRAY_LAST_LINE = {{"abc", "def", ""}, {"abc", "def"}};
    private static final String[][] EQUAL_ARRAY_LAST_LINE2 = {{"abc", "def"}, {"abc", "def", ""}};
    private static final String[][] EQUAL_ARRAY_EMPTY_LINES_ONLY = {{"", "", "#abc"}, {"#abc", ""}};
    private static final String[][] EQUAL_ARRAY_ZERO_SIZED = {{}, {"", ""}};

    private static final String[][] DIF_ARRAY = {{"abc", "def"}, {"abc", "ghi"}};
    private static final String[][] DIF_ARRAY_DIF_LENGTH = {{"abc"}, {"abc", "def"}};
    private static final String[][] DIF_ARRAY_EMPTY_LINE = {{"abc", "", "def", "ghi"}, {"abc", "def"}};
    private static final String[][] DIF_ARRAY_ZERO_SIZED = {{"", "", "abc"}, {}};

    private static final ArrayTest[] EQUAL_ARRAYS = {
            new ArrayTest(EQUAL_ARRAY, false),
            new ArrayTest(EQUAL_ARRAY_COMMENT, true),
            new ArrayTest(EQUAL_ARRAY_COMMENT2, true),
            new ArrayTest(EQUAL_ARRAY_EMPTY_LINE, true),
            new ArrayTest(EQUAL_ARRAY_EMPTY_LINE2, true),
            new ArrayTest(EQUAL_ARRAY_EMPTY_LINES_ONLY, true),
            new ArrayTest(EQUAL_ARRAY_LAST_LINE, true),
            new ArrayTest(EQUAL_ARRAY_LAST_LINE2, true),
            new ArrayTest(EQUAL_ARRAY_ZERO_SIZED, true)
    };

    private static final ArrayTest[] DIF_ARRAYS = {
            new ArrayTest(DIF_ARRAY, false),
            new ArrayTest(DIF_ARRAY_DIF_LENGTH, false),
            new ArrayTest(DIF_ARRAY_EMPTY_LINE, true),
            new ArrayTest(DIF_ARRAY_ZERO_SIZED, true)
    };

    private ArrayTest test;
    private boolean shouldFail;

    @Parameterized.Parameters
    public static Collection<Object[]> generateParameters() {
        List<Object[]> parameters = new LinkedList<Object[]>();

        for (ArrayTest test : EQUAL_ARRAYS) {
            parameters.add(new Object[] {test, false});
        }

        for (ArrayTest test : DIF_ARRAYS) {
            parameters.add(new Object[] {test, true});
        }

        return parameters;
    }

    public TestUtilsTest(ArrayTest test, boolean shouldFail) {
        this.test = test;
        this.shouldFail = shouldFail;
    }

    @Test
    public void testArrays() {
        try {
            TestUtils.assertArrayMatches(test.getArray()[0], test.getArray()[1], test.isIgnoreCommentsEmptyLines());
        } catch (AssertionFailedError e) {
            if (!shouldFail) fail("Expected test to pass, but it failed, test: " + test);
            return;
        }

        if (shouldFail) fail("Expected test to fail, but it passed, test: " + test);
    }
}
