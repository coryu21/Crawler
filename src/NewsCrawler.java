import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

public class NewsCrawler implements Runnable {
	String host;
	String path;
	int total = 0;

	public NewsCrawler(String host, String path) {
		this.host = host;
		this.path = path;
	}

	public void run() {
		webCrawling(this.host);
	}

	public void webCrawling(String host) {
		URL url = null;
		String newsLink[] = new String[20];
		for (int page = 1; page < 9999; page++) {
			if (page != 1)
				host = host.replace("page=" + Integer.toString(page - 1),
						"page=" + Integer.toString(page));
			try {
				url = new URL(host);
				findLinks(url, newsLink);
				for (int i = 0; i < newsLink.length; i++) {
					//Thread.sleep(2000);
					++total;
					if(total%10 ==0){
						System.out.println(Thread.currentThread().getName() + "] -> 10개 돌파!");
					}
					url = new URL(newsLink[i]);
					getContentInURL(url);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.printf(">>현재까지 %dPage까지 크롤링하였고 총 기사의 수는 %d입니다\n",
			// page,
			// total);
		}
	}

	private void findLinks(URL url, String[] newsLink) {
		String input;
		String temp = "";
		boolean linksCheck = false;
		int count = 0;

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((input = in.readLine()) != null) {
				if (input.contains("type06_headline"))
					linksCheck = true;
				if (linksCheck && input.contains("</div>"))
					linksCheck = false;
				if (linksCheck) {
					String link = find(input, temp);
					if (!link.equals("")) {
						newsLink[count++] = link;
						temp = link;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String find(String input, String temp) {
		String link;

		if (input.contains("<a href")) {
			int start = input.indexOf("\"");
			link = input.substring(start + 1);
			int end = link.indexOf("\"");
			link = link.substring(0, end);
			if (!link.startsWith("http://")) {
				return "";
			}
			if (temp.equals(link))
				return "";
			else
				return link;
		} else {
			return "";
		}
	}

	public void getContentInURL(URL url) {
		String input;
		boolean articleBodyCheck = false;
		boolean articleHeadCheck = true;
		BufferedWriter out = null;
		String title = null;	
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			while ((input = in.readLine()) != null) {
				if (input.equals("")) {
				} else {
					if (articleHeadCheck) {
						if (input.contains("<title>")) {
							title = replaceTitle(input);
							
							articleHeadCheck = false;
						}
					} else {
						if (input.contains("me2:category1")) {
							int last1 = input.lastIndexOf("\"");
							input = input.substring(0, last1);
							int last2 = input.lastIndexOf("\"");
							input = path + "\\" +input.substring(last2+1, last1);
							
							File dir = new File(input);
							if(!dir.exists())
								dir.mkdir();
							out = new BufferedWriter(new OutputStreamWriter(
									new FileOutputStream(input + "\\" + title)));
						} else {
							if (input.contains("본문 내용"))
								articleBodyCheck = !articleBodyCheck;
							if (articleBodyCheck)
								extractBody(input, out);
						}
					}
				}
			}
			in.close();
			out.close();
		} catch (Exception e) {
			System.out.println(url.toString());
			System.out.println(url);
			e.printStackTrace();
		}
	}

	private void extractBody(String input, BufferedWriter out)
			throws IOException {
		String temp = input.replaceAll(
				"<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
		temp = temp.replaceAll("<!--.*-->", "");
		temp = temp.replaceAll("\\t", "");
		temp = temp.replaceAll("\\s{2,}", " ");
		if (!temp.equals("")) {
			// System.out.println(temp);
			out.write(temp);
		}
	}

	public String replaceTitle(String contents) {
		int start = contents.indexOf("<title>");
		int end = contents.indexOf("</title>");
		String title = contents.substring(start + 7, end);
		if (title.contains("&quot;")) {
			title = title.replace("&quot;", "\"");
		}
		// System.out.println("기사의 제목 : " + title);
		title = trimTitle(title);
		// System.out.println("바뀔 파일명 : " + title);

		return title;
	}

	public String trimTitle(String title) {
		String temp = title;
		temp = temp.trim() + ".txt";
		temp = temp.replace(" ", "_");
		temp = temp.replace("\\", "_");
		temp = temp.replace("/", "_");
		temp = temp.replace(":", "_");
		temp = temp.replace("*", "_");
		temp = temp.replace("?", "");
		temp = temp.replace("\"", "_");
		temp = temp.replace("<", ")");
		temp = temp.replace(">", "_");
		temp = temp.replace("|", "_");
		return temp;
	}
}
