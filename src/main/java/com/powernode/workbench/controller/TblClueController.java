package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblClue;
import com.powernode.settings.pojo.TblDicValue;
import com.powernode.workbench.service.TblActivityService;
import com.powernode.workbench.service.TblClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/clue")
public class TblClueController {

    @Autowired
    private TblActivityService tblActivityService;
    @Autowired
    private TblClueService tblClueService;
    @Value("${sessionUser}")
    private String SESSION_USER;

    //查询所有下拉选
    @RequestMapping("/options")
    public Result getAllOptions(){
        try {
            List<TblUser> allUsers = tblActivityService.getAllUsers();

            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            Map<String, Set<TblDicValue>> dicMap = (Map<String, Set<TblDicValue>>) context.getServletContext().getAttribute("dicMap");
            Set<TblDicValue> appellation = dicMap.get("appellation");
            Set<TblDicValue> clueState = dicMap.get("clueState");
            Set<TblDicValue> source = dicMap.get("source");

            Map map = new HashMap();
            map.put("users",allUsers);
            map.put("appellation",appellation);
            map.put("clueState",clueState);
            map.put("source",source);

            return Result.ok(map);
        } catch (RuntimeException e) {
            return Result.build(1404,"未找到数据");
        }
    }



    //保存创建线索
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result saveCreate(TblClue tblClue, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblClue.setCreateby(name);
        try {
            tblClueService.saveCreateClue(tblClue);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1500,e);
        }

    }

    //分页搜索线索
    @RequestMapping("/find")
    public Result pageFind(@RequestParam(value = "pageSize", required = true) int pageSize,
                           @RequestParam(value = "pageNo", required = true) int pageNo,
                           @RequestParam(value = "fullname",required = false) String fullname,
                           @RequestParam(value = "company",required = false) String company,
                           @RequestParam(value = "phone",required = false) String phone,
                           @RequestParam(value = "source",required = false) String source,
                           @RequestParam(value = "owner",required = false) String owner,
                           @RequestParam(value = "mphone",required = false) String mphone,
                           @RequestParam(value = "state",required = false) String state
                           ){
        try {
            PageResult pageResult = tblClueService.pageFindClues(pageSize, pageNo, fullname, company, phone, source, owner, mphone, state);
            return Result.ok(pageResult);
        } catch (ResultException e) {
            return Result.build(1500,e);
        }

    }

    @RequestMapping("/del")
    public Result deleteClues(String[] ids){
        try {
            tblClueService.delClueById(ids);
            return Result.ok();
        } catch (ResultException e) {
           return Result.build(1500,e);
        }
    }

    @RequestMapping("/id")
    public Result selectClue(String id){
        List<TblUser> allUsers = tblActivityService.getAllUsers();

        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        Map<String, Set<TblDicValue>> dicMap = (Map<String, Set<TblDicValue>>) context.getServletContext().getAttribute("dicMap");
        Set<TblDicValue> appellation = dicMap.get("appellation");
        Set<TblDicValue> clueState = dicMap.get("clueState");
        Set<TblDicValue> source = dicMap.get("source");

        Map map = new HashMap();
        map.put("users",allUsers);
        map.put("appellation",appellation);
        map.put("clueState",clueState);
        map.put("source",source);


        try {
            TblClue tblClue = tblClueService.selectClueById(id);

            map.put("tblClue",tblClue);
            return Result.ok(map);
        } catch (ResultException e) {
            return Result.build(1500,e);
        }


    }

    @RequestMapping("/update")
    public Result updateClue(TblClue tblClue, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblClue.setEditby(name);
        try {
            tblClueService.updateClue(tblClue);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1500,e);
        }
    }

    @RequestMapping("/detail")
    public Result getDetailBuId(String id){
        try {
            TblClue details = tblClueService.getAllDetails(id);
            return Result.ok(details);
        } catch (ResultException e) {
            return Result.build(1404,e);
        }
    }

    @RequestMapping("/convert")
    public Result convert(@RequestParam(value = "clueId", required = true) String clueId,
                          @RequestParam(value = "activityId", required = false) String activityId,
                          @RequestParam(value = "stage", required = false) String stage,
                          @RequestParam(value = "money", required = false) String money,
                          @RequestParam(value = "tranName", required = false) String tranName,
                          @RequestParam(value = "exDate", required = false) String exDate,
                          @RequestParam(value = "customerName", required = false) String customerName,
                          String tranFlag,
                          HttpServletRequest request
                          ){
        String createby = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblClueService.convert(clueId, activityId, stage, money, tranName, exDate, createby, customerName, tranFlag);
        return Result.ok();
    }




}
