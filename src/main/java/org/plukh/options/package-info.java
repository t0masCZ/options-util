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

/**
 * Collection of classes for creation of application-specific option sets.
 * To create and use a custom set of options, follow this steps:
 * <p/>
 * <ol>
 * <li>Extend {@link org.plukh.options.Options} interface and declare a set of getters/setters specific for options in an application.</li>
 * <li>Annotate the interface with {@link org.plukh.options.Persistence @Persistence} annotation to specify the type of persistence
 * (backing store) to be used for long-term options storage.</li>
 * <li>Annotate each option getter with {@link org.plukh.options.Option @Option} annotation.</li>
 * <li>Call {@link org.plukh.options.OptionsFactory#createOptionsInstance(Class)} method to instantiate a proxy object implementing the
 * interface declared in steps 1-3.</li>
 * <li>Optionally, call {@link org.plukh.options.Options#configurePersistenceProvider(PersistenceConfig)} to configure persistence provider.
 * <li>Access option getters and setters as usual</li>
 * </ol>
 * <p/>
 * Multiple options interfaces can safely co-exist inside a single application; each one can be configured differently.
 * Such interfaces are fully independent - options with the same name in different interfaces are not related to each
 * other in any way.
 */

package org.plukh.options;