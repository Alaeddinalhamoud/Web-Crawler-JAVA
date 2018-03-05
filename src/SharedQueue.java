import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class SharedQueue {
	
	 static Queue<String> q = new LinkedList<String>();
	String Url;
	 Lock  lock;
	 
	public SharedQueue(String Url) {
	this.Url=Url;	
	}
	
	public  synchronized static void Set(String Url) {
		 
			q.add(Url);
		 
	}
	//Sync Coz it remove the item
	public synchronized static  String Get() {
		String Url=q.poll();
		 return Url;
	}
	
	public synchronized static boolean IsEmpty() {
		if(q.isEmpty()) {
			return true;
		}
		 return false;
	}
	
	public synchronized static boolean IsContains(String Url) {
		if(q.contains(Url)) {
			return true;
		}
		 return false;
	}

	 public synchronized static int Size() {
		 int size=q.size();
		 return size;
	 }

}
