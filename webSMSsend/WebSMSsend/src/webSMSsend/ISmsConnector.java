/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webSMSsend;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public interface ISmsConnector {
public int RemainingSMS();
public void Initialize(String userName, String passWord, IGui Gui);
public void Send(String smsRecv, String smsText)throws Exception;
public void Send(String smsRecv, String smsText, String senderName, boolean simulation)throws Exception;
}
