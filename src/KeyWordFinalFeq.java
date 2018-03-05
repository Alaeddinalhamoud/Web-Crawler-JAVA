
import java.util.HashMap;

import java.util.Map;

public class KeyWordFinalFeq {
	static Map<String, Integer> KeyWordFinal = new HashMap<String, Integer>();
	String word;
	int Frq;

	public KeyWordFinalFeq(String word, int Frq, int Key) {
		this.word = word;
		this.Frq = Frq;
	}

	public synchronized static void Set(String word, int Frq) {
		KeyWordFinal.put(word, Frq);
	}

	public synchronized static int Get(String Key) {
		int Value = KeyWordFinal.get(Key);
		return Value;
	}

	public synchronized static boolean ContainsKey(String Key) {
		if (KeyWordFinal.containsKey(Key)) {
			return true;
		}
		return false;
	}

	public synchronized static int GetTotal() {

		int KeyWordsSum = KeyWordFinal.entrySet().stream().mapToInt(e -> e.getValue()).sum();

		return KeyWordsSum;
	}

	public synchronized static void print() {
		System.out.println("------------Key Word Freq---------------------------");
		KeyWordFinal.forEach((key, value) -> {
			System.out.println(key + "  = " + value);
		});
		System.out.println("---------------------------------------------------");
	}

}
