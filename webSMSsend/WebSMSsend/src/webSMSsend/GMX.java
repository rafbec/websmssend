/*
 *
 *
    Copyright 2011 schirinowski@gmail.com
    This file is part of WebSMSsend.
    The following code is based on parts of the LessIsMore package by fkoest.
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

package webSMSsend;

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

    public static final String WEBSERVICE_URL = "http://app5.wr-gmbh.de/WRServer/WRServer.dll/WR";

    String customerID;
    String senderPhoneNumber;

    public void Send(String smsRecv, String smsText) throws Exception {
        Send(smsRecv, smsText, "" , false);
    }

    public void Send(String smsRecv, String smsText, String senderName, boolean simulation) throws Exception {
        NetworkHandler connection = new NetworkHandler(username_, password_,gui_);
        smsRecv = connection.checkRecv(smsRecv);

        Hashtable params = new Hashtable();
        params.put("email_address", username_);
        params.put("password", password_);

        gui_.SetWaitScreenText("Login wird geladen...");
        Hashtable result = sendPackage("GET_CUSTOMER", "1.10", params, true);

        if(result == null)
            throw new Exception("Fehler");

        Object returnCode = result.get("rslt");

        if(returnCode == null)
            throw new RuntimeException("Could not parse response");

        if(returnCode.equals("0"))
                System.out.println("Success!");
        else if(returnCode.equals("25"))
            throw new Exception("Invalid E-Mail/password");
        else
            System.out.println("Unknown Return code: "+returnCode);

        Object customerIDObj = result.get("customer_id");
        Object senderPhoneNumberObj = result.get("cell_phone");
        // Maximum number of free SMS a user can send
        String freeMaxMonth = result.get("free_max_month").toString();
        // Number of free SMS remaining the current month
        String freeRemainingMonth = result.get("free_rem_month").toString();

        System.out.println("Customer ID: "+customerIDObj);
        System.out.println("Sender Phone Number: "+senderPhoneNumberObj);

        if(senderPhoneNumberObj == null || freeMaxMonth == null || freeRemainingMonth == null)
            throw new Exception("Fehler beim Holen");

        senderPhoneNumber = senderPhoneNumberObj.toString();
        customerID = customerIDObj.toString();
        int remSMS = Integer.parseInt(freeMaxMonth);

        if(customerID == null)
            throw new Exception("No customer ID!");

        if(senderPhoneNumber == null)
            throw new Exception("No Sender Number!");

        params.put("customer_id", customerID);
        params.put("receivers", "\\<TBL ROWS=\"1\" COLS=\"3\"\\>receiver_id\\\\;receiver_name\\\\;receiver_number\\\\;1\\\\;Bla\\\\;" + smsRecv + "\\\\;\\</TBL\\>");
        params.put("sms_text", smsText);
        params.put("send_option", "sms");
        params.put("sms_sender", senderPhoneNumber);

        if (!simulation) {
            gui_.SetWaitScreenText("SMS wird gesendet...");
            result = sendPackage("SEND_SMS", "1.01", params, false);
        }

        int SMSneeded = CountSMS(smsText);
        if (remSMS > 0) {
            remSMS = remSMS - SMSneeded; //Counting amount of used SMS
        }
        gui_.setRemSMS(remSMS, Integer.parseInt(freeMaxMonth));
        System.out.println(result);
    }

    private Hashtable sendPackage(String method, String version, Hashtable params, boolean gmxFlag) {

		try {

			HttpConnection connection = (HttpConnection) Connector.open(WEBSERVICE_URL, Connector.READ_WRITE );

			if(connection == null)
				throw new RuntimeException("Could not establish connection");

			connection.setRequestMethod(HttpConnection.POST);
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type", "text/plain");
			connection.setRequestProperty("Content-Encoding", "wr-cs");
			connection.setRequestProperty("User-Agent", "Mozilla/3.0 (compatible)");


			Writer writer = new OutputStreamWriter(connection.openOutputStream());

			String request = createRequest(method, version, params, gmxFlag);

			System.out.println("Request :" + request);

			writer.write(request);
			writer.flush();

			int responseCode = connection.getResponseCode();
            if (responseCode != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP response code: " + responseCode);
            }

            Reader reader = new InputStreamReader(connection.openInputStream());

            int length = (int)connection.getLength();

            System.out.println("Receiving " + length + "bytes...");

            char[] buffer = new char[length];

            reader.read(buffer, 0, length);

            String asString = String.valueOf(buffer);

            System.out.println("Response: " + asString);

			if(asString.indexOf("<WR TYPE=\"RSPNS\"") < 0) {

				throw new RuntimeException("No valid response!");
			}

			String line = asString.substring(asString.indexOf("<WR TYPE=\"RSPNS\""), asString.indexOf("</WR>")+5);

			writer.close();
			reader.close();

			return parseResponse(line);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new RuntimeException(e.getClass() + ": " + e.getMessage());
		}
	}

	private String createRequest(String method, String version, Hashtable params, boolean gmxFlag) {

		StringBuffer request = new StringBuffer();

		request.append("<WR TYPE=\"RQST\" NAME=\"" + method + "\" VER=\""+ version +"\" PROGVER=\"1.13.04\">" + (gmxFlag ? "gmx=1\\p" : ""));

		Enumeration paramKeys = params.keys();

		while(paramKeys.hasMoreElements()) {
			Object key = paramKeys.nextElement();
			Object value = params.get(key);
			request.append(key + "=" + value + "\\p");
		}

		request.append("</WR>");

		return request.toString();
	}

	private Hashtable parseResponse(String response) {

		Hashtable result = new Hashtable();

		String dataString = response.substring(response.indexOf('>')+1, response.indexOf("</WR>"));

		while(dataString.length() > 0) {

			int posKeyEnd = dataString.indexOf("=");
			int posValueEnd = dataString.indexOf("\\p");

			String key = dataString.substring(0, posKeyEnd);
			String value = dataString.substring(posKeyEnd+1, posValueEnd);

			result.put(key, value);

			dataString = dataString.substring(posValueEnd+2);
		}

		return result;
	}


}
