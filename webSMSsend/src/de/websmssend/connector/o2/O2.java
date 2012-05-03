/*
 *
 *
    Copyright 2011 redrocketracoon@googlemail.com
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

package de.websmssend.connector.o2;

import de.websmssend.connector.base.URLEncoder;
import de.websmssend.connector.base.SmsConnector;
import de.websmssend.connector.base.Properties;
import de.websmssend.connector.base.SmsData;
import java.io.IOException;
import javax.microedition.pki.CertificateException;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class O2 extends SmsConnector {

    private ResumeData resumeData = null;

    /** Maximum SMS length allowed */
    protected static final int MAX_SMS_LENGTH = 1800;

    public O2() {
        specs.AddProperty(new int[]{Properties.CAN_SEND_WITH_ALTERNATIVE_SENDER,
            Properties.CAN_SIMULATE_SEND_PROCESS,
            Properties.CAN_ABORT_SEND_PROCESS_WHEN_NO_FREE_SMS_AVAILABLE});
    }

    public String getName() {
        return "O2";
    }

    public String getPasswordFieldLabel() {
        return "Passwort:";
    }

    public String getRemSmsText() {
        if (this.getRemainingSMS() == -1) {
            return "Verbleibende Frei-SMS: ?";
        } else {
            return "Verbleibende Frei-SMS: " + remsms;
        }
    }

    public int getMaxSMSLength() {
        return MAX_SMS_LENGTH;
    }

    public int countSms(String smsText) {
        return (smsText.length() > 0) ? ((smsText.length() + 160 - 1) / 160) : 0;         //ceiled division
    }

    public void checkSmsSenderNameConstraints(String senderName) throws Exception {
        if (senderName.length() < 5) {
            //Fehlermeldung "Name zu kurz" falls Text als Absender gewählt
            throw new Exception("Der Absender muss mindestens 5 Buchstaben lang sein");
        } else if (senderName.length() > 10) {
            //Fehlermeldung "Name zu lang" falls Text als Absender gewählt
            throw new Exception("Der Absender darf h\u00F6chstens 10 Buchstaben lang sein");
        }
    }

    public void checkSmsSenderNameCharacters(String text) throws Exception {
        char[] checkname = text.toCharArray();
        for (int i = 0; i < checkname.length; i++) {
            if ((checkname[i] >= 'a' && checkname[i] <= 'z') || (checkname[i] >= 'A' && checkname[i] <= 'Z')) {
            } else {
                throw new Exception("Nur Buchstaben ohne Umlaute sind erlaubt!");
            }
        }
    }

    public int send(SmsData Sms) throws Exception {
        int SenderMode = 0; //0=Phonenumber, 1=Text( SenderName )
        boolean failedToGetRemSms = false;
        gui = Sms.getGui();

        if (Sms.getSenderName().length() >= 5) {
            SenderMode = 1;
        }

        try {
            gui.debug("starte sendSMS02()" + (Sms.isSimulation() ? " SIMULATION!" : ""));
            gui.debug("Die SMS ist Zeichen lang: " + Sms.getSmsText().length());
            gui.debug("Anzahl SMS: " + countSms(Sms.getSmsText()));
            long totaltime = System.currentTimeMillis();
            if (Sms.getSmsRecv().equals("")) {
                gui.setWaitScreenText("kein Empf\u00E4nger angegeben!");
                Thread.sleep(1000);
                throw new Exception("kein Empf\u00E4nger!");
            }
            if (Sms.getSmsText().equals("")) {
                gui.setWaitScreenText("kein SMS-Text angegeben!");
                Thread.sleep(1000);
                throw new Exception("leere SMS!");
            }
            gui.setWaitScreenText("Einstellungen werden geladen...");
            Thread.sleep(500);
            String url;

            NetworkHandler connection = new NetworkHandler(Sms.getUsername(), Sms.getPassword(), gui,this);
            gui.setWaitScreenText("Login wird geladen...");

            String smsRecv = checkRecv(Sms.getSmsRecv());
            //#if Test
//#             // Output only on developer site, message contains sensitive data
//#             gui.debug("Empf\u00E4nger-Handynummer: " + smsRecv);
            //#else
            gui.debug("Empf\u00E4nger-Handynummer: " + smsRecv.substring(0, 6) + "*******");
            //#endif
            url = "https://login.o2online.de/auth/login?_flowId=login";

            for (int i = 0; i < 2; i++) {
                try {
                    connection.httpHandler("GET", url, "login.o2online.de", "", true);
                    //      continue;
                    break;
                } catch (CertificateException ex) {
                    gui.debug(ex.toString());
                    if (i == 1) {
                        throw ex;
                    }
                    gui.setWaitScreenText("SSL-Fehler, starte erneut...");
                    Thread.sleep(3000);
                } catch (IOException ex) {
                    gui.debug("IOException: " + ex.toString());
                    if (i == 1) {
                        throw ex;
                    }
                    gui.setWaitScreenText("Netzwerkfehler, starte erneut...");
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    gui.setWaitScreenText("Keine Verbindung m\u00F6glich :" + ex.toString() + "\n" + ex.getMessage());
                    Thread.sleep(3000);
                    throw ex;
                }
            }

            gui.setWaitScreenText("Zugangsdaten werden gesendet...");

            url = "https://login.o2online.de/auth/;jsessionid=" +
                    connection.getSessionID()
                    +"?wicket:interface=:0:loginForm::IFormSubmitListener::";

            connection.httpHandler("POST", url, "login.o2online.de",
                    "id6_hf_0="+ "&loginName%3AloginName=" + URLEncoder.encode(connection.getUsername().trim())
                    + "&password%3Apassword=" + URLEncoder.encode(connection.getPassword().trim())
                    , false);//False

            gui.setWaitScreenText("Zugangsdaten werden gepr\u00FCft...");

            url = "https://email.o2online.de/ssomanager.osp?APIID=AUTH-WEBSSO";

            connection.httpHandler("GET", url, "email.o2online.de", "", false);//false
            gui.debug(connection.getContent());
            //Check der Zugangsdaten (SSTOKEN wird nur bei richtigen Zugangsdaten übertragen
            if (connection.getCookie().indexOf("SSOTOKEN") == -1) {
                Exception ex = new Exception("Zugangsdaten falsch!");
                throw ex;
            }
            gui.setWaitScreenText("Senden wird vorbereitet...");
            url = "https://email.o2online.de/smscenter_new.osp?Autocompletion=1&MsgContentID=-1";

            connection.httpHandler("GET", url, "email.o2online.de", "", true);

            

            //Build SMS send request and get remaining SMS.
            gui.setWaitScreenText("SMS wird gesendet...");
            String[] returnValue;
            String postRequest = "";
            returnValue = connection.getSendPostRequest(true, SenderMode); //Sendermode: 0=phone number 1=text
            postRequest = returnValue[0];
            gui.debug("Post-Request: " + postRequest);  
            
            try {
                remsms = Integer.parseInt(returnValue[1]);
                remsms = remsms - countSms(Sms.getSmsText()); //Counting amount of used SMS and subtract from remaining freesms
            } catch (Exception ex) {
                gui.debug("Failed to receive remaining SMS: " + ex.toString() + ex.getMessage());
                failedToGetRemSms = true;
            }

            //for Debug purposes: simulates no more Free-SMS available
            //remsms =-1;

            if (remsms < 0 && failedToGetRemSms == false) { //No free sms remaining ask user what to do. In case it's not possible to aquire remsms send anyway
                resumeData = new ResumeData(SenderMode, postRequest, smsRecv, Sms, connection);
                return -1;
            } else { //enough free sms avaiable
                sendSms(new ResumeData(SenderMode, postRequest, smsRecv, Sms, connection));
                gui.debug("Fertig mit sendSMS02, Dauer: " + (System.currentTimeMillis() - totaltime) + " ms");
                return 0;
            }
        } catch (OutOfMemoryError ex) {
            gui.setWaitScreenText("Systemspeicher voll!");
            gui.debug("Systemspeicher voll!" + ex.getMessage());
            Thread.sleep(7000);
            throw ex;
        } catch (Exception ex) {
            gui.setWaitScreenText(ex.getMessage());
            gui.debug(ex.toString() + ": " + ex.getMessage());
            ex.printStackTrace();
            Thread.sleep(7000);
            throw ex;
        } catch (Throwable e) {
            gui.setWaitScreenText("Unklarer Fehler: " + e.toString());
            gui.debug("Unklarer Fehler: " + e.toString());
            Thread.sleep(10000);
            throw new Exception("Fehler!");
        }
    }

    public boolean resumeSending() throws Exception {
        gui.debug("Resume SMS sending process");
        if (resumeData != null) {
            sendSms(resumeData);
            return true;
        } else {
            return false;
        }

    }

    private void sendSms(ResumeData resumeData) throws IOException, Exception {
        
        gui.setWaitScreenText("SMS wird gesendet...");
        final String url = "https://email.o2online.de/smscenter_send.osp";
        String postRequest = resumeData.getPostRequest();
        SmsData Sms = resumeData.getSms();

        if (resumeData.getSenderMode() == 1) {
            //Text as Sender
            postRequest = postRequest + "SMSTo=" + URLEncoder.encode(resumeData.getSmsRecv()) + "&SMSText="
                    + URLEncoder.encode(Sms.getSmsText()) + "&SMSFrom=" + URLEncoder.encode(Sms.getSenderName()) + "&Frequency=5";
        } else {
            postRequest = postRequest + "SMSTo=" + URLEncoder.encode(resumeData.getSmsRecv()) + "&SMSText="
                    + URLEncoder.encode(Sms.getSmsText()) + "&SMSFrom=&Frequency=5";
        }

        if (!Sms.isSimulation()) {
            resumeData.getConnection().httpHandler("POST", url, "email.o2online.de", postRequest, false); //false
        }

        gui.setWaitScreenText("SMS wurde versandt!");
        saveItem(REMAINING_SMS_FIELD, remsms + "");
    }

    //<editor-fold defaultstate="collapsed" desc="Save- and Get-Function for Networkhandler">
    void saveSetting(String itemName, String value){
        saveItem(itemName, value);
    }

    String getSetting(String itemName){
        return getItem(itemName);
    }
    //</editor-fold>
}
