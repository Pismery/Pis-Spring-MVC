package org.pismery.mvc.handleradapter;

import org.pismery.mvc.MyModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PisHandlerAdapter {
    MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler);
}
