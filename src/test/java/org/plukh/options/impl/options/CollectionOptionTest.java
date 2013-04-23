/*
 *
 *  * Copyright 2012 by Victor Denisov (vdenisov@redline.ru).
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

package org.plukh.options.impl.options;

import org.junit.Before;
import org.junit.Test;
import org.plukh.options.impl.collections.OptionsQueue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class CollectionOptionTest {
    private CollectionOption option;
    private Collection collection;

    @Before
    public void setUp() {
        option = new CollectionOption(String.class, OptionsQueue.class);
        collection = (Collection) option.getValue();
    }

    //Instantiation tests
    @Test
    public void mustThrowExceptionOnUnsupportedElementClass() {
        try {
            option = new CollectionOption(this.getClass(), OptionsQueue.class);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(this.getClass().getName()));
        }

        try {
            option = new CollectionOption(Queue.class, this.getClass(), OptionsQueue.class, LinkedList.class);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(this.getClass().getName()));
        }
    }

    @Test
    public void mustThrowExceptionIfCollectionClassDoesNotImplementProperInterface() {
        try {
            option = new CollectionOption(String.class, this.getClass());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(this.getClass().getName()));
        }

        try {
            option = new CollectionOption(Queue.class, String.class, this.getClass(), LinkedList.class);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(this.getClass().getName()));
        }
    }

    @Test
    public void mustThrowExceptionIfBackingClassDoesNotImplementProperInterface() {
        try {
            option = new CollectionOption(Queue.class, String.class, OptionsQueue.class, this.getClass());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(this.getClass().getName()));
        }
    }

    //Escape tests

    @Test
    public void stringWithoutSpecialCharactersShouldRemainUnchanged() {
        final String str = "abcdefg";
        assertEquals(str, option.escapeString(str));
    }

    @Test
    public void emptyStringShouldReturnEmptyEscapedString() {
        assertEquals("", option.escapeString(""));
    }

    @Test
    public void stringWithOneSpecialCharShouldReturnItEscaped() {
        final String str = ":";
        assertEquals("\\:", option.escapeString(str));
    }

    @Test
    public void allSpecialCharsShouldBeEscaped() {
        final String str = ":\\";
        assertEquals("\\:\\\\", option.escapeString(str));
    }

    @Test
    public void severalSpecialCharsWithTextInBetweenShouldBeEscaped() {
        final String str = "abc:def:ghi\\xyz";
        assertEquals("abc\\:def\\:ghi\\\\xyz", option.escapeString(str));
    }

    //Unescape tests

    @Test
    public void stringWithoutEscapedCharactersShouldRemainUnchanged() {
        final String str = "abcdefg";
        assertEquals(str, option.unescapeString(str));
    }

    @Test
    public void emptyStringShouldReturnEmptyUnescapedString() {
        assertEquals("", option.unescapeString(""));
    }

    @Test
    public void stringWithOneSpecialCharShouldReturnItUnescaped() {
        final String str = "\\:";
        assertEquals(":", option.unescapeString(str));
    }

    @Test
    public void allSpecialCharsShouldBeUnescaped() {
        final String str = "\\:\\\\";
        assertEquals(":\\", option.unescapeString(str));
    }

    @Test
    public void severalSpecialCharsWithTextInBetweenShouldBeUnescapedWithTextIntact() {
        final String str = "abc\\:def\\:ghi\\\\xyz";
        assertEquals("abc:def:ghi\\xyz", option.unescapeString(str));
    }

    @Test
    public void severalSlashesShouldBeUnescapedProperly() {
        final String str = "\\\\\\\\\\:";
        assertEquals("\\\\:", option.unescapeString(str));
    }

    //Conversion tests


    //Other tests
}
