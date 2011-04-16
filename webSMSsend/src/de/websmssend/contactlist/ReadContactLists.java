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

import javax.microedition.lcdui.*;
import javax.microedition.pim.*;
import java.util.*;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author Copyright 2011 redrocketracoon@googlemail.com
 */
public class ReadContactLists extends List implements Runnable, CommandListener {

    private final String[] NAME_FIELDS = {"FAMILY", "GIVEN", "OTHER", "PREFIX", "SUFFIX"};
    PIM pim;
    PIMList pimlist;
    Contact contact = null;
    Enumeration enumeration;
    Displayable parentDisplay;
    MIDlet parentMidlet;
    IPhoneNumberPickerObserver observer;
    List list;
    String contactName = "";
    String contactNo = "";
    Vector contacts;
    Thread thread = null;
    Command DebugCmd = new Command("Debug", Command.OK, 0);
    Command exitCmd = new Command("Zur\u00FCck", Command.EXIT, 1);

    /**
     * Constructor. Reads contactlists only once.
     * @param parentMidlet
     * @param parentDisplay
     * @param observer
     */
    public ReadContactLists(MIDlet parentMidlet, Displayable parentDisplay, IPhoneNumberPickerObserver observer) {
        super("Kontaktliste", IMPLICIT);
        this.parentDisplay = parentDisplay;
        this.parentMidlet = parentMidlet;
        this.observer = observer;

        if (contacts != null) {
            for (int i = 0; i < contacts.size(); i++) {
                append(((ContactItem) contacts.elementAt(i)).getName(), null);
            }
        } else {
            pim = PIM.getInstance();
            thread = new Thread(this);
            thread.start();
        }

        addCommand(exitCmd);
        addCommand(DebugCmd);
        setCommandListener(this);
    }

    /**
     *
     */
    public void run() {
        readContacts();
    }

    /**
     * Reads all contacts and shows them in the list
     */
    public void readContacts() {
        contacts = new Vector();
        ContactItem conItem;
        String conName;
        String conForName;
        String[] conAllNames;
        String[] conPhoneNummbers;
        String[] lists = pim.listPIMLists(PIM.CONTACT_LIST);
        for (int i = 0; i < lists.length; i++) {
            try {
                pimlist = pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY, lists[i]);
            } catch (Exception e) {
            }

            try {
                enumeration = pimlist.items();
            } catch (Exception e) {
            }

            while (enumeration.hasMoreElements()) {
                contact = (Contact) enumeration.nextElement();
                conItem = new ContactItem();
                try {
                    conName = "";
                    conForName = "";
                    conAllNames = new String[0];

                    try {
                        //read all Names
                        try {
                            conAllNames = contact.getStringArray(Contact.NAME, Contact.ATTR_NONE);
                        } catch (Exception e) {
                            debug("ATTR_NONE: " + e.getMessage(),conItem);
                        }

                        if (conAllNames.length > Contact.NAME_FAMILY) {
                            conName = conAllNames[Contact.NAME_FAMILY];
                        }

                        if (conAllNames.length > Contact.NAME_GIVEN) {
                            conForName = conAllNames[Contact.NAME_GIVEN];
                        }

                        debug("Anzahl: " + conAllNames.length,conItem);

                        for (int y = 0; y < conAllNames.length; y++) {
                            if (y < NAME_FIELDS.length) {
                                debug(NAME_FIELDS[y]+": "+conAllNames[y],conItem);
                            } else {
                                debug("PROPRIETARY: "+conAllNames[y],conItem);
                            }
                        }
                    } catch (Exception e) {
                        debug("Fehler beim Einlesen des Namens:\r\n" + e.getMessage(),conItem);
                    }

                    if (conName == null) {
                        conName = "";
                    }
                    if (conForName == null) {
                        conForName = "";
                    }

                    if (conName.length() != 0) {
                        if (conForName.length() != 0) {
                            conItem.setName(conName + ", " + conForName);
                        } else {
                            conItem.setName(conName);
                        }
                    } else if (conForName.length() != 0) {
                        conItem.setName(conForName);
                    } else { //Backup Method find first non null value and set as name
                        for (int y = 0; y < conAllNames.length; y++) {
                            if (conAllNames[y].length() !=0) {
                                conItem.setName(conAllNames[y]);
                                debug("AnyName: " + conItem.getName(), conItem);
                                break;
                            }
                        }
                        
                        //if all names are null try Organisation field
                        if (conItem.getName().length() == 0){
                            try{
                               if(pimlist.isSupportedField(Contact.ORG)){
                                   conItem.setName(contact.getString(Contact.ORG, Contact.ATTR_NONE));
                               }
                               debug("Organisation: " + conItem.getName(), conItem);
                            }catch(Exception ex){
                                debug("Organisation: " + ex.getMessage(),conItem);
                            }
                        }
                    }
                } catch (Exception e) {
                    debug("ContactItem: " + e.getMessage(),conItem);
                }

                int phoneNos = contact.countValues(Contact.TEL);
                try {
                    conPhoneNummbers = new String[phoneNos];
                    for (int y = 0; y < phoneNos; y++) {
                        conPhoneNummbers[y] = contact.getString(contact.TEL, y);
                    }
                    conItem.setPhoneNumbers(conPhoneNummbers);
                } catch (Exception e) {
                }

                if (conItem.getName().equals("")) { //if no name could be found show first phone number
                    if (conItem.getPhoneNumbers() != null) {
                        if (conItem.getPhoneNumbers().length > 0) {
                            append("" + conItem.getPhoneNumbers()[0], null);
                            contacts.addElement(conItem);
                        }
                    } else { //ignore contact since no information could be retrieved
                    }
                } else {
                    append(conItem.getName() + "", null);
                    contacts.addElement(conItem);
                }
            }
        }
    }

    private void debug(String debugMessage, ContactItem contact){
        contact.addDebugMsg(debugMessage);
    }

    /**
     *
     * @param c
     * @param d
     */
    public void commandAction(Command c, Displayable d) {
        if (c == exitCmd) {
            try {
                Display.getDisplay(parentMidlet).setCurrent(parentDisplay);
            } catch (Exception e) {
            }
        } else if (c == List.SELECT_COMMAND) {
            ContactItem item = (ContactItem) contacts.elementAt(getSelectedIndex());

            if (item.getPhoneNumbers().length == 1) {
                observer.SetPhoneNumber(item.getPhoneNumbers()[0]);
            } else {
                PickPhoneNumberList picklist = new PickPhoneNumberList(parentMidlet, this, observer);
                picklist.addPhoneNumbers(item.getPhoneNumbers());
                Display.getDisplay(parentMidlet).setCurrent(picklist);
            }
        } else if (c == DebugCmd) {
            try {
                ContactItem item = (ContactItem) contacts.elementAt(getSelectedIndex());
                Display.getDisplay(parentMidlet).setCurrent(new Alert("Debug", item.getDebugMessages(), null, AlertType.CONFIRMATION), Display.getDisplay(parentMidlet).getCurrent());
            } catch (Exception ex) {
            }
        }
    }
}
