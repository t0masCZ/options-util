package org.plukh.options.impl.options;

import org.plukh.options.UnsupportedOptionClassException;
import org.plukh.options.impl.PrimitivesUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.text.MessageFormat.format;

public class OptionFactory {
    private static final Map<Class, Class<? extends AbstractOption>> OPTION_CLASSES = new HashMap<>();

    static {
        registerOptionClassForType(Boolean.class, BooleanOption.class);
        registerOptionClassForType(String.class, StringOption.class);
        registerOptionClassForType(Date.class, DateOption.class);
        registerOptionClassForType(Number.class, NumberOption.class);
    }

    /**
     * Registers a new option implementation supporting a specific value class. If implementation supports multiple
     * different classes, this method can be called multiple times.
     *
     * @param dataTypeClass class of option's value supported by this option implementation.
     * @param optionClass   class of the option's implementation.
     */
    static void registerOptionClassForType(Class dataTypeClass, Class<? extends AbstractOption> optionClass) {
        OPTION_CLASSES.put(dataTypeClass, optionClass);
    }


    /**
     * Returns an option instance supporting a specific class.
     *
     * @param clazz class for which to return an option.
     * @return an option instance supporting the parameter class.
     * @throws UnsupportedOptionClassException if {@code clazz} is not supported by any of registered option classes.
     * @throws IllegalAccessException,         InstantiationException if there is an error instantiating option class.
     */
    public static AbstractOption getOptionForClass(Class<?> clazz) throws UnsupportedOptionClassException, IllegalAccessException, InstantiationException {
        clazz = PrimitivesUtils.primitiveToWrapper(clazz);

        Class<? extends AbstractOption> optionClass = OPTION_CLASSES.get(clazz);

        if (optionClass == null) {
            for (Class<?> clazzFor : OPTION_CLASSES.keySet()) {
                if (clazzFor.isAssignableFrom(clazz)) {
                    optionClass = OPTION_CLASSES.get(clazzFor);
                }
            }
        }

        if (optionClass == null) {
            throw new UnsupportedOptionClassException(format("There is no supported option class for {0}", clazz.getName()));
        }
        try {
            return optionClass.newInstance();
        } catch (Exception e) {
            try {
                return optionClass.getConstructor(Class.class).newInstance(clazz);
            } catch (Exception e1) {
                throw e;
            }
        }
    }
}
