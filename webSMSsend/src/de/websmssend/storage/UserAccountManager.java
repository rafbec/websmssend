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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author redrocketracoon@googlemail.com
 */
public class UserAccountManager extends StorageManager {

    private Vector userAccounts;

    /**
     *
     */
    public UserAccountManager() {
        this.recordStoreName = "UserAccounts";
        try {
            userAccounts = readUserAccounts();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param account
     * @return true if successful otherwise false
     */
    public boolean addUserAccount(UserAccount account) {
        boolean added = false;
        if (userAccounts != null) {
            try {
                account.setAccountManager(this);
                userAccounts.addElement(account);
                added = saveUserAccounts();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
        }
        return added;
    }

    /**
     *
     * @param number
     * @return true if successful otherwise false
     */
    public boolean deleteUserAccount(int number) {
        boolean deleted = false;
        if (userAccounts != null) {
            try {
                userAccounts.removeElementAt(number);
                deleted = saveUserAccounts();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
        }
        return deleted;
    }

    /**
     *
     * @param number
     * @return
     */
    public UserAccount getUserAccount(int number) {
        if (userAccounts != null) {
            if (number < userAccounts.size()) {
                return (UserAccount) userAccounts.elementAt(number);
            }
        }
        return null;
    }

    /**
     *
     * @return List of all account names
     */
    public String[] getAccountNames() {
        if (userAccounts != null) {
            String[] names = new String[userAccounts.size()];
            for (int i = 0; i < userAccounts.size(); i++) {
                names[i] = ((UserAccount) userAccounts.elementAt(i)).getAccountName();
            }
            return names;
        } else {
            return null;
        }
    }

    /**
     *
     * @param userAccount
     * @return user account number, returns -1 if the account is not found.
     */
    public int getAccountNumber(UserAccount userAccount) {
        return userAccounts.indexOf(userAccount);
    }

    /**
     *
     * @return Count of user accounts
     */
    public int getAccountCount() {
        if (userAccounts != null) {
            return userAccounts.size();
        } else {
            return 0;
        }
    }

    /**
     * notifys AccountManager of any changes in one of the UserAccount objects
     * @param changedAccount
     */
    public void update(UserAccount changedAccount) {
        try {
            saveUserAccounts();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    private Vector readUserAccounts() {
        Vector accounts = null;
        try {
            byte[] data = this.getData();
            accounts = changeAccountsFromByteArray(data);
            return accounts;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        accounts = new Vector();
        return accounts;
    }

    private boolean saveUserAccounts() {
        boolean saved = false;
        if (userAccounts != null) {
            try {
                byte[] data = changeAccountsToByteArray();
                this.saveData(data);
                saved = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return saved;
    }

    private byte[] changeAccountsToByteArray() {
        byte[] data = null;
        UserAccount account;
        Hashtable connectorSettings;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            //"Header" writes number of Accounts which will follow
            dos.writeInt(userAccounts.size());

            for (int i = 0; i < userAccounts.size(); i++) {
                account = (UserAccount) userAccounts.elementAt(i);
                dos.writeUTF(account.getAccountName());
                dos.writeUTF(account.getUserName());
                dos.writeUTF(account.getPassWord());
                dos.writeUTF(account.getSenderName());
                dos.writeInt(account.getProvider());
                dos.writeInt(account.getSenderMode());

                connectorSettings = account.getConnectorSettings().getConnectorSettings();
                dos.writeInt(connectorSettings.size());

                Enumeration keys = connectorSettings.keys();
                Enumeration values = connectorSettings.elements();

                while (keys.hasMoreElements() && values.hasMoreElements()){
                    dos.writeUTF((String) keys.nextElement());
                    dos.writeUTF((String) values.nextElement());
                }
            }

            data = baos.toByteArray();

            dos.close();
            baos.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    private Vector changeAccountsFromByteArray(byte[] data) {
        Vector accounts = new Vector();
        UserAccount account;
        ConnectorSettings connectorSettings;
        int conSetNumber;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);

            int accountNumber = dis.readInt();

            for (int i = 0; i < accountNumber; i++) {
                account = new UserAccount();
                account.setAccountName(dis.readUTF());
                account.setUserName(dis.readUTF());
                account.setPassWord(dis.readUTF());
                account.setSenderName(dis.readUTF());
                account.setProvider(dis.readInt());
                account.setSenderMode(dis.readInt());

                connectorSettings = account.getConnectorSettings();
                conSetNumber = dis.readInt();
                for (int j = 0; j < conSetNumber; j++) {
                    connectorSettings.addKeyValuePair(dis.readUTF(), dis.readUTF());
                }

                accounts.addElement(account);
                account.setAccountManager(this);
            }

            dis.close();
            bais.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accounts;
    }
}
