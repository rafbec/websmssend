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

package ConnectorBase;

import Storage.IConnectorSettings;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public interface ISmsConnector {
    /**
     *
     */
    public static final int NO_MORE_FREE_SMS = -1;
    /**
     *
     */
    public static final int SMS_SENT = 0;

    /**
     *
     * @return
     */
    public int getMaxSMSLength();

    /**
     *
     * @return
     */
    public abstract int getRemainingSMS();

    /**
     *
     * @return
     */
    public abstract int getMaxFreeSMS();

    /**
     *
     * @return
     */
    public abstract String getRemSmsText();

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
     * @return true = resuming was possible false = no resume data, resuming was not successfull
     * @throws Exception
     */
    public abstract boolean resumeSending() throws Exception;

    /**
     *
     * @param smsText
     * @return
     */
    public int countSms(String smsText);

    /**
     *
     * @param smsText
     * @return
     */
    public int countSmsTextCharacters(String smsText);

    /**
     *
     * @param connectorSettings
     */
    public void setConnectorSettings(IConnectorSettings connectorSettings);
}
