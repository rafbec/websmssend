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
public abstract class SmsConnector implements ISmsConnector {

    protected static final String REMAINING_SMS_FIELD = "RemainingSMS";
    protected static final String MAX_FREE_SMS = "MaxFreeSMS";

    protected IGui gui = null;
    protected int remsms = -1;
    protected int maxfreesms = -1;
    protected ConnectorSpecification specs = new ConnectorSpecification();

    public int getRemainingSMS() {
        try {
            String remsms = gui.getItem(getName() + REMAINING_SMS_FIELD);
            this.remsms = Integer.parseInt(remsms);
        } catch (Exception ex) {
            remsms=-1; //-1 if no value could be restored
        }
        return remsms;
    }

    public int getMaxFreeSMS() {
        try {
            String maxfreesms = gui.getItem(getName() + MAX_FREE_SMS);
            this.maxfreesms = Integer.parseInt(maxfreesms);
        } catch (Exception ex) {
            maxfreesms = -1; //-1 if no value could be restored
        }
        return maxfreesms;
    }

    public boolean hasProperty(int Property) {
        return specs.HasPropterty(Property);
    }

    protected void SaveItem(String ItemName, String Content)
    {
        gui.saveItems(getName() + ItemName, Content); //saves Field with Connectorname
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
