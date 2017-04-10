/**
 * 
 */
package ru.sbespalko.test.forkjoin;

/**
 * @author sergey
 *
 */
public class FindMaxPosition {
	private int[] data;
	
	public FindMaxPosition(int[] data) {
		this.data = data;
	}
	public int findMax(){
		int maxPos = 0;
		int max = data[0];
		for (int i = 1; i < data.length; i++) {
			if(max < data[i]) {
				maxPos = i;
				max = data[i];
			}
		}
		return maxPos;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] data = new int[100_000_000];
		IntRandomFill filler = new IntRandomFill(data);
		filler.fill();
		
		
		long startTime = System.nanoTime();
		FindMaxPosition finder = new FindMaxPosition(data);
		int max = finder.findMax();
		System.out.println("Time for work nonForkJoin: " + ((System.nanoTime() - startTime) / 1e9));
		System.out.println("MaxPosition = "+ max);

	}

}
