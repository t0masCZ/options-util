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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.AbstractCollection;
import java.util.Collection;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CollectionOption {
    /**
     * Optionally, specify an unique key which will unambigously identify this particular collection option when saving/loading
     * options from a backing store. If no key is specified, or if its value is null or empty, name of the property
     * referenced by the getter method will be used instead.
     * <p/>
     * Only the following characters can be used for option keys: lower and upper English letters, digits, dot ("."),
     * underscore ("_") and minus sign ("-").
     */
    String key() default "";

    /**
     * Optionally, specify a default string value for this collection option. The provided value will be used to initialize the
     * option instance backing this property. See {@link org.plukh.options.impl.options.CollectionOption#convertStringToValue(String)}
     * for appropriate string format.
     */
    String defaultValue() default "";

    /**
     * Set this to <code>true</code> to make an option transient. Transient options aren't saved or loaded to/from the
     * backing store.
     */
    boolean transientOption() default false;

    Class elementClass() default String.class;

    Class<? extends Collection> backingClass() default AbstractCollection.class;
}
