package t4hash;

import java.util.Arrays;

public class MyString {

	private char[] data;
	
	public MyString(String title) {
		data = title.toCharArray();
	}

	public Object length() {
		return data.length;
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof MyString))
			return false;
		MyString other = (MyString) o;
		return Arrays.equals(data, other.data);
	}

	/* 7 and 37 are just random prime numbers chosen to minimize the risk of 2 different
	 * MyString objects having the same hashValue.
	 * This implementation is very similar to java.lang.String.hashCode() with the difference
	 * that this method only counts the 20 first characters, since class MyString is meant to be
	 * used for author names, book titles and book content. These attributes should hopefully
	 * not collide too often and that's why this implementation is chosen.
	 */
	@Override
	public int hashCode(){
		int hashVal = 7;
		int length = data.length < 20 ? data.length: 20;
		for(int i = 0; i < length; i++){
			hashVal = hashVal * 37 + data[i];
		}

		return hashVal;
	}

	@Override
	public String toString() {
		return new String(data);
	}
	
}
