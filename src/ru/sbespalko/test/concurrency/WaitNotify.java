package ru.sbespalko.test.concurrency;

public class WaitNotify {

    public static void main(String[] args) {
        ThreadB b = new ThreadB();
        b.start();

        synchronized (b) {
            try {
                System.out.println("Waiting for b to complete...");
                b.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Total is: " + b.total);
        }
        int summ = 0;
        for (int i = 0; i < 100; i++) {
            summ += i;
        }
        System.out.println(summ);

    }
}

class ThreadB extends Thread {
    int total;

    @Override
    public void run() {
        // сихронизируемся по this из-за того, что используем notify()
        // работает и без синхронизации
        synchronized (this) {
            for (int i = 0; i < 100; i++) {
                total += i;
            }

            notify();
        }

    }
}
