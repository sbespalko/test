package ru.sbespalko.test.concurrency;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NameList {
	private List<String> names = Collections.synchronizedList(new LinkedList<>());

	public void add(String name) {
		names.add(name);
	}

	public synchronized String removeFirst() {
		if (names.size() > 0) {
			return (String) names.remove(0);
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		final NameList nl = new NameList();
		nl.add("Ozymandias");
		class NameDropper extends Thread {
			@Override
			public void run() {
				String name = nl.removeFirst();
				System.out.println(name);
			}
		}
		Thread t1 = new NameDropper();
		Thread t2 = new NameDropper();
		t1.start();
		t2.start();
	}
}
