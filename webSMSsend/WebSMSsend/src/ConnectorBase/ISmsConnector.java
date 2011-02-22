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

import webSMSsend.IGui;

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
    public int getRemainingSMS(IGui gui);

    /**
     *
     * @return
     */
    public int getMaxFreeSMS(IGui gui);

    /**
     *
     * @return
     */
    public String getRemSmsText(IGui gui);

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
    public int CountSms(String smsText);
}
