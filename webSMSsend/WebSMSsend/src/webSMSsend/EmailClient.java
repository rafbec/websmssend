/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webSMSsend;

/**
 *
 * @author Qusay H. Mahmoud
 * http://developers.sun.com/mobility/midp/articles/midp2network/
 *
 * Copyright 2010 redrocketracoon@googlemail.com
 */
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.*;

public class EmailClient {
   private webSMSsend parent;
   private SocketConnection sc;
   private InputStream is;
   private OutputStream os;
   private String smtpServerAddress="mail.gmx.net";

   private String user, password, from,cc, to, subject, msg;

   public EmailClient (webSMSsend m,String user,String password, String from,
           String to,String cc, String subject, String msg) {
      parent = m;
      this.from = from;
      this.to = to;
      this.cc = cc;
      this.subject = subject;
      this.msg = msg;
      this.user = user;
      this.password = password;
   }

   public int run() throws Exception { //0=failed 1=sucessfull
        int i =0;
      try {
         parent.getWaitScreen1().setText("Sende E-Mail");
          sc = (SocketConnection)
            Connector.open("socket://"+smtpServerAddress+":25",3,true);
         is = sc.openInputStream();
         os = sc.openOutputStream();

         os.write(("HELO there" + "\r\n").getBytes());
         os.write(("AUTH LOGIN"+ "\r\n").getBytes());
         os.write((user+"\r\n").getBytes());
         os.write((password+"\r\n").getBytes());
         os.write(("MAIL FROM: "+ from +"\r\n").getBytes());
         os.write(("RCPT TO: "+ to + "\r\n").getBytes());
         if (!cc.equals("")) os.write(("RCPT TO: "+ cc + "\r\n").getBytes()); //not CC but works...;)
         os.write("DATA\r\n".getBytes());
         // stamp the msg with date
         os.write(("Date: " + new Date() + "\r\n").getBytes());
         os.write(("From: "+from+"\r\n").getBytes());
         os.write(("To: "+to+"\r\n").getBytes());
         os.write(("Subject: "+subject+"\r\n").getBytes());
         os.write(("\r\n"+msg+"\r\n").getBytes()); // message body
         os.write(".\r\n".getBytes());
         os.write("QUIT\r\n".getBytes());

         // debug
         StringBuffer sb = new StringBuffer();
         int c = 0;
         while (((c = is.read()) != -1) ) {
            sb.append((char) c);
         }
        parent.debug("SMTP server response - " + sb.toString());
        if (sb.toString().indexOf("Message accepted")!=-1){
            i=1; //successfull
        } else{
            throw new IOException("E-Mail not send");
        }
      } catch(IOException e) {
        parent.debug("Cannot connect to SMTP server. Ping the server to make sure it is running...\n"+e.getMessage());
        throw e;
      } finally {
         try {
            if(is != null) {
               is.close();
            }
            if(os != null) {
               os.close();
            }
            if(sc != null) {
               sc.close();
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return i;
   }
}
