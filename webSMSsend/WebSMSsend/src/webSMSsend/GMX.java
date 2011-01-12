/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webSMSsend;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class GMX extends SmsConnector {

    public void Send(String smsRecv, String smsText) throws Exception {
        Send(smsRecv, smsText, "" , false);
    }

    public void Send(String smsRecv, String smsText, String senderName, boolean simulation) throws Exception {
    }



}
