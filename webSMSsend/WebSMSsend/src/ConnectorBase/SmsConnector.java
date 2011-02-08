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
public abstract class SmsConnector implements ISmsConnector {

    protected static final String REMAINING_SMS_FIELD = "RemainingSMS";
    protected static final String MAX_FREE_SMS = "MaxFreeSMS";

    protected IGui gui = null;
    protected int remsms = -1;
    protected int maxfreesms = -1;
    protected ConnectorSpecification specs = new ConnectorSpecification();

    public int getRemainingSMS() {
        try {
            String remsms = gui.GetItem(getName() + REMAINING_SMS_FIELD);
            this.remsms = Integer.parseInt(remsms);
        } catch (Exception ex) {
            remsms=-1; //-1 if no value could be restored
        }
        return remsms;
    }

    public int getMaxFreeSMS() {
        try {
            String maxfreesms = gui.GetItem(getName() + MAX_FREE_SMS);
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
        gui.SaveItem(getName() + ItemName, Content); //saves Field with Connectorname
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
