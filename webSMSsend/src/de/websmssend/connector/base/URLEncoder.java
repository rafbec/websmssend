/*
 *
 *
    Copyright 2009 Max Hänze --- maximum.blogsite.org

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

package de.websmssend.connector.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

    public class URLEncoder {

        public static String encode(String s) throws IOException {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            DataOutputStream dOut = new DataOutputStream(bOut);
            StringBuffer ret = new StringBuffer(); //return value
            dOut.writeUTF(s);
            ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
            bIn.read();
            bIn.read();
            int c = bIn.read();
            while (c >= 0) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '*' || c == '_') {
                    ret.append((char) c);
                } else if (c == ' ') {
                    ret.append('+');
                } else {
                    if (c < 128 ) {
                        appendHex(c, ret);
                    } else if (c < 224) {
                        appendHex(c, ret);
                        appendHex(bIn.read(), ret);
                    } else if (c < 240) {
                        appendHex(c, ret);
                        appendHex(bIn.read(), ret);
                        appendHex(bIn.read(), ret);
                    }
                }
                c = bIn.read();
            }
            return ret.toString();
        }
        private static void appendHex(int arg0, StringBuffer buff) {
            buff.append('%');
            if (arg0 < 16) {
                buff.append('0');
            }
            buff.append(Integer.toHexString(arg0));
        }
}
