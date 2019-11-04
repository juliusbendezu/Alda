package t4hash;

import java.util.Arrays;

public class ISBN10 {

	private char[] isbn;

	public ISBN10(String isbn) {
		if (isbn.length() != 10)
			throw new IllegalArgumentException("Wrong size, must be 10");
		if (!checkDigit(isbn))
			throw new IllegalArgumentException("Not a valid isbn 10");
		this.isbn = isbn.toCharArray();
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof ISBN10))
			return false;
		ISBN10 other = (ISBN10) o;
		return Arrays.equals(isbn, other.isbn);
	}

	/* isbn is stored as a sequence of chars, but is usually represented with only numbers
	 * and sometimes an X. Since the ascii-values for numbers 0-9 run from 48-57
	 * I attempt to make a good distribution by adding i times a prime number.
	 */
	@Override
	public int hashCode(){
		int hashVal = 7;
		for(int i = 0; i < isbn.length; i++){
			hashVal += isbn[i] + (i * 7);
		}

		return hashVal;
	}

	private boolean checkDigit(String isbn) {
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			sum += Character.getNumericValue(isbn.charAt(i)) * (10 - i);
		}
		int checkDigit = (11 - (sum % 11)) % 11;

		return isbn.endsWith(checkDigit == 10 ? "X" : "" + checkDigit);
	}

	@Override
	public String toString() {
		return new String(isbn);
	}
}
