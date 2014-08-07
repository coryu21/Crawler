import java.io.*; 
import java.net.*; 
import java.util.*;

public class BasicCrawler implements Runnable {
	Vector badExtList; 
	Vector badDirList; 
	static String host;

	public BasicCrawler(int numOfAgent, String host) {
		this.host = host;
		badUrl(); 

		(new Thread(this)).start();
		try {
			Thread.sleep(5000);
			for (int i = 1; i < numOfAgent; i++) {
				Thread.sleep(100);
				(new Thread(this)).start();
			}
		} catch (Exception e) {
		}
	}

	
	public void run() {
		while (true) {
			webCrawling(host); 
		}
	}
	protected void webCrawling(String link) {
		String content = "";
		URL url = null;
		try {
			url = new URL(link);
			content = getContentIn(url);
		} catch (Exception e) {
		}
		if (content != null) {

			System.out.print("n-" + link);
			System.out.println("  Page update contents : " + content);
			

			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			extractLinkFrom(url, content, "<a", "href"); 
			extractLinkFrom(url, content, "<frame", "src");
															
			extractLinkFrom(url, content, "<iframe", "src"); 
															
			extractLinkFrom(url, content, "<meta", "url");
															
			extractLinkFrom(url, content, "<form", "action"); 
															

		} else {
			System.out.print("^");
			System.out.println("^^^^");
		}
	}

	protected String getContentIn(URL url) {
		String content = "";
		String inputLine = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt")));
			
			System.out.println(in.readLine());
			while ((inputLine = in.readLine()) != null) {
				out.write(inputLine);
				content += inputLine + "\n";
				if (content.length() > 65500)
					break;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content; 
	}

	protected void extractLinkFrom(URL url, String content, String linkTag,
			String option) {
		String lowerCaseContent = content.toLowerCase();
		StringTokenizer st = null;
		String newLink = null;
		int index = 0;

		while ((index = lowerCaseContent.indexOf(linkTag, index)) != -1) {
			if ((index = lowerCaseContent.indexOf(option, index)) == -1
					| (index = lowerCaseContent.indexOf("=", index)) == -1)
				break;
			index++;
			st = new StringTokenizer(content.substring(index), "tnr>#),< ");
			newLink = st.nextToken();
			if (badLink(newLink))
				continue;

			if (newLink.startsWith("/"))
				newLink = newLink.substring(1);

			try {
				URL urlLink = new URL(url, newLink);
				newLink = urlLink.toString();
				if (newLink != null && newLink.length() < 200
						&& newLink.length() > 20 && 
						newLink.indexOf(host) != -1) { 
					System.out.println("new Link = " + newLink); 
																	
				}
			} catch (MalformedURLException e) {
			}
		}
	}

	public boolean badLink(String link) {
		boolean bad = false;
		link = link.trim().toLowerCase();
		if (link.startsWith("mail") || link.startsWith("javascript"))
			bad = true;

		for (int i = 0; i < badExtList.size(); i++)
			if (link.endsWith((String) badExtList.elementAt(i)))
				bad = true;

		for (int i = 0; i < badDirList.size(); i++)
			if (link.indexOf((String) badDirList.elementAt(i)) != -1)
				bad = true;
		return bad;
	}

	public void badUrl() {
		badExtList = new Vector();
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"conf\badExtList.txt"));
			String ext;
			while ((ext = in.readLine()) != null) {
				badExtList.addElement(ext);
			}
			in.close();
		} catch (Exception e) {
		}

		badDirList = new Vector();
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"conf\badDirList.txt"));
			String dir;
			while ((dir = in.readLine()) != null) {
				badDirList.addElement(dir);
			}
			in.close();
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		int numOfAgent = 10; 
		System.out.println("Page Agent Started .....(Hwang, Insoo)");
		System.out.println("--------------------------------------");
		new BasicCrawler(numOfAgent, "http://news.naver.com/main/read.nhn?oid=001&sid1=100&aid=0007004187&mid=shm&mode=LSD&nh=20140709114205");
	}
}