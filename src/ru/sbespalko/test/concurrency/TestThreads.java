package ru.sbespalko.test.concurrency;

import java.util.ArrayList;
import java.util.List;
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
 * На текстах (i7 4-core*2(hyper) 3.4Ghz)получается ~1/6 - 1/10 от частоты процессора.
 *
 * @author Bespalko
 *
 */

public class TestThreads {
    private int              testingTime;
    private int              threadsNum;
    private volatile boolean stopRequested;
    private volatile int     summOps = 0;
    List<Thread>             list;

    TestThreads(int testingTime, int threadsNum) {
        this.testingTime = testingTime;
        this.threadsNum = threadsNum;
        list = new ArrayList<>();
    }

    public void printResults(long startTime) {
        System.out.format("Working time: %5.2f sec %n", (System.nanoTime() - startTime) / 1e9);
        System.out.format("SummOps in %2d sec with %2d threads(ideal %6d): %6d mln%n", testingTime, threadsNum, 13600 * testingTime, summOps);
        System.out.format("SummOps in 1 sec with %2d threads(ideal 13600 mln) %5d mln%n", threadsNum, summOps / testingTime);
        System.out.format("SummOps in 1 sec with 1 thread (ideal 3400): %d mln%n", summOps / (testingTime * threadsNum));
    }

    public void test() throws InterruptedException {

        class backCounter extends Thread {

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
            list.add(new backCounter());
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
        new TestThreads(10, 20).test();
    }
}
