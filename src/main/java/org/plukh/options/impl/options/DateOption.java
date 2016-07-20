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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOption extends AbstractOption {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

    @Override
    public Object convertStringToValue(String s) throws ParseException {
        if (s == null || s.isEmpty()) return null;
        try {
            return sdf.parse(s);
        } catch (java.text.ParseException e) {
            throw new ParseException("Error parsing date string: " + s, e);
        }
    }

    @Override
    public String convertValueToString(Object o) {
        if (o == null) return null;
        return sdf.format((Date)o);
    }

    @Override
    public void setValue(Object value) {
        if (value != null && !(value instanceof Date)) throw new IllegalArgumentException("Can only set value to java.util.Date");
        super.setValue(value);
    }
}
