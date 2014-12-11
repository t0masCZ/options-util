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
 * This exception is thrown if a particular Java class is not supported (yet) as a valid class for option properties. See
 * {@link org.plukh.options.impl.options} package description for a list of valid option classes.
 *
 * @see org.plukh.options.impl.options
 */
public class UnsupportedOptionClassException extends Exception {
    public UnsupportedOptionClassException() {
    }

    public UnsupportedOptionClassException(String message) {
        super(message);
    }
}
