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

/**
 * Extend this interface to define application-specific set of configuration options. In the subinterface, declare getter and
 * setter methods for application's options, following standard JavaBean naming convention, then annotate getters with
 * {@link Option @Option} annotation.
 * <p/>
 * <b>Data types supported for options: </b><br />
 * <ul>
 *     <li>{@code int} and {@code Integer}</li>
 *     <li>{@code boolean} and {@code Boolean}</li>
 *     <li>{@code String}</li>
 *     <li>{@code java.util.Date}</li>
 *     <li>{@code java.util.Collection}</li>
 * </ul>
 * For collections, implementation will maintain the order in which elements were iterated by Collection's iterator when saving
 * option values to the backing store.
 * <p/>
 * For instructions on how to use other (arbtirary) data types in application's options, refer to
 * {@link org.plukh.options.impl.options.AbstractOption}'s documentation.
 * <p/>
 * Interfaces extending {@code Options} can be optionally annotated with {@link Persistence @Persistence} annotation
 * to specify the way of persisting their values to a permanent backing store. If options interface is not annotated with
 * {@code @Persistence}, transient persistence provider, which simply ignores load/save calls, will be used.
 * <p/>
 * <b>Example:</b>
 * <p/>
 * Define an application-specific options interface:
 * <p/>
 * <pre>
 * &#064;Persistence //By default, options will be persisted to .properties file
 * public interface MyOptions extends Options {
 *     &#064;Option(key = "myapp.option")
 *     int getMyOption();
 *     void setMyOption(int value);
 * }
 * </pre>
 * Create an instance of a proxy class implementing this interface with a call to {@link OptionsFactory#getOptionsInstance(Class)}:
 * <p/>
 * <pre>
 * MyOptions options = (MyOptions) OptionsFactory.getOptionsInstance(MyOptions.class);
 * options.setMyOption(3);
 * ...
 * </pre>
 * <p/>
 * <b>Note:</b>
 * <ul>
 *     <li>Proxy objects implementing options interfaces are thread-safe for individual operations; however, atomicity
 *     of a sequence of calls to getters/setters is not guaranteed; if you need to set/get several values atomically,
 *     consider either implementing a custom option class (see {@link org.plukh.options.impl.options.AbstractOption}
 *     for details) or manually synchronizing access to the proxy object.</li>
 * </ul>
 *
 * @see Option
 * @see OptionsFactory
 * @see Persistence
 */
public interface Options {
    /**
     * Sets configuration of a persistence provider. See documentation for specific persistence providers for
     * information on configuration information class used by the particular provider.
     *
     * @param configuration provider-specific configuration object.
     * @throws OptionsException when an errors occures when setting up the provider.
     */
    void configurePersistenceProvider(PersistenceConfig configuration) throws OptionsException;

    /**
     * Loads option values from a backing store. The store to be used is determined by {@link PersistenceProvider} configured
     * for this set of options. Options with values missing from the store will be reset to default value on successful load.
     *
     * @param suppressConversionErrors set to {@code true} to suppress ConversionException. Any conversion errors will be
     *                                 ignored; values of affected options will be reset to default (like if they weren't
     *                                 present in the store at all).
     *
     * @return {@code true} if options were successfully loaded from the store, {@code false} if they weren't present in
     * the store at all (for example, if options file wasn't present on user's filesystem). In case of errors (conversion
     * or otherwise), exception is thrown (see below).
     *
     * @throws ConversionException if there were conversion errors when setting option values, and
     * {@code suppressConversionErrors} was set to false.
     * @throws OptionsException when an error occures while saving options. Examine the cause of this exception to get
     * additional information.
     *
     * @see Persistence
     * @see PersistenceProvider
     */

    boolean load(boolean suppressConversionErrors) throws OptionsException;

    /**
     * Saves option values to a backing store. The store to be used is determined by {@link PersistenceProvider} configured
     * for this set of options.
     *
     * @param nonDefaultOnly set to {@code true} to request persistence provider of the backing store to only save values
     *                       which are different from their defaults.
     * @throws OptionsException when an error occures while saving options. Examine the cause of this exception to get
     * additional information.
     *
     * @see Persistence
     * @see PersistenceProvider
     */
    void save(boolean nonDefaultOnly) throws OptionsException;

    /**
     * Resets all option values to their defaults. If no default is specified, the option's value becomes {@code null}.
     */
    void resetToDefault();
}
