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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO: Document non-convertable transient options

/**
 * Annotate getters in an interface with this annotation to mark them as option accessors. For each annotated getter,
 * the library will create a backing option instance to store its string and object representations.
 * <p/>
 * <strong>Examples:</strong>
 * <p/>
 * <i>Declare an option with all properties at default:</i>
 * <pre>
 * ...
 * <b>&#064;Option</b>
 * int getOption();
 * void setOption(int value); //Every non-read-only option should have both a getter and a setter
 * ...
 * </pre>
 * <i>Declare an option with a custom key (to be used when saving option's value to a backing store):</i>
 * <pre>
 * ...
 * <b>&#064;Option</b>(key = "myapp.login.username")
 * String getUsername();
 * void setUsername(String username);
 * ...
 * </pre>
 * @see Options
 * @see OptionsFactory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {
    /**
     * Optionally, specify an unique key which will unambigously identify this particular option when saving/loading
     * options from a backing store. If no key is specified, or if its value is null or empty, name of the property
     * referenced by the getter method will be used instead.
     * <p/>
     * Only the following characters can be used for option keys: lower and upper English letters, digits, dot ("."),
     * underscore ("_") and minus sign ("-").
     */
    String key() default "";

    /**
     * Optionally, specify a default string value for this option. The provided value will be used to initialize the
     * option instance backing this property.
     */
    String defaultValue() default "";

    /**
     * Set this to <code>true</code> to make an option read-only. For the read-only option, there should be no setter
     * present in the interface being annotated, which means that the application has no way to change the option's value.
     * Other than that, it behaves exactly as a regular option.
     * <p/>
     * Note that the option marked both read-only and transient will not be loaded from the backing
     * store (transientOption takes precedence).
     */
    boolean readOnly() default false;

    /**
     * Set this to <code>true</code> to make an option transient. Transient options aren't saved or loaded to/from the
     * backing store.
     * <p/>
     * Note that the option marked both read-only and transient will not be loaded from the backing
     * store (transientOption takes precedence).
     */
    boolean transientOption() default false;
}
