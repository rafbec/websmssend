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
 * SmsData bundles all necessary information for connectors to send an SMS
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class SmsData {

    private String username;
    private String password;
    private IGui gui;
    private String smsRecv;
    private String smsText;
    private String senderName;
    private boolean simulation;

    /**
     *
     * @param username
     * @param password
     * @param gui
     * @param smsRecv
     * @param smsText
     * @param senderName
     * @param simulation
     */
    public SmsData(String username, String password, IGui gui, String smsRecv, String smsText, String senderName, boolean simulation) {
        this.username = username;
        this.password = password;
        this.gui = gui;
        this.smsRecv = smsRecv;
        this.smsText = smsText;
        this.senderName = senderName;
        this.simulation = simulation;
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
     * @return the smsRecv
     */
    public String getSmsRecv() {
        return smsRecv;
    }

    /**
     * @param smsRecv the smsRecv to set
     */
    public void setSmsrecv(String smsRecv) {
        this.smsRecv = smsRecv;
    }

    /**
     * @return the smsText
     */
    public String getSmsText() {
        return smsText;
    }

    /**
     * @param smsText the smsText to set
     */
    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName the senderName to set
     */
    public void setSendername(String senderName) {
        this.senderName = senderName;
    }

    /**
     * @return the simulation
     */
    public boolean isSimulation() {
        return simulation;
    }

    /**
     * @param simulation the simulation to set
     */
    public void setSimualtion(boolean simulation) {
        this.simulation = simulation;
    }
}
