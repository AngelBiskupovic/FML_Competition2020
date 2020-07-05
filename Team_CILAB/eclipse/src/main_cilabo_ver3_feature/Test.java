package main_cilabo_ver3_feature;

public class Test {
	public static void main(String[] args) {

//		ArrayList<String> a = new ArrayList<>();
//		a.add("a");
//		a.add("b");
//		String[] b = a.toArray(new String[0]);
//		System.out.println(b);

		int loop = 2222;
		String binary = Integer.toBinaryString(loop);
		int le = 12;
		while(binary.length() != le) {
			binary = "0" + binary;
		}
		System.out.println(binary);
	}
}
