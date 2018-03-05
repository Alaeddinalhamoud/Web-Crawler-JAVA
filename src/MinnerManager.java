
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MinnerManager implements Runnable {

	List<String> Visited = new LinkedList<String>();
	public static int MAX_PAGES_NUM = 100;
	public static int TIME_OUT = 20;// Whole app.
	public static int MAX_QUEUE_SIZE = 5000;
	public static int MAX_WORD_COUNT = 5000;
	public static int MAX_THREAD_NUMBER;
	public static List<String> keywords;
	public static String Rootlink;
	ExecutorService exe;
	public static final Object lock = new Object();

	public MinnerManager(String RootLink, int MAX_THREAD_NUMBER, List<String> keywords) {
		// TODO Auto-generated constructor stub
		MinnerManager.Rootlink = RootLink;
		MinnerManager.MAX_THREAD_NUMBER = MAX_THREAD_NUMBER;
		MinnerManager.keywords = keywords;
	}

	@Override
	public void run() {

		// TODO Auto-generated method stub
		PrintHeader();
		//Insert First Link to the Queue...
		SharedQueue.Set(Rootlink);

		exe = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);

		while (true) {
			System.out.print(".");
			String NextUrl;
			if (!SharedQueue.IsEmpty()) {
				
				if (SharedQueue.Size() > MAX_QUEUE_SIZE) {
					System.out.println("");
					System.out.println("*** Minner Reach the Max Queue Size " + MAX_QUEUE_SIZE);
					break;
				}
				if (Visited.size() > MAX_PAGES_NUM) {
					System.out.println("");
					System.out.println("*** Minner Reach the Max Page Number " + MAX_PAGES_NUM);
					break;
				}
				if (KeyWordFinalFeq.GetTotal() > MAX_WORD_COUNT) {
					System.out.println("");
					System.out.println("*** Minner Reach the Max KeyWords Number " + MAX_WORD_COUNT);
					break;
				}
				//Get Next Url From the Queue
				NextUrl = SharedQueue.Get();
				//Create Obj for new Thread
				WebMiner r1 = new WebMiner(NextUrl, keywords);// ???Send link to
																// GetLinks
																// Method
				// Add The link to the visited list .
				Visited.add(NextUrl);
				exe.execute(r1);
			} else {
				// wait till Queue have new Links
				Wait();
			}
			
		}

		exe.shutdown();
		Termination();

		// To print the final result for Keywords from (Shared Resourse map)
		KeyWordFinalFeq.print();
		System.out.println("Total Pages have been visited : " + Visited.size());
		System.out.println("Total Pinding Pages On the Queue : " + SharedQueue.Size());
		System.out.println("Total KeyWords : " + KeyWordFinalFeq.GetTotal());

	}

	public static void PrintHeader() {
		System.out.println("------------Working on Minning Web Pages---------------------------");
		System.out.println("--------------------Please Hold On---------------------------------");
		System.out.println("--------------------We Are Working On -----------------------------");
		System.out.println("-------------------- " + MAX_PAGES_NUM + " Pages--------------------------------------");
		System.out.println("Working on Keywords ");
		System.out.print("Please Wait");
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
			synchronized (lock) {
				lock.wait();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("There is no more pages");
		}
	}

	public void Termination() {
		try

		{
			// I use await Termination to stop all thread waiting for replay
			// form http
			// request
			if (!exe.awaitTermination(TIME_OUT, TimeUnit.SECONDS)) {
				System.err.println("Threads didn't finish in " + TIME_OUT + " Seconds!");
				exe.shutdownNow();
			}
			System.out.println("*** Shutdown time " + TIME_OUT + " Seconds");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
