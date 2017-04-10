/**
 * 
 */
package ru.sbespalko.test.forkjoin;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sergey
 *
 */
public class IntRandomFill {
	private int[] data;

	public IntRandomFill(int[] data) {
		this.data = data;
	}

	public void fill() {
		for (int i = 0; i < data.length; i++) {
			data[i] = ThreadLocalRandom.current().nextInt();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		int[] data = new int[100_000_000];
		IntRandomFill filler = new IntRandomFill(data);
		filler.fill();
		System.out.println("Time for work nonForkJoin: " + ((System.nanoTime() - startTime) / 1e9));
		String str = "";
		for (int i : data) {
			str = str + i + " ";
			if (str.length() > 500) {
				break;
			}
		}
		System.out.println(str);
		System.out.println("Lenght = " + data.length);
	}

}
