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

import org.plukh.options.*;
import org.plukh.options.impl.persistence.TransientPersistenceProvider;

import java.util.Date;
import java.util.Queue;

@Persistence(TransientPersistenceProvider.class)
public interface TestOptions extends Options {
    @Option(key = "int")
    int getInt();
    void setInt(int value);

    @Option(key = "int2")
    int getInt2();
    void setInt2(int value);

    @Option(key = "boolean")
    boolean getBoolean();
    void setBoolean(boolean value);

    @Option(key = "option.string")
    String getString();
    void setString(String value);

    @Option(key = "option.date")
    Date getDate();
    void setDate(Date date);

    @CollectionOption(key = "option.queue")
    Queue getQueue();

    @Option
    int getUnnamedOption();
    void setUnnamedOption(int value);

}
