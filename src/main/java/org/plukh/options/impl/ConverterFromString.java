package org.plukh.options.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by t0mas on 20.7.16.
 */
public class ConverterFromString {
    private final static Map<Class<?>, Function<String, Object>> classToConverter = new HashMap<>();

    static  {
        classToConverter.put(Boolean.class, Boolean::parseBoolean);
        classToConverter.put(Boolean.TYPE, Boolean::parseBoolean);
        classToConverter.put(Byte.class, Byte::parseByte);
        classToConverter.put(Byte.TYPE, Byte::parseByte);
        classToConverter.put(Short.class, Short::parseShort);
        classToConverter.put(Short.TYPE, Short::parseShort);
        classToConverter.put(Integer.class, Integer::parseInt);
        classToConverter.put(Integer.TYPE, Integer::parseInt);
        classToConverter.put(Long.class, Long::parseLong);
        classToConverter.put(Long.TYPE, Long::parseLong);
        classToConverter.put(Float.class, Float::parseFloat);
        classToConverter.put(Float.TYPE, Float::parseFloat);
        classToConverter.put(Double.class, Double::parseDouble);
        classToConverter.put(Double.TYPE, Double::parseDouble);
        classToConverter.put(String.class, s -> s);
    }

    public static Function<String, Object> getConverter(Class<?> paramTypeToCreate) {
        return classToConverter.get(paramTypeToCreate);
    }
}
