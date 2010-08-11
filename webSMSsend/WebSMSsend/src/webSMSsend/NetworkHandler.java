/*
 *
 *
    Copyright 2009 Max Hänze --- maximum.blogsite.org
    Copyright 2010 Christian Morlok --- cmorlok.de

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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;
import javax.microedition.pki.CertificateException;
import me.regexp.RE;


public class NetworkHandler {

    private String username;
    private String password;
    private String content="";
    private String cookie="";

    

    public String getCookie(){
        return cookie;
        }

    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content2){
        content=content2;
    }
    public void setCookie(String cookie2){
        cookie=cookie2;
    }
    public NetworkHandler(String usernameIn,String passwordIn){
        username=usernameIn;
        password=passwordIn;
        
    }


    public String checkRecv(String smsRecv){
        if(smsRecv.startsWith("0")){
            if (smsRecv.startsWith("00"))
            {
                smsRecv="+".concat(smsRecv.substring(2));
                return smsRecv;
            }
                smsRecv="+49".concat(smsRecv.substring(1));
        }
        return smsRecv;
    }



    //statical query line 850!
    public String[] getSendPostRequest(boolean getRemSMS) throws Exception{
        String[] lineSplit=null;
        
        RE split=new RE('\n'+"");
        lineSplit=split.split(content);
        content="";

        String[][] argumentArray=new String[19][2];
        argumentArray[0][0]="SID";
        argumentArray[1][0]="MsgContentID";
        argumentArray[2][0]="FlagDLR";
        argumentArray[3][0]="FlagFlash";
        argumentArray[4][0]="FlagAnonymous";
        argumentArray[5][0]="FlagDefSender";
        argumentArray[6][0]="FlagVCal";
        argumentArray[7][0]="FlagVCard";
        argumentArray[8][0]="CID";
        argumentArray[9][0]="DID";
        argumentArray[10][0]="RepeatStartDate";
        argumentArray[11][0]="RepeatEndDate";
        argumentArray[12][0]="RepeatType";
        argumentArray[13][0]="RepeatEndType";
        argumentArray[14][0]="FolderID";
        argumentArray[15][0]="SMSToNormalized";
        argumentArray[16][0]="FID";
        argumentArray[17][0]="RURL";
        argumentArray[18][0]="REF";

        String[] argumentStrings=new String[19];
        int smsFormLine=getRegexLineMatch(lineSplit,"<form name=\"frmSMS\" action=\"/smscenter_send.osp\" onsubmit=\"return false\" onreset=\"return false\" method=\"post\">",850,false)+1;
        String remSMSstring=null;
        if (getRemSMS){
            int remSMSline=getRegexLineMatch(lineSplit,"<span class=\"FREESMS\"><strong>Frei-SMS: (.+) Web2SMS noch in diesem Monat mit Ihrem Internet-Pack inklusive!</strong></span><br>",lineSplit.length-smsFormLine,false);
            remSMSstring=lineSplit[remSMSline];
            remSMSstring=getRegexMatch(remSMSstring,"<span class=\"FREESMS\"><strong>Frei-SMS: (.+) Web2SMS noch in diesem Monat mit Ihrem Internet-Pack inklusive!</strong></span><br>",1);
            //System.out.println("remaining SMS: "+remSMSstring);
        }

        System.arraycopy(lineSplit, smsFormLine , argumentStrings, 0, 19);
        lineSplit=null;
        for (int i=0;i<argumentArray.length;i++){
            
                argumentArray[i][1] = URLEncoder.encode(getRegexMatch(argumentStrings[i], "<input type=\"Hidden\" name=\"" + argumentArray[i][0] + "\" value=\"(.*)\"", 1));
            
        }
        content="";
        for (int i=0;i<argumentArray.length;i++){
            content=content+argumentArray[i][0]+"="+argumentArray[i][1]+"&";
        }
        String[] returnValue=new String[2];
        returnValue[0]=content;
        if(getRemSMS){
            returnValue[1]=remSMSstring;
        }
        return returnValue;
    }
    /*
     * SMS-Manager
     *
    public int getGMXremSMS() throws Exception{
        String[] lineSplit=null;
        System.out.println(content);
        RE split=new RE('\n'+"");
        lineSplit=split.split(content);
        int lineNR=getRegexLineMatch(lineSplit,"<div class=\"content breadcrumb\">",0,true);

        split=new RE("dd>");
        lineSplit=split.split(lineSplit[lineNR+2]);


        int matchNB=getRegexLineMatch(lineSplit,"<strong>(.+)</strong> von <strong>(.+)</strong> versandt .*</",0,true);
        String smsSend=getRegexMatch(lineSplit[matchNB],"<strong>(.+)</strong> von <strong>(.+)</strong> versandt .*</",1);
        String smsFree=getRegexMatch(lineSplit[matchNB],"<strong>(.+)</strong> von <strong>(.+)</strong> versandt .*</",2);
        int smsRem=Integer.parseInt(smsFree)-Integer.parseInt(smsSend);
        return smsRem;
    }*/

    public String getRegexMatch(String lineSplit,String expression,int paren) {
        String content;
        RE regexp=new RE(expression,RE.MATCH_CASEINDEPENDENT);

        regexp.match(lineSplit);
        content=regexp.getParen(paren);

        return content;
    }

    //returns matched line number
    //order = true: begin with first line
    //order = false: begin with last line, if startLine = 0 begin with last
    //line and so on... (performance issues)
    public int getRegexLineMatch(String[] lineSplit,String expression,int startLine,boolean order) throws Exception{
        RE regexp=new RE(expression,RE.MATCH_CASEINDEPENDENT);
        int i;
        if (order){
            for (i=startLine;!regexp.match(lineSplit[i]);i++){}
        }
        else{
             for (i=lineSplit.length-startLine-1;!regexp.match(lineSplit[i]);i--){}
        }
        return i;
    }
    
    //statical match line 190!
    public String getFlowExecutionKey() throws Exception{
        String[] lineSplit;
        RE split=new RE('\n'+"");
        lineSplit=split.split(content);
        //System.out.println(content);
        RE regexp=new RE(".*name=\"_flowExecutionKey\" value=\"(.*)\" />.*");
        int i;
        for(i=190;i<lineSplit.length;i++){ //190 first match line
            if (regexp.match(lineSplit[i])){
                return regexp.getParen(1);
            }
        }
        for (i=0;i<190;i++){
            if (regexp.match(lineSplit[i])){
                return regexp.getParen(1);
            }
        }
        
        throw new Exception("flowExecutionKey not found!");

        
    }
    public String getRegexStringMatch(String regex,String seperator,int startline, int paren) throws Exception{
        String[] lineSplit;
        RE split=new RE(seperator);
        lineSplit=split.split(content);
        //System.out.println(content);
        RE regexp=new RE(regex);
        int i;
        for(i=startline;i<lineSplit.length;i++){ //190 first match line
            
            if (regexp.match(lineSplit[i])){
                
                return regexp.getParen(paren);
            }
        }
        

        throw new Exception("Fehler! RegexStringMatch");


    }

    public int httpsHandler(String requestMode, String url, String host,String postReq,boolean loadContent) throws CertificateException, IOException, Exception {
        byte[] data;
        HttpsConnection con=null;
        content="";
        StringBuffer out= new StringBuffer();
         InputStream is = null;
         OutputStream os = null;
         try {

             con = (HttpsConnection)Connector.open(url,Connector.READ_WRITE);
             //firefox browser identification
             con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.9) Gecko/2009040821 Firefox/3.0.9 (.NET CLR 3.5.30729)");
             con.setRequestProperty("Host",host);
             con.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
             con.setRequestProperty("Accept-Language","de-de");
             con.setRequestProperty("Accept-Encoding","deflate");
             con.setRequestProperty("Accept-Charset","ISO-8859-15,utf-8;q=0.7,*;q=0.7");
             con.setRequestProperty("Keep-Alive","300");
             con.setRequestProperty("cookie", cookie);

             if (requestMode.equals("POST")){
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
             }


             //send post request
             if (requestMode.equals("POST")){
                con.setRequestMethod(HttpConnection.POST);
                con.setRequestProperty("Content-Length", ""+postReq.getBytes().length);
                os = con.openOutputStream();
                os.write(postReq.getBytes());

             }else{
                 con.setRequestMethod(HttpConnection.GET);
             }


             /* getHeaderField returns only 1 cookie...
             if (con.getHeaderField("Set-Cookie")!=null){
                 cookie=con.getHeaderField("Set-Cookie");
                 if (cookie.indexOf( ';' )>0)
                    cookie=cookie.substring( 0, cookie.indexOf( ';' ));
             }*/

             //workaround
             String cookieTemp;
             for (int i=0;con.getHeaderField(i)!=null;i++){
                 if (con.getHeaderFieldKey(i)!=null){
                     //System.out.println(con.getHeaderFieldKey(i)+": "+con.getHeaderField(i));
                 if (con.getHeaderFieldKey(i).toLowerCase().equals("set-cookie")){
                     cookieTemp=con.getHeaderField(i);
                     if (cookieTemp.indexOf( ';' )>0)
                        cookie=cookie+cookieTemp.substring( 0, cookieTemp.indexOf( ';' ))+";";
                     else cookie=cookie+cookieTemp+";";
                 }}
             }
             //eoWorkaround

             //load content
             if (loadContent){
             is = con.openDataInputStream();
             if (con.getResponseCode() == HttpConnection.HTTP_OK) {

                 // Get length and process data
                 int len = (int)con.getLength();
                 if (len > 0) {
                     data = new byte[len];
                     //save whole page as string
                     for (int n; (n = is.read(data)) != -1;) {
                        out.append(new String(data, 0, n));
                     }
                     data=null;
                     content=out.toString();
                 } else {
                    return 1;
                 }


             } else if(con.getResponseCode() == HttpConnection.HTTP_MOVED_TEMP){
                 String newUrl=con.getHeaderField("Location");
                 //System.out.println(newUrl);
                 if (is != null)
                    is.close();
                 if (con != null)
                    con.close();
                 if (os != null)
                    os.close();
                 httpsHandler("GET",newUrl,host,"",true);

             }else {
                throw new Exception("Connection failed, response code: "+con.getResponseCode());
             }
             }
         } finally {
             if (is != null)
                 is.close();
             if (con != null)
                 con.close();
             if (os != null)
                 os.close();
         }
         
         //System.out.println(content+"\n\n\n");
        return 0;
    }


    public int httpHandler(String requestMode, String url, String host,String postReq,boolean loadContent) throws CertificateException, IOException, Exception {
        byte[] data;
        HttpConnection con=null;
        content="";
        StringBuffer out= new StringBuffer();
         InputStream is = null;
         OutputStream os = null;
         try {

             con = (HttpConnection)Connector.open(url,Connector.READ_WRITE);
             //firefox browser identification
             con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.9) Gecko/2009040821 Firefox/3.0.9 (.NET CLR 3.5.30729)");
             con.setRequestProperty("Host",host);
             con.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
             con.setRequestProperty("Accept-Language","de-de");
             con.setRequestProperty("Accept-Encoding","deflate");
             con.setRequestProperty("Accept-Charset","ISO-8859-15,utf-8;q=0.7,*;q=0.7");
             con.setRequestProperty("Keep-Alive","300");
             con.setRequestProperty("cookie", cookie);
             if (requestMode.equals("POST")){
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
             }
             

             //send post request
             if (requestMode.equals("POST")){
                con.setRequestMethod(HttpConnection.POST);
                con.setRequestProperty("Content-Length", ""+postReq.getBytes().length);
                os = con.openOutputStream();
                os.write(postReq.getBytes());
                
             }else{
                 con.setRequestMethod(HttpConnection.GET);
             }


             /* getHeaderField returns only 1 cookie...
             if (con.getHeaderField("Set-Cookie")!=null){
                 cookie=con.getHeaderField("Set-Cookie");
                 if (cookie.indexOf( ';' )>0)
                    cookie=cookie.substring( 0, cookie.indexOf( ';' ));
             }*/
            
             //workaround
             String cookieTemp;
             for (int i=0;con.getHeaderField(i)!=null;i++){
                 if (con.getHeaderFieldKey(i)!=null){
                     //System.out.println(con.getHeaderFieldKey(i)+": "+con.getHeaderField(i));
                 if (con.getHeaderFieldKey(i).toLowerCase().equals("set-cookie")){
                     cookieTemp=con.getHeaderField(i);
                     if (cookieTemp.indexOf( ';' )>0)
                        cookie=cookie+cookieTemp.substring( 0, cookieTemp.indexOf( ';' ))+";";
                     else cookie=cookie+cookieTemp+";";
                 }}
             }
             //eoWorkaround

             //load content
             if (loadContent){
             is = con.openDataInputStream();
             System.out.println(con.getResponseCode());
             if (con.getResponseCode() == HttpConnection.HTTP_OK) {
                 
                 // Get length and process data
                 int len = (int)con.getLength();
                 if (len > 0) {
                     data = new byte[len];
                     //save whole page as string
                     for (int n; (n = is.read(data)) != -1;) {
                         String stoapp = new String(data, 0, n, "Cp1252");
                        //out.append(new String(data, 0, n));
                         out.append(stoapp);
                     }
                     data=null;
                     content=out.toString();
                 } else {
                    int chunkSize = 512;
                    int offset = 0;
                    int readLength = 0;
                    byte [] chunk = new byte[chunkSize];
                    byte [] entireStream = new byte[chunkSize];
                    byte [] temp = null;
                    while ( (readLength = is.read(chunk, 0 , chunkSize)) != -1 )
                    {
                        temp = entireStream;
                        entireStream = new byte [offset+readLength];
                        System.arraycopy(temp, 0, entireStream, 0, offset);
                        System.arraycopy(chunk, 0, entireStream, offset, readLength);
                        offset += readLength;
                    }
                    content = new String(entireStream);
                    //return 1;
                 }

             } else if(con.getResponseCode() == HttpConnection.HTTP_MOVED_TEMP){
                 String newUrl=con.getHeaderField("Location");
                 System.out.println(newUrl);
                 if (is != null)
                    is.close();
                 if (con != null)
                    con.close();
                 if (os != null)
                    os.close();
                 httpHandler("GET",newUrl,host,"",true);

             }
                 else {
                throw new Exception("Connection failed, response code: "+con.getResponseCode());
             }
             }
         } finally {
             if (is != null)
                 is.close();
             if (con != null)
                 con.close();
             if (os != null)
                 os.close();
         }
         
         //System.out.println(content+"\n\n\n");
        return 0;
    }
    
    
}
