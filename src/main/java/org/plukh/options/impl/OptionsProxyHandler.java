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

package org.plukh.options.impl;

import org.plukh.options.*;
import org.plukh.options.impl.options.AbstractOption;
import org.plukh.options.impl.persistence.PersistenceOptions;
import org.plukh.options.impl.persistence.PropertiesPersistenceProvider;
import org.plukh.options.impl.persistence.StreamPersistenceProvider;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class OptionsProxyHandler implements InvocationHandler {
    private Map<Method, Method> methods;
    private Map<Method, AbstractOption> getters;
    private Map<Method, AbstractOption> setters;

    public static final Map<Class, Object> DEFAULT_PRIMITIVE_VALUES = new HashMap<Class, Object>();
    static {
        DEFAULT_PRIMITIVE_VALUES.put(byte.class, (byte) 0);
        DEFAULT_PRIMITIVE_VALUES.put(short.class, (short)0);
        DEFAULT_PRIMITIVE_VALUES.put(int.class, 0);
        DEFAULT_PRIMITIVE_VALUES.put(long.class, 0L);
        DEFAULT_PRIMITIVE_VALUES.put(float.class, 0.0f);
        DEFAULT_PRIMITIVE_VALUES.put(double.class, 0.0d);
        DEFAULT_PRIMITIVE_VALUES.put(boolean.class, Boolean.FALSE);
        DEFAULT_PRIMITIVE_VALUES.put(char.class, '\u0000');
    }

    private Class<? extends Options> optionsClass;

    private PersistenceProvider persistenceProvider;

    public OptionsProxyHandler(Class<? extends Options> optionsClass, Map<Method, AbstractOption> getters, Map<Method, AbstractOption> setters) throws NoSuchMethodException {
        this.optionsClass = optionsClass;
        this.getters = getters;
        this.setters = setters;
        this.methods = getMethods();
        this.persistenceProvider = new PropertiesPersistenceProvider();
    }

    public OptionsProxyHandler(Class<? extends Options> optionsClass, Map<Method, AbstractOption> getters, Map<Method, AbstractOption> setters, PersistenceProvider persistenceProvider) throws NoSuchMethodException {
        this.optionsClass = optionsClass;
        this.getters = getters;
        this.setters = setters;
        this.methods = getMethods();
        this.persistenceProvider = persistenceProvider;
    }

    //TODO: implement test for InvocationTargetException!!!
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methods.containsKey(method)) {
            try {
                return methods.get(method).invoke(this, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        if (getters.containsKey(method)) return getValue(getters.get(method), method.getReturnType());
        setValue(setters.get(method), args[0]);
        return null;
    }

    private synchronized Object getValue(AbstractOption option, Class type) {
        return option.getValue() == null && DEFAULT_PRIMITIVE_VALUES.containsKey(type) ?
                DEFAULT_PRIMITIVE_VALUES.get(type) : option.getValue();
    }

    private synchronized void setValue(AbstractOption option, Object value) {
        option.setValue(value);
    }

    private Map<Method, Method> getMethods() throws NoSuchMethodException {
        Map<Method, Method> methods = new HashMap<Method, Method>();

        methods.put(Options.class.getMethod("load", boolean.class),
                       getClass().getMethod("load", boolean.class));
        methods.put(Options.class.getMethod("save", boolean.class),
                       getClass().getMethod("save", boolean.class));
        methods.put(Options.class.getMethod("resetToDefault"),
                       getClass().getMethod("resetToDefault"));
        methods.put(Options.class.getMethod("configurePersistenceProvider", PersistenceConfig.class),
                       getClass().getMethod("configurePersistenceProvider", PersistenceConfig.class));

        //PersistenceOptions methods
        methods.put(PersistenceOptions.class.getMethod("saveToStream", OutputStream.class, boolean.class),
                                  getClass().getMethod("saveToStream", OutputStream.class, boolean.class));
        methods.put(PersistenceOptions.class.getMethod("loadFromStream", InputStream.class, boolean.class),
                                  getClass().getMethod("loadFromStream", InputStream.class, boolean.class));


        return methods;
    }

    public synchronized boolean load(boolean suppressConversionErrors) throws OptionsException {
        return persistenceProvider.load(getters.values(), suppressConversionErrors);
    }

    public synchronized void save(boolean nonDefaultOnly) throws OptionsException {
        persistenceProvider.save(getters.values(), nonDefaultOnly);
    }

    public synchronized void resetToDefault() {
        for (AbstractOption option : getters.values()) {
            option.resetToDefaultValue();
        }
    }

    public synchronized PersistenceProvider getPersistenceProvider() {
        return persistenceProvider;
    }

    public synchronized void setPersistenceProvider(PersistenceProvider persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
    }

    public synchronized void configurePersistenceProvider(PersistenceConfig configuration) throws ProviderConfigurationException {
        configuration.setOptionsClass(optionsClass);
        persistenceProvider.configure(configuration);
    }

    public synchronized void loadFromStream(InputStream in, boolean suppressConversionErrors) throws OptionsException {
        ((StreamPersistenceProvider)persistenceProvider).loadFromStream(in, getters.values(), suppressConversionErrors);
    }

    public synchronized void saveToStream(OutputStream out, boolean nonDefaultOnly)
            throws OptionsException {
        ((StreamPersistenceProvider)persistenceProvider).saveToStream(out, getters.values(), nonDefaultOnly);
    }
}
