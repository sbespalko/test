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
			long x = ThreadLocalRandom.current().nextInt();
			x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
			x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
			x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
			x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
			x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
			
			data[i] = (int) x;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] data = new int[100_000_000];
		int[] data1 = new int[100_000_000];
		
		long startTime = System.nanoTime();
		IntRandomFill filler = new IntRandomFill(data);
		filler.fill();
		
		IntRandomFill filler1 = new IntRandomFill(data1);
		filler1.fill();
		
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
