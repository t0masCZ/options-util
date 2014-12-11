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

package org.plukh.options.interfaces;

import org.plukh.options.Option;
import org.plukh.options.Options;

public interface LegalCharacterInKeyTestOptions extends Options {
    @Option(key = "-")
    int getWithMinus();
    void setWithMinus(int value);

    @Option(key = "_")
    int getWithUnderscore();
    void setWithUnderscore(int value);

    @Option(key = ".")
    int getWithDot();
    void setWithDot(int value);

    @Option(key = "123abcABC.efg_dh-OP")
    int getAllAllowed();
    void setAllAllowed(int value);
}
