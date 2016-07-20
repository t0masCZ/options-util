package org.plukh.options.impl.options;

import org.apache.commons.lang3.ClassUtils;
import org.plukh.options.ParseException;
import org.plukh.options.impl.ConverterFromString;

import java.util.function.Function;


public class NumberOption extends AbstractOption {

    private final Class<?> clazz;
    private final Function<String, Object> converter;

    public NumberOption(Class<?> clazz) {
        this.clazz = ClassUtils.primitiveToWrapper(clazz);
        converter = ConverterFromString.getConverter(clazz);
    }

    @Override
    public Object convertStringToValue(String s) throws ParseException {
        if (s == null || s.isEmpty()) return null;
        try {
            return converter.apply(s);
        } catch (NumberFormatException e) {
            throw new ParseException("Error converting string: " + s + " to integer value", e);
        }
    }

    @Override
    public String convertValueToString(Object o) {
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    @Override
    public void setValue(Object value) {
        if (value != null && !(clazz.isAssignableFrom(value.getClass()))) {
            throw new IllegalArgumentException("Can only set value to Integer");
        }
        super.setValue(value);
    }
}
