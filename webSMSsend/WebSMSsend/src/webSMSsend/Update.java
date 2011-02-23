/*
 *
 *
Copyright 2009 Max Hänze --- maximum.blogsite.org
Copyright 2010 Christian Morlok --- cmorlok.de
modifiziert 2010 von RedRocket ---
modifiziert 2011 von schirinowski@gmail.com
This file is part of WebSMSsend.

WebSMSsend is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

WebSMSsend is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILIT ublic License for more details.

You should have received a copy of the GNU General Public License
along with WebSMSsend.  If not, see <http://www.gnu.org/licenses/>.

 *
 *
 */

package webSMSsend;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;

/**
 * This static class implements the update facility for webSMSsend.
 *
 * @author umoenks
 */
public class Update {
    /**
     * The URL to the text file in which the most current version number is
     * stored.
     */
    private static final String CURRENT_VERSION_URL =
            "http://websmssend.googlecode.com/files/current_version.txt";

    private static String currentVersion = null;

    public static final String BUILD_URL =
            "http://websmssend.googlecode.com/files/WebSMSsend.jad";

    public static String getCurrentVersion() throws Exception {
        if (currentVersion == null) {
            currentVersion = retrieveCurrentVersion();
        }
        return currentVersion;
    }

    private static String retrieveCurrentVersion() throws Exception {
        HttpConnection connection = (HttpConnection) Connector.open(CURRENT_VERSION_URL);

        if (connection == null) {
            throw new RuntimeException("Konnte keine Verbindung zum Server aufbauen");
        }

        connection.setRequestMethod(HttpConnection.GET);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Content-Encoding", "wr-cs");
        connection.setRequestProperty("User-Agent", "Mozilla/3.0 (compatible)");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpConnection.HTTP_OK) {
            String resp = connection.getResponseMessage();
            connection.close();
            throw new IOException("HTTP Antwort-Code: " + responseCode
                    + ", Grund: " + resp);
        }

        Reader reader = new InputStreamReader(connection.openInputStream());

        int length = (int) connection.getLength();
        System.out.println("Empfange " + length + " Bytes...");

        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        String asString = String.valueOf(buffer);
        System.out.println("Antwort-Inhalt: " + asString);
        reader.close();
        connection.close();

        return asString;
    }

    private static Hashtable splitVersionNumber(String versionNumber) {
        Hashtable splitVersion = new Hashtable();

        int majorStopIdx = versionNumber.indexOf('.');
        splitVersion.put("major", versionNumber.substring(0, majorStopIdx++));

        int minorStopIdx = versionNumber.indexOf('.', majorStopIdx);
        splitVersion.put("minor", versionNumber.substring(majorStopIdx, minorStopIdx++));
        splitVersion.put("build", versionNumber.substring(minorStopIdx));

        return splitVersion;
    }

    public static boolean isUpdateAvailable(String installedVersion) throws Exception {
        Hashtable current = splitVersionNumber(getCurrentVersion());
        Hashtable installed = splitVersionNumber(installedVersion);

        boolean updateAvailable = false;

        int currentMajor = Integer.parseInt(current.get("major").toString());
        int installedMajor = Integer.parseInt(installed.get("major").toString());

        if (installedMajor < currentMajor) {
            updateAvailable = true;
        } else {
            int currentMinor = Integer.parseInt(current.get("minor").toString());
            int installedMinor = Integer.parseInt(installed.get("minor").toString());

            if (installedMinor < currentMinor) {
                updateAvailable = true;
            } else {
                int currentBuild = Integer.parseInt(current.get("build").toString());
                int installedBuild = Integer.parseInt(installed.get("build").toString());

                if (installedBuild < currentBuild) {
                    updateAvailable = true;
                }
            }
        }

        return updateAvailable;
    }
}
