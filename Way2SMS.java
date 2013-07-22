import java.net.*;
import java.io.*;

public class Way2SMS {
	
	String eckie,cookie;
	String smsc = "12489smssending34908=67547valdsvsikerexzc435457; Path=/";
	String token=null,phone=null,i_m=null,kriya=null,diffNo=null;
	boolean loginFlag = false, initFlag = false;

	public String login(String user, String pass) throws Exception {
		System.out.println("Processing Login...");
		String loginContent = "username=" + user + "&password=" + pass;
		
		URL u = new URL("http://o.way2sms.com/w2sauth.action");
		HttpURLConnection hc = (HttpURLConnection) u.openConnection();
		user = URLEncoder.encode(user, "utf-8");
		pass = URLEncoder.encode(pass, "utf-8");
		hc.setDoOutput(true);
        	hc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5");
        	hc.setRequestProperty("Content-Length", String.valueOf(loginContent.length()));
        	hc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        	hc.setRequestProperty("Accept", "*/*");
        	hc.setRequestProperty("Referer", "http://o.way2sms.com/content/index.html");
        	hc.setRequestMethod("POST");
        	hc.setInstanceFollowRedirects(false);
        
        	PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(hc.getOutputStream()), true);
        	printWriter.print(loginContent);
        	printWriter.flush();
        	printWriter.close();
       		cookie = hc.getHeaderField("Set-Cookie");
        
        	eckie = cookie.substring(cookie.indexOf('~')+1, cookie.indexOf(';'));
//        	System.out.println(cookie + "\n" + eckie);
        
        	URL u1 = new URL("http://o.way2sms.com/Main.action?id=" + eckie);
        	HttpURLConnection hc1 = (HttpURLConnection) u1.openConnection();
        	hc1.setRequestProperty("Cookie", cookie);
        
        	BufferedReader br = new BufferedReader(new InputStreamReader(hc1.getInputStream()));
        	String content="";
        	while((content=br.readLine())!=null)
        		if(content.contains(user)) {
        			System.out.println("Login Successful");
        			loginFlag = true;
        			initialize();
        			break;
        		}
        		if(content == null) {
        		System.out.println("Login Unsuccessful");
        		loginFlag = false;
        		}
      
        
        	return cookie;
	}
	
	public void initialize() throws Exception {
		boolean b = true;
		System.out.println("Initializing...");
		while(b) {
			System.out.println("while");
			URL u = new URL("http://o.way2sms.com/jsp/SingleSMS.jsp?Token=" + eckie);
			HttpURLConnection hc = (HttpURLConnection) u.openConnection();
			hc.setRequestProperty("Cookie", cookie);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(hc.getInputStream()));
			String content,page="";
			while((content=br.readLine())!=null)
				page += "\n" + content;
//			System.out.println(page);
//			b = false;
				if(page.contains("i_m' val"))
					i_m = page.substring(page.indexOf("m' value=")+10,page.indexOf("/", page.indexOf("m' value="))-2);
				if(page.contains("kriya' val")) 
					kriya = page.substring(page.indexOf("a' value=")+10,page.indexOf("/", page.indexOf("a' value="))-2);
				if(page.contains("t_15_k_5' val"))
					token = page.substring(page.indexOf("5' value=")+10,page.indexOf("/", page.indexOf("5' value="))-2);
				if(page.contains("m_15_b' val"))
					phone = page.substring(page.indexOf("b' value=")+10,page.indexOf("/", page.indexOf("b' value="))-2);
				if(page.contains("diffNo' val"))
					diffNo = page.substring(page.indexOf("o' value=")+10,page.indexOf("/", page.indexOf("o' value="))-2);
			
			if(token==null || phone==null || i_m==null || kriya==null || diffNo==null);
			else break;
		}
		
		initFlag = true;
		System.out.println("Initialization Complete");
		diffNo = URLEncoder.encode(diffNo,"utf-8");
		
/*		System.out.println("i_m = " + i_m);
		System.out.println("kriya = " + kriya);
		System.out.println("t_15_k_5 = " + token);
		System.out.println("m_15_b = " + phone);
		System.out.println("diffNo = " + diffNo);*/
	}
	
	public void send(String msg, String phoneNo) throws Exception {
		if(!loginFlag)
			throw new RuntimeException("way2sms login");
		if(!initFlag)
			initialize();
		System.out.println("Sending Message to " + phoneNo + "...");
		msg = URLEncoder.encode(msg, "utf-8");	
		
		String smsContent = "i_m=" + i_m + "&kriya=" + kriya + "&" + token + "=" + eckie + "&diffNo=" + diffNo + "&" + phone + "=" + phoneNo + "&txtLen=300&textArea=" + msg;
//		System.out.println(smsContent);
		
		URL u1 = new URL("http://o.way2sms.com/jsp/w2ssms.action?" + smsContent);
		HttpURLConnection hc1 = (HttpURLConnection) u1.openConnection();
//		System.out.println("\n" + cookie + "\n");
		hc1.setRequestProperty("Cookie", cookie);
		hc1.addRequestProperty("Cookie", smsc);
		String ct;
		String page="";
		BufferedReader br = new BufferedReader(new InputStreamReader(hc1.getInputStream()));
		while((ct=br.readLine())!=null)
			page += "\n" + ct;
		if(page.contains("successfully"))
			System.out.println("Message has been submitted successfully to " + phoneNo);
		else
			System.out.println("Message Sending Failed");
	}
	
	public void logout() throws Exception {
		System.out.println("\nProcessing LogOut...");
		URL u = new URL("http://o.way2sms.com/jsp/logout.jsp");
		HttpURLConnection hc = (HttpURLConnection) u.openConnection();
		
		hc.setRequestProperty("Cookie", cookie);
		BufferedReader br = new BufferedReader(new InputStreamReader(hc.getInputStream()));
		String content, page="";
		
		while((content=br.readLine())!=null)
			page += "\n" + content;
		if(page.contains("successfully"))
			System.out.println("You have been successfully logged out of Way2SMS");
		loginFlag = initFlag = false;
	}
	
	public static void main(String a[]) throws Exception {
		Way2SMS t = new Way2SMS();
		String msg, phoneNo;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("UName and PWD: ");
		t.login(br.readLine(), br.readLine());
		while(true) {
			System.out.print("\nPhone No.: ");
			phoneNo = br.readLine();
			
			if(phoneNo.equals("0"))
				break;
			
			System.out.println("Message: ");
			msg = br.readLine();
			
			t.send(msg, phoneNo);
		}
		
		t.logout();
		System.out.println("---X---X---Session Ended---X---X---");
	}
}
