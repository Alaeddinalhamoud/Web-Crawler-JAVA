
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jsoup.Jsoup;

/**
 * @author Yi Hong, updated by Marco Perez
 * 
 *  You are free to make any changes to this
 *  class though you don't have to.
 *    
 */

public class Utils {
	
	/* Regular expression for  */
	public static final Pattern link_pattern=Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
	public static final Pattern href_pattern=Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
	
	
	/**
	 * 
	 * returns the HTML source code of a web page
	 * 
	 * @param  address: "base" page or seed URL
	 * 
	 *         e.g. http://www.bbc.co.uk/news  
	 *         
	 *         
	 *         
	 *                   
	 * @return the HTML source code of the given web page
	 *          
	 */
	public static String getTextFromAddress(String address){
		
		 URL url;
		    InputStream is = null;
		    BufferedReader br;
		    String line;

		    try {
		        url = new URL(address);
		        is = url.openStream();  
		        br = new BufferedReader(new InputStreamReader(is));

		        StringBuffer sb=new StringBuffer(); 
		        
		        while ((line = br.readLine()) != null) {
		        	sb.append(line);
		        	sb.append("\n");
		        }
		        
		        String content=sb.toString();
		        return content;
		        
		    }  catch (MalformedURLException mue) {
		    	//ignore dead links
		    	//mue.printStackTrace();
		    } catch (IOException ioe) {
		    	//ignore IO error
		    	//ioe.printStackTrace();
		    } finally {
		        try {
		            if (is != null) is.close();
		        } catch (IOException ioe) {
		            ioe.printStackTrace();
		            System.out.println(ioe.toString());
		        }
		    }
		    return "";
		
	}
	
	/**
	 *  
	 * Extracts all internal hyperlinks on a given page.
	 * 
	 *  For example: 
	 *  Suppose http://example.com/some.html 
	 *  has the following content:  
	 * 
	 *  <HTML>
	 *      <b>A sample page with 3 links</b>
	 *      <a href="1.html">link1</a>
	 *      <a href="http://example.com/other.html">other page</a>
	 *      <a href="http://www.google.com">Google</a>
	 *  </HTML>
	 *  
	 *  
	 *  extractHyperlinks ("http://example.com/some.html", html_source_text) 
	 *  
	 *  should return:
	 *  
	 *     http://example.com/1.html 
	 *     http://example.com/other.html 
	 *     
	 *  Note: 
	 *     http://www.google.com will not be returned it's domain does not start with example.com
	 *  
	 * @param address: URL 
	 * @param html:  source code 
	 * @return a list of "internal" hyperlinks  
	 */
	

	
	public static ArrayList<String> extractHyperlinks(String address, String content){
		
		ArrayList<String> ret=new ArrayList<String>();
		
		try{
			URL  url = new URL(address);
			Matcher matcher = link_pattern.matcher(content);
			
         while (matcher.find()) {
        	
        	 String links = matcher.group(1);  
        	 Matcher hrefs = href_pattern.matcher(links);
        	 
             while(hrefs.find()){
            	String link = hrefs.group(1).replaceAll("'", "").replaceAll("\"", "");
            	String absolutePath="";
            	String path=url.getPath();
        	    if ((path == null) || path.equals("") || path.equals("/")){
        	    	absolutePath="";
        	    }
        	    int lastSlashPos = path.lastIndexOf('/');
        	    if (lastSlashPos >= 0){
        	    	absolutePath=path.substring(0, lastSlashPos); //strip off the slash
        	    }else{
        	    	absolutePath= ""; 
        	    }

            	String addr="";     	
            	URI uri=new URI(link);  	
            	if(!uri.isAbsolute()){	            	
            		addr=url.getProtocol()+"://"+url.getHost()+absolutePath+"/"+link;
            		ret.add(addr);
            	}else{
            		if(link.contains(url.getHost())){
            		if(!link.contains("mailto:")){
            			addr=link;
            			ret.add(addr);
            		}
            		}
            	}
            } 
        
		}
            
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
        return ret;
} 
	
	
	/**
	 * 
	 * Strips HTML tags from a string
	 * 
	 * @param html
	 * @return string with all HTML tags removed
	 * 
	 */
	public static String getPlainText(String html){
		String text="";
		try{
			text= Jsoup.parse(html).text();
		}catch(Exception ex){
			return "";
		}
		return text;
	}
	
	
	
	/**
	 *  count the occurrences of each word
	 * 
	 * @param text
	 * @return HashMap storing the occurrences of each word in the text
	 */
	public static Map<String, Integer> calculate(List<String> keyWords,String text){ 
		 
		 Map<String, Integer> map = new HashMap<String, Integer>();
		 
		 keyWords.forEach(
				 	k->map.put(k, 0)
				 );
		 
		 String[] tokens = text.split("[\\s | , | \\.]");
		 
		 for(String kw:keyWords) {
			 map.put(kw, Long.valueOf(Stream.of(tokens).filter(w->w.equalsIgnoreCase(kw)).count()).intValue());
		 }
		 
			
			return map;
	}
	
			
	}
		
