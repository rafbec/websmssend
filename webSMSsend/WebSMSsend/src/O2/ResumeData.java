/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package O2;

import ConnectorBase.SmsData;

/**
 * Saves Data necessary to send SMS after user input
 * @author @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class ResumeData {
    private int SenderMode;
    private String postRequest;
    private String smsRecv;
    private SmsData Sms;
    private NetworkHandler connection;

    /**
     *
     * @param SenderMode
     * @param postRequest
     * @param smsRecv
     * @param Sms
     * @param connection
     */
    public ResumeData(int SenderMode, String postRequest, String smsRecv, SmsData Sms, NetworkHandler connection) {
        this.SenderMode = SenderMode;
        this.postRequest = postRequest;
        this.smsRecv = smsRecv;
        this.Sms = Sms;
        this.connection = connection;
    }

    /**
     * @return the SenderMode
     */
    public int getSenderMode() {
        return SenderMode;
    }

    /**
     * @return the postRequest
     */
    public String getPostRequest() {
        return postRequest;
    }

    /**
     * @return the smsRecv
     */
    public String getSmsRecv() {
        return smsRecv;
    }

    /**
     * @return the Sms
     */
    public SmsData getSms() {
        return Sms;
    }

    /**
     * @return the connection
     */
    public NetworkHandler getConnection() {
        return connection;
    }

    

}
