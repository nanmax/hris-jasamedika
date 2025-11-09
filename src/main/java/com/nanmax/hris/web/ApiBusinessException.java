package com.nanmax.hris.web;
public class ApiBusinessException extends RuntimeException {
    public ApiBusinessException(String msg) {
        super(msg);
    }
}