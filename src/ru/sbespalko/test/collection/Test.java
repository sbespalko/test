package ru.sbespalko.test.collection;

import java.util.LinkedHashSet;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Set<String> set = new LinkedHashSet<>();
        set.add("b");
        set.add("c");
        set.add("a");
        set.add("z");
        set.add("t");
        set.add("d");
        System.out.println(set.toString());
    }

}
