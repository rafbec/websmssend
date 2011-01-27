/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ConnectorBase;

import webSMSsend.IGui;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public interface ISmsConnector {
// Maximum SMS length
public int getMaxSMSLength();
public String getName();
public int getRemainingSMS();
public int getMaxFreeSMS();
public String getRemSmsText();
public String getPasswordFieldLabel();
public void Initialize(String userName, String passWord, IGui Gui);
public void Send(String smsRecv, String smsText)throws Exception;
public void Send(String smsRecv, String smsText, String senderName, boolean simulation)throws Exception;
public int CountSms(String smsText);
}
