/*
 * Created on 2013-06-26 1:37:35 PM
 */

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 * Searches professors emails on the mcgill.ca/directory/staff site
 *  
 * @author Olga Tsibulevskaya
 */
public class ProfMail {
	private String email;
	/**
	 * Gets a student and looks for email for his prof
	 * @param s a student whom prof to find
	 */
	public ProfMail(StudentFinal s) {
		String surname = s.getNameProfLast();
		String name = s.getNameProfFirst();
		String html = web(surname, name);
		search(html);
	}
	/**
	 * Looks for email for the professor whose name is specified
	 * @param surname last name of the professor
	 * @param name	first name of the professor
	 */
	public ProfMail(String surname, String name) {
		String html = web(surname, name);
		search(html);
	}
	public String getEmail() {
		return email;
	}
	/**
	 * Gets the whole html page
	 * @param surname
	 * @param name
	 * @return a string with html data 
	 */
	private String web(String surname, String name) {
		String url = "http://www.mcgill.ca/directory/staff/";	
		HttpClient httpClient = new HttpClient();
	    PostMethod post = new PostMethod(url);
	  
	    post.addParameter("last", surname);
	    post.addParameter("first", name);
	  
	    post.addParameter("form_build_id", "form-7dff2c92dda505010006558560a0a8e8");
	    post.addParameter("form_id", "mcgill_directory_staff_form");
	
	    try {
	    	httpClient.executeMethod(post); 
	    } catch (HttpException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    if (post.getStatusCode() == HttpStatus.SC_OK) {
	       
	    } 
	    else {
	         post.getStatusLine();
	    }
	    post.releaseConnection();
	    
	    GetMethod httpget = new GetMethod(url);
        try {
           	httpClient.executeMethod(httpget); // to check it or not?
        }
        catch(Exception e) {
            e.printStackTrace();
        }
         
        String html = "";
        try {
        	InputStream in = httpget.getResponseBodyAsStream();
        	java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        	html = s.hasNext() ? s.next() : "";
        } catch (IOException e) {
			e.printStackTrace();
		}
        return html;	 
	}
	/**
	 * Extracts email form the html string and sets email
	 * @param html
	 */
	private void search(String html) {
		 Document doc = Jsoup.parse(html);
			
		 Elements span = doc.select("span.spamspan");
		 if (! span.isEmpty()) {
			 String spanS = span.html();
			 String stripped = spanS.replaceAll("<[^>]*>", "");
			 String subspan = stripped.substring(0,stripped.indexOf("("));
			 CharSequence s1 = "[dot]";
			 CharSequence s2 = ".";
			 spanS = subspan.replace(s1, s2);
			 s1 = "[at]";
			 s2 = "@";
			 subspan = spanS.replace(s1, s2);
			 spanS = subspan.replaceAll(" ", "");
			 email = spanS.toLowerCase();
		 }
	}
}