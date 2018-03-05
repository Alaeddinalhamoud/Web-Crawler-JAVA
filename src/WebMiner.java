import java.io.IOException;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

public class WebMiner implements Runnable {

	// Temporary links from jsoup to the queue
	public Elements links;// NONVisteed to Queue
	public static List<String> keywords;
	private String Rootlink;

	public WebMiner(String url, List<String> keywords) {
		this.Rootlink = url;
		WebMiner.keywords = keywords;
	}

	@Override
	public void run() {
		Mine(Rootlink);
	}

	public synchronized void Mine(String Rooturl) {
		// To Mine in Rooturl to get more links using JSOUP
		Boolean ConnectionStatus = Initial(Rooturl);
		
		
		//If JSOUP is false that mean Erorr 404
		if (ConnectionStatus.equals(true)) {
			String value = Rooturl;
			
			// Idea.... After Finish minning Call to search Method();Should Be
			// sync A()
			// B()); Retuenned
			GetCalc(value);
			synchronized (MinnerManager.lock) {
				MinnerManager.lock.notifyAll();// To wake-up all Thread on waiting list ....
			}	
		} else {
			// if Internet not working or Link syntax not currect will give this
			// msg .
			System.out.println(
					"Please Check Your Internet Connection or The Syntax of Your URL..." + ConnectionStatus.toString());
		}
	}

	// This method to get the the links from rootlink and called again if the
	// queue
	// is empty will save the links in
	// links Elements form JSOUP
	public synchronized Boolean Initial(String url) {

		Document doc;

		try {
			//Get All the page content with HTL DOM
			doc = Jsoup.connect(url).get();
			//Get All the links 
			links = doc.select("a[href]");

			for (Element link : links) {
				//Give me the links without DOM
				String NextLink = link.attr("abs:href");

				// if link not duplicated add to queue

				if (!(SharedQueue.IsContains(NextLink))) {

					// if link not null or ""
					if (!(NextLink == "")) {
						// Add to main queue
						SharedQueue.Set(NextLink);
					}
				}
			}

			return true;

		} catch (IOException e) {

			// TODO Auto-generated catch block
			// Sometimes can't get link content becouse of ERROR 404

			System.out.println("Report : The current Link does not exist ERORR 404 : " + url);

			return false;
		}
	}

	// To Calc the key words link
	public synchronized void GetCalc(String Url) {

		String PageContent;
		// To Save Keywords freq for currnet link.
		Map<String, Integer> KeyWordTempFeq = new HashMap<String, Integer>();
		int finalvalue;

		String LinkfromNonVisted = Url;
		// Get the link content using JSUOP, just contnet without html DOM
		PageContent = GetContect(LinkfromNonVisted);

		if (PageContent != null) {

			// find the key words in current web page content
			KeyWordTempFeq = (Utils.calculate(keywords, PageContent));
			// This loop will read the key words array one by one using the
			// arraysize.
			for (int k = 0; k < keywords.size(); k++) {

				// Get the first keyword from array
				int valueNew = KeyWordTempFeq.get(keywords.get(k));
				// Problem...
				if (KeyWordFinalFeq.ContainsKey(keywords.get(k))) {
					// Get the old keyword from final map to update it
					int valueOld = KeyWordFinalFeq.Get(keywords.get(k));
					finalvalue = valueNew + valueOld;

				} else {

					finalvalue = valueNew;

				}
				// Save New Result to the final KeyWord Freq map (Shared
				// Resourse)
				KeyWordFinalFeq.Set(keywords.get(k), finalvalue);

			}
		}

	}

	// This method to get the content of link using JSOUP .text() will return
	// plian
	// text HTML without DOM
	public String GetContect(String url) {

		Document doc;

		String pageContent;

		try {

			doc = Jsoup.connect(url).get();
			//Get the page content with without HTML DOM
			pageContent = doc.text();

			return pageContent;

		} catch (IOException e) {

			// TODO Auto-generated catch block

			// e.printStackTrace();

			return null;// If no content no no connection

		}

	}

	public void Sleep(int time) {
		try {
			Thread.sleep(time);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void Wait() {
		try {
			synchronized (MinnerManager.lock) {
				MinnerManager.lock.wait();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

}
