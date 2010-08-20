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

         WriteToStream("HELO there" + "\r\n");
         WriteToStream("AUTH LOGIN"+ "\r\n");
         WriteToStream(user+"\r\n");
         WriteToStream(password+"\r\n");
         WriteToStream("MAIL FROM: "+ from +"\r\n");
         WriteToStream("RCPT TO: "+ to + "\r\n");
         if (!cc.equals("")) WriteToStream("RCPT TO: "+ cc + "\r\n"); 
         WriteToStream("DATA\r\n");
         // stamp the msg with date
         WriteToStream("Date: " + new Date() + "\r\n");
         if (!cc.equals("")) WriteToStream("Reply-To: "+ cc + "\r\n"); //this way we can answer the sender easily
         WriteToStream("From: "+from+"\r\n");
         WriteToStream("To: "+to+"\r\n");
         if (!cc.equals("")) WriteToStream("Cc: "+ cc + "\r\n");
         WriteToStream("Subject: "+subject+"\r\n");
         WriteToStream("\r\n"+msg+"\r\n"); // message body
         WriteToStream(".\r\n");
         WriteToStream("QUIT\r\n");

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
        parent.debug("Cannot connect to SMTP server. Ping the server to make sure it is running...\n"+e.toString()+e.getMessage());
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

   private void WriteToStream(String command) throws IOException{
       os.write(command.getBytes());
       os.flush();
   }
}
