package org.pismery.service;

import org.pismery.annotation.PisService;

import java.util.HashMap;
import java.util.Map;

@PisService("helloService")
public class HelloServiceImpl implements HelloService {


    @Override
    public Map<String, Object> hello(String name,String addr) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("addr", addr);
        return result;
    }
}
