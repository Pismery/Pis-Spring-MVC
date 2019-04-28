package org.pismery.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PisModelAndView {
    private String viewName;
    private Map<String,Object> model;
}
