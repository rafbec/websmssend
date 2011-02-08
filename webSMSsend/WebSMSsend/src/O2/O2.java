/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package O2;

import ConnectorBase.URLEncoder;
import ConnectorBase.SmsConnector;
import ConnectorBase.Properties;
import ConnectorBase.SmsData;
import java.io.IOException;
import javax.microedition.pki.CertificateException;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class O2 extends SmsConnector {

    public O2() {
        specs.AddProperty(new int[] {Properties.CAN_SEND_NAME_AS_SENDER, Properties.CAN_SIMULATE_SEND_PROCESS});
    }

    // Maximum SMS length allowed
    protected static final int MAX_SMS_LENGTH = 1800;

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
    
    public int CountSms(String smsText) {
        return (smsText.length() > 0) ? ((smsText.length() + 160 - 1) / 160) : 0;         //ceiled division
    }
   
    public void Send(SmsData Sms) throws Exception {
        int SenderMode=0; //0=Phonenumber, 1=Text( SenderName )
        gui = Sms.getGui();

        if (Sms.getSendername().length() >=5){
            SenderMode = 1;
        }

        try {
            gui.Debug("starte sendSMS02()" + (Sms.isSimualtion() ? " SIMULATION!" : ""));
            long totaltime = System.currentTimeMillis();
            if (Sms.getSmsrecv().equals("")) {
                gui.SetWaitScreenText("kein Empf\u00E4nger angegeben!");
                Thread.sleep(1000);
                throw new Exception("kein Empf\u00E4nger!");
            }
            if (Sms.getSmstext().equals("")) {
                gui.SetWaitScreenText("kein SMS-Text angegeben!");
                Thread.sleep(1000);
                throw new Exception("leere SMS!");
            }
            gui.SetWaitScreenText("Einstellungen werden geladen...");
            Thread.sleep(500);
            String url;

            NetworkHandler connection = new NetworkHandler(Sms.getUsername(), Sms.getPassword(),gui);
            gui.SetWaitScreenText("Login wird geladen...");

            String smsRecv = checkRecv(Sms.getSmsrecv());
            //#if Test
//#             // Output only on developer site, message contains sensitive data
//#             gui.Debug("Empf\u00E4nger-Handynummer: " + smsRecv);
            //#else
            gui_.Debug("Empf\u00E4nger-Handynummer: " + smsRecv.substring(0, 6) + "*******");
            //#endif
            url = "https://login.o2online.de/loginRegistration/loginAction"
                    + ".do?_flowId=" + "login&o2_type=asp&o2_label=login/co"
                    + "mcenter-login&scheme=http&" + "port=80&server=email."
                    + "o2online.de&url=%2Fssomanager.osp%3FAPIID" + "%3DAUT"
                    + "H-WEBSSO%26TargetApp%3D%2Fsms_new.osp%3F%26o2_type%3"
                    + "Durl" + "%26o2_label%3Dweb2sms-o2online";
            for (int i = 0; i < 2; i++) {
                try {
                    connection.httpHandler("GET", url, "login.o2online.de", "", true);
                    //      continue;
                    break;
                } catch (CertificateException ex) {
                    gui.Debug(ex.toString());
                    if (i == 1) {
                        throw ex;
                    }
                    gui.SetWaitScreenText("SSL-Fehler, starte erneut...");
                    Thread.sleep(3000);
                } catch (IOException ex) {
                    gui.Debug("IOException: " + ex.toString());
                    if (i == 1) {
                        throw ex;
                    }
                    gui.SetWaitScreenText("Netzwerkfehler, starte erneut...");
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    gui.SetWaitScreenText("Keine Verbindung m\u00F6glich :" + ex.toString() + "\n" + ex.getMessage());
                    Thread.sleep(3000);
                    throw ex;
                }
            }

            String flowExecutionKey;

            flowExecutionKey = connection.getFlowExecutionKey();

            gui.SetWaitScreenText("Zugangsdaten werden gesendet...");
            gui.Debug("Flow ExecutionKey: " + flowExecutionKey);
            url = "https://login.o2online.de/loginRegistration/loginAction.do";
            connection.httpHandler("POST", url, "login.o2online.de", "_flowExecutionKey="
                    + URLEncoder.encode(flowExecutionKey)
                    + "&loginName=" + URLEncoder.encode(connection.getUsername().trim())
                    + "&password=" + URLEncoder.encode(connection.getPassword().trim())
                    + "&_eventId=login", false);//False

            gui.SetWaitScreenText("Zugangsdaten werden gepr\u00FCft...");
            url = "https://email.o2online.de/ssomanager.osp?APIID=AUTH-WEBSSO&"
                    + "TargetApp=/sms_new.osp%3f&o2_type=url&o2_label=web2sms-o2online";
            String localCookie = connection.getCookie();
            connection.httpHandler("GET", url, "email.o2online.de", "", false);//false

            if (localCookie.equals(connection.getCookie())) {
                Exception ex = new Exception("Zugangsdaten falsch!");
                throw ex;
            }
            gui.SetWaitScreenText("Senden wird vorbereitet...");
            url = "https://email.o2online.de/smscenter_new.osp?Autocompletion=1&MsgContentID=-1";
            long starttime = System.currentTimeMillis();
            connection.httpHandler("GET", url, "email.o2online.de", "", true);
            long httphandlertime = System.currentTimeMillis() - starttime;
            gui.Debug("Fertig mit connection.httpHandler, Dauer: " + httphandlertime + " ms");
            //System.out.println(connection.getContent()+"\n\n\n");
            String postRequest = "";
            gui.SetWaitScreenText("SMS wird gesendet...");
            url = "https://email.o2online.de/smscenter_send.osp";

            //Build SMS send request and get remaining SMS.

            String[] returnValue;
            starttime = System.currentTimeMillis();
            returnValue = connection.getSendPostRequest(true, SenderMode); //Sendermode: 0=phone number 1=text
            gui.Debug("Fertig mit getSendPostRequest, Dauer: " + (System.currentTimeMillis() - starttime) + " ms" + "HttpHandler: " + httphandlertime + " ms");
            postRequest = returnValue[0];

            try {
                    remsms = Integer.parseInt(returnValue[1]);
            } catch (Exception ex) {
                gui.Debug("Failed to receive remaining SMS: " + ex.toString() + ex.getMessage());
            }

            if (SenderMode == 1) { //Text as Sender
                postRequest = postRequest + "SMSTo=" + URLEncoder.encode(smsRecv) + "&SMSText="
                        + URLEncoder.encode(Sms.getSmstext()) + "&SMSFrom="
                        + URLEncoder.encode(Sms.getSendername()) + "&Frequency=5";
            } else {
                postRequest = postRequest + "SMSTo=" + URLEncoder.encode(smsRecv) + "&SMSText="
                        + URLEncoder.encode(Sms.getSmstext()) + "&SMSFrom=&Frequency=5";
            }

            if (!Sms.isSimualtion()) {
                connection.httpHandler("POST", url, "email.o2online.de", postRequest, false);//false
            }
            //if (remsms_>0) remsms_--;
            int SMSneeded = CountSms(Sms.getSmstext());
            if (remsms > 0) {
                remsms = remsms - SMSneeded; //Counting amount of used SMS
            }
            gui.Debug("Die SMS ist Zeichen lang: " + Sms.getSmstext().length());
            gui.Debug("Anzahl SMS: " + SMSneeded);
            gui.Debug("Fertig mit sendSMS02, Dauer: " + (System.currentTimeMillis() - totaltime) + " ms");
            gui.SetWaitScreenText("SMS wurde versandt!");
            SaveItem(REMAINING_SMS_FIELD, remsms+"");

        } catch (OutOfMemoryError ex) {
            gui.SetWaitScreenText("Systemspeicher voll!");
            gui.Debug("Systemspeicher voll!" + ex.getMessage());
            Thread.sleep(7000);
            throw ex;
        } catch (Exception ex) {
            gui.SetWaitScreenText(ex.toString() + ": " + ex.getMessage());
            gui.Debug(ex.toString() + ": " + ex.getMessage());
            ex.printStackTrace();
            Thread.sleep(7000);
            throw ex;
        } catch (Throwable e) {
            gui.SetWaitScreenText("Unklarer Fehler: " + e.toString());
            gui.Debug("Unklarer Fehler: " + e.toString());
            Thread.sleep(10000);
            throw new Exception("Fehler!");
        }
    }
}
