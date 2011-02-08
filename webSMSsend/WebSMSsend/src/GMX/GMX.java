/*
 *
 *
    Copyright 2011 schirinowski@gmail.com
    This file is part of WebSMSsend.
    The following code is based on parts of the LessIsMore package by fkoester.
    For details on LessIsMore see <http://wiki.evolvis.org/lessismore/index.php/LessIsMore>
    and <https://evolvis.org/scm/viewvc.php/lessismore/LessIsMore-MIDlet-Prototype/trunk/src/org/evolvis/lessismore/>

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

package GMX;

import ConnectorBase.SmsConnector;
import ConnectorBase.Properties;
import ConnectorBase.SmsData;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * This class implements the interface to the GMX SMS service.
 * 
 * @author Copyright 2011 schirinowski@gmail.com
 */
public class GMX extends SmsConnector {
    // Default GMX SMS server is app5, according to some internet forum entries,
    // the default recovery procedure is app{0,...,4,6,7} if app5 is not
    // available. As app5 has a questionable performance in terms of the delay
    // between receiving the SMS to be sent and the time it is actually sent,
    // app5 is not the default server of webSMSsend
    public static final String WEBSERVICE_URL = "http://app3.wr-gmbh.de/WRServer/WRServer.dll/WR";
    // Maximum SMS length according to http://www.gmx.net/gmx-sms
    protected static final int MAX_SMS_LENGTH = 760;

    String customerID;
    String senderPhoneNumber;

    public GMX(){
        specs.AddProperty(new int[]{Properties.CAN_SIMULATE_SEND_PROCESS});
    }

    public String getName() {
        return "GMX";
    }

    public String getPasswordFieldLabel() {
        return "SMS-Manager Freischaltcode:";
    }

    public String getRemSmsText() {
        String text = "Verbleibende Frei-SMS: ";

        if (getRemainingSMS() == -1 ) {
            text += "?";
        } else {
            text += remsms;
        }

        if (this.getMaxFreeSMS() == -1 ) {
            text += "/?";
        } else {
            text += "/" + maxfreesms;
        }
        return text;
    }

    public int getMaxSMSLength() {
        return MAX_SMS_LENGTH;
    }

