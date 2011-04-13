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

package de.websmssend.contactlist;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class ContactItem {

    private String[] phoneNumbers;
    private String name;
    private String allNamesDebug;

    /**
     * @return the phoneNumbers
     */
    public String[] getPhoneNumbers() {
        return phoneNumbers== null ? new String[0] : phoneNumbers;
    }

    /**
     * @param phoneNumbers the phoneNumbers to set
     */
    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name == null ? new String() : name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the allNamesDebug
     */
    public String getAllNamesDebug() {
        return allNamesDebug == null ? new String() : allNamesDebug;
    }

    /**
     * @param allNamesDebug the allNamesDebug to set
     */
    public void setAllNamesDebug(String allNamesDebug) {
        this.allNamesDebug = allNamesDebug;
    }

    

}
