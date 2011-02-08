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
/**
 * checks if a connector has one of the Properties specified in ConnectorBase.Properties
 * @param Property
 * @return true or false
 */
public boolean hasProperty(int Property) ;
public abstract void Send(SmsData Sms)throws Exception;
public int CountSms(String smsText);
}
