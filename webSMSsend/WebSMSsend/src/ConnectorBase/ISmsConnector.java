/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectorBase;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public interface ISmsConnector {
// Maximum SMS length

    /**
     *
     * @return
     */
    public int getMaxSMSLength();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public int getRemainingSMS();

    /**
     *
     * @return
     */
    public int getMaxFreeSMS();

    /**
     *
     * @return
     */
    public String getRemSmsText();

    /**
     *
     * @return
     */
    public String getPasswordFieldLabel();

    /**
     * checks if a connector has one of the Properties specified in ConnectorBase.Properties
     * @param Property
     * @return true or false
     */
    public boolean hasProperty(int Property);

    /**
     * sends SMS
     * @param Sms
     * @return 0 = SMS sent successfully; -1 = SMS not sent having no free SMS available;
     * @throws Exception
     */
    public abstract int send(SmsData Sms) throws Exception;

    /**
     * resumes interrupted send(SmsData Sms) after asking user
     */
    public abstract void resumeSending() throws Exception;

    /**
     *
     * @param smsText
     * @return
     */
    public int CountSms(String smsText);
}
