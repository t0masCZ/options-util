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

package org.plukh.options.impl.persistence;

import java.io.*;

public class FileUtils {
    private static final int BUF_SIZE = 2048;

    public static void copyFile(File source, File destination) throws IOException {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(destination));

            byte[] buf = new byte[BUF_SIZE];
            int c;
            while ((c = in.read(buf)) != -1) {
                out.write(buf, 0, c);
            }
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }
}
