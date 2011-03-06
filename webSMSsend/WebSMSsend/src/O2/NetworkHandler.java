/*
 *
 *
    Copyright 2009 Max Hänze --- maximum.blogsite.org
    Copyright 2010 Christian Morlok --- cmorlok.de
    Copyright 2010 redrocketracoon@googlemail.com
    Copyright 2011 schirinowski@gmail.com
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

package O2;
import ConnectorBase.SmsConnector;
import ConnectorBase.URLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;
import javax.microedition.pki.CertificateException;
import me.regexp.RE;
import webSMSsend.IGui;



public class NetworkHandler {
    private final static String START_LINE_SAVE_NAME = "Startline";

    // taken from ftp://ftp.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP1252.TXT
    // the first 128 characters are the same as ASCII
    // undefined chars are replaced by SPACE (0x0020).
    private char[] cp1252map = "\u20AC\u0020\u201A\u0192\u201E\u2026\u2020\u2021\u02C6\u2030\u0160\u2039\u0152\u0020\u017D\u0020\u0020\u2018\u2019\u201C\u201D\u2022\u2013\u2014\u02DC\u2122\u0161\u203A\u0153\u0020\u017E\u0178\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF".toCharArray();
    private boolean cp1252internal;

    private String username;
    private String password;
    private String content="";
    private String cookie="";
    private IGui GUI;
    private O2 parent;


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

    public NetworkHandler(String usernameIn, String passwordIn, IGui Debugger, O2 parent) {
        username=usernameIn;
        password=passwordIn;
        GUI = Debugger;
        this.parent = parent;

        // test if jvm supports cp1252 encoding
        cp1252internal = true;
        try {
            byte[] testByte = {(byte) 0x80};
            String testString = new String(testByte, "Cp1252");
        } catch (UnsupportedEncodingException ex) {
            GUI.debug(ex.toString());
            GUI.debug("JVM doesn't support Cp1252 encoding, falling  back to internal decoding.");
            cp1252internal = false;
        } catch (Exception ex){
            GUI.debug("Exception at NetworkHandler: "+ex.toString());
        }
    }

    //Save Startline
    private void SaveStartLine(int Startline)
    {
        if (parent != null){
            parent.saveSetting(START_LINE_SAVE_NAME, Startline + "");
        }
    }

    private int GetStartLine() {
        int startline=0;
        try {
            if (parent != null) {
                String line = parent.getSetting(START_LINE_SAVE_NAME);
                startline = Integer.parseInt(line);
            }
        } finally {
            return startline;
        }
    }

    private String byteArrayToString(byte[] bytes, int off, int len) throws UnsupportedEncodingException {
        String ret;
        if (cp1252internal) {
            ret = new String(bytes, off, len, "Cp1252");
        } else {
            char[] chars = new char[len];
            for (int i = off; i < len + off; ++i) {
                byte b = bytes[i];
                chars[i] = (b >= 0) ? (char) b : cp1252map[b + 128];
            }
            ret =  new String(chars);
        }
        System.out.println("decoded: " + ret);
        return ret;
    }

    //statical query line 850!
    public String[] getSendPostRequest(boolean getRemSMS, int SENDERMODE) throws Exception{
        GUI.debug("getSendPostRequest( " + getRemSMS + " )");
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
        argumentArray[5][1]=""+SENDERMODE;
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
        int smsFormLine=0;
        int startline = GetStartLine();
        GUI.debug("Saved Startline: " + startline);
        try{
            smsFormLine=getRegexLineMatch(lineSplit,"<form name=\"frmSMS\" action=\"/smscenter_send.osp\" onsubmit=\"return false\" onreset=\"return false\" method=\"post\">",startline,false)+1;
        }
        catch (ArrayIndexOutOfBoundsException e) { //Fallback: Regex the whole html string
            GUI.debug("Fallback: RegexLineMatch: Startline is not correct");
            smsFormLine=getRegexLineMatch(lineSplit,"<form name=\"frmSMS\" action=\"/smscenter_send.osp\" onsubmit=\"return false\" onreset=\"return false\" method=\"post\">",0,false)+1;
        }
        catch (Exception ex){
            throw ex;
        }
        startline= (lineSplit.length-smsFormLine);
        SaveStartLine(startline);
        GUI.debug("Current Startline: " + startline);
        String remSMSstring=null;

        //Try to get remaining SMS if it fails make a debug message and go on
        try {
            int remSMSline=getRegexLineMatch(lineSplit,"<span class=\"FREESMS\"><strong>Frei-SMS: (.+) Web2SMS noch in diesem Monat mit Ihrem Internet-Pack inklusive!</strong></span><br>",lineSplit.length-smsFormLine,false);
            remSMSstring=lineSplit[remSMSline];
            remSMSstring=getRegexMatch(remSMSstring,"<span class=\"FREESMS\"><strong>Frei-SMS: (.+) Web2SMS noch in diesem Monat mit Ihrem Internet-Pack inklusive!</strong></span><br>",1);
            //System.out.println("remaining SMS: "+remSMSstring);
        } catch (Exception e){
            GUI.debug("RemainingSMS konnten nicht ermittelt werden: " + e.getMessage());
        }

        System.arraycopy(lineSplit, smsFormLine , argumentStrings, 0, 19);
        lineSplit=null;
        for (int i=0;i<argumentArray.length;i++){
            if (argumentArray[i][1]==null) //leave predefined values alone (FlagDefSender)
                argumentArray[i][1] = URLEncoder.encode(getRegexMatch(argumentStrings[i], "<input type=\"Hidden\" name=\"" + argumentArray[i][0] + "\" value=\"(.*)\"", 1));
            
        }
        content="";
        for (int i=0;i<argumentArray.length;i++){
            content=content+argumentArray[i][0]+"="+argumentArray[i][1]+"&";
        }
        String[] returnValue=new String[2];
        returnValue[0]=content;
        if(remSMSstring != null){
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
        GUI.debug("getRegexMatch( " + lineSplit + ", " + expression + ", " + paren + " )");
        String content;
        RE regexp=new RE(expression,RE.MATCH_CASEINDEPENDENT);

        regexp.match(lineSplit);
        content=regexp.getParen(paren);

        GUI.debug("getRegexMatch return " + content);
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
    
    private static final String CHECK_FLOW = // .
	"name=\"_flowExecutionKey\" value=\"";

    private static String getFlowExecutionkeyALT(final String html) {
		String ret = "";
		int i = html.indexOf(CHECK_FLOW);
		if (i > 0) {
			int j = html.indexOf("\"", i + 35);
			if (j >= 0) {
				ret = html.substring(i + 32, j);
			}
		}
		return ret;
	}

     public String getFlowExecutionKey() throws Exception{
         GUI.debug("Using getFlowExecutionkeyALT()");
         String key = getFlowExecutionkeyALT(content);
         if (key.equals("")) {
             throw new Exception("flowExecutionKey not found!");
         }
         return key;
    }

//Original GetFlowExecutionKey has statical match line 190!
//    public String getFlowExecutionKey() throws Exception{
//        GUI.debug("getFlowExecutionKey()");
//        try{
//            String[] lineSplit;
//            RE split = new RE('\n' + "");
//            lineSplit = split.split(content);
//            GUI.debug("lineSplit finished");
//            RE regexp = new RE(".*name=\"_flowExecutionKey\" value=\"(.*)\" />.*");
//            int i;
//            for (i = 190; i < lineSplit.length; i++) { //190 first match line
//                if (regexp.match(lineSplit[i])) {
//                    GUI.debug("getFlowExecutionKey: key found at line " + i);
//                    return regexp.getParen(1);
//                }
//            }
//            for (i = 0; i < 190; i++) {
//                if (regexp.match(lineSplit[i])) {
//                    GUI.debug("getFlowExecutionKey: key found at line " + i);
//                    return regexp.getParen(1);
//                }
//            }
//
//            throw new Exception("flowExecutionKey not found!");
//
//        }catch(Exception ex){
//            GUI.debug(ex.toString() + " " + ex.getMessage() + "Fallback: getFlowExecutionkeyALT()");
//            GUI.debug("Fallback: getFlowExecutionkeyALT()");
//            String key=getFlowExecutionkeyALT(content);
//            if (key.equals("")) throw new Exception("flowExecutionKey not found!");
//            return key;
//        }
//    }

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
//        GUI.debug("httpHandler( " + requestMode + ", " + url + ", " + host + ", " + postReq + ", " + loadContent + " )");
        GUI.debug("httpHandler( " + requestMode + ", " + url + ", " + host + ", postReq, " + loadContent + " )");
        byte[] data;
        HttpConnection con=null;
        content="";
        StringBuffer out= new StringBuffer();
         InputStream is = null;
         OutputStream os = null;
         try {
             try {
               con = (HttpConnection)Connector.open(url,Connector.READ_WRITE);
             } catch (Exception ex) {
                 GUI.debug("Connector.open failed, exception: " + ex.toString());
                 throw ex;
             }

             try {
             //firefox browser identification
             con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.9) Gecko/2009040821 Firefox/3.0.9 (.NET CLR 3.5.30729)");
             con.setRequestProperty("Host",host);
             con.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
             con.setRequestProperty("Accept-Language","de-de");
             con.setRequestProperty("Accept-Encoding","identity");
             con.setRequestProperty("Accept-Charset","ISO-8859-15,utf-8;q=0.7,*;q=0.7");
             con.setRequestProperty("Keep-Alive","300");
             con.setRequestProperty("cookie", cookie);
             if (requestMode.equals("POST")){
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestMethod(HttpConnection.POST);
                con.setRequestProperty("Content-Length", ""+postReq.getBytes().length);
                try {
                    os = con.openOutputStream();
                    os.write(postReq.getBytes());
                } catch (IOException ex) {
                    GUI.debug("writing to output stream failed, exception: " + ex.toString());
                    throw ex;
                }

             }else{
                 con.setRequestMethod(HttpConnection.GET);
             }
             } catch (IOException ex) {
                 GUI.debug("con.setRequestProperty failed, exception: " + ex.toString());
                 throw ex;
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
                 try {
             is = con.openDataInputStream();
                 } catch (IOException ex) {
                     GUI.debug("con.openDataInputStream failed, exception: " + ex.toString());
                     throw ex;
                 }
             GUI.debug("HTTP Response Code: "+con.getResponseCode());
             if (con.getResponseCode() == HttpConnection.HTTP_OK) {
                 
                 // Get length and process data
                 try {
                 int len = (int)con.getLength();
                 if (len > 0) {
                     data = new byte[len];
                     //save whole page as string
                     for (int n; (n = is.read(data)) != -1;) {
                         //String stoapp = new String(data, 0, n, "Cp1252");
                        //out.append(new String(data, 0, n));
                         //out.append(stoapp);
                         out.append(byteArrayToString(data, 0, n));
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
                    //content = new String(entireStream);
                    content = byteArrayToString(entireStream, 0, entireStream.length);
                    //return 1;
                 }
                 } catch (IOException ex) {
                     GUI.debug("reading input stream failed, exception: " + ex.toString());
                     throw ex;
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
         } catch (Exception ex){
             throw ex;
         } catch (Throwable t){
             throw new Exception("Throwable: "+t.toString()+t.getMessage());
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
