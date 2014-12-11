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

import org.plukh.options.impl.OptionsProxyHandler;
import org.plukh.options.impl.collections.OptionsQueue;
import org.plukh.options.impl.options.*;
import org.plukh.options.impl.persistence.PropertiesPersistenceProvider;
import org.plukh.options.impl.persistence.TransientPersistenceProvider;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code OptionsFactory} creates proxy object instances used to implement application-specific option interfaces.
 * This class provides a single static method which should be called - {@link #getOptionsInstance(Class)} -
 * which returns a proxy object instance implementing the custom options interface passed as method's parameter.
 * <p/>
 * Proxy object instances are cached, so this method can be called repeatedly. Subsequent invocations will return exactly
 * the same options object instance.
 *
 * @see Options
 */
public class OptionsFactory {
    private final static Pattern GETTER_PATTERN = Pattern.compile("^((get)|(is))([A-Z].*)$");
    private static final int GROUP_NAME = 4;
    private static final int GROUP_PREFIX = 1;

    private static final Pattern VALID_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+");

    static {
        registerStandardOptionTypes();
        registerStandardCollectionTypes();
    }

    private final static Map<Class<? extends Options>, Options> optionsInstanceCache = new HashMap<Class<? extends Options>, Options>();

    /**
     * Dummy method used to make sure that this class is loaded and initialized when unit-testing. Don't call in
     * production code (though no harm will come from calling it as well).
     */
    public static void init() {

    }

    /**
     * Returns proxy object instance implementing application-specific options interface. This method is thread-safe.
     * @param optionsClass Application-specifc options interface class extending {@link Options}.
     * @return an instance of the object implementing the passed interface.
     * @throws OptionsException in case of instantiation errors.
     */

    //Throw an unchecked exception from this method for convenience; we don't expect options to be misconfigured in production!
    public static Options getOptionsInstance(Class<? extends Options> optionsClass) throws OptionsInstantiationException {
        synchronized (optionsInstanceCache) {
            if (optionsInstanceCache.containsKey(optionsClass)) return optionsInstanceCache.get(optionsClass);

            Options options = null;
            try {
                options = createOptionsInstance(optionsClass);
            } catch (OptionsException e) {
                throw new OptionsInstantiationException(e);
            }
            optionsInstanceCache.put(optionsClass, options);
            return options;
        }
    }

    private static Options createOptionsInstance(Class<? extends Options> optionsClass) throws OptionsException {
        //Create a list of (annotated) getters
        List<Method> getters = createGettersList(optionsClass);
        //Find matching setters
        Map<Method, Method> setters = findMatchingSetters(optionsClass, getters);

        //Create options based on getters' annotations
        Map<Method, AbstractOption> gettersWithOptions = new HashMap<Method, AbstractOption>();
        Map<Method, AbstractOption> settersWithOptions = new HashMap<Method, AbstractOption>();
        fillOptionsMaps(getters, setters, gettersWithOptions, settersWithOptions);

        //Get configured persistence provider
        PersistenceProvider pp;
        try {
            pp = getPersistenceProvider(optionsClass);
        } catch (ProviderConfigurationException e) {
            //Shouldn't happen, just wrap and throw again
            throw new OptionsException("Error applying default provider configuration", e);
        }

        //Instantiate handler
        OptionsProxyHandler handler = createHandler(optionsClass, gettersWithOptions, settersWithOptions, pp);

        //Instantiate and return proxy object
        return getProxyInstance(optionsClass, handler);
    }

    protected static List<Method> createGettersList(Class<? extends Options> optionsClass) throws OptionsException {
        List<Method> getters = new LinkedList<Method>();
        Method[] methods = optionsClass.getMethods();

        for (Method method : methods) {
            //Check if this method is an (annotated) getter
            Matcher m = GETTER_PATTERN.matcher(method.getName());
            if (m.matches()) {
                Option optionAnnotation = method.getAnnotation(Option.class);
                CollectionOption collectionAnnotation = method.getAnnotation(CollectionOption.class);

                if (optionAnnotation != null && collectionAnnotation != null)
                    throw new OptionsException("Only one of @Option/@CollectionOption annotations can be used for method " + method);

                if (optionAnnotation != null || collectionAnnotation != null) {
                    validateMethodName(method, m.group(GROUP_NAME), m.group(GROUP_PREFIX));
                    getters.add(method);
                }
            }
        }

        if (getters.isEmpty()) throw new OptionsException("No annotated getters found in " + optionsClass.getName());

        //System.out.println("Created getters: " + getters);
        return getters;
    }

    private static void validateMethodName(Method method, String name, String prefix) throws OptionsException {
        final Class clazz = method.getDeclaringClass();

        //Validate naming further (only boolean getters can have "is" prefix, only one (is|get) prefix
        //should be present for a given property)

        //See if we're dealing with a boolean
        if (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)) {
            try {
                Method anotherMethod = clazz.getMethod(prefix.equals("is") ? "get" + name : "is" + name);
                throw new OptionsException("Only one of (get, is) prefixes can be used for a boolean getter: " + method);
            } catch (NoSuchMethodException e) {
                //Everything's fine
            }
        }  else {
            if (prefix.equals("is")) throw new OptionsException("Only boolean getters can have \"is\" prefix: " + method);
        }
    }

    protected static Map<Method, Method> findMatchingSetters(Class<? extends Options> optionsClass, List<Method> getters)
            throws OptionsException {
        Map<Method, Method> gettersToSetters = new HashMap<Method, Method>();

        for (Method getter : getters) {
            String optionName = getPropertyName(getter, false);
            Method setter;
            try {
                setter = optionsClass.getMethod("set" + optionName, new Class[] {getter.getReturnType()});
            } catch (NoSuchMethodException e) {
                setter = null;
            }

            //See if the method is annotated as read-only or is annotated as collection option
            if (getter.getAnnotation(Option.class) != null && getter.getAnnotation(Option.class).readOnly() || getter.getAnnotation(CollectionOption.class) != null) { //Collection options are read-only always
                if (setter != null) throw new OptionsException("Read-only getter " + getter +
                        " has a matching setter in " + getter.getDeclaringClass().getName());
            } else {
                if (setter == null) throw new OptionsException("No suitable setter for getter " + getter +
                        " found in " + getter.getDeclaringClass().getName());
                gettersToSetters.put(getter, setter);
            }
        }

        return gettersToSetters;
    }

    private static String getPropertyName(Method getter, boolean decapitalize) {
        Matcher m = GETTER_PATTERN.matcher(getter.getName());
        m.matches();
        //Use properly decapitalized property name
        return decapitalize ? Introspector.decapitalize(m.group(GROUP_NAME)) : m.group(GROUP_NAME);
    }

    protected static void fillOptionsMaps(List<Method> getters, Map<Method, Method> setters,
                                          Map<Method, AbstractOption> gettersWithOptions,
                                          Map<Method, AbstractOption> settersWithOptions) throws OptionsException {
        Set<String> keys = new HashSet<String>();

        try {
            for (Method getter : getters) {
                AbstractOption option;
                try {
                    option = instantiateOption(getter);
                } catch (UnsupportedOptionClassException e) {
                    //See if this is a scalar transient option
                    Option optionAnnotation = getter.getAnnotation(Option.class);

                    if (optionAnnotation != null && optionAnnotation.transientOption())
                        option = new NonConvertableOption();
                    else
                        throw e;
                }

                setOptionFromAnnotation(getter, option);

                //Validate key uniqueness
                if (keys.contains(option.getKey())) throw new OptionsException("Key " + option.getKey() + " for method "
                        + getter + " already defined for " + getter.getDeclaringClass().getName());
                //Validate character keys
                final Character invalidChar = validateKey(option.getKey());
                if (invalidChar != null) throw new OptionsException("Key " + option.getKey() + " for method " + getter +
                        " in class " + getter.getDeclaringClass().getName() + " contains invalid character '" +
                        invalidChar + "'");
                //Add option to maps and update the key set
                keys.add(option.getKey());
                gettersWithOptions.put(getter, option);
                //Setter can be missing if option is read-only
                if (setters.containsKey(getter)) settersWithOptions.put(setters.get(getter), option);
            }
        } catch (UnsupportedOptionClassException e) {
            throw new OptionsException(e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof OptionsException) throw (OptionsException) e;
            throw new OptionsException("Exception creating options", e);
        }
    }

    private static AbstractOption instantiateOption(Method getter) throws UnsupportedOptionClassException,
            IllegalAccessException, InstantiationException {
        if (getter.getAnnotation(Option.class) != null) return instantiateScalarOption(getter);
        else return instantiateCollectionOption(getter);
    }

    private static AbstractOption instantiateCollectionOption(Method getter) throws UnsupportedOptionClassException {
        CollectionOption collectionAnnotation = getter.getAnnotation(CollectionOption.class);


        //See if user had specified a backing collection class
        if (collectionAnnotation.backingClass().equals(AbstractCollection.class)) {
            return AbstractOption.getCollectionOption(collectionAnnotation.elementClass(), getter.getReturnType());
        }

        //Validate backing class
        try {
            Constructor constructor = collectionAnnotation.backingClass().getConstructor();
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOptionClassException("Backing class " + collectionAnnotation.backingClass().getName() +
                    " doesn't have a public default constructor");
        }

        return AbstractOption.getCollectionOption(collectionAnnotation.elementClass(), getter.getReturnType(),
                collectionAnnotation.backingClass());
    }

    private static AbstractOption instantiateScalarOption(Method getter) throws UnsupportedOptionClassException,
            InstantiationException, IllegalAccessException {
        return AbstractOption.getOptionForClass(getter.getReturnType());
    }

    private static Character validateKey(String key) {
        Matcher m = VALID_KEY_PATTERN.matcher(key);
        //Try a find and see if all of the key matches the pattern
        char illegalChar;
        if (m.find()) {
            if (m.end() == key.length()) return null;
            return key.charAt(m.end());
        }
        return key.charAt(0);
    }

    protected static void setCollectionOptionFromAnnotation(Method getter, org.plukh.options.impl.options.CollectionOption option) {
        CollectionOption annotation = getter.getAnnotation(CollectionOption.class);

        option.setKey(annotation.key().isEmpty() ? getPropertyName(getter, true) : annotation.key());
        if (annotation.defaultValue() == null || !annotation.defaultValue().isEmpty()) option.setDefaultValue(annotation.defaultValue());
        option.setTransient(annotation.transientOption());
        option.setReadOnly(true);
    }

    protected static void setScalarOptionFromAnnotation(Method getter, AbstractOption option) {
        Option annotation = getter.getAnnotation(Option.class);

        option.setKey(annotation.key().isEmpty() ? getPropertyName(getter, true) : annotation.key());
        //Ignore default value for non-convertable options
        if (!(option instanceof NonConvertableOption) &&
                (annotation.defaultValue() == null || !annotation.defaultValue().isEmpty())) option.setDefaultValue(annotation.defaultValue());
        option.setTransient(annotation.transientOption());
        option.setReadOnly(annotation.readOnly());
    }

    protected static void setOptionFromAnnotation(Method getter, AbstractOption option) {
        if (getter.getAnnotation(Option.class) != null) setScalarOptionFromAnnotation(getter, option);
        else setCollectionOptionFromAnnotation(getter, (org.plukh.options.impl.options.CollectionOption) option);
    }

    private static PersistenceProvider getPersistenceProvider(Class<? extends Options> optionsClass) throws OptionsException,
            ProviderConfigurationException {
        PersistenceProvider provider;

        //Try to look up @Persistence annotation on options class
        Persistence persistenceAnnotation = optionsClass.getAnnotation(Persistence.class);

        if (persistenceAnnotation == null) {
            provider = new TransientPersistenceProvider();
        } else {
            //Read annotation elements and instantiare a corresponding persistence provider class
            switch (persistenceAnnotation.value()) {
                case TRANSIENT: provider = new TransientPersistenceProvider();
                    break;
                case PROPERTIES_FILE: provider = new PropertiesPersistenceProvider();
                    break;
                //TODO: Implement XML provider
                case XML_FILE: throw new OptionsException("XML provider not implemented yet");
                case CUSTOM:
                    Class providerClass = persistenceAnnotation.provider();
                    try {
                        provider = (PersistenceProvider) providerClass.newInstance();
                    } catch (Exception e) {
                        throw new OptionsException("Error instantiating custom persistence provider for class: " +
                                providerClass.getName(), e);
                    }
                    break;
                default: throw new OptionsException("Unsupported persistence type");
            }
        }

        provider.configure(new PersistenceConfig(optionsClass));

        return provider;
    }

    protected static OptionsProxyHandler createHandler(Class<? extends Options> optionsClass, Map<Method, AbstractOption> gettersWithOptions,
                                                       Map<Method, AbstractOption> settersWithOptions,
                                                       PersistenceProvider pp) throws OptionsException {
        try {
            return new OptionsProxyHandler(optionsClass, gettersWithOptions, settersWithOptions, pp);
        } catch (NoSuchMethodException e) {
            throw new OptionsException("Internal options handler error", e);
        }
    }

    protected static Options getProxyInstance(Class<? extends Options> optionsClass, OptionsProxyHandler handler) {
        return (Options) Proxy.newProxyInstance(optionsClass.getClassLoader(),
                new Class<?>[]{optionsClass}, handler);
    }

    protected int getInstanceCacheSize() {
        return optionsInstanceCache.size();
    }

    protected static void reset() {
        //Clear cache
        optionsInstanceCache.clear();
    }

    protected static void registerStandardOptionTypes() {
        AbstractOption.registerOptionClassForType(int.class, IntegerOption.class);
        AbstractOption.registerOptionClassForType(Integer.class, IntegerOption.class);
        AbstractOption.registerOptionClassForType(boolean.class, BooleanOption.class);
        AbstractOption.registerOptionClassForType(Boolean.class, BooleanOption.class);
        AbstractOption.registerOptionClassForType(String.class, StringOption.class);
        AbstractOption.registerOptionClassForType(Date.class, DateOption.class);
    }

    protected static void registerStandardCollectionTypes() {
        AbstractOption.registerCollectionOptionClassForType(Queue.class, OptionsQueue.class);
    }
}
