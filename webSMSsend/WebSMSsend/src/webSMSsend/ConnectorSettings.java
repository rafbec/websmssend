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

package webSMSsend;

/**
 * Gives Sms-Connectors the ability to save settings
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class ConnectorSettings {
    /**
     *
     * @param itemName
     * @param content
     */
    public static void saveItems(String itemName, String content) {
        String[] data = {content};
        ioSettings.saveData(ioSettings.MakeFieldName(itemName), data, data.length);
    }

    /**
     *
     * @param itemName
     * @return Value of Setting
     */
    public static String getItem(String itemName) {
        return ioSettings.getData(ioSettings.MakeFieldName(itemName), 1);
    }
}
