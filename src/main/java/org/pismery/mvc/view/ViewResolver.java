package org.pismery.mvc.view;

import org.pismery.mvc.MyModelAndView;

public interface ViewResolver {
    String parse(MyModelAndView mv);
    String matchView();
}
