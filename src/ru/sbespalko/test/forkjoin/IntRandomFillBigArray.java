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
    private int[]      data;
    private int        start;
    private int        end;

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

    /*
     * (non-Javadoc)
     *
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute() {
        if ((end - start) <= IntRandomFillBigArray.THRESHOLD) {
            for (int i = start; i < end; i++) {
                long x = ThreadLocalRandom.current().nextInt();
                x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
                x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
                x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
                x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
                x = (long) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
                data[i] = (int) x;
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

        int[] data = new int[100_000_000];
        int[] data1 = new int[100_000_000];

        long startTime = System.nanoTime();
        ForkJoinPool fjPool = new ForkJoinPool();
        IntRandomFillBigArray filler = new IntRandomFillBigArray(data);
        fjPool.invoke(filler);

        IntRandomFillBigArray filler1 = new IntRandomFillBigArray(data1);
        fjPool.invoke(filler1);

        System.out.println("Time for work ForkJoin: " + ((System.nanoTime() - startTime) / 1e9));
        System.out.println("Threshold = " + IntRandomFillBigArray.THRESHOLD);
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
