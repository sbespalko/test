package ru.sbespalko.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

class ChicksYack {

	synchronized void yack(long id) {
		for (int x = 1; x < 3; x++) {
			System.out.print(id + " ");
			Thread.yield();
		}
	}
}

public class Test2 implements Runnable {
	ChicksYack c;

	public static void main(String[] args) throws InterruptedException, IOException {
		new Test2().go();
		Runtime rt = Runtime.getRuntime();
		int cpus = rt.availableProcessors();
		System.out.println(rt.freeMemory()/(1024*1024) + "mb");
		System.out.println(rt.maxMemory()/(1024*1024) + "mb");
		System.out.println(rt.totalMemory()/(1024*1024) + "mb");
		//rt.exec("cmd");
		System.out.println(cpus);
	}

	void go() throws InterruptedException {
		c = new ChicksYack();
		TimeUnit.SECONDS.sleep(1);
		Test2 t1 = new Test2();

		t1.c = c;
		Test2 t2 = new Test2();
		t2.c = c;
		new Thread(t1).start();
		new Thread(t2).start();
	}

	public void run() {
		c.yack(Thread.currentThread().getId());
	}
}
