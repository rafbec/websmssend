/*
 *
 *
    Copyright 2012 redrocketracoon@googlemail.com
    Inspired by Felix Bachstein's WebSMS GMX Connector https://github.com/felixb/websms-connector-gmx
 
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

import de.websmssend.connector.base.SmsConnector;
import de.websmssend.connector.base.Properties;
import de.websmssend.connector.base.SmsData;
import java.io.IOException;
import javax.microedition.io.HttpsConnection;
import javax.microedition.pki.CertificateException;

/**
 *
 * @author Copyright 2012 redrocketracoon@googlemail.com
 */
public class GMX extends SmsConnector {

    private SmsData resumeSms = null;

    /** Maximum SMS length allowed */
    protected static final int MAX_SMS_LENGTH = 760;

    public GMX() {
        specs.AddProperty(new int[]{Properties.CAN_SEND_WITH_ALTERNATIVE_SENDER,
            Properties.CAN_SIMULATE_SEND_PROCESS,
            Properties.CAN_ABORT_SEND_PROCESS_WHEN_NO_FREE_SMS_AVAILABLE});
    }

    public String getName() {
        return "GMX 2.0";
    }

    public String getPasswordFieldLabel() {
        return "Passwort:";
    }
    
    public String getSenderStandardLabel() {
        return "GMX SMS";   
    }

    public String getSenderAlternativeLabel() {
        return "Meine Mobiltelefonnummer";
    }

    public String getSenderAlternativeDescription() {
        return "Telefonnummer (00491...):";
    }

    public String getRemSmsText() {
        StringBuffer text = new StringBuffer("Verbleibende Frei-SMS: ");

        if (getRemainingSMS() == -1 ) {
            text.append("?");
        } else {
            text.append(remsms);
        }

        if (this.getMaxFreeSMS() == -1 ) {
            text.append("/?");
        } else {
            text.append("/").append(maxfreesms);
        }
        return text.toString();
    }

    public int getMaxSMSLength() {
        return MAX_SMS_LENGTH;
    }

    public int countSms(String smsText) {
        return (smsText.length() > 0) ? ((smsText.length() + 160 - 1) / 160) : 0;         //ceiled division
    }

    public void checkSmsSenderNameConstraints(String senderName) throws Exception {
    }

    public void checkSmsSenderNameCharacters(String text) throws Exception {
        char[] checkname = text.toCharArray();
        for (int i = 0; i < checkname.length; i++) {
            if ((checkname[i] >= '0' && checkname[i] <= '9') && text.startsWith("00")) {
            } else {
                throw new Exception("Die Handynummer muss im Format 0049... angegeben werden z.B. 0049176111111");
            }
        }
    }

