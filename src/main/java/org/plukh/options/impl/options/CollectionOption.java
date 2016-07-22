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

import org.plukh.options.ParseException;
import org.plukh.options.UnsupportedOptionClassException;
import org.plukh.options.impl.collections.CollectionBackedOption;
import org.plukh.options.impl.collections.CollectionInitializationException;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Implement with new @CollectionOption annotation scheme
public class CollectionOption extends AbstractOption {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("([^:\\\\]*)([:\\\\])");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([:\\\\])");

    public CollectionOption(Class<?> elementClass, Class<?> optionsCollectionClass) {
        super(elementClass);
        //Validate parameters
        if (!isValidOptionClass(elementClass)) throw new IllegalArgumentException("Class " + elementClass.getName() +
            " is not a valid option class");

        if (!CollectionBackedOption.class.isAssignableFrom(optionsCollectionClass))
            throw new IllegalArgumentException("Class " + optionsCollectionClass.getName() +
                " is not a valid collection class (valid collection classes must implement CollectionBackedOption interface)");

        try {
            Constructor constructor = optionsCollectionClass.getConstructor(Class.class);
            value = constructor.newInstance(elementClass);
            stringToValueConverted = true;
            valueToStringConverted = false;
        } catch (Exception e) {
            throw new CollectionInitializationException("Error initializing collection option instance for collection class: "
                    + optionsCollectionClass.getName(), e);
        }

        //Collection classes are read-only by definition
        setReadOnly(true);
    }

    public CollectionOption(Class<?> collectionClass, Class<?> elementClass, Class<?> optionsCollectionClass, Class<?> backingClass) {
        super(elementClass);
        //Validate parameters
        if (!isValidOptionClass(elementClass)) throw new IllegalArgumentException("Class " + elementClass.getName() +
                " is not a valid option class");

        if (!CollectionBackedOption.class.isAssignableFrom(optionsCollectionClass))
            throw new IllegalArgumentException("Class " + optionsCollectionClass.getName() +
                    " is not a valid collection class (valid collection classes must implement CollectionBackedOption interface)");

        if (!collectionClass.isAssignableFrom(backingClass))
            throw new IllegalArgumentException("Class " + backingClass.getName() + " cannot be used as a backing class for " +
                    " options collection class " + collectionClass.getName() + ", not assignment-compatible");

        try {
            Constructor constructor = optionsCollectionClass.getConstructor(Class.class, Class.class);
            value = constructor.newInstance(elementClass, backingClass);
            stringToValueConverted = true;
            valueToStringConverted = false;
        } catch (Exception e) {
            throw new CollectionInitializationException("Error initializing collection option instance for collection class: "
                    + optionsCollectionClass.getName(), e);
        }
    }

    @Override
    public Object convertStringToValue(String s) throws ParseException {
        return null;
    }

    @Override
    public String convertValueToString(Object o) {
        if (o == null) return null;
        if (!(o instanceof CollectionBackedOption))
            throw new IllegalArgumentException("Can only convert values implementing CollectionBackedOption interface");

        Collection collection = ((CollectionBackedOption)o).getBackingCollection();

        AbstractOption option;
        try {
            option = OptionFactory.getOptionForClass(getOptionClass());
        } catch (UnsupportedOptionClassException e) {
            throw new IllegalArgumentException("Collection element class is unsupported, weird: " + getOptionClass().getName());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new OptionConversionException("Error instantiating option for class: " + getOptionClass().getName(), e);
        }

        StringBuilder sb = new StringBuilder();

        int c = 0;
        for (Object value : collection) {
            //Append : to output
            if (c > 0) sb.append(':');
            ++c;

            if (value != null) {
                //Obtain a compatible option class
                if (!getOptionClass().isInstance(value)) throw new IllegalArgumentException("Collection element " +
                        value.toString() + " is not instance of the element class " + getOptionClass().getName());

                //Convert value to string; escape the converted string
                final String string = escapeString(option.convertValueToString(value));

                //Append to output
                sb.append(string);
            } else {
                //Should be no nulls!
                throw new IllegalArgumentException("Options collection should not contain nulls!");
            }
        }

        return sb.toString();
    }

    String escapeString(String string) {
        if (string == null) return null;

        StringBuffer sb = new StringBuffer();

        Matcher matcher = ESCAPE_PATTERN.matcher(string);

        while (matcher.find()) {
            matcher.appendReplacement(sb, "$1\\\\$2");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    String unescapeString(String string) {
        if (string == null) return null;

        StringBuffer sb = new StringBuffer();

        Matcher matcher = UNESCAPE_PATTERN.matcher(string);

        while (matcher.find()) {
            matcher.appendReplacement(sb, "$1");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    @Override
    public void setValue(Object value) {
        throw new IllegalArgumentException("Collection options are by definition read-only, can't assign values to them");
    }
}
