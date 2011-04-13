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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class PickPhoneNumberList extends List implements CommandListener {
    Displayable parentDisplay;
    MIDlet parentMidlet;
    IPhoneNumberPickerObserver observer;
    Command backCmd = new Command("Zur\u00FCck", Command.BACK, 1);

    /**
     *
     * @param midlet
     * @param parent
     * @param observer
     */
    public PickPhoneNumberList(MIDlet midlet, Displayable parent, IPhoneNumberPickerObserver observer) {
        super("Telefonnummer ausw\u00E4hlen", IMPLICIT);
        this.parentDisplay = parent;
        this.parentMidlet = midlet;
        this.observer = observer;

        addCommand(backCmd);
        setCommandListener(this);
    }

    /**
     *
     * @param phoneNumbers
     */
    public void addPhoneNumbers(String[] phoneNumbers) {
        for (int i = 0; i < phoneNumbers.length; i++) {
            append(phoneNumbers[i], null);
        }
    }

    /**
     *
     * @param c
     * @param d
     */
    public void commandAction(Command c, Displayable d) {
        if (c == backCmd) {
            Display.getDisplay(parentMidlet).setCurrent(parentDisplay);
        } else if (c == List.SELECT_COMMAND) {
            observer.SetPhoneNumber(this.getString(getSelectedIndex()));
        }
    }
}