    public int CountSms(String smsText) {
        if (smsText.length() > 160) {
            return (int) Math.floor((smsText.length() + 151) / 152);
        } else if (smsText.length() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public void Send(SmsData Sms) throws Exception {
        try {
            gui = Sms.getGui();
            gui.Debug("Starte " + getClass().getName() + ".Send()" + (Sms.isSimualtion() ? " SIMULATION!" : ""));
            long totaltime = System.currentTimeMillis();

            if (Sms.getSmsrecv().equals("")) {
                throw new Exception("Kein Empf\u00E4nger angegeben");
            }
            if (Sms.getSmstext().equals("")) {
                throw new Exception("Kein SMS-Text angegeben");
            }

            if (Sms.getSmstext().length() > getMaxSMSLength()) {
                throw new Exception("SMS-Text zu lang (max. 760 Zeichen)");
            }

            String smsRecv = checkRecv(Sms.getSmsrecv());

            //#if Test
//#             // Output only on developer site, message contains sensitive data
//#             gui.Debug("Empf\u00E4nger-Handynummer: " + smsRecv);
            //#else
            gui_.Debug("Empf\u00E4nger-Handynummer: " + smsRecv.substring(0, 6) + "*******");
            //#endif

            Hashtable params = new Hashtable();
            params.put("email_address", Sms.getUsername());
            params.put("password", Sms.getPassword());

            gui.SetWaitScreenText("Login...");
            Hashtable result = sendPackage("GET_CUSTOMER", "1.10", params, true);

            if(result == null)
                throw new Exception("Login-Fehler");

            Object returnCode = result.get("rslt");

            if(returnCode == null)
                throw new RuntimeException("Konnte Server-Antwort nicht lesen");

            if(returnCode.equals("0")) {
                gui.SetWaitScreenText("Login erfolgreich");
            }
            else if(returnCode.equals("25")) {
                throw new Exception("E-Mail/Freischaltcode falsch");
            }
            else {
                gui.SetWaitScreenText("Unbekannte Serverantwort: " + returnCode);
            }

            Object customerIDObj = result.get("customer_id");
            Object senderPhoneNumberObj = result.get("cell_phone");
            // Maximum number of free SMS a user can send
            String freeMaxMonth = result.get("free_max_month").toString();
            // Number of free SMS remaining the current month
            String freeRemainingMonth = result.get("free_rem_month").toString();

            gui.SetWaitScreenText("Senden wird vorbereitet...");
            
            //#if Test
//#             // Output only on developer site, message contains sensitive data
//#             gui.Debug("Kundennummer: "+customerIDObj);
//#             gui.Debug("Absender-Handynummer: "+senderPhoneNumberObj);
            //#else
            gui_.Debug("Kundennummer: "+customerIDObj.toString().substring(0, 4) + "*******");
            gui_.Debug("Absender-Handynummer: "+senderPhoneNumberObj.toString().substring(0, 6) + "*******");
            //#endif
            
            if(senderPhoneNumberObj == null || freeMaxMonth == null || freeRemainingMonth == null)
                throw new Exception("Fehler beim Lesen der Serverantwort");

            senderPhoneNumber = senderPhoneNumberObj.toString();
            customerID = customerIDObj.toString();
            int remSMS = Integer.parseInt(freeRemainingMonth);

            if(customerID == null)
                throw new Exception("Keine Kundennummer empfangen");

            if(senderPhoneNumber == null)
                throw new Exception("Keine Absender-Handynummer empfangen");

            params.put("customer_id", customerID);
            params.put("receivers", "\\<TBL ROWS=\"1\" COLS=\"3\"\\>receiver_id\\\\;receiver_name\\\\;receiver_number\\\\;1\\\\;Bla\\\\;" + smsRecv + "\\\\;\\</TBL\\>");
            params.put("sms_text", Sms.getSmstext());
            params.put("send_option", "sms");
            params.put("sms_sender", senderPhoneNumber);

            if (!Sms.isSimualtion()) {
                gui.SetWaitScreenText("SMS wird gesendet...");
                result = sendPackage("SEND_SMS", "1.01", params, false);

                //Counting amount of used SMS
                int SMSneeded = CountSms(Sms.getSmstext());
                // Check if there are free SMS left
                if (remSMS - SMSneeded > 0) {
                    remSMS = remSMS - SMSneeded;
                }
                else {
                    remSMS = 0;
                }
                gui.Debug("Die SMS ist Zeichen lang: " + Sms.getSmstext().length());
                gui.Debug("Anzahl SMS: " + SMSneeded);
                gui.SetWaitScreenText("SMS wurde gesendet");
            }

            remsms = remSMS;
            try {
                maxfreesms = Integer.parseInt(freeMaxMonth);
            } catch (Exception ex) {
                maxfreesms = 0;
            }
            SaveItem(REMAINING_SMS_FIELD, remsms+"");
            SaveItem(MAX_FREE_SMS, maxfreesms+"");
            gui.Debug("Fertig mit " + getClass().getName() + ".Send(), Dauer: " + (System.currentTimeMillis() - totaltime) + " ms");
        } catch (OutOfMemoryError ex) {
            gui.SetWaitScreenText("Systemspeicher voll. " + ex.getMessage());
            Thread.sleep(3000);
            throw ex;
        } catch (Exception ex) {
            gui.SetWaitScreenText("SMS nicht gesendet: " + ex.getMessage());
            ex.printStackTrace();
            Thread.sleep(3000);
            throw ex;
        } catch (Throwable e) {
            gui.SetWaitScreenText("Unklarer Fehler: " + e.toString());
            Thread.sleep(10000);
            throw new Exception("Fehler!");
        }
    }

    private Hashtable sendPackage(String method, String version, Hashtable params, boolean gmxFlag) throws Exception {
        HttpConnection connection = (HttpConnection) Connector.open(WEBSERVICE_URL, Connector.READ_WRITE);

        if (connection == null) {
            throw new RuntimeException("Konnte keine Verbindung zum Server aufbauen");
        }

        connection.setRequestMethod(HttpConnection.POST);
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Content-Encoding", "wr-cs");
        connection.setRequestProperty("User-Agent", "Mozilla/3.0 (compatible)");

        Writer writer = new OutputStreamWriter(connection.openOutputStream());

        //#if Test
//#         // Output only on developer site, message contains sensitive data
//#         gui.Debug("Erstelle Serveranfrage mit folgenden Parametern: " + params);
        //#endif
        
        String request = createRequest(method, version, params, gmxFlag);
        //#if Test
//#         // Output only on developer site, message contains sensitive data
//#         gui.Debug("Serveranfrage: " + request);
        //#endif

        writer.write(request);
        writer.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpConnection.HTTP_OK) {
            throw new IOException("HTTP Antwort-Code: " + responseCode);
        }

        Reader reader = new InputStreamReader(connection.openInputStream());

        int length = (int) connection.getLength();
        gui.Debug("Empfange " + length + " Bytes...");

        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        String asString = String.valueOf(buffer);

        //#if Test
//#         // Output only during development, message contains sensitive data
//#         gui.Debug("Serverantwort: " + asString);
        //#endif

        if (asString.indexOf("<WR TYPE=\"RSPNS\"") < 0) {
            throw new RuntimeException("Keine g\u00FCltige Serverantwort empfangen");
        }

        String line = asString.substring(asString.indexOf("<WR TYPE=\"RSPNS\""), asString.indexOf("</WR>") + 5);

        writer.close();
        reader.close();

        return parseResponse(line);
    }

    private String createRequest(String method, String version, Hashtable params, boolean gmxFlag) {
        StringBuffer request = new StringBuffer();
        request.append("<WR TYPE=\"RQST\" NAME=\"" + method + "\" VER=\"" + version + "\" PROGVER=\"1.13.04\">" + (gmxFlag ? "gmx=1\\p" : ""));

        Enumeration paramKeys = params.keys();
        while (paramKeys.hasMoreElements()) {
            Object key = paramKeys.nextElement();
            Object value = params.get(key);
            request.append(key + "=" + value + "\\p");
        }

        request.append("</WR>");
        return request.toString();
    }

    private Hashtable parseResponse(String response) {
        Hashtable result = new Hashtable();
        String dataString = response.substring(response.indexOf('>') + 1, response.indexOf("</WR>"));

        while (dataString.length() > 0) {
            int posKeyEnd = dataString.indexOf("=");
            int posValueEnd = dataString.indexOf("\\p");

            String key = dataString.substring(0, posKeyEnd);
            String value = dataString.substring(posKeyEnd + 1, posValueEnd);

            result.put(key, value);

            dataString = dataString.substring(posValueEnd + 2);
        }
        //#if Test
//#         // Output only during development, message contains sensitive data
//#         gui.Debug("Serverantwort geparsed: " + result);
        //#endif
        return result;
    }
}
