package com.company;

public class NoPathExeption extends Throwable {
    public NoPathExeption() {
        super("Any path founded");
    }

    public NoPathExeption(String message) {
        super(message);
    }
}
