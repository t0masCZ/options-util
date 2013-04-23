/*
 *
 *  * Copyright 2012, 2013 by Victor Denisov (vdenisov@plukh.org).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.plukh.options.impl.options;

import org.plukh.options.ParseException;

public class StringOption extends AbstractOption {

    public StringOption() {
    }

    public StringOption(String key, String value) {
        super(key, value);
        this.valueToStringConverted = true;
        this.stringToValueConverted = true;
    }

    public StringOption(String key) {
        super(key);
    }

    @Override
    public Object convertStringToValue(String s) throws ParseException {
        return s;
    }

    @Override
    public String convertValueToString(Object o) {
        return (String) o;
    }

    @Override
    public void setValue(Object value) {
        if (value != null && !(value instanceof String)) throw new IllegalArgumentException("Can only set value to String");
        super.setValue(value);
    }
}
