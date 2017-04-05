package ru.sbespalko.test.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ����� <code>TestRunnable</code> ������������� ������ ������� � ������������
 * �������. ��� ������� ����� ������� ������� ����� ����(+hyperthreading - 1)
 * �������������� ������� ������ �� �����������. 
 * ��� ������������� hyperthreading 1 ����� ������������� ��� ���������������. 
 * �� ������ ��������� - ����� ������ � ������ �������/��������� ���������. 
 * ���-�� �������� ++, ����������� ����� �������� �� ��� �����. 
 * ���-�� �������� ++, ����������� ����� �������� �� 1 �������. 
 * ���-�� �������� ++, ����������� 1 ������� �� 1 �������. 
 * �� ������� (i7 3.4Ghz)���������� ~1/6 - 1/10 �� ������� ����������.
 * 
 * @author Bespalko
 *
 */
public class TestRunnable {
	private int testingTime;
	private int threadsNum;
	private volatile boolean stopRequested;
	private long summOps = 0;
	List<Thread> list;
	

	TestRunnable(int testingTime, int threadsNum) {
		this.testingTime = testingTime;
		this.threadsNum = threadsNum;
		list = new ArrayList<>();
	}

	// ������ ������
	public void printResults(long startTime) {
		System.out.format("Working time: %5.2f sec %n", (System.nanoTime() - startTime) / 1e9);
		System.out.format("SummOps in %2d sec with %2d threads(ideal %6d): %6d mln%n", testingTime, threadsNum,
				13600 * testingTime, summOps);
		System.out.format("SummOps in 1 sec with %2d threads(ideal 13600 mln) %5d mln%n", threadsNum,
				summOps / testingTime);
		System.out.format("SummOps in 1 sec with 1 thread (ideal 3400): %d mln%n",
				summOps / (testingTime * threadsNum));
	}

	// ��� ���-�� ������� ������ ����� ���� (+ hyperthreading), �������� ������
	// ����������� ���������� �����!!!
	// ������� ��������� �����-�������. ��������� ��������, ���� ����������
	// tetingTime, ����� ����, ���� ��� ���������
	// �������� ����� �����, ����������� ��������.
	public void test() throws InterruptedException {

		class backCounter implements Runnable {

			long i = 0;

			@Override
			public void run() {
				while (!stopRequested) {
					i++;
				}
				summOps += (int) (i / 1e6);
			}

		}

		for (int th = 0; th < threadsNum; th++) {
			list.add(new Thread(new backCounter()));
		}
		long startTime = System.nanoTime();
		for (Thread thread : list) {
			thread.start();
		}
		TimeUnit.SECONDS.sleep(testingTime);
		stopRequested = true;
		for (Thread thread : list) {
			while (thread.isAlive()) {
			}
		}
		this.printResults(startTime);
	}

	public static void main(String[] args) throws InterruptedException {
		new TestRunnable(10, 7).test();
	}
}