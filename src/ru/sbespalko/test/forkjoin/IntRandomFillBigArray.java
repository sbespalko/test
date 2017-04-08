/**
 * 
 */
package ru.sbespalko.test.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sergey
 *
 */
@SuppressWarnings("serial")
public class IntRandomFillBigArray extends RecursiveAction {

	private static int THRESHOLD;
	private int[] data;
	private int start;
	private int end;

	public IntRandomFillBigArray(int[] data) {
		this.data = data;
		IntRandomFillBigArray.THRESHOLD = data.length / Runtime.getRuntime().availableProcessors();
		this.start = 0;
		this.end = data.length;
	}

	public IntRandomFillBigArray(int[] data, int THERSHOLD) {
		this(data);
		IntRandomFillBigArray.THRESHOLD = THERSHOLD;
	}

	public IntRandomFillBigArray(int[] data, int start, int end) {
		this.data = data;
		this.start = start;
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute() {
		if ((end - start) <= IntRandomFillBigArray.THRESHOLD) {
			for (int i = start; i < end; i++) {
				data[i] = ThreadLocalRandom.current().nextInt();
			}
		} else {
			int HalfWay = ((end - start) / 2) + start;
			IntRandomFillBigArray worker1 = new IntRandomFillBigArray(data, start, HalfWay);
			IntRandomFillBigArray worker2 = new IntRandomFillBigArray(data, HalfWay, end);
			worker2.compute();
			ForkJoinTask.invokeAll(worker1, worker2);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		int[] data = new int[100_000_000];
		ForkJoinPool fjPool = new ForkJoinPool();
		IntRandomFillBigArray filler = new IntRandomFillBigArray(data);
		fjPool.invoke(filler);
		System.out.println("Time for work ForkJoin: " + ((System.nanoTime() - startTime) / 1e9));
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
