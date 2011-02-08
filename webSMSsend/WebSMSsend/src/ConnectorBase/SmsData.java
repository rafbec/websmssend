/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectorBase;

import webSMSsend.IGui;

/**
 * Smsdata bundles all necessary information for connectors to send a sms
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class SmsData {

    private String username;
    private String password;
    private IGui gui;
    private String smsrecv;
    private String smstext;
    private String sendername;
    private boolean simualtion;

    /**
     *
     */
    public SmsData() {
    }

    /**
     *
     * @param username
     * @param password
     * @param gui
     * @param smsrecv
     * @param smstext
     * @param sendername
     * @param simualtion
     */
    public SmsData(String username, String password, IGui gui, String smsrecv, String smstext, String sendername, boolean simualtion) {
        this.username = username;
        this.password = password;
        this.gui = gui;
        this.smsrecv = smsrecv;
        this.smstext = smstext;
        this.sendername = sendername;
        this.simualtion = simualtion;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the gui
     */
    public IGui getGui() {
        return gui;
    }

    /**
     * @param gui the gui to set
     */
    public void setGui(IGui gui) {
        this.gui = gui;
    }

    /**
     * @return the smsrecv
     */
    public String getSmsrecv() {
        return smsrecv;
    }

    /**
     * @param smsrecv the smsrecv to set
     */
    public void setSmsrecv(String smsrecv) {
        this.smsrecv = smsrecv;
    }

    /**
     * @return the smstext
     */
    public String getSmstext() {
        return smstext;
    }

    /**
     * @param smstext the smstext to set
     */
    public void setSmstext(String smstext) {
        this.smstext = smstext;
    }

    /**
     * @return the sendername
     */
    public String getSendername() {
        return sendername;
    }

    /**
     * @param sendername the sendername to set
     */
    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    /**
     * @return the simualtion
     */
    public boolean isSimualtion() {
        return simualtion;
    }

    /**
     * @param simualtion the simualtion to set
     */
    public void setSimualtion(boolean simualtion) {
        this.simualtion = simualtion;
    }
}
