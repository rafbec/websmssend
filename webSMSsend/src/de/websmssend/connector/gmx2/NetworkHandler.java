/*
 *
 *
Copyright 2012 redrocketracoon@googlemail.com
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
package de.websmssend.connector.gmx2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connector;
import javax.microedition.pki.CertificateException;
import javax.microedition.io.HttpsConnection;
import de.websmssend.IApp;
import de.websmssend.connector.base.BasicAuth;

public class NetworkHandler {

    
    // taken from ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT
    // the first 128 characters are the same as ASCII
    // undefined chars are replaced by SPACE (0x0020).
    private char[] cp1252map = "\u20AC\u0020\u201A\u0192\u201E\u2026\u2020\u2021\u02C6\u2030\u0160\u2039\u0152\u0020\u017D\u0020\u0020\u2018\u2019\u201C\u201D\u2022\u2013\u2014\u02DC\u2122\u0161\u203A\u0153\u0020\u017E\u0178\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF".toCharArray();
    private boolean cp1252internal;
    private String username;
    private String password;
    private String content = "";
    private String cookie = "";
    private String lasturl = "";
    private int responseCode;
    private IApp GUI;
    private GMX parent;

    public String getCookie() {
        return cookie;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getContent() {
        return content;
    }

    public String getLastUrl() {
        return lasturl;
    }
    
    public int getResponseCode(){
        return responseCode;
    }

    public void setContent(String content2) {
        content = content2;
    }

    public void setCookie(String cookie2) {
        cookie = cookie2;
    }

    public NetworkHandler(String usernameIn, String passwordIn, IApp Debugger, GMX parent) {
        username = usernameIn;
        password = passwordIn;
        GUI = Debugger;
        this.parent = parent;

        // test if jvm supports cp1252 encoding
        cp1252internal = true;
        try {
            byte[] testByte = {(byte) 0x80};
            String testString = new String(testByte, "Cp1252");
        } catch (UnsupportedEncodingException ex) {
            GUI.debug(ex.toString());
            GUI.debug("JVM doesn't support Cp1252 encoding, falling  back to internal decoding.");
            cp1252internal = false;
        } catch (Exception ex) {
            GUI.debug("Exception at NetworkHandler: " + ex.toString());
        }
    }

    public String getSendPostRequest(String recipient, String message, int SENDERMODE) throws Exception {
        GUI.debug("getSendPostRequest( )");

        StringBuffer postData = new StringBuffer();


        return postData.toString();
    }

    public int httpHandler(String requestMode, String url, String host, String postReq, boolean loadContent) throws CertificateException, IOException, Exception {
        GUI.debug("httpHandler( " + requestMode + ", " + url + ", " + host + ", " + postReq + " , " + loadContent + " , "  + " )");
        byte[] data;
        HttpsConnection con = null;
        content = "";
        StringBuffer out = new StringBuffer();
        InputStream is = null;
        OutputStream os = null;
        try {
            try {
                con = (HttpsConnection) Connector.open(url, Connector.READ_WRITE);
            } catch (Exception ex) {
                GUI.debug("Connector.open failed, exception: " + ex.toString());
                throw ex;
            }

            try {
                //firefox browser identification
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; de-DE) AppleWebKit/533.19.4 (KHTML, like Gecko) AdobeAIR/3.1");
                con.setRequestProperty("Host", host);
                con.setRequestProperty("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
                con.setRequestProperty("Accept-Language", "de-DE,de");
                con.setRequestProperty("Accept-Encoding", "identity");
                con.setRequestProperty("Accept-Charset", "ISO-8859-15,utf-8;q=0.7,*;q=0.7");
                con.setRequestProperty("Keep-Alive", "300");
                con.setRequestProperty("cookie", cookie);
                con.setRequestProperty("Authorization", "Basic "+ BasicAuth.encode(username, password));
                

                if (requestMode.equals("POST")) {

                    con.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                    con.setRequestMethod(HttpsConnection.POST);
                    con.setRequestProperty("Content-Length", "" + postReq.getBytes("UTF-8").length);
                    try {
                        os = con.openOutputStream();
                        os.write(postReq.getBytes("UTF-8"));
                    } catch (IOException ex) {
                        GUI.debug("writing to output stream failed, exception: " + ex.toString());
                        throw ex;
                    }

                } else {
                    con.setRequestMethod(HttpsConnection.GET);
                }
            } catch (IOException ex) {
                GUI.debug("con.setRequestProperty failed, exception: " + ex.toString());
                throw ex;
            }

            //workaround
            try {
                for (int i = 0; con.getHeaderField(i) != null; i++) {
                    if (con.getHeaderFieldKey(i) != null) {
                        if (con.getHeaderFieldKey(i).toLowerCase().equals("set-cookie")) {
                            if (cookie.length() > 0) {
                                cookie = cookie + ";" + con.getHeaderField(i);
                            } else {
                                cookie = con.getHeaderField(i);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                GUI.debug("Exception while trying to add cookies");
            }

            lasturl = con.getURL();
            //load content
            if (loadContent) {
                try {
                    is = con.openDataInputStream();
                } catch (IOException ex) {
                    GUI.debug("con.openDataInputStream failed, exception: " + ex.toString());
                    throw ex;
                }
                GUI.debug("HTTP Response Code: " + con.getResponseCode());
                responseCode = con.getResponseCode();
                if (con.getResponseCode() == HttpsConnection.HTTP_OK || con.getResponseCode() == HttpsConnection.HTTP_ACCEPTED) {

                    // Get length and process data
                    try {
                        int len = (int) con.getLength();
                        if (len > 0) {
                            data = new byte[len];
                            out = new StringBuffer(len);
                            //save whole page as string
                            for (int n; (n = is.read(data)) != -1;) {
                                out.append(byteArrayToString(data, 0, n));
                            }
                            data = null;
                            content = out.toString();
                        } else {
                            int chunkSize = 512;
                            int offset = 0;
                            int readLength = 0;
                            byte[] chunk = new byte[chunkSize];
                            byte[] entireStream = new byte[chunkSize];
                            byte[] temp = null;
                            while ((readLength = is.read(chunk, 0, chunkSize)) != -1) {
                                temp = entireStream;
                                entireStream = new byte[offset + readLength];
                                System.arraycopy(temp, 0, entireStream, 0, offset);
                                System.arraycopy(chunk, 0, entireStream, offset, readLength);
                                offset += readLength;
                            }
                            content = byteArrayToString(entireStream, 0, entireStream.length);
                        }
                        GUI.debug("Host: " + con.getHost());
                        GUI.debug("URL: " + con.getURL());
                        GUI.debug(content); //write html content to Debug Window
                    } catch (IOException ex) {
                        GUI.debug("reading input stream failed, exception: " + ex.toString());
                        throw ex;
                    }

                } else if (con.getResponseCode() == HttpsConnection.HTTP_MOVED_TEMP) {
                    String newUrl = con.getHeaderField("Location");
                    System.out.println(newUrl);
                    if (is != null) {
                        is.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                    httpHandler("GET", newUrl, host, "", true);

                } else {
                    throw new Exception("Connection failed, response code: " + con.getResponseCode());
                }

            }
        } catch (Exception ex) {
            throw ex;
        } catch (Throwable t) {
            throw new Exception("Throwable: " + t.toString() + t.getMessage());
        } finally {
            if (is != null) {
                is.close();
            }
            if (con != null) {
                con.close();
            }
            if (os != null) {
                os.close();
            }
        }
        return 0;
    }

    private String byteArrayToString(byte[] bytes, int off, int len) throws UnsupportedEncodingException {
        String ret;
        if (cp1252internal) {
            ret = new String(bytes, off, len, "Cp1252");
        } else {
            char[] chars = new char[len];
            for (int i = off; i < len + off; ++i) {
                byte b = bytes[i];
                chars[i] = (b >= 0) ? (char) b : cp1252map[b + 128];
            }
            ret = new String(chars);
        }
        //System.out.println("decoded: " + ret);
        return ret;
    }
}
