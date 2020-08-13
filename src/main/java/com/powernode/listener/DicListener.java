package com.powernode.listener;

import com.powernode.settings.pojo.TblDicValue;
import com.powernode.settings.service.TblDicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class DicListener implements ServletContextListener {

    @Autowired
    private TblDicService dicService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getAutowireCapableBeanFactory().autowireBean(this);
        //TblDicService bean = context.getBean(TblDicService.class);
        Map<String, Set<TblDicValue>> dicMap = dicService.getDicMap();
        //Map<String, Set<TblDicValue>> dicMap = bean.getDicMap();
        sce.getServletContext().setAttribute("dicMap",dicMap);



        ResourceBundle bundle = ResourceBundle.getBundle("properties/Stage2Possibility");
        Enumeration<String> keys = bundle.getKeys();
        Map<String, String> pMap = new TreeMap<>();
        while (keys.hasMoreElements()){
            String s = keys.nextElement();
            String string = bundle.getString(s);
            pMap.put(s,string);
        }
        sce.getServletContext().setAttribute("pMap",pMap);


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
