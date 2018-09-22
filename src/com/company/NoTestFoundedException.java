package com.company;

public class NoTestFoundedException extends Throwable {

    public NoTestFoundedException() {
        super("No test founded");
    }

    public NoTestFoundedException(String message) {
        super(message);
    }


}