    public int send(SmsData Sms) throws Exception {
        int SenderMode = 0; //0=Phonenumber, 1=Text( SenderName )
        gui = Sms.getGui();
                
        if (Sms.getSenderName().length() > 0) {
            SenderMode = 1;
        }      

        try {
            gui.debug("starte sendSMSGMX()" + (Sms.isSimulation() ? " SIMULATION!" : ""));
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

            NetworkHandler connection = new NetworkHandler(Sms.getUsername(), Sms.getPassword(), gui, this);
            gui.setWaitScreenText("Login wird geladen...");
            String smsRecv = checkRecv(Sms.getSmsRecv());
            //#if Test
//#             // Output only on developer site, message contains sensitive data
//#             gui.debug("Empf\u00E4nger-Handynummer: " + smsRecv);
            //#else
            gui.debug("Empf\u00E4nger-Handynummer: " + smsRecv.substring(0, 6) + "*******");
            //#endif

            gui.setWaitScreenText("Zugangsdaten werden gepr\u00FCft...");

            url = "https://sms-submission-service.gmx.de/sms-submission-service/gmx/sms/2.0/SmsCapabilities";

            for (int i = 0; i < 2; i++) {
                try {
                    connection.httpHandler("GET", url, "sms-submission-service.gmx.de", "", true);
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
            
            //Check user credentials
            String response = connection.getContent();
            if (response.indexOf("authentication error") != -1) {
                Exception ex = new Exception("Zugangsdaten falsch!");
                throw ex;
            }
            gui.setWaitScreenText("Login erfolgreich");
            
            //Parse Response (Remaining SMS and maximal SMS per month)
            parseSmsCount(response);
            //Calculate remaining SMS after sending SMS
            remsms = remsms - countSms(Sms.getSmsText());

            if (resumeSms != Sms && remsms < 0) {
                //not enough free SMS and no Resume in Progress. Abort and ask user what to do
                resumeSms = Sms;
                return -1;
            } else {
                //Resume in progress or enough freesms
                gui.setWaitScreenText("SMS wird gesendet...");
                resumeSms = null;
                if (SenderMode == 1) {
                    //send with phone number as sender
                    url = "https://sms-submission-service.gmx.de/sms-submission-service/gmx/sms/2.0/SmsSubmission?"
                            + "destinationNumber=" + smsRecv + "&sourceNumber=" + Sms.getSenderName()
                            + "&clientType=GMX_ANDROID&messageType=SMS&options=SEND_ERROR_NOTIFY_MAIL";
                } else {
                    //without sourceNumber "GMX SMS" will be the sender
                    url = "https://sms-submission-service.gmx.de/sms-submission-service/gmx/sms/2.0/SmsSubmission?"
                            + "destinationNumber=" + smsRecv + "&clientType=GMX_ANDROID&messageType=SMS&options=SEND_ERROR_NOTIFY_MAIL";
                }
                gui.debug(url);
                
                if (!Sms.isSimulation()) {
                    connection.httpHandler("POST", url, "sms-submission-service.gmx.de", Sms.getSmsText(), true);
                }
                if (connection.getResponseCode() == HttpsConnection.HTTP_ACCEPTED) {
                    gui.setWaitScreenText("SMS wurde versandt!");
                    
                }

                saveItem(REMAINING_SMS_FIELD, remsms + "");
                saveItem(MAX_FREE_SMS_FIELD, maxfreesms + "");
                gui.debug("Fertig mit sendSMSGMX, Dauer: " + (System.currentTimeMillis() - totaltime) + " ms");
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

    private void parseSmsCount(String response) {
        //Parse Response MAX_MONTH_FREE_SMS=10&MONTH_FREE_SMS=4&LIMIT_MONTH_AUTO_SMS=100&AVAILABLE_FREE_SMS=6&USER_TYPE=GMX_FREEMAIL&MAX_WEBCENT=0&MONTH_PAY_SMS=0&MONTH_AUTO_SMS=0
        final String MAX_SMS = "MAX_MONTH_FREE_SMS=";
        final String REM_SMS = "MONTH_FREE_SMS="; //Amount of already used free SMS
        try {
            gui.debug(response);
            int index = response.indexOf(MAX_SMS);
            int endindex = response.indexOf("&", index);
            maxfreesms = Integer.parseInt(response.substring(index + MAX_SMS.length(), endindex));

            index = response.indexOf(REM_SMS, endindex);
            remsms = Integer.parseInt(response.substring(index + REM_SMS.length(), response.indexOf("&", index)));
            remsms = maxfreesms - remsms;
        } catch (NumberFormatException e) {
            gui.debug(e.getMessage());
        }
    }

    public boolean resumeSending() throws Exception {
        gui.debug("Resume SMS sending process");
        if (resumeSms != null) {
            send(resumeSms);
            return true;
        } else {
            return false;
        }

    }

    protected String checkRecv(String smsRecv) {
        if (smsRecv.startsWith("0")) {
            smsRecv = "0049".concat(smsRecv.substring(1));
        }
        if (smsRecv.startsWith("+")) {
            smsRecv = "00".concat(smsRecv.substring(1));
        }
        return smsRecv;
    }

    //<editor-fold defaultstate="collapsed" desc="Save- and Get-Function for Networkhandler">
    void saveSetting(String itemName, String value) {
        saveItem(itemName, value);
    }

    String getSetting(String itemName) {
        return getItem(itemName);
    }
    //</editor-fold>
}
