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

import java.util.Hashtable;

/**
 *
 * @author redrocketracoon@googlemail.com
 */
public class ConnectorSettings implements IConnectorSettings {

    Hashtable items;
    UserAccount userAccount;

    public ConnectorSettings(UserAccount userAccount) {
        this.userAccount = userAccount;
        items = new Hashtable();
    }

    void addKeyValuePair(String key, String value){
        items.put(key, value);
    }

    Hashtable getConnectorSettings(){
        return items;
    }

    public void saveItem(String itemName, String content) {
        if (items.containsKey(itemName)){
            items.remove(itemName);
        }
        items.put(itemName, content);
        changed();
    }

    public String getItem(String itemName) {
        return (String) items.get(itemName);
    }

    private void changed() {
        if (userAccount != null) {
            userAccount.update(this);
        }
    }
}
