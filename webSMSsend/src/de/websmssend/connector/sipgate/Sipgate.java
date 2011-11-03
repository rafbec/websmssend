/*
 *
 *
    Copyright 2011 manfred dreese (http://www.dreese.de)
 
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

package de.websmssend.connector.sipgate;


import de.websmssend.connector.base.SmsConnector;
import de.websmssend.connector.base.SmsData;
import de.websmssend.connector.base.URLEncoder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.pki.CertificateException;


/**
 * This class implements the {@link ConnectorBase.ISmsConnector communication
 * interface} to the Sipgate service.
 * 
 * @author Copyright 2011 manfred dreese dev@dreese.de
 * @version $Revision$
 */
public class Sipgate extends SmsConnector {
    
    /** Target URL for XML-RPC Requests */
    protected static final String SIPGATEAPI_TARGET="https://samurai.sipgate.net/RPC2";
   
    /** Tokens for generation of XMLRPC Message samurai.sessionInitiate */ 
    static String[] strXmlSendTokens = new String[]
    {
    "<?xml version=\"1.0\"?>\r\n<methodCall><methodName>samurai.SessionInitiate</methodName><params><param><value><struct><member><name>Content</name><value><string>",
    /*text*/ 
    "</string></value></member><member><name>RemoteUri</name><value><string>sip:" 
    /*MSIN*/,
    "@sipgate.net</string></value></member><member><name>TOS</name><value><string>text</string></value></member></struct></value></param></params></methodCall>\r\n\r\n"
    };
    
    /** Template for Account Balance query samurai.BalanceGet */
    static String strXmlGetBalance = "<?xml version=\"1.0\"?>\r\n<methodCall><methodName>samurai.BalanceGet</methodName></methodCall>\r\n\r\n";
    
    
    /** Maximum SMS length allowed */
    protected static final int MAX_SMS_LENGTH = 1800;
    
    protected static final int SIPGATE_SMS_FAILED = -999;

    public int getMaxSMSLength() {
        return Sipgate.MAX_SMS_LENGTH;
    }
    
    public String getName() {
        return "Sipgate";
    }

    public String getPasswordFieldLabel() {
        return "Sipgate-Passwort:";
    }

    public String getRemSmsText() {
        String szResult = " ";
        return szResult;
    }

    public int send(SmsData Sms) throws Exception {
        int nResult = SIPGATE_SMS_FAILED;
        HttpsConnection con = (HttpsConnection) Connector.open(SIPGATEAPI_TARGET);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-type", "text/xml");
        con.setRequestProperty("Host", "sipgate.net");
        con.setRequestProperty("User-Agent", "webSMSSend");
        con.setRequestProperty("Authorization", "Basic " + BasicAuth.encode(Sms.getUsername(), Sms.getPassword()));

        for (int i = 0; i < 2; i++) {
            try {
                StringBuffer stbEscapedText = new StringBuffer();
                for (int idx=0 ; idx<Sms.getSmsText().length();idx++)
                {
                    switch( Sms.getSmsText().charAt(idx))
                    {
                        case '>':
                            stbEscapedText.append("&gt;");
                            break;
                        case '<':
                            stbEscapedText.append("&lt;");
                            break;
                        case '&':
                            stbEscapedText.append("&amp;");
                            break;
                        default:
                            stbEscapedText.append( Sms.getSmsText().charAt(idx));
                    }
                }
                String strEscapedText = stbEscapedText.toString();
                
                
                OutputStreamWriter osw = new OutputStreamWriter(con.openOutputStream(), "UTF-8");
                StringBuffer bfrOut = new StringBuffer();
                
                bfrOut.append(strXmlSendTokens[0]);
                bfrOut.append(strEscapedText);
                bfrOut.append(strXmlSendTokens[1]);
                bfrOut.append(checkRecv(Sms.getSmsRecv()));
                bfrOut.append(strXmlSendTokens[2]);
                String strXmlOut = bfrOut.toString();

                osw.write(strXmlOut);
                
                osw.flush();
                osw.close();      
                
                // Search Input Stream for "Method Success Token
                InputStreamReader isr = new InputStreamReader(con.openInputStream());
                int nBuffer = 0;
                StringBuffer stbInput = new StringBuffer();
                
                while ( (nBuffer = isr.read()) != -1)
                {
                    stbInput.append((char) nBuffer);
                }
                
                String strTest = stbInput.toString();
                if (strTest.indexOf("Method success") > 0)
                {
                    nResult = SmsConnector.SMS_SENT;
                }

                // +++ATH
                con.close();

                break;
            
            } catch (Exception ex) {
                // Discard postflight errors
                if (nResult >= SmsConnector.SMS_SENT)
                {
                    return nResult;
                }
                gui.debug(ex.toString());
                gui.setWaitScreenText("Keine Verbindung m\u00F6glich :" + ex.toString() + "\n" + ex.getMessage());
                Thread.sleep(3000);
                throw ex;
            }
        }

        // Throw an Exception if send process failed
        if (nResult == SIPGATE_SMS_FAILED)
        {
            throw new Exception("SMS konnte nicht gesendet werden.");
        }
        
        return nResult;
    }

    public boolean resumeSending() throws Exception {
        return false;
    }

    public int countSms(String smsText) {
        return (smsText.length() > 0) ? ((smsText.length() + 160 - 1) / 160) : 0;         //ceiled division
    }

    public void checkSmsSenderNameConstraints(String senderName) throws Exception {
    }

    public void checkSmsSenderNameCharacters(String senderName) throws Exception {
    }
    
    protected String checkRecv(String smsRecv) {
        if (smsRecv.startsWith("0")) {
            if (smsRecv.startsWith("00")) {
                smsRecv = "".concat(smsRecv.substring(2));
                return smsRecv;
            }
            smsRecv = "49".concat(smsRecv.substring(1));
        }
        
        if (smsRecv.startsWith("+"))
        {
            smsRecv = "".concat(smsRecv.substring(1));
        }
        return smsRecv;
    }
    
    
}
