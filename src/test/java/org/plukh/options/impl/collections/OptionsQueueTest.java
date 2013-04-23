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

package org.plukh.options.impl.collections;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.DelayQueue;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OptionsQueueTest {
    private OptionsQueue queue;

    @Before
    public void setUp() {
        queue = new OptionsQueue(String.class);
    }

    @Test
    public void classMustHaveProperConstructors() throws NoSuchMethodException {
        //Single-arg Class constructor
        queue.getClass().getConstructor(Class.class);
        //Dual-arg Class constructor
        queue.getClass().getConstructor(Class.class, Class.class);
    }

    @Test
    public void backingCollectionClassMustBeUsed() {
        queue = new OptionsQueue(String.class, DelayQueue.class);
        assertTrue(queue.getBackingCollection() instanceof DelayQueue);
    }

    @Test
    public void addMethodsShouldThrowExceptionForWrongClasses() {
        try {
            queue.add(1);
            fail("Expected exception not thrown for add()");
        } catch (IllegalArgumentException e) {
            //Expected
        }

        try {
            Collection c = new LinkedList();
            c.add(1);
            queue.addAll(c);
            fail("Expected exception not thrown for addAll()");
        } catch (IllegalArgumentException e) {
            //Expected
        }

        try {
            queue.offer(1);
            fail("Expected exception not thrown for offer()");
        } catch (IllegalArgumentException e) {
            //Expected
        }
    }
}
