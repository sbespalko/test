package ru.sbespalko.test.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс <code>TestRunnable</code> демонстрирует работу потоков в многоядерной
 * системе. При задании числа потоков меньшим числа ядер(+hyperthreading - 1)
 * быстродействие каждого потока не уменьшается. 
 * При использовании hyperthreading 1 поток резервируется для диспетчеризации. 
 * На печать выводится - время работы с учетом запуска/остановки процессов. 
 * Кол-во операций ++, выполненное всеми потоками за все время. 
 * Кол-во операций ++, выполненное всеми потоками за 1 секунду. 
 * Кол-во операций ++, выполненное 1 потоком за 1 секунду. 
 * На текстах (i7 3.4Ghz)получается ~1/6 - 1/10 от частоты процессора.
 * 
 * @author Bespalko
 *
 */
public class TestRunnable {

	private final Lock lock = new ReentrantLock();
	private final Condition counting = lock.newCondition();
	private int testingTime;
	private int threadsNum;
	private long summOps;
	private int procCount;
	private int procSpeed; // 3000Mhz

	public TestRunnable(int testingTime, int threadsNum, int procSpeed) {
		this.testingTime = testingTime;
		this.threadsNum = threadsNum;
		this.procCount = Runtime.getRuntime().availableProcessors();
		this.procSpeed = procSpeed;
	}

	// Просто печать
	public void printResults(long workTime) {
		System.out.format("Working time: %5.2f sec %n", workTime / 1e9);
		System.out.format("SummOps in %2d sec with %2d threads(ideal %6d mln): %6d mln%n", testingTime, threadsNum,
				procSpeed * procCount * testingTime, summOps);
		System.out.format("SummOps in 1 sec with %2d threads(ideal %2d mln) %5d mln%n", threadsNum,
				procSpeed * procCount, summOps / testingTime);
		System.out.format("SummOps in 1 sec with 1 thread (ideal %4d mln): %d mln%n", procSpeed,
				summOps / (testingTime * threadsNum));
		System.out.format("Your processor speed = %5dMhz%nLogic processor count = %2d%n", procSpeed, procCount);
	}

	// При кол-ве потоков меньше числа ядер (+ hyperthreading), счетчики должны
	// насчитывать постоянное число!!!
	// Создаем локальный класс-счетчик. Запускаем счетчики, даем поработать
	// tetingTime, потом ждем, пока все закроются
	// Печатаем общую сумму, насчитанную потоками.
	public void test() throws InterruptedException {

		class backCounter implements Runnable {

			long i = 0;

			@Override
			public void run() {
				while (!Thread.interrupted()) {
					i++;
				}
				summOps += (int) (i / 1e6);
			}

		}
		ExecutorService executor = Executors.newFixedThreadPool(procCount);

		Thread timer = new Thread() {
			@Override
			public void run() {
				int time = 0;
				while (time++ < testingTime) {
					System.out.println("time: " + time);
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lock.lock();
				counting.signal();
				lock.unlock();
			}
		};

		timer.start();

		for (int th = 0; th < threadsNum; th++) {
			executor.execute(new backCounter());
		}

		long startTime = System.nanoTime();

		lock.lock();
		try {
			counting.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		executor.shutdownNow();
		while (!executor.isTerminated()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
		this.printResults(System.nanoTime() - startTime);
		System.exit(0);
	}

	public static void main(String[] args) throws InterruptedException {
		new TestRunnable(10, 3, 2200).test();
	}
}
