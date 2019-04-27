package org.pismery.mvc.handlermapping;

import org.pismery.mvc.PisHandler;

import javax.servlet.http.HttpServletRequest;

public interface PisHandlerMapping {
    PisHandler getHandler(HttpServletRequest req);
}
