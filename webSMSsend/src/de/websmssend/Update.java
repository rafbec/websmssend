/*
 *
 *
Copyright 2011 von schirinowski@gmail.com
Copyright 2011 redrocketracoon@googlemail.com
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

package de.websmssend;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * This class implements static methods for the update facility of webSMSsend.
 *
 * @author umoenks
 */
public class Update {
    /**
     * The URL to the text file in which the most current version number is
     * stored.
     */
    private static final String CURRENT_VERSION_URL =
            "http://websmssend.googlecode.com/svn/trunk/webSMSsend/WebSMSsend/current_version.txt";

    private static String currentVersion = null;
    private static String buildURL = null;

    public static boolean alreadyCheckedForUpdate() {
        return !(currentVersion == null || buildURL == null);
    }

    public static String getBuildUrl() throws Exception {
       if (!alreadyCheckedForUpdate()) {
            retrieveCurrentVersion();
        }
        return buildURL;
    }

    public static String getCurrentVersion() throws Exception {
        if (!alreadyCheckedForUpdate()) {
            retrieveCurrentVersion();
        }
        return currentVersion;
    }

    private static void retrieveCurrentVersion() throws Exception {
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

        int newline = asString.indexOf("\r\n");
        currentVersion = asString.substring(0, newline).trim();
        // extract buildURL in the second line
        buildURL = asString.substring(newline + 2, asString.length()).trim();
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
        } else if (installedMajor == currentMajor) {
            int currentMinor = Integer.parseInt(current.get("minor").toString());
            int installedMinor = Integer.parseInt(installed.get("minor").toString());

            if (installedMinor < currentMinor) {
                updateAvailable = true;
            } else if (installedMinor == currentMinor) {
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
