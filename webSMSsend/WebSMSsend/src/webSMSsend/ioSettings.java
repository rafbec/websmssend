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

package webSMSsend;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class ioSettings {

    protected static String getSaveEachCharacter() {
        String data = getData(MakeFieldName("SaveEachCharacter"), 1);
        if (data.equals(""))
        {
            data="true"; //standard true
        }
        return data;
    }

    protected static String getAutoUpdate() {
        String data = getData(MakeFieldName("autoUpdate"), 1);
        if (data.equals(""))
        {
            data="true"; //standard true
        }
        return data;
    }

    protected static String getDebug() {
        return getData(MakeFieldName("debug"), 1);
    }

    protected static String getUsername() {
        return getData(MakeFieldName("LoginData"), 1);
    }

    protected static String getPassword() {
        return getData(MakeFieldName("LoginData"), 2);
    }

    protected static int getSetup() {
        String data = getData(MakeFieldName("setup"), 1);
        if (data.equals("")) {
            return 0; //=SENDERMODE_STANDARD
        } else {
            return Integer.parseInt(data);
        }

    }

    protected static String getActiveAccount() {
        return getData("ActiveAccountNumber", 1);
    }

    protected static int getSenderMode() {
        String data = getData(MakeFieldName("SenderSetup"), 1);
        if (data.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(data);
        }
    }

    protected static String getSenderName() {
        String Sender = getData(MakeFieldName("SenderSetup"), 2);
        if (Sender.equals("")) {
            return "Absender"; //Als Standard Absender, damit man auf jeden Fall das Textfeld in smsSettings sieht
        } else {
            return Sender;
        }
    }

    protected static String getTempSMSto() {
        return getData("TempSMS", 1);
    }

    protected static String getTempSMStext() {
        return getData("TempSMS", 2);
    }

    protected static String getLastSMSto() {
        return getData("LastSMS", 1);
    }

    protected static String getLastSMStext() {
        return getData("LastSMS", 2);
    }

    protected static int saveToRMS(String username, String password) {
        String[] data = {username, password};
        return saveData(MakeFieldName("LoginData"), data, 2);
    }

    protected static int saveSaveEachCharacter(String contentLoad) {
        String[] data = {contentLoad};
        return saveData(MakeFieldName("SaveEachCharacter"), data, 1);
    }

    protected static int saveDebug(String debug) {
        String[] data = {debug};
        return saveData(MakeFieldName("debug"), data, 1);
    }

    protected static int saveAutoUpdate(String autoUpdate) {
        String[] data = {autoUpdate};
        return saveData(MakeFieldName("autoUpdate"), data, 1);
    }
    protected static int saveSetup(String provider) {
        String[] data = {provider};
        return saveData(MakeFieldName("setup"), data, 1);
    }

    protected static int saveActiveAccount(String AccountNumber) {
        String[] data = {AccountNumber};
        return saveData("ActiveAccountNumber", data, 1);
    }

    protected static int saveSenderSetup(int SenderMode, String SenderName) {
        String[] data = {"" + SenderMode, SenderName};
        return saveData(MakeFieldName("SenderSetup"), data, 2);
    }

    protected static int saveTempSMS(String smsTo, String smsText) {
        String[] data = {smsTo, smsText};
        return saveData(("TempSMS"), data, 2);
    }

    protected static int saveLastSMS(String smsTo, String smsText) {
        String[] data = {smsTo, smsText};
        return saveData(("LastSMS"), data, 2);
    }

    public static String MakeFieldName(String Field) {
        String AccountNumber = getActiveAccount();
        if (AccountNumber.equals("")) //Kein Account gesetzt--> Account 1 setzen
        {
            saveActiveAccount("0");
            return Field + "0";
        } else //Standardfall: Irgendein Account ist aktiviert
        {
            return Field + AccountNumber;
        }
    }

    protected static String getData(String field, int nb) {
        try {
            RecordStore rs = RecordStore.openRecordStore(field, true);

            if (rs.getNumRecords() > nb - 1) {
                String data = "";
                if (rs.getRecord(nb) != null) {
                    data = new String(rs.getRecord(nb));
                }

                rs.closeRecordStore();
                return data;
            } else {
                rs.closeRecordStore();
                return "";
            }

        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    protected static int saveData(String field, String[] content, int nb) {

        try {
            RecordStore rs = RecordStore.openRecordStore(field, true);
            if (rs.getNumRecords() > nb - 1) {
                for (int i = 1; i <= nb; i++) {
                    rs.setRecord(i, content[i - 1].getBytes(), 0, content[i - 1].getBytes().length);

                }

            } else {
                for (int i = 1; i <= nb; i++) {
                    rs.addRecord(content[i - 1].getBytes(), 0, content[i - 1].getBytes().length);
                }

            }
            rs.closeRecordStore();
            return 0;
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
            return 1;
        }

    }
}
