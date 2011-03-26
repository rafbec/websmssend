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

import de.websmssend.connector.base.SmsData;

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
