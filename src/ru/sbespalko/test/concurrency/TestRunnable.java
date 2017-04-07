package ru.sbespalko.test.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	private int testingTime;
	private int threadsNum;
	private long summOps = 0;

	public TestRunnable(int testingTime, int threadsNum) {
		this.testingTime = testingTime;
		this.threadsNum = threadsNum;
	}

	// Просто печать
	public void printResults(long workTime) {
		System.out.format("Working time: %5.2f sec %n", workTime / 1e9);
		System.out.format("SummOps in %2d sec with %2d threads(ideal %6d): %6d mln%n", testingTime, threadsNum,
				13600 * testingTime, summOps);
		System.out.format("SummOps in 1 sec with %2d threads(ideal 13600 mln) %5d mln%n", threadsNum,
				summOps / testingTime);
		System.out.format("SummOps in 1 sec with 1 thread (ideal 3400): %d mln%n",
				summOps / (testingTime * threadsNum));
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
				while (!Thread.currentThread().isInterrupted()) {
					i++;
				}
				summOps += (int) (i / 1e6);
			}

		}
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		long startTime = System.nanoTime();
		for (int th = 0; th < threadsNum; th++) {
			executor.execute(new backCounter());
		}
		TimeUnit.SECONDS.sleep(testingTime);
		executor.shutdownNow();
		this.printResults(System.nanoTime() - startTime);
	}

	public static void main(String[] args) throws InterruptedException {
		new TestRunnable(10, 100).test();
	}
}
