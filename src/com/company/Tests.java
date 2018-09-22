package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Tests {


    private HashSet<Test> tests;


    public Tests(HashSet<Test> tests) {
        this.tests = tests;
    }

    public Tests() {
        tests = new HashSet<>();
    }

    public HashSet<Test> getTests() {
        return tests;
    }

    public void setTests(HashSet<Test> tests) {
        this.tests = tests;
    }

    public boolean addTest(Test t) {
        return tests.add(t);
    }

    public boolean removeTest(Test t) {
        return tests.remove(t);
    }

    public List<Failures> runTests() {
        List<Failures> allFailures = new ArrayList<>();
        tests.forEach(test ->
                allFailures.add(test.run()));

        return allFailures;
    }

}
