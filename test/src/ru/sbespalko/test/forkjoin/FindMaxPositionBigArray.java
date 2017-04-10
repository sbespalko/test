/**
 * 
 */
package ru.sbespalko.test.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author sergey
 * @param <V>
 *
 */
@SuppressWarnings("serial")
public class FindMaxPositionBigArray extends RecursiveTask<Integer> {
	private static int THRESHOLD;
	private int[] data;
	private int start;
	private int end;
	
	public FindMaxPositionBigArray(int[] data) {
		this.data = data;
		FindMaxPositionBigArray.THRESHOLD = data.length/Runtime.getRuntime().availableProcessors();
		this.start = 0;
		this.end = data.length;
	}

	public FindMaxPositionBigArray(int[] data, int THRESHOLD) {
		this(data);
		FindMaxPositionBigArray.THRESHOLD = THRESHOLD;
	}

	public FindMaxPositionBigArray(int[] data, int start, int end) {
		this.data = data;
		this.start = start;
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveTask#compute()
	 */
	@Override
	protected Integer compute() {
		int maxPos = 0;
		if ((end - start) <= FindMaxPositionBigArray.THRESHOLD) {
			int max =data[0];
			for (int i = 1; i < data.length; i++) {
				if(max < data[i]) {
					maxPos = i;
					max = data[i];
				}
			}
		}else {
			int HalfWay = ((end - start)/2) + start;
			FindMaxPositionBigArray worker1 = new FindMaxPositionBigArray(data, start, HalfWay);
			worker1.fork();
			FindMaxPositionBigArray worker2 = new FindMaxPositionBigArray(data,HalfWay,end);
			int maxPos2 = worker2.compute();
			int maxPos1 = worker1.join();
			if(data[maxPos1] < data[maxPos2]) {
				maxPos = maxPos2;
			}else if(data[maxPos1] > data[maxPos2]){
				maxPos = maxPos1;
			}else {
				maxPos = maxPos1 < maxPos2 ? maxPos1 : maxPos2;
			}
		}
		return maxPos;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int[] data = new int[100_000_000];
		ForkJoinPool fjPool = new ForkJoinPool();
		IntRandomFillBigArray filler = new IntRandomFillBigArray(data);
		fjPool.invoke(filler);
		
		long startTime = System.nanoTime();
		
		FindMaxPositionBigArray finder = new FindMaxPositionBigArray(data);
		int max = fjPool.invoke(finder);
		System.out.println("Time for work ForkJoin: " + ((System.nanoTime() - startTime) / 1e9));
		System.out.println("MaxPosition = "+ max);

	}

}
