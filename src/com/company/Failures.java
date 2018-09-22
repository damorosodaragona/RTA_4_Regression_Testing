package com.company;

import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.concurrent.atomic.AtomicReference;

public class Failures extends SummaryGeneratingListener {


    public String toString() {
        AtomicReference<String> failures = new AtomicReference<>(new String());
        getSummary().getFailures().forEach(failure -> failures.set(failures.get().concat(failure.getException().getStackTrace().toString() + System.lineSeparator())));
        return failures.get();
    }
}
