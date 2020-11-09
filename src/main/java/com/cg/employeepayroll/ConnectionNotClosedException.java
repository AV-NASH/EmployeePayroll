package com.cg.employeepayroll;

public class ConnectionNotClosedException extends Throwable {
    public ConnectionNotClosedException(String s) {
        super(s);
    }
}
