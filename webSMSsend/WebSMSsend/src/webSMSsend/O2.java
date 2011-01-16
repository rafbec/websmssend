/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webSMSsend;

import java.io.IOException;
import javax.microedition.pki.CertificateException;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class O2 extends SmsConnector {

    public void Send(String smsRecv, String smsText) throws Exception {
        Send(smsRecv, smsText, "" , false);
    }

    public void Send(String smsRecv, String smsText, String senderName, boolean simulation) throws Exception {
        int SenderMode=0; //0=Phonenumber, 1=Text( SenderName )

        if (senderName.length() >=5){
            SenderMode = 1;
        }

        try {
            gui_.Debug("starte sendSMS02()");
            long totaltime = System.currentTimeMillis();
            if (smsRecv.equals("")) {
                gui_.SetWaitScreenText("kein Empf\u00E4nger angegeben!");
                Thread.sleep(1000);
                throw new Exception("kein Empf\u00E4nger!");
            }
            if (smsText.equals("")) {
                gui_.SetWaitScreenText("kein SMS-Text angegeben!");
                Thread.sleep(1000);
                throw new Exception("leere SMS!");
            }
            gui_.SetWaitScreenText("Einstellungen werden geladen...");
            Thread.sleep(500);
            String url;

            NetworkHandler connection = new NetworkHandler(username_, password_,gui_);
            gui_.SetWaitScreenText("Login wird geladen...");

            smsRecv = connection.checkRecv(smsRecv);
            gui_.Debug("Login wird geladen: smsRecv: " + smsRecv);
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
                    gui_.Debug(ex.toString());
                    if (i == 1) {
                        throw ex;
                    }
                    gui_.Debug("SSL-Fehler, starte erneut...");
                    gui_.SetWaitScreenText("SSL-Fehler, starte erneut...");
                    Thread.sleep(3000);
                } catch (IOException ex) {
                    gui_.Debug("IOException: " + ex.toString());
                    if (i == 1) {
                        throw ex;
                    }
                    gui_.Debug("Netzwerkfehler, starte erneut...");
                    gui_.SetWaitScreenText("Netzwerkfehler, starte erneut...");
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    gui_.Debug("Keine Verbindung m\u00F6glich :" + ex.toString() + " " + ex.getMessage());
                    gui_.SetWaitScreenText("Keine Verbindung m\u00F6glich :" + ex.toString() + "\n" + ex.getMessage());
                    Thread.sleep(3000);
                    throw ex;
                }
            }

            String flowExecutionKey;

            flowExecutionKey = connection.getFlowExecutionKey();

            gui_.SetWaitScreenText("Zugangsdaten werden gesendet...");
            gui_.Debug("Zugangsdaten werden gesendet: Flow ExecutionKey: " + flowExecutionKey);
            url = "https://login.o2online.de/loginRegistration/loginAction.do";
            connection.httpHandler("POST", url, "login.o2online.de", "_flowExecutionKey="
                    + URLEncoder.encode(flowExecutionKey)
                    + "&loginName=" + URLEncoder.encode(connection.getUsername().trim())
                    + "&password=" + URLEncoder.encode(connection.getPassword().trim())
                    + "&_eventId=login", false);//False

            gui_.SetWaitScreenText("Zugangsdaten werden gepr\u00FCft...");
            url = "https://email.o2online.de/ssomanager.osp?APIID=AUTH-WEBSSO&"
                    + "TargetApp=/sms_new.osp%3f&o2_type=url&o2_label=web2sms-o2online";
            String localCookie = connection.getCookie();
            connection.httpHandler("GET", url, "email.o2online.de", "", false);//false

            if (localCookie.equals(connection.getCookie())) {
                Exception ex = new Exception("Zugangsdaten falsch!");
                throw ex;
            }
            gui_.SetWaitScreenText("Senden wird vorbereitet...");
            url = "https://email.o2online.de/smscenter_new.osp?Autocompletion=1&MsgContentID=-1";
            long starttime = System.currentTimeMillis();
            connection.httpHandler("GET", url, "email.o2online.de", "", true);
            long httphandlertime = System.currentTimeMillis() - starttime;
            gui_.Debug("Fertig mit connection.httpHandler, Dauer: " + httphandlertime + " ms");
            //System.out.println(connection.getContent()+"\n\n\n");
            String postRequest = "";
            gui_.SetWaitScreenText("SMS wird gesendet...");
            url = "https://email.o2online.de/smscenter_send.osp";

            //Build SMS send request and get remaining SMS.

            String[] returnValue;
            starttime = System.currentTimeMillis();
            returnValue = connection.getSendPostRequest((remsms_ != -1), SenderMode); //Sendermode: 0=phone number 1=text
            gui_.Debug("Fertig mit getSendPostRequest, Dauer: " + (System.currentTimeMillis() - starttime) + " ms" + "HttpHandler: " + httphandlertime + " ms");
            postRequest = returnValue[0];

            try {
                if (remsms_ != -1) {
                    remsms_ = Integer.parseInt(returnValue[1]);
                }
            } catch (Exception ex) {
                gui_.Debug("Failed to receive remaining SMS: " + ex.toString() + ex.getMessage());
            }

            if (SenderMode == 1) { //Text as Sender
                postRequest = postRequest + "SMSTo=" + URLEncoder.encode(smsRecv) + "&SMSText="
                        + URLEncoder.encode(smsText) + "&SMSFrom="
                        + URLEncoder.encode(senderName) + "&Frequency=5";
            } else {
                postRequest = postRequest + "SMSTo=" + URLEncoder.encode(smsRecv) + "&SMSText="
                        + URLEncoder.encode(smsText) + "&SMSFrom=&Frequency=5";
            }

            if (!simulation) {
                connection.httpHandler("POST", url, "email.o2online.de", postRequest, false);//false
            }
            //if (remsms_>0) remsms_--;
            int SMSneeded = CountSMS(smsText);
            if (remsms_ > 0) {
                remsms_ = remsms_ - SMSneeded; //Counting amount of used SMS
            }
            gui_.Debug("Die SMS ist Zeichen lang: " + smsText.length());
            gui_.Debug("Anzahl SMS: " + SMSneeded);
            gui_.Debug("Fertig mit sendSMS02, Dauer: " + (System.currentTimeMillis() - totaltime) + " ms");
            gui_.SetWaitScreenText("SMS wurde versandt!");
            gui_.setRemSMS(remsms_, 0);

        } catch (OutOfMemoryError ex) {
            gui_.SetWaitScreenText("Systemspeicher voll!");
            gui_.Debug("Systemspeicher voll!" + ex.getMessage());
            Thread.sleep(7000);
            throw ex;
        } catch (Exception ex) {
            gui_.SetWaitScreenText(ex.toString() + ": " + ex.getMessage());
            gui_.Debug(ex.toString() + ": " + ex.getMessage());
            ex.printStackTrace();
            Thread.sleep(7000);
            throw ex;
        } catch (Throwable e) {
            gui_.SetWaitScreenText("Unklarer Fehler: " + e.toString());
            gui_.Debug("Unklarer Fehler: " + e.toString());
            Thread.sleep(10000);
            throw new Exception("Fehler!");
        }
    }

}
