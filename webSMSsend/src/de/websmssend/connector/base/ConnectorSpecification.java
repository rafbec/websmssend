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

package de.websmssend.connector.base;

import java.util.Vector;

/**
 *
 * @author Rafael
 */
public class ConnectorSpecification {

    private Vector properties_ = new Vector();

    public void AddProperty(int[] Properties) {
        for (int y = 0; y < Properties.length; y++) {
            properties_.addElement(new Integer(Properties[y]));
        }
    }

    public boolean HasPropterty(int Property) {
        if (properties_.contains(new Integer(Property))) {
            return true;
        } else {
            return false;
        }
    }
}
