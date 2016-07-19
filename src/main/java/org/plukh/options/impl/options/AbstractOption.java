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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract superclass for all other options classes. Option classes for specific option types must extend this
 * class, implementing two abstract methods {@link #convertStringToValue(String)} and {@link #convertValueToString(Object)}.
 * Additionally, it is recommended that implementations override {@link #setValue(Object)} method to ensure that the object
 * passed is one of the applicable classes (see existing implementations for details), but this is not a strict requirement.
 * <p/>
 * Option classes extending {@code AbstractOption} should be registered via {@link #registerOptionClassForType(Class, Class)}
 * calls for all supported value classes.
 */
public abstract class AbstractOption {
    protected static final Map<Class, Class<? extends AbstractOption>> OPTION_CLASSES = new HashMap<Class, Class<? extends AbstractOption>>();
    protected static final Map<Class<? extends Collection>, Class<? extends CollectionBackedOption>> COLLECTION_CLASSES = new HashMap
            <Class<? extends Collection>, Class<? extends CollectionBackedOption>>();

    protected String key;
    protected String stringValue;
    protected Object value;

    protected String defaultValue;
    protected boolean isTransient;
    protected boolean readOnly;

    protected boolean defaultValueSet;

    protected boolean stringToValueConverted;
    protected boolean valueToStringConverted;

    public AbstractOption() {
    }

    public AbstractOption(String key, String stringValue) {
        this.key = key;
        this.stringValue = stringValue;
        this.stringToValueConverted = false;
        this.valueToStringConverted = true;
    }

    public AbstractOption(String key) {
        this.key = key;
    }

    /**
     * Converts string representation of the option into an actual object. This method takes a string respresentation
     * of the object, parses it and returns an object instance of the class implemented by the subclass in question.
     * Implementations should attemt to be lenient in their parsing, overlooking, to the extent possible, minor parsing
     * errors and inconsistencies, as it's expected that string values will be edited by end-users in a backing store
     * - such as a .properties file or a database - directly.
     *
     * @param s String representation to convert
     * @return Object instance of the options class implemented by the subclass
     * @throws ParseException if the string value contains errors which make parsing of the value impossible
     */
    public abstract Object convertStringToValue(String s) throws ParseException;

    /**
     * Converts an object to its string representation. Takes an object of a type supported by the option class and
     * returns a human-readable string representation of it. Converted strings should be easy to understand and to edit
     * by end-users (who are generally not aware of exact implementation details). It also should allow to fully restore
     * an object's state when the string will later be read from a store (for example, maintaining the exact order of
     * elements in a collection, etc).
     *
     * @param o an object to be converted to a string
     * @return string representation of the object
     */
    public abstract String convertValueToString(Object o);

    /**
     * Attemts to convert a string to value by calling {@link #convertStringToValue(String)}. Unlike
     * {@link #convertStringToValue(String)}, this method doesn't throw an exception if conversion is unsuccessful. Useful
     * for validating user input before assigning the string value to an option.
     * @return {@code true} if conversion is successful, {@code false} otherwise.
     */
    public boolean tryStringToValueConversion() {
        try {
            getValue();
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * Returns the value of an option in object form. If the value hadn't been converted from string previously,
     * it'll be converted during this call.
     * @return object representation of the option's value.
     * @throws ParseException when the value hadn't been converted from string before, and conversion fails.
     */
    public Object getValue() throws ParseException {
        if (stringToValueConverted) return value;

        if (!valueToStringConverted || stringValue == null) return null;

        value = convertStringToValue(stringValue);
        stringToValueConverted = true;

        return value;
    }

    /**
     * Returns the value of an option in string form (as converted by {@link #convertValueToString(Object)}). If the value
     * hadn't been converted to string before, it'll be converted during this call.
     * @return string representation of the option's value.
     */
    public String getStringValue() {
        if (valueToStringConverted) return stringValue;

        if (!stringToValueConverted || value == null) return null;

        stringValue = convertValueToString(value);
        valueToStringConverted = true;

        return stringValue;
    }

    /**
     * Returns a key associated with this option. The key should be unique between all options in a given application-specific
     * options interface.
     * @return key associated with this option.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets a key to be associated with this option. The key should be unique between all options in a given application-specific
     * options interface.
     * @param key key to associate with this option.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets a value of this option in string form.
     * @param stringValue string representation of the option's value, compatible with {@link #convertStringToValue(String)}.
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
        value = null;
        valueToStringConverted = true;
        stringToValueConverted = false;
    }

    /**
     * Resets the option to its default value.
     */
    public void resetToDefaultValue() {
        setStringValue(defaultValue);
    }

    /**
     * Returns default value of this option.
     * @return default value.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets a default value of this option. Note that setting default value to an option doesn't change the current
     * value, if any.
     * @param defaultValue default value to set, in string form.
     */
    public void setDefaultValue(String defaultValue) {
        defaultValueSet = true;
        if (!stringToValueConverted && !valueToStringConverted) setStringValue(defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Sets a value of this option, in object form. Value must be of one of the classes supported by this option. It is
     * recommended that implementations override this method to make an appropriate check.
     * @param value value to set.
     */
    public void setValue(Object value) {
        this.value = value;
        stringValue = null;
        valueToStringConverted = false;
        stringToValueConverted = true;
    }

    /**
     * Returns {@code true} if the option is transient. Transient options must not be persisted to the backing store.
     * @return {@code true} if the option is transient, false otherwise.
     */
    public boolean isTransient() {
        return isTransient;
    }

    /**
     * Sets transient flag of this option. Transient options must not be persisted to the backing store.
     * @param aTransient {@code true} if the option should be flagged as transient, false otherwise.
     */
    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    /**
     * Returns {@code true} if the option is read-only. Read-only options must not be modified when read from a backing
     * store.
     * @return {@code true} if the option is read-only, false otherwise.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets read-only flag of this option. Read-only options must not be modified when read from a backing
     * store.
     * @param readOnly {@code true} if the option should be flagged as read-only, {@code false} otherwise.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Returns an option instance supporting a specific class.
     * @param clazz class for which to return an option.
     * @return an option instance supporting the parameter class.
     * @throws UnsupportedOptionClassException if {@code clazz} is not supported by any of registered option classes.
     * @throws IllegalAccessException, InstantiationException if there is an error instantiating option class.
     */
    public static AbstractOption getOptionForClass(Class clazz) throws UnsupportedOptionClassException, IllegalAccessException, InstantiationException {
        Class<? extends AbstractOption> optionClass = OPTION_CLASSES.get(clazz);

        /*
        if (optionClass == null) {
            for (Class clazzFor: OPTION_CLASSES.keySet()) {
                if (clazzFor.isAssignableFrom(clazz)) {
                    optionClass = OPTION_CLASSES.get(clazzFor);
                }
            }
        }*/

        if (optionClass == null) throw new UnsupportedOptionClassException("There is no supported option class for " +
                clazz.getName());
        return optionClass.newInstance();
    }

    public static CollectionOption getCollectionOption(Class elementClass, Class collectionClass) throws UnsupportedOptionClassException {
        Class optionCollectionClass = COLLECTION_CLASSES.get(collectionClass);
        if (optionCollectionClass == null)
            throw new UnsupportedOptionClassException("There is no supported options collection class for " +
                    collectionClass.getName());
        return collectionClass.isInterface() ? new CollectionOption(elementClass, optionCollectionClass) :
                                               new CollectionOption(collectionClass, elementClass, optionCollectionClass, collectionClass);
    }

    public static CollectionOption getCollectionOption(Class elementClass, Class collectionClass, Class backingClass) throws UnsupportedOptionClassException {
        Class optionCollectionClass = COLLECTION_CLASSES.get(collectionClass);
        if (optionCollectionClass == null)
            throw new UnsupportedOptionClassException("There is no supported options collection class for " +
                    collectionClass.getName());
        return new CollectionOption(collectionClass, elementClass, optionCollectionClass, backingClass);
    }

    /**
     * Registers a new option implementation supporting a specific value class. If implementation supports multiple
     * different classes, this method can be called multiple times.
     * @param dataTypeClass class of option's value supported by this option implementation.
     * @param optionClass class of the option's implementation.
     */
    public static void registerOptionClassForType(Class dataTypeClass, Class<? extends AbstractOption> optionClass) {
        OPTION_CLASSES.put(dataTypeClass, optionClass);
    }

    public static void registerCollectionOptionClassForType(Class <? extends Collection> collectionClass,
                                                            Class<? extends CollectionBackedOption> optionCollectionClass) {
        COLLECTION_CLASSES.put(collectionClass, optionCollectionClass);
    }

    public boolean isDefaultValueSet() {
        return defaultValueSet;
    }

    protected boolean isValidOptionClass(Class clazz) {
        return OPTION_CLASSES.containsKey(clazz);
    }

    @Override
    public String toString() {
        return "Option: " + key +
                "=" + value +
                " (" + (stringToValueConverted ? "converted S2V" : "not converted S2V") + ")" +
                " ~ " + stringValue +
                "(" + (valueToStringConverted ? "converted V2S" : "not converted V2S") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractOption)) return false;

        AbstractOption option = (AbstractOption) o;
        if (key != null ? !key.equals(option.key) : option.key != null) return false;

        if (stringToValueConverted) return value == null ? option.value == null : value.equals(option.value);

        if (valueToStringConverted)
            return stringValue == null ? option.stringValue == null : stringValue.equals(option.stringValue);

        return (value == null && stringValue == null && option.value == null && option.stringValue == null);
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
