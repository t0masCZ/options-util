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

public interface PrimitiveGettersTestOptions extends Options  {
    @Option(readOnly = true)
    int getIntPrimitive();
    @Option(readOnly = true)
    Integer getInt();
    @Option(readOnly = true)
    boolean getBooleanPrimitive();
    @Option(readOnly = true)
    Boolean getBoolean();
    @Option(readOnly = true)
    short getShortPrimitive();
    @Option(readOnly = true)
    Short getShort();
    @Option(readOnly = true)
    long getLongPrimitive();
    @Option(readOnly = true)
    Long getLong();
    @Option(readOnly = true)
    float getFloatPrimitive();
    @Option(readOnly = true)
    Float getFloat();
    @Option(readOnly = true)
    byte getBytePrimitive();
    @Option(readOnly = true)
    Byte getByte();
    @Option(readOnly = true)
    float getDoublePrimitive();
    @Option(readOnly = true)
    Float getDouble();
}
