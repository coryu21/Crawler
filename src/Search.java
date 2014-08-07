import java.net.URLEncoder;
import java.util.Scanner;

public class Search {

	public static void main(String argsp[]) {
		Scanner s = new Scanner(System.in);
		while (true) {
			String input = s.nextLine();
			String url = URLEncoder.encode(input);
			System.out.println(url);
		}
	}

}
