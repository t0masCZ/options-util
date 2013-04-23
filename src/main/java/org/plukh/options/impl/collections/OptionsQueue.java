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

package org.plukh.options.impl.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class OptionsQueue implements CollectionBackedOption, Queue {
    public static final Class<? extends Queue> DEFAULT_QUEUE_IMPLEMENTATION_CLASS = LinkedList.class;

    private final Queue queue;
    private final Class elementClass;


    public OptionsQueue(Class elementClass) {
        this.elementClass = elementClass;
        try {
            queue = DEFAULT_QUEUE_IMPLEMENTATION_CLASS.newInstance();
        } catch (IllegalAccessException e) {
            throw new CollectionInitializationException("Error initializing backing Queue instance", e);
        } catch (InstantiationException e) {
            throw new CollectionInitializationException("Error initializing backing Queue instance", e);
        }
    }

    public OptionsQueue(Class elementClass, Class<? extends Queue> backingClass) {
        this.elementClass = elementClass;

        try {
            queue = backingClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new CollectionInitializationException("Error initializing backing Queue instance for class " +
                    backingClass.getName(), e);
        } catch (InstantiationException e) {
            throw new CollectionInitializationException("Error initializing backing Queue instance for class " +
                    backingClass.getName(), e);
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        if (o == null) throw new NullPointerException("OptionsQueue doesn't allow for null elements");
        if (!(elementClass.isAssignableFrom(o.getClass()))) throw new IllegalArgumentException("Only elements of class " +
                elementClass.getName() + " supported in this OptionsQueue instance");
        return queue.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean offer(Object o) {
        if (o == null) throw new NullPointerException("OptionsQueue doesn't allow for null elements");
        if (!(elementClass.isAssignableFrom(o.getClass()))) throw new IllegalArgumentException("Only elements of class " +
                elementClass.getName() + " supported in this OptionsQueue instance");
        return queue.offer(o);
    }

    @Override
    public Object remove() {
        return queue.remove();
    }

    @Override
    public Object poll() {
        return queue.poll();
    }

    @Override
    public Object element() {
        return queue.element();
    }

    @Override
    public Object peek() {
        return queue.peek();
    }

    @Override
    public boolean containsAll(Collection c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        //Validate incoming collection
        for (Object o : c) {
            if (o == null) throw new NullPointerException("OptionsQueue doesn't allow for null elements");
            if (!(elementClass.isAssignableFrom(o.getClass()))) throw new IllegalArgumentException("Only elements of class " +
                    elementClass.getName() + " supported in this OptionsQueue instance");
        }
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public Collection getBackingCollection() {
        return queue;
    }

    @Override
    public Class getCollectionClass() {
        return Queue.class;
    }
}
