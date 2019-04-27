package org.pismery.mvc;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class PisHandler {
    private Object controller;
    private Method method;
    private String url;

    public PisHandler(Object controller, Method method, String url) {
        this.controller = controller;
        this.method = method;
        this.url = url;
    }
}
