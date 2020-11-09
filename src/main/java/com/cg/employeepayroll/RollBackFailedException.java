package com.cg.employeepayroll;

public class RollBackFailedException extends Throwable {
    public RollBackFailedException(String s) {
        super(s);
    }
}
