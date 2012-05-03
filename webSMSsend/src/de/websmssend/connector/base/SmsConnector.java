/*
 *
 *
    Copyright 2012 redrocketracoon@googlemail.com
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

package de.websmssend.connector.base;

import de.websmssend.storage.IConnectorSettings;
import de.websmssend.IApp;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public abstract class SmsConnector implements ISmsConnector {

    protected static final String REMAINING_SMS_FIELD = "RemainingSMS";
    protected static final String MAX_FREE_SMS_FIELD = "MaxFreeSMS";

    protected IApp gui = null;
    protected int remsms = -1;
    protected int maxfreesms = -1;
    protected ConnectorSpecification specs = new ConnectorSpecification();
    protected IConnectorSettings connectorSettings;

    public int getRemainingSMS() {
        try {
            String remsms = getItem(REMAINING_SMS_FIELD);
            this.remsms = Integer.parseInt(remsms);
        } catch (Exception ex) {
            remsms=-1; //-1 if no value could be restored
        }
        return remsms;
    }

    public int getMaxFreeSMS() {
        try {
            String maxfreesms = getItem(MAX_FREE_SMS_FIELD);
            this.maxfreesms = Integer.parseInt(maxfreesms);
        } catch (Exception ex) {
            maxfreesms = -1; //-1 if no value could be restored
        }
        return maxfreesms;
    }

    public boolean hasProperty(int Property) {
        return specs.HasPropterty(Property);
    }

    public void setConnectorSettings(IConnectorSettings connectorSettings) {
        this.connectorSettings = connectorSettings;
    }

    public int countSmsTextCharacters(String smsText) {
        return smsText.length();
    }

    protected void saveItem(String ItemName, String Content)
    {
        if (connectorSettings != null) {
            connectorSettings.saveItem(ItemName, Content);
        }
    }

    protected String getItem(String ItemName){
        if (connectorSettings != null){
            return connectorSettings.getItem(ItemName);
        } else{
            return "";
        }
    }

    protected String checkRecv(String smsRecv) {
        if (smsRecv.startsWith("0")) {
            if (smsRecv.startsWith("00")) {
                smsRecv = "+".concat(smsRecv.substring(2));
                return smsRecv;
            }
            smsRecv = "+49".concat(smsRecv.substring(1));
        }
        return smsRecv;
    }
}
