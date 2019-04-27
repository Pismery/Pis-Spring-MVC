package org.pismery.controller;

import org.pismery.annotation.PisAutowired;
import org.pismery.annotation.PisController;
import org.pismery.annotation.PisRequestMapping;
import org.pismery.annotation.PisRequestName;
import org.pismery.mvc.MyModelAndView;
import org.pismery.service.HelloService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@PisController("helloController")
@PisRequestMapping("/hello")
public class HelloController {


    @PisAutowired
    private HelloService helloService;


    @PisRequestMapping("/world")
    public MyModelAndView helloWorld(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @PisRequestName("pisName") String name,
                                     @PisRequestName String addr) throws IOException {
        Map<String, Object> hello = helloService.hello(name, addr);
        Map<String, Object> hello1 = helloService.hello(name, addr);
        Map<String, Object> hello2 = helloService.hello(name, addr);
        Map<String, Object> hello3 = helloService.hello(name, addr);
        Map<String, Object> hello4 = helloService.hello(name, addr);
        return new MyModelAndView("index.pishtml", hello);
    }
}
