package org.pismery.mvc;

import org.pismery.mvc.handleradapter.PisAnnotationHandlerAdapter;
import org.pismery.mvc.handleradapter.PisHandlerAdapter;
import org.pismery.mvc.handlermapping.PisHandlerMapping;
import org.pismery.mvc.handlermapping.PisUrlHandlerMapping;
import org.pismery.mvc.view.PisViewResolver;
import org.pismery.mvc.view.ViewResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PisDispatcherServlet extends HttpServlet {

    private PisHandlerMapping handlerMapping;
    private PisHandlerAdapter handlerAdapter;
    private List<ViewResolver> viewResolvers;

    @Override
    public void init() throws ServletException {
        // 初始化 Context
        PisApplicationContext context = new PisApplicationContext("/application.properties");
        // 文件上传
        initMultipartResolver(context);
        // 国际化
        initLocaleResolver(context);
        // View 解析
        initThemeResolver(context);
        // 解析url -> method
        initHandlerMappings(context);
        // 适配器
        initHandlerAdapters(context);
        // 异常处理
        initHandlerExceptionResolvers(context);
        // 从 request 中找到默认 viewName
        initRequestToViewNameTranslator(context);
        // 将 viewName 和 local (本地化信息) 解析成 View
        initViewResolvers(context);
        // 存储一个请求结果至 FlashMap ，可用于一个请求的输入是另一个请求的输出的场景；如重定向；
        initFlashMapManager(context);
    }

    private void initHandlerMappings(PisApplicationContext context) {
        this.handlerMapping = new PisUrlHandlerMapping(context);
    }

    private void initHandlerAdapters(PisApplicationContext context) {
        if (this.handlerMapping == null) {
            return;
        }
        this.handlerAdapter = new PisAnnotationHandlerAdapter();
    }

    private void initViewResolvers(PisApplicationContext context) {
        String templateRoot = context.getProperties().getProperty("templateRoot");
        String rootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File rootDir = new File(rootPath);
        for (File templateFile : rootDir.listFiles()) {
            if (viewResolvers == null) {
                viewResolvers = new ArrayList<>();
            }

            viewResolvers.add(new PisViewResolver(templateFile.getName(), templateFile));
        }
    }

    private void initFlashMapManager(PisApplicationContext context) {

    }

    private void initRequestToViewNameTranslator(PisApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(PisApplicationContext context) {

    }

    private void initThemeResolver(PisApplicationContext context) {

    }

    private void initLocaleResolver(PisApplicationContext context) {

    }

    private void initMultipartResolver(PisApplicationContext context) {
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

    /**
     * 所有请求的处理入口
     * @param req 请求
     * @param resp 响应
     * @throws IOException
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1. 根据 url 获取 handler;
        PisHandler handler = handlerMapping.getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 Pis No found!..");
        }
        //2. 通过 handler 调用目标方法；
        PisModelAndView mv = handlerAdapter.handle(req, resp, handler);

        if (mv == null || mv.getViewName() == null) {
            resp.getWriter().write("404 Pis No found!..");
            return;
        }

        //3. 解析 View，返回前端
        for (ViewResolver viewResolver : viewResolvers) {
            if (!mv.getViewName().equalsIgnoreCase(viewResolver.matchView())) {
                continue;
            }

            String parse = viewResolver.parse(mv);
            if (parse != null) {
                resp.getWriter().write(parse);
                break;
            }
        }

    }

}
