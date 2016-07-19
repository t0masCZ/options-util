package org.plukh.options.impl.options;

import org.plukh.options.ParseException;

/**
 * Created by t0mas on 18.7.16.
 */
public class DoubleOption extends AbstractOption {
    @Override
    public Object convertStringToValue(String s) throws ParseException {
        if (s == null || s.isEmpty()) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new ParseException("Error converting string: " + s + " to double value", e);
        }
    }

    @Override
    public String convertValueToString(Object o) {
        if (o == null) return null;
        return o.toString();
    }

    @Override
    public void setValue(Object value) {
        if (value != null && !(value instanceof Double)) throw new IllegalArgumentException("Can only set value to Double");
        super.setValue(value);
    }
}