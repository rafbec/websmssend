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
package Storage;

/**
 *
 * @author redrocketracoon@googlemail.com
 */
public class UserAccount {

    private UserAccountManager accountManager;
    private String accountName;
    private String userName;
    private String passWord;
    private int provider;
    private int senderMode;
    private String senderName;

    /**
     *
     * @param accountManager
     */
    public void setAccountManager(UserAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * @return the accountName
     */
    public String getAccountName() {
        return accountName == null ? "" : accountName;
    }

    /**
     * @param accountName the accountName to set
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
        changed();

    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName == null ? "" : userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
        changed();
    }

    /**
     * @return the passWord
     */
    public String getPassWord() {
        return passWord == null ? "" : passWord;
    }

    /**
     * @param passWord the passWord to set
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
        changed();
    }

    /**
     * @return the provider
     */
    public int getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(int provider) {
        this.provider = provider;
        changed();
    }

    /**
     * @return the senderMode
     */
    public int getSenderMode() {
        return senderMode;
    }

    /**
     * @param senderMode the senderMode to set
     */
    public void setSenderMode(int senderMode) {
        this.senderMode = senderMode;
        changed();
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName == null ? "" : senderName;
    }

    /**
     * @param senderName the senderName to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
        changed();
    }

    private void changed() {
        if (accountManager != null) {
            accountManager.update(this);
        }
    }
}
