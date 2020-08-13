package com.powernode.workbench.controller;

import com.powernode.utils.Result;
import com.powernode.workbench.service.EchartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequestMapping("/echarts")
@RestController
public class EchartsController {

    @Autowired
    private EchartsService echartsService;

    @RequestMapping("/actOwner")
    public Result getOwnerCount(){
        Map<String, Object> owner = echartsService.getOwnerCount();
        return Result.ok(owner);
    }

    @RequestMapping("/actCost")
    public Result getActCost(){
        List<List<Integer>> actCost = echartsService.getActCost();
        return Result.ok(actCost);

    }

    @RequestMapping("/actAvgCost")
    public Result cost(){
      Map actAvgCost = echartsService.actAvgCost();
        return Result.ok(actAvgCost);
    }

    @RequestMapping("/clueSource")
    public Result getClueSource(HttpServletRequest request){
        List<Map<String, String>> clueSource = echartsService.getClueSource();
        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");

        Map map = new HashMap();
        map.put("clueSource", clueSource);
        map.put("source",dicMap.get("source"));
        return Result.ok(map);
    }

    @RequestMapping("/contactsSource")
    public Result getContactsSource(HttpServletRequest request){
        List<Map<String, String>> contactsSource = echartsService.getContactsSource();
        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");

        Map map = new HashMap();
        map.put("contactsSource", contactsSource);
        map.put("source",dicMap.get("source"));
        return Result.ok(map);
    }

    @RequestMapping("/clueState")
    public Result getClueState(){
        Map clueState = echartsService.getClueState();
        return Result.ok(clueState);
    }

    @RequestMapping("/clueAddress")
    public Result getClueAddress(){
        Map clueAddress = echartsService.getClueAddress();
        return Result.ok(clueAddress);
    }

    @RequestMapping("/contactsAddress")
    public Result contactsAddress(){
        Map map = echartsService.contactsAddress();
        return Result.ok(map);
    }

    @RequestMapping("/tranMoney")
    public Result tranMoney(){
        Map<String, Object> tranMoney = echartsService.getTranMoney();
        return Result.ok(tranMoney);
    }

    @RequestMapping("/tranMoneyOth")
    public Result tranMoney2(){
        Map tranMoney2 = echartsService.getTranMoney2();
        return Result.ok(tranMoney2);
    }
}
