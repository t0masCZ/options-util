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

import org.junit.Before;
import org.junit.Test;
import org.plukh.options.impl.OptionsProxyHandler;
import org.plukh.options.impl.collections.CollectionBackedOption;
import org.plukh.options.impl.collections.OptionsQueue;
import org.plukh.options.impl.options.AbstractOption;
import org.plukh.options.impl.options.NonConvertibleOption;
import org.plukh.options.impl.persistence.OptionsAccessPersistenceProvider;
import org.plukh.options.impl.persistence.PropertiesPersistenceProvider;
import org.plukh.options.impl.persistence.TransientPersistenceProvider;
import org.plukh.options.interfaces.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OptionsFactoryTest {
    @Before
    public void setUp() {
        OptionsFactory.clearInstancesCache();
    }

    @Test
    public void testProxyInstantiation() throws InstantiationException, OptionsException {
        TestOptions o = OptionsFactory.getOptionsInstance(TestOptions.class);
        assertTrue("getOptionsInstance() should return an instance of provided interface", o != null);
    }

    @Test
    public void twoMethodsWithSameValueClassShouldCreateTwoDifferentOptions() throws OptionsException, InstantiationException {
        TestOptions options = OptionsFactory.getOptionsInstance(TestOptions.class);
        options.setInt(1);
        options.setInt2(2);
        options.setInt(3);
        assertEquals(3, options.getInt());
        assertEquals(2, options.getInt2());
    }

    @Test
    public void uninitializedPrimitiveGettersShouldNotThrowNullPointerException() throws OptionsException {
        PrimitiveGettersTestOptions options = OptionsFactory.getOptionsInstance(PrimitiveGettersTestOptions.class);

        try {
            assertEquals(false, options.getBooleanPrimitive());
            assertEquals(null, options.getBoolean());
            assertEquals(0, options.getIntPrimitive());
            assertEquals(null, options.getInt());
            assertEquals(0, options.getShortPrimitive());
            assertEquals(null, options.getShort());
            assertEquals(0, options.getLongPrimitive());
            assertEquals(null, options.getLong());
            assertEquals(0f, options.getFloatPrimitive());
            assertEquals(null, options.getFloat());
            assertEquals(0, options.getBytePrimitive());
            assertEquals(null, options.getByte());
            assertEquals(0f, options.getDoublePrimitive());
            assertEquals(null, options.getDouble());

        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("Primitive getter had thrown an NPE");
        }
    }

    @Test
    public void unsupportedOptionClassShouldThrowAnException() throws InstantiationException {
        testOptionsInstantiation(UnsupportedClassTestOptions.class, "void",
                "Expected exception not thrown for unsupported options class", "Class");
    }

    @Test
    public void missingSetterShouldThrowAnException() throws InstantiationException, NoSuchMethodException {
        testForInstantiationException(MissingSetterTestOptions.class, "getInt",
                "Expected exception not thrown for missing setter method");
    }

    @Test
    public void optionShouldHaveKeyFromAnnotationIfItsPresent() throws OptionsException, InstantiationException {
        AbstractOption option = getSingleOptionInstance(KeyUnnamedTestOptions.class);
        assertEquals("unnamed", option.getKey());
    }

    @Test
    public void optionShouldHaveKeyFromGetterNameIfAbsentInAnnotation() throws OptionsException, InstantiationException {
        AbstractOption option = getSingleOptionInstance(KeyNamedTestOptions.class);
        assertEquals("option.named", option.getKey());
    }

    @Test
    public void testDefaultValue() throws OptionsException, InstantiationException {
        AbstractOption option = getSingleOptionInstance(AnnotationDefaultTestOptions.class);
        assertEquals("5", option.getDefaultValue());
    }

    @Test
    public void testTransient() throws OptionsException, InstantiationException {
        AbstractOption option = getSingleOptionInstance(AnnotationTransientTestOptions.class);
        assertEquals(true, option.isTransient());
    }

    @Test
    public void testReadOnly() throws OptionsException, InstantiationException {
        AbstractOption option = getSingleOptionInstance(AnnotationReadOnlyTestOptions.class);
        assertEquals(true, option.isReadOnly());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEqualsCompareSameOptions() {
        TestOptions options = OptionsFactory.getOptionsInstance(TestOptions.class);
        assertTrue(options.equals(options));
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void testEqualsCompareWithNull() {
        TestOptions options = OptionsFactory.getOptionsInstance(TestOptions.class);
        assertFalse(options.equals(null));
    }

    @Test
    public void testEqualsCompareDifferentOptions() {
        TestOptions options = OptionsFactory.getOptionsInstance(TestOptions.class);
        AnnotationTransientTestOptions options2 = OptionsFactory.getOptionsInstance(AnnotationTransientTestOptions.class);
        assertFalse(options.equals(options2));
    }

    @Test
    public void optionsFactoryShouldCacheProxies() {
        TestOptions options = OptionsFactory.getOptionsInstance(TestOptions.class);
        TestOptions sameOptions = OptionsFactory.getOptionsInstance(TestOptions.class);
        assertSame(options, sameOptions);
        assertEquals(options, sameOptions);
    }

    @Test
    public void onlyAnnotatedGettersShouldCreateOptionInstances() throws OptionsException, InstantiationException {
        Collection<AbstractOption> options = getOptionsFromHandler(SingleAnnotatedOptionTestOptions.class);
        assertEquals(1, options.size());
    }

    @Test
    public void onlyBooleanShouldAllowIsInGetter() throws InstantiationException, NoSuchMethodException {
        testForInstantiationException(GetterWithIsPrefixTestOptions.class, "isInt",
                "Expected exception not thrown for non-boolean getter with \"is\" prefix");
    }

    @Test
    public void onlyOneOfIsOrGetPrefixShouldBePresentForBooleanGetter()
            throws InstantiationException, NoSuchMethodException {
        try {
            OptionsFactory.getOptionsInstance(BooleanGetterNamingTestOptions.class);
            fail("Expected exception not thrown for boolean getter with both \"is\" and \"get\" prefixes");
        } catch (OptionsInstantiationException e) {
            //Ordering of methods is inconsistent in Java 7, so we have to check for both methods
            Method methodIs = BooleanGetterNamingTestOptions.class.getMethod("isBoolean");
            Method methodGet = BooleanGetterNamingTestOptions.class.getMethod("getBoolean");
            assertTrue(e.getMessage().contains(methodIs.toString()) || e.getMessage().contains(methodGet.toString()));
        }
    }

    @Test
    public void readOnlyOptionWithSetterShouldThrowAnException() throws InstantiationException, NoSuchMethodException {
        testForInstantiationException(ReadOnlySetterTestOptions.class, "getInt",
                "Expected exception not thrown for setter of a read-only option");
    }

    @Test
    public void noAnnotatedGettersShouldThrowAnException() throws OptionsException, InstantiationException {
        testForInstantiationException(NoAnnotatedGettersTestOptions.class,
                "Expected exception not thrown for class with no annotated getters");
    }

    @Test
    public void defaultPersistenceProviderShouldBePropertiesProvider() throws OptionsException {
        testPersistenceProviderInstantiation(DefaultPersistenceProviderTestOptions.class, PropertiesPersistenceProvider.class);
    }

    @Test
    public void classNotAnnotatedWithPersistenceShouldGetTransientPersistenceProvider() throws OptionsException {
        testPersistenceProviderInstantiation(NoPersistenceAnnotationTestOptions.class, TransientPersistenceProvider.class);
    }

    @Test
    public void testCustomPersistenceProviderInstantiation() throws OptionsException {
        testPersistenceProviderInstantiation(CustomPersistenceProviderTestOptions.class, TestPersistenceProvider.class);
    }

    @Test
    public void allLegalCharactersShouldBeAllowedInKeys() {
        try {
            OptionsFactory.getOptionsInstance(LegalCharacterInKeyTestOptions.class);
        } catch (OptionsInstantiationException e) {
            fail("Unable to instantiate proxy for interface with legal characters in keys");
        }
    }

    @Test
    public void illegalCharactersInKeysShouldThrowAnException() throws InstantiationException {
        testOptionsInstantiation(IllegalCharacterInKeyTestOptions.class, "'#'",
                "Expected exception not thrown for class with illegal character in the key", "Class");
    }

    @Test
    public void firstIllegalCharacterInKeyNameShouldThrowAnException() throws InstantiationException {
        testOptionsInstantiation(FirstIllegalCharacterInKeyTestOptions.class, "'@'",
                "Expected exception not thrown for class with illegal character as first in the key", "Class");
    }

    @Test
    public void duplicateKeysShouldThrowAnException() throws InstantiationException {
        testOptionsInstantiation(DuplicateKeyTestOptions.class, null,
                "Options with duplicate keys should result in an exception", "Class");
    }

    @Test
    public void transientOptionOfNonSupportedClassShouldGetNonConvertibleOption() throws OptionsException,
            InstantiationException {
        AbstractOption option = getSingleOptionInstance(TransientUnsupportedClassTestOptions.class);
        assertTrue("Option of unsupported class should be backed by NonConvertibleOption", option instanceof NonConvertibleOption);
    }

    //Collection option tests

    @Test
    public void ifBothOptionAndCollectionOptionAnnotationsPresentAnExceptionMustBeThrown() throws NoSuchMethodException, InstantiationException {
        testForInstantiationException(BothOptionAndCollectionOptionTestOptions.class, "getInt",
                "If getter is annotated with both @Option and @CollectionOption, an exception must be thrown");
    }

    @Test
    public void collectionOptionsAreReadOnly() throws NoSuchMethodException, InstantiationException {
        testForInstantiationException(CollectionOptionIsReadOnly.class, "getQueue",
                "All collection options are read-only");
    }

    @Test
    public void optionsAnnotatesWithCollectionOptionMustImplementCollectionInterfaceInReturnType() throws
            NoSuchMethodException, InstantiationException {
        testOptionsInstantiation(IntAnnotatedWithCollectionOptionTestOptions.class, null,
                "Method annotated with @CollectionOption must return an instance of Collection interface", null);
    }

    @Test
    public void transientCollectionOptionShouldStillOnlyAllowCollectionsInReturnType() throws NoSuchMethodException,
            InstantiationException {
        testOptionsInstantiation(TransientCollectionOptionReturnsNonCollectionClassTestOptions.class, null,
                "Transient collection option should only allow Collections in return type", null);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testElementClassForCollectionOption() {
        ElementClassCollectionOptionTestOptions options = OptionsFactory.getOptionsInstance(ElementClassCollectionOptionTestOptions.class);
        Collection c = options.getCollection();
        c.add(1);
        assertEquals(1, c.size());
        try {
            c.add("a");
            fail("Should not be possible to add String element to Integer collection");
        } catch (IllegalArgumentException e) {
            //Ok
        }
    }

    //TODO: test default flags

    @Test
    public void collectionOptionsInstantiation() {
        CollectionOptionsTestOptions options = OptionsFactory.getOptionsInstance(CollectionOptionsTestOptions.class);

        //Assert backing implementations
        CollectionBackedOption c;
        c = (CollectionBackedOption) options.getQueue();
        assertEquals(OptionsQueue.DEFAULT_QUEUE_IMPLEMENTATION_CLASS, c.getBackingCollection().getClass());
        c = (CollectionBackedOption) options.getBlockingQueue();
        assertEquals(LinkedBlockingQueue.class, c.getBackingCollection().getClass());
    }

    @Test
    public void backingClassWithoutDefaultConstructorShouldThrowAnException() throws InstantiationException {
        testOptionsInstantiation(CollectionClassWithoutDefaultConstructorTestOptions.class,
                BlockingQueue.class.getName(),
                "Backing class without default constructor should throw a (meaningful) exception", "Class");
    }

    private AbstractOption getSingleOptionInstance(Class<? extends Options> optionsClass) throws OptionsException, InstantiationException {
        Collection<AbstractOption> oo = getOptionsFromHandler(optionsClass);
        return oo.iterator().next();
    }

    private Collection<AbstractOption> getOptionsFromHandler(Class<? extends Options> optionsClass) throws OptionsException, InstantiationException {
        Options options = OptionsFactory.getOptionsInstance(optionsClass);
        OptionsProxyHandler handler = (OptionsProxyHandler) Proxy.getInvocationHandler(options);

        //Set our fake class as persistence provider, then fake load to get actual option objects
        OptionsAccessPersistenceProvider pp = new OptionsAccessPersistenceProvider();
        handler.setPersistenceProvider(pp);
        handler.load(false);

        return pp.getOptions();
    }

    private void testForInstantiationException(Class<? extends Options> optionsClass, String methodName, String message)
            throws InstantiationException, NoSuchMethodException {
        Method method = optionsClass.getMethod(methodName);
        testOptionsInstantiation(optionsClass, method.toString(), message, "Method");
    }

    private void testForInstantiationException(Class<? extends Options> optionsClass, String message)
            throws InstantiationException {
        testOptionsInstantiation(optionsClass, optionsClass.getName(), message, "Class");
    }

    private void testOptionsInstantiation(Class<? extends Options> optionsClass, String lookForString, String message, String objectType)
            throws InstantiationException {
        try {
            OptionsFactory.getOptionsInstance(optionsClass);
            fail(message);
        } catch (OptionsInstantiationException e) {
            if (lookForString != null) assertTrue(objectType + " name must be present in the error message: " + e.getMessage(),
                    e.getMessage().contains(lookForString));
        }
    }

    private void testPersistenceProviderInstantiation(Class<? extends Options> optionsClass,
                                                      Class<? extends PersistenceProvider> providerClass) throws OptionsException {
        Options options = OptionsFactory.getOptionsInstance(optionsClass);
        OptionsProxyHandler handler = (OptionsProxyHandler) Proxy.getInvocationHandler(options);
        assertTrue(handler.getPersistenceProvider().getClass().equals(providerClass));
    }
}
