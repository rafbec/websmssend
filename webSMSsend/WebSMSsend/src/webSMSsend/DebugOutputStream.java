/*
 *
 *
    Copyright 2010 Christian Morlok --- cmorlok.de

    This file is part of WebSMSsend.

    WebSMSsend is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WebSMSsend is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WebSMSsend.  If not, see <http://www.gnu.org/licenses/>.

 *
 *
 */

package webSMSsend;

import java.io.IOException;
import java.io.OutputStream;

public class DebugOutputStream extends OutputStream {
    webSMSsend GUI;

    public DebugOutputStream(webSMSsend parent) {
        super();
        GUI = parent;
    }

    public void write(int b) throws IOException {
        char[] data = {(char) b};
        GUI.getDebug().insert(data, 0, 1, GUI.getDebug().size());
    }

    public void write(byte[] b, int off, int len) throws IOException {
        String s = new String(b, off, len, "UTF-8");
        GUI.getDebug().insert(s.toCharArray(), 0, len, GUI.getDebug().size());
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
}
