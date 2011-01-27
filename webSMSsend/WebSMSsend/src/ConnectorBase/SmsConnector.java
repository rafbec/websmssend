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

    // Maximum SMS length allowed
    protected static final int MAX_SMS_LENGTH = 1800;
    protected static final String REMAINING_SMS_FIELD = "RemainingSMS";
    protected static final String MAX_FREE_SMS = "MaxFreeSMS";

    protected IGui gui_ = null;
    protected int remsms_ = -1;
    protected int maxfreesms_ = -1;
    protected String password_;
    protected String username_;

    public void Initialize(String userName, String passWord, IGui Gui) {
        password_ = passWord;
        username_ = userName;
        gui_ = Gui;
    }

    public int getRemainingSMS() {
        try {
            String remsms = gui_.GetItem(getName() + REMAINING_SMS_FIELD);
            remsms_ = Integer.parseInt(remsms);
        } catch (Exception ex) {
            remsms_=-1; //-1 if no value could be restored
        }
        return remsms_;
    }

    public int getMaxFreeSMS() {
        try {
            String maxfreesms = gui_.GetItem(getName() + MAX_FREE_SMS);
            maxfreesms_ = Integer.parseInt(maxfreesms);
        } catch (Exception ex) {
            maxfreesms_ = -1; //-1 if no value could be restored
        }
        return maxfreesms_;
    }

    protected void SaveItem(String ItemName, String Content)
    {
        gui_.SaveItem(getName() + ItemName, Content); //saves Field with Connectorname
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

    public int getMaxSMSLength() {
        return MAX_SMS_LENGTH;
    }
}
