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

package org.plukh.options;

import java.util.Collection;
import java.util.Collections;

//TODO: Document
//TODO: write tests
public class ConversionException extends OptionsException {
    private Collection<String> conversionErrors;

    public ConversionException(String message, Collection<String> conversionErrors) {
        super(message);
        this.conversionErrors = Collections.unmodifiableCollection(conversionErrors);
    }

    public ConversionException(String message, Throwable cause, Collection<String> conversionErrors) {
        super(message, cause);
        this.conversionErrors = Collections.unmodifiableCollection(conversionErrors);
    }

    public Collection<String> getConversionErrors() {
        return conversionErrors;
    }
}
