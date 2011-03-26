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

/**
 *
 * @author redrocketracoon@googlemail.com
 */
public class AppSettingsManager extends StorageManager {

    private AppSettings appSettings;

    public AppSettingsManager() {
        this.recordStoreName = "AppSettings";
    }

    public AppSettings getAppSettings() {
        readAppSettings();
        if (appSettings == null) {
            //no Appsettings exist therefore create blank one:
            appSettings = new AppSettings(this);
        }
        return appSettings;
    }

    public void update(AppSettings changedAppSettings) {
        try {
            saveAppSettings();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readAppSettings() {
        try {
            byte[] data = this.getData();
            changeAppSettingsFromByteArray(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean saveAppSettings() {
        boolean saved = false;
        if (appSettings != null) {
            try {
                byte[] data = changeAppSettingsToByteArray();
                this.saveData(data);
                saved = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return saved;
    }

    private byte[] changeAppSettingsToByteArray() {
        byte[] data = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(appSettings.getActiveAccount());
            dos.writeBoolean(appSettings.isAutoUpdate());
            dos.writeBoolean(appSettings.isDebug());
            dos.writeBoolean(appSettings.isSaveEachCharacter());
            dos.writeUTF(appSettings.getTempSmsText());
            dos.writeUTF(appSettings.getTempSmsTo());
            dos.writeUTF(appSettings.getLastSmsText());
            dos.writeUTF(appSettings.getLastSmsTo());

            baos.close();
            dos.close();
            data = baos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    private void changeAppSettingsFromByteArray(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);

            appSettings = new AppSettings(this);
            appSettings.setActiveAccount(dis.readInt());
            appSettings.setAutoUpdate(dis.readBoolean());
            appSettings.setDebug(dis.readBoolean());
            appSettings.setSaveEachCharacter(dis.readBoolean());
            appSettings.setTempSmsText(dis.readUTF());
            appSettings.setTempSmsTo(dis.readUTF());
            appSettings.setLastSmsText(dis.readUTF());
            appSettings.setLastSmsTo(dis.readUTF());

            bais.close();
            dis.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
