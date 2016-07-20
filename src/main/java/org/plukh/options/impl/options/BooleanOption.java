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

public class BooleanOption extends AbstractOption {
    private static final String[] TRUE_STRINGS = {"yes", "true", "1"};
    private static final String[] FALSE_STRINGS = {"no", "false", "0"};

    @Override
    public Object convertStringToValue(String s) throws ParseException {
        if (s == null || s.isEmpty()) return null;
        for (String yes : TRUE_STRINGS) {
            if (yes.equalsIgnoreCase(s)) return Boolean.TRUE;
        }
        for (String no : FALSE_STRINGS) {
            if (no.equalsIgnoreCase(s)) return Boolean.FALSE;
        }
        throw new ParseException("Error parsing string: " + s);
    }

    @Override
    public String convertValueToString(Object o) {
        if (o == null) return null;
        return (Boolean) o ? "true" : "false";
    }

    @Override
    public void setValue(Object value) {
        if (value != null && !(value instanceof Boolean)) throw new IllegalArgumentException("Can only set value to Boolean");
        super.setValue(value);
    }
}

