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

package org.plukh.options;

/**
 * {@code ParseException} is thrown when string-to-value conversion fails for an instance of the option class (see
 * {@link org.plukh.options.impl.options.AbstractOption#convertStringToValue(String)}). This exception is most often encountered
 * as a cause of {@code OptionsException} when invoking {@link org.plukh.options.Options#load(boolean)} method from
 * from an application-specific interface instance.
 */
public class ParseException extends RuntimeException {
    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
