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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class TestUtils {
    public static File createTempDir(String prefix) throws IOException {
        File tempFile = File.createTempFile(prefix, null);
        //noinspection ResultOfMethodCallIgnored
        tempFile.delete();

        File tempDir = new File(tempFile.getAbsolutePath() + ".d");
        //noinspection ResultOfMethodCallIgnored
        tempDir.mkdir();

        return tempDir;
    }

    public static void cleanDir(File directory) {
        if (directory == null) return;

        final File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                //Recursively clean
                cleanDir(file);
            }
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    public static void assertArrayMatches(String[] expectedArray, String[] array) {
        assertArrayMatches(expectedArray, array, true);
    }

    public static void assertArrayMatches(String[] expectedArray, String[] array, boolean ignoreCommentsEmptyLines) {
        if (expectedArray == null && array != null) fail("Expected array is null, and actual array is not null");
        if (expectedArray != null && array == null) fail("Expected array is not null, and actual array is null");

        //If we reached this line, if expected array is null, actual array is definitely null too
        if (expectedArray == null) return;

        if (expectedArray.length != array.length && !ignoreCommentsEmptyLines) fail("Expected and actual arrays have different lengths");

        //Now that we're done with trivial cases, compare arrays line by line
        for (int i = 0, j = 0; (i < expectedArray.length && j <= array.length) ||
                               (i <= expectedArray.length && j < array.length); ) {
            if (i < expectedArray.length) {
                //Expected array has comparable lines
                final String expectedLine = expectedArray[i];

                if (j < array.length) { //Both arrays have lines to compare
                    final String line = array[j];

                    if (ignoreCommentsEmptyLines) {
                        //See if we should ignore a line
                        if (skippableLine(expectedLine)) {
                            ++i;
                            continue;
                        }
                        if (skippableLine(line)) {
                            ++j;
                            continue;
                        }
                    }

                    //If we're here, than both lines are comparable (either because of the ignoreCommentsEmptyLines
                    // being false or because both are non-skippable)
                    if (expectedLine == null) {
                        assertEquals(getFailMessage(i, j), null, line);
                        ++i; ++j;
                    } else {
                        assertEquals(getFailMessage(i, j), expectedLine, line);
                        ++i; ++j;
                    }
                } else {
                    //Array is out of comparable lines, expected array can only contain empy lines and comments
                    if (ignoreCommentsEmptyLines && skippableLine(expectedLine)) {
                        ++i;
                    } else {
                        fail(getFailMessage(i, j));
                    }
                }
            } else {
                //Expected array is out of comparable lines, array can only contain empty lines and comments
                if (j < array.length) {
                    if (ignoreCommentsEmptyLines && skippableLine(array[j])) {
                        ++j;
                    } else {
                        fail(getFailMessage(i, j));
                    }
                }
            }
        }
    }

    private static String getFailMessage(int i, int j) {
        return "Contents mismatch between expected and actual arrays on line: " + i + " (expected), " +
                j + " (actual)";
    }

    private static boolean skippableLine(String line) {
        if (line == null) return true;
        line = line.trim();
        return line.isEmpty() || line.startsWith("#");
    }

    public static void assertMapsMatch(Map<Object, Object> expectedMap, Map<Object, Object> map) {
        //No length shortcut - let's actually find different entries
        Collection<String> differenceExpectedActual = getMapDifference(expectedMap, map);
        Collection<String> differenceActualExpected = getMapDifference(map, expectedMap);

        //Fail if there are differences
        if (!differenceActualExpected.isEmpty() || !differenceExpectedActual.isEmpty()) {
            StringBuilder sb = new StringBuilder("Found differences between sets: ");
            sb.append("expected to actual: ").append(differenceExpectedActual).append(", ");
            sb.append("actual to expected: ").append(differenceActualExpected);

            fail(sb.toString());
        }
    }

    private static Collection<String> getMapDifference(Map<Object, Object> map1, Map<Object, Object> map2) {
        Collection<String> difference = new LinkedList<String>();

        for (Object key : map1.keySet()) {
            if (map2.containsKey(key)) {
                if (!map1.get(key).equals(map2.get(key)))
                    difference.add(key + ": " + map1.get(key) + " <> " + map2.get(key));
            } else {
                difference.add(key + ": missing");
            }
        }

        return difference;
    }

    public static void resetOptionsFactory() {
        OptionsFactory.reset();
    }
}
