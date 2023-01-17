
public class test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://aa/aa.jsp#adf;asdf;adf?command=3333";
		int symi1 = url.indexOf("?");
		int symi2 = url.indexOf("#");
		String symbol1 = "";
		String symbol2 = "";
		if (symi1 > 0 && symi2 > 0) {
			if (symi1 < symi2) {
				symbol1 = url.substring(symi1+1, symi2);
				symbol2 = url.substring(symi2);
			} else {
				symbol2 = url.substring(symi2, symi1);
				symbol1 = url.substring(symi1+1);
			}
		} else if (symi1 > 0) {
			symbol1 = url.substring(symi1+1);
		} else if (symi2 > 0) {
			symbol2 = url.substring(symi2);
		}

		
		System.out.println(symbol1 + symbol2);
	}

}
