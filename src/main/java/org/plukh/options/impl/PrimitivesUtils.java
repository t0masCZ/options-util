package org.plukh.options.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PrimitivesUtils {
    private final static Map<Class<?>, Function<String, Object>> CLASS_TO_CONVERTER = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();
    private static final Map<Class, Object> DEFAULT_PRIMITIVE_VALUES = new HashMap<>();

    static {
        CLASS_TO_CONVERTER.put(Boolean.class, Boolean::parseBoolean);
        CLASS_TO_CONVERTER.put(Boolean.TYPE, Boolean::parseBoolean);
        CLASS_TO_CONVERTER.put(Byte.class, Byte::parseByte);
        CLASS_TO_CONVERTER.put(Byte.TYPE, Byte::parseByte);
        CLASS_TO_CONVERTER.put(Short.class, Short::parseShort);
        CLASS_TO_CONVERTER.put(Short.TYPE, Short::parseShort);
        CLASS_TO_CONVERTER.put(Integer.class, Integer::parseInt);
        CLASS_TO_CONVERTER.put(Integer.TYPE, Integer::parseInt);
        CLASS_TO_CONVERTER.put(Long.class, Long::parseLong);
        CLASS_TO_CONVERTER.put(Long.TYPE, Long::parseLong);
        CLASS_TO_CONVERTER.put(Float.class, Float::parseFloat);
        CLASS_TO_CONVERTER.put(Float.TYPE, Float::parseFloat);
        CLASS_TO_CONVERTER.put(Double.class, Double::parseDouble);
        CLASS_TO_CONVERTER.put(Double.TYPE, Double::parseDouble);

        PRIMITIVES_TO_WRAPPERS.put(Boolean.TYPE, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(Byte.TYPE, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(Character.TYPE, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(Double.TYPE, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(Float.TYPE, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(Integer.TYPE, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(Long.TYPE, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(Short.TYPE, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(Void.TYPE, Void.TYPE);

        DEFAULT_PRIMITIVE_VALUES.put(byte.class, (byte) 0);
        DEFAULT_PRIMITIVE_VALUES.put(short.class, (short) 0);
        DEFAULT_PRIMITIVE_VALUES.put(int.class, 0);
        DEFAULT_PRIMITIVE_VALUES.put(long.class, 0L);
        DEFAULT_PRIMITIVE_VALUES.put(float.class, 0.0f);
        DEFAULT_PRIMITIVE_VALUES.put(double.class, 0.0d);
        DEFAULT_PRIMITIVE_VALUES.put(boolean.class, Boolean.FALSE);
        DEFAULT_PRIMITIVE_VALUES.put(char.class, '\u0000');
    }

    public static Function<String, Object> getStringConverter(Class<?> paramTypeToCreate) {
        return CLASS_TO_CONVERTER.get(paramTypeToCreate);
    }

    public static Class<?> primitiveToWrapper(Class<?> cls) {
        Class convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = (Class)PRIMITIVES_TO_WRAPPERS.get(cls);
        }
        return convertedClass;
    }

    static Object getDefaultValue(Class<?> cls) {
        return DEFAULT_PRIMITIVE_VALUES.get(cls);
    }
}
