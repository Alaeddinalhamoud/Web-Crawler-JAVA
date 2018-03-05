import java.util.Arrays;
import java.util.List;

public class Main {
	public static Thread mainThread;

	public static void main(String[] args) {

		// Write down The web site you want to Mine.
		String RootLink = "https://www.bbc.co.uk";
		// Number of thread you want to use.
		int MAX_THREAD_NUMBER = 10;
		// Keywords you want to find
		List<String> keywords = Arrays.asList("university", "sports", "holidays", "britain", "brexit", "EU", "Winter");

		MinnerManager rm = new MinnerManager(RootLink, MAX_THREAD_NUMBER, keywords);
		mainThread = new Thread(rm);
		mainThread.start();

	}

}
