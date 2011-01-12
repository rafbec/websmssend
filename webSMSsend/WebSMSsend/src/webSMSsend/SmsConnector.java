/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webSMSsend;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public abstract class SmsConnector implements ISmsConnector {





protected IGui gui_ = null;
protected int remsms_ = 0;
protected String password_;
protected String username_;

    public void Initialize(String userName, String passWord, IGui Gui) {
        password_ = passWord;
        username_ = userName;
        gui_ = Gui;
    }

    public int RemainingSMS() {
        return remsms_;
    }

    public abstract void Send(String smsRecv, String smsText) throws Exception;

    public abstract void Send(String smsRecv, String smsText, String senderName, boolean simulation) throws Exception ;

    protected int CountSMS(String smsText) {
        return gui_.CountSMS(smsText);
    }
}
