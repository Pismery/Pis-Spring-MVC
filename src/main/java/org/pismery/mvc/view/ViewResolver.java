package org.pismery.mvc.view;

import org.pismery.mvc.PisModelAndView;

public interface ViewResolver {
    String parse(PisModelAndView mv);
    String matchView();
}
