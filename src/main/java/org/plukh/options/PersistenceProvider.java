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

import org.plukh.options.impl.options.AbstractOption;

import java.util.Collection;

/**
 * Specifies a contract for providers of persistent backing stores for saving and loading option values.
 * {@code Options-util} provides a standard implementation for persisting options to {@code .properties} files, as well
 * as a {@link org.plukh.options.impl.persistence.TransientPersistenceProvider} when options should not be
 * persisted to a store at all.
 * <p/>
 * Provider classes implementing this interface <b>must</b> implement a public no-args constructor.
 * <p/>
 * Implementing classes should be familiar with underlying options implementation used by proxy objects.
 *
 * @see AbstractOption
 * @see org.plukh.options.impl.options
 */
public interface PersistenceProvider {

    /**
     * Initializing persistence provider
     *
     * @param classForPersisting class for persisting
     */
    void init(Class<? extends Options> classForPersisting);

    /**
     * Called by the proxy object to pass on information which can be used to configure this persistence provider.
     * Classes implementing this interface should clearly state what object class they expect for
     * configuration and what information inside the class is and is not mandatory for a successful configuration.
     * <p/>
     * Additionally, each implementing class should be able to successfully function (load and save options from/to its
     * backing store) without this method being called at all (in other words, each provider should be initialized with
     * a reasonable default configuration).
     *
     * @param configuration configuration object, according to provider's specification.
     * @throws ProviderConfigurationException when configuration information is incomplete, erroneous or otherwise
     * unusable to the persistence provider. This includes the case when the application passes an object of the wrong
     * class to the persistence provider; persistence providers are expected to not perform an unchecked cast from
     * {@code configuration} parameter to their specific configuration class.
     */
    void configure(PersistenceConfig configuration) throws ProviderConfigurationException;

    /**
     * Requests implementing provider class to save a collection of options to the backing store. Providers
     * should store options as if they were an ordered collection (trying to match collection's iteration order), but
     * this is not an absolute requirement.
     *
     * @param options collection of options to be persisted.
     * @param nonDefaultOnly if set to {@code true}, advises persistence provider to store only those options which
     *                       have non-default values to a backing store. Provider should try to follow this advice, but
     *                       it's not an absolute requirement.
     * @throws OptionsException if an errors occurs while persisting options to the backing store.
     */
    void save(Collection<AbstractOption> options, boolean nonDefaultOnly) throws OptionsException;

    /**
     * Requests implementing provider class to load a collection of options from the backing store. Options which aren't
     * present in the store must be reset to their default values on successful load. If options with unknown keys are
     * found in the store, they must be silently ignored. If conversion errors preclude provider from setting one or more
     * option values, {@link ConversionException} must be thrown, listing keys of options which failed to convert. It is
     * advised that persistence provider implementations do not throw {@code ConversionException} on the first conversion
     * error, instead trying to complete parsing option values from the store and forming a collection of fields with
     * conversion errors to be included in {@code ConversionException}.
     * </p>
     * If all the options cannot be loaded successfully for any reason (inclusing IO errors and conversion errors), the
     * original option values must remain unmodified (see {@code suppressConversionErrors} below for an exception.
     *
     *
     * @param options collection of options to be loaded from the backing store.
     * @param suppressConversionErrors set to {@code true} to suppress ConversionException. Any conversion errors will be
     *                                 ignored; values of affected options will be reset to default (like if they weren't
     *                                 present in the store at all).
     *
     * @return {@code true} if options were successfully loaded from the store, {@code false} if they weren't present in
     * the store at all (for example, if options file wasn't present on user's filesystem). In case of errors (conversion
     * or otherwise), appropriate exception must be thrown (see below).
     *
     * @throws ConversionException if there were conversion errors when setting option values, and
     * {@code suppressConversionErrors} was set to false.
     * @throws OptionsException if an error occurs while loading options from the backing store.
     */
    boolean load(Collection<AbstractOption> options, boolean suppressConversionErrors) throws OptionsException;
}
