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

package org.plukh.options.impl;

import org.junit.Before;
import org.junit.Test;
import org.plukh.options.OptionsException;
import org.plukh.options.PersistenceProvider;
import org.plukh.options.ProviderConfigurationException;
import org.plukh.options.impl.options.AbstractOption;
import org.plukh.options.impl.options.BooleanOption;
import org.plukh.options.impl.options.IntegerOption;
import org.plukh.options.impl.options.StringOption;
import org.plukh.options.PersistenceConfig;
import org.plukh.options.interfaces.TestOptions;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OptionsProxyHandlerTest {
    private static final String[] GETTERS = {"getInt", "getInt2", "getBoolean", "getString"};
    private static final String[] SETTERS = {"setInt", "setInt2", "setBoolean", "setString"};
    private static final Class[] SETTER_CLASSES = {int.class, int.class, boolean.class, String.class};
    @SuppressWarnings("unchecked")
    public static final Class<? extends AbstractOption>[] OPTION_CLASSES = new Class[] {IntegerOption.class, IntegerOption.class, BooleanOption.class,
            StringOption.class};

    private static final String[] DEFAULT_VALUES = new String[] {"1", "2", "true", "1"};

    private TestOptions options;
    private OptionsProxyHandler handler;
    private Map<Method,AbstractOption> getters;
    private Map<Method,AbstractOption> setters;

    @Before
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        createHandler();
        createProxy();
    }

    private void createHandler() throws InstantiationException, IllegalAccessException, NoSuchMethodException {
        //Create mock getter/setter maps
        getters = new HashMap<Method, AbstractOption>();
        setters = new HashMap<Method, AbstractOption>();

        for (int i = 0; i < GETTERS.length; ++i) {
            AbstractOption option = OPTION_CLASSES[i].newInstance();
            option.setDefaultValue(DEFAULT_VALUES[i]);
            getters.put(TestOptions.class.getMethod(GETTERS[i]), option);
            setters.put(TestOptions.class.getMethod(SETTERS[i], new Class[]{SETTER_CLASSES[i]}), option);
        }

        handler = new OptionsProxyHandler(TestOptions.class, getters, setters);
    }

    private void createProxy() {
        options = (TestOptions) Proxy.newProxyInstance(TestOptions.class.getClassLoader(),
                new Class<?>[]{TestOptions.class}, handler);
    }

    @Test
    public void testInstantiation() {
        assertTrue("Failed proxy instantiation with proper handler", Proxy.isProxyClass(options.getClass()) && Proxy.getInvocationHandler(options).equals(handler));
    }

    @Test
    public void testLoadMethod() throws OptionsException {
        PersistenceProvider pp = getMockPersistenceProvider();
        handler.setPersistenceProvider(pp);

        expect(pp.load(getters.values(), false)).andReturn(true);
        replay(pp);

        options.load(false);
        verify(pp);
    }

    @Test
    public void testSaveMethod() throws OptionsException {
        PersistenceProvider pp = getMockPersistenceProvider();
        handler.setPersistenceProvider(pp);

        pp.save(getters.values(), false);
        replay(pp);

        options.save(false);
        verify(pp);
    }

    @Test
    public void testResetToDefaultsMethod() {
        options.setInt(10);
        options.setInt2(20);
        options.setBoolean(false);
        options.setString("abc");

        options.resetToDefault();

        assertEquals(1, options.getInt());
        assertEquals(2, options.getInt2());
        assertEquals(true, options.getBoolean());
        assertEquals("1", options.getString());
    }

    @Test
    public void testSetPersistenceProviderConfigMethod() throws OptionsException, ProviderConfigurationException {
        PersistenceProvider pp = getMockPersistenceProvider();
        handler.setPersistenceProvider(pp);

        PersistenceConfig config = new PersistenceConfig(TestOptions.class);

        pp.configure(config);
        replay(pp);

        options.configurePersistenceProvider(config);
        verify(pp);
    }

    @Test
    public void testValueSetterGetter() {
        options.setInt(5);
        options.setInt2(10);
        options.setBoolean(true);
        options.setString("abc");

        assertEquals(5, options.getInt());
        assertEquals(10, options.getInt2());
        assertEquals(true, options.getBoolean());
        assertEquals("abc", options.getString());
    }

    private PersistenceProvider getMockPersistenceProvider() {
        return createMock(PersistenceProvider.class);
    }
}
