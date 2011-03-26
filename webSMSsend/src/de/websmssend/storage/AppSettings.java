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
package de.websmssend.storage;

/**
 * Stores all App specific settings
 * @author redrocketracoon@googlemail.com
 */
public class AppSettings {

    private AppSettingsManager appSettingsManager;
    private int activeAccount;
    private boolean debug;
    private boolean autoUpdate;
    private boolean saveEachCharacter;
    private String tempSmsText;
    private String tempSmsTo;
    private String lastSmsText;
    private String lastSmsTo;

    public AppSettings(AppSettingsManager appSettingsManager) {
        this.appSettingsManager = appSettingsManager;
    }

    /**
     * @return the activeAccount
     */
    public int getActiveAccount() {
        return activeAccount;
    }

    /**
     * @param activeAccount the activeAccount to set
     */
    public void setActiveAccount(int activeAccount) {
        this.activeAccount = activeAccount;
        changed();
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        changed();
    }

    /**
     * @return the autoUpdate
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    /**
     * Determines the app's automatic update behaviour
     * @param autoUpdate the autoUpdate to set
     */
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        changed();
    }

    /**
     * @return the saveEachCharacter
     */
    public boolean isSaveEachCharacter() {
        return saveEachCharacter;
    }

    /**
     * @param saveEachCharacter the saveEachCharacter to set
     */
    public void setSaveEachCharacter(boolean saveEachCharacter) {
        this.saveEachCharacter = saveEachCharacter;
        changed();
    }

    /**
     * @return the tempSmsText
     */
    public String getTempSmsText() {
        return tempSmsText == null ? "" : tempSmsText;
    }

    /**
     * @param tempSmsText the tempSmsText to set
     */
    public void setTempSmsText(String tempSmsText) {
        this.tempSmsText = tempSmsText;
        changed();
    }

    /**
     * @return the tempSmsTo
     */
    public String getTempSmsTo() {
        return tempSmsTo == null ? "" : tempSmsTo;
    }

    /**
     * @param tempSmsTo the tempSmsTo to set
     */
    public void setTempSmsTo(String tempSmsTo) {
        this.tempSmsTo = tempSmsTo;
        changed();
    }

    /**
     * @return the lastSmsText
     */
    public String getLastSmsText() {
        return lastSmsText == null ? "" : lastSmsText;
    }

    /**
     * @param lastSmsText the lastSmsText to set
     */
    public void setLastSmsText(String lastSmsText) {
        this.lastSmsText = lastSmsText;
        changed();
    }

    /**
     * @return the lastSmsTo
     */
    public String getLastSmsTo() {
        return lastSmsTo == null ? "" : lastSmsTo;
    }

    /**
     * @param lastSmsTo the lastSmsTo to set
     */
    public void setLastSmsTo(String lastSmsTo) {
        this.lastSmsTo = lastSmsTo;
        changed();
    }

    private void changed(){
        appSettingsManager.update(this);
    }
}
