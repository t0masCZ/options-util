/*
 *
 *  * Copyright 2012 by Victor Denisov (vdenisov@redline.ru).
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

import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FileUtilsTest {
    @Test
    public void testCopyFile() throws Exception {
        File source = null;
        File destination = null;
        OutputStream out = null;
        InputStream in = null;

        try {
            //Create source and destination files
            source = File.createTempFile("futest", "");
            destination = new File(source.getAbsolutePath() + ".copy");

            //Create source file contents
            out = new BufferedOutputStream(new FileOutputStream(source));

            byte[] buf = new byte[3072];
            Arrays.fill(buf, (byte) 0x0F);

            out.write(buf);
            out.close();

            //Do copy
            FileUtils.copyFile(source, destination);

            //Try reading and comparing files
            byte[] copyBuf = new byte[3072];
            in = new BufferedInputStream(new FileInputStream(destination));
            int pos = 0;
            int c;
            while ((c = in.read(copyBuf, pos, copyBuf.length - pos)) != -1 && pos < 3072) {
                pos += c;
            }

            assertEquals(3072, pos);
            assertTrue(Arrays.equals(buf, copyBuf));

        } finally {
            if (out != null) out.close();
            if (in != null) in.close();

            if (source != null) //noinspection ResultOfMethodCallIgnored
                source.delete();
            if (destination != null) //noinspection ResultOfMethodCallIgnored
                destination.delete();

        }
    }
}
