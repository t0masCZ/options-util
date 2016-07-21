package org.plukh.options.impl.options;

import org.plukh.options.ParseException;
import org.plukh.options.impl.PrimitivesUtils;

import java.util.function.Function;

import static java.text.MessageFormat.format;


public class NumberOption extends AbstractOption {

    private final Function<String, Object> converter;

    public NumberOption(Class<?> clazz) {
        super(PrimitivesUtils.primitiveToWrapper(clazz));
        converter = PrimitivesUtils.getStringConverter(clazz);
    }

    @Override
    public Object convertStringToValue(String s) throws ParseException {
        if (s == null || s.isEmpty()) return null;
        try {
            return converter.apply(s);
        } catch (NumberFormatException e) {
            throw new ParseException(format("Error converting string: {0} to {1} value", s, getOptionClass().getSimpleName()), e);
        }
    }
}
