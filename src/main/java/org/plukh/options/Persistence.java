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

import org.plukh.options.impl.persistence.TransientPersistenceProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate interface extending {@link Options} with this annotation to specify what backing store provider should
 * be used by the {@link org.plukh.options.Options#load(boolean)} and {@link Options#save(boolean)} methods to persist
 * option values.
 * <p/>
 * To use a custom persistence provider, use {@code PersistenceType.CUSTOM} as {@code value} and specify a custom provider
 * class in {@code provider} element.
 *
 * @see PersistenceType
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Persistence {
    /**
     * One of the constants specified in {@link PersistenceType} enum. Use {@code PersistenceType.CUSTOM} to
     * use a custom persistence provider. Default is {@link org.plukh.options.impl.persistence.PropertiesPersistenceProvider},
     * which stores options to a {@code .properties} file.
     */
    PersistenceType value() default PersistenceType.PROPERTIES_FILE;

    /**
     * Specify a custom persistence provider class. The value of this element is ignored if {@code value} element is
     * set to anything but {@code PersistenceType.CUSTOM}.
     */
    Class<? extends PersistenceProvider> provider() default TransientPersistenceProvider.class;
}
