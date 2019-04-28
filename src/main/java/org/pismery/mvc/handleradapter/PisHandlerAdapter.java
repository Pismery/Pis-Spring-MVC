package org.pismery.mvc.handleradapter;

import org.pismery.mvc.PisModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PisHandlerAdapter {
    PisModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler);
}
