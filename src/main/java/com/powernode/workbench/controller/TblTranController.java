package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblDicValue;
import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblActivityService;
import com.powernode.workbench.service.TblTranService;
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

@RestController
@RequestMapping("/tran")
public class TblTranController {

    @Autowired
    private TblTranService tblTranService;
    @Autowired
    private TblActivityService tblActivityService;
    @Value("${sessionUser}")
    private String SESSION_USER;

    @RequestMapping("/options")
    public Result getAllOptions(HttpServletRequest request){
        //Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        Map dicMap = (Map) context.getServletContext().getAttribute("dicMap");

        Map map = new HashMap();
        map.put("stage",dicMap.get("stage"));
        map.put("transactionType",dicMap.get("transactionType"));
        map.put("source",dicMap.get("source"));
        return Result.ok(map);
    }

    @RequestMapping("/trans")
    public Result getAllTran(@RequestParam(value = "pageSize", required = true) int pageSize,
                             @RequestParam(value = "pageNum", required = true) int pageNum,
                             @RequestParam(value = "owner",required = false) String owner,
                             @RequestParam(value = "name",required = false) String name,
                             @RequestParam(value = "customerId",required = false) String customerId,
                             @RequestParam(value = "stage",required = false) String stage,
                             @RequestParam(value = "type",required = false) String type,
                             @RequestParam(value = "source",required = false) String source,
                             @RequestParam(value = "contactsId",required = false) String contactsId
                             ){
        PageResult pageResult = tblTranService.pageAllTran(pageSize, pageNum, owner, name, customerId, stage, type, source, contactsId);
        return Result.ok(pageResult);
    }

    @RequestMapping("/optionsAdd")
    public Result getAllOptionsAdd(HttpServletRequest request){
        Map map = new HashMap();

        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
        map.put("stage",dicMap.get("stage"));
        map.put("transactionType",dicMap.get("transactionType"));
        map.put("source",dicMap.get("source"));

        List<TblUser> allUsers = tblActivityService.getAllUsers();
        map.put("user",allUsers);
        return Result.ok(map);

    }

    @RequestMapping("/possible")
    public Result getPossible(String stage, HttpServletRequest request){
        Map pMap = (Map)request.getSession().getServletContext().getAttribute("pMap");
        Object o = pMap.get(stage);
        return Result.ok(o);
    }

    @RequestMapping("/activity")
    public Result getAllActivity(){
        List<TblActivity> allActivity = tblTranService.getAllActivity();
        return Result.ok(allActivity);
    }

    @RequestMapping("/contacts")
    public Result getAllContacts(){
        List<TblContacts> allContacts = tblTranService.getAllContacts();
        return Result.ok(allContacts);
    }

    @RequestMapping("/customer")
    public Result getCustomer(String name){
        List<Map<String, String>> customer = tblTranService.getCustomer(name);
        return Result.ok(customer);
    }

    @RequestMapping("/customerId")
    public Result getCustomerId(String customerName, String customerOwner, HttpServletRequest request){
        String cteateby = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        String customerId = tblTranService.getCustomerId(customerName, customerOwner, cteateby);
        return Result.ok(customerId);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addTran(TblTran tblTran, HttpServletRequest request){
        System.out.println(tblTran) ;
        String createby = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblTranService.addTran(tblTran, createby);
        return Result.ok();
    }

    @RequestMapping("/edit")
    public Result getTranById(String id, HttpServletRequest request){
        Map map = new HashMap();

        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
        map.put("stage",dicMap.get("stage"));
        map.put("transactionType",dicMap.get("transactionType"));
        map.put("source",dicMap.get("source"));

        List<TblUser> allUsers = tblActivityService.getAllUsers();
        map.put("user",allUsers);

        TblTran tran = tblTranService.getTranById(id);
        map.put("tran",tran);
        return Result.ok(map);
    }

    @RequestMapping("/update")
    public Result updateTran(TblTran tblTran, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblTranService.updateTran(tblTran, name);
        return Result.ok();
    }

    @RequestMapping("/del")
    public Result delTran(String[] ids){
        tblTranService.delTranById(ids);
        return Result.ok();
    }

    @RequestMapping("/detail")
    public Result getTranDetail(String id, HttpServletRequest request){
        TblTran tranDetail = tblTranService.getTranById(id);
        Map pMap = (Map)request.getSession().getServletContext().getAttribute("pMap");
        String possible = (String)pMap.get(tranDetail.getStage());
        tranDetail.setPossible(possible);
        return Result.ok(tranDetail);
    }

    @RequestMapping("/stages")
    public Result getStage(String tranId, HttpServletRequest request){
        Map<String, Set<TblDicValue>> dicMap = (Map<String, Set<TblDicValue>>)request.getSession().getServletContext().getAttribute("dicMap");
        Map<String, String> pMap = (Map<String, String>)request.getSession().getServletContext().getAttribute("pMap");
        Set<TblDicValue> dicSet = dicMap.get("stage");
        Map<String, Object> stages = tblTranService.getStages(tranId, dicSet, pMap);

        return Result.ok(stages);
    }


    @RequestMapping(value = "/history", method = RequestMethod.POST)
    public Result addHistory(TblTranHistory tblTranHistory, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblTranHistory.setCreateby(name);
        TblTran tblTran = tblTranService.addHistory(tblTranHistory);
        Map pMap = (Map)request.getSession().getServletContext().getAttribute("pMap");
        String possible = (String)pMap.get(tblTran.getStage());
        tblTran.setPossible(possible);

        return Result.ok(tblTran);
    }

    @RequestMapping("/getHistory")
    public Result getAllHistory(String tranId, HttpServletRequest request){
        List<TblTranHistory> historyList = tblTranService.getAllHistory(tranId);
        Map pMap = (Map)request.getSession().getServletContext().getAttribute("pMap");
        for (TblTranHistory tblTranHistory : historyList) {
            String possible = (String)pMap.get(tblTranHistory.getStage());
            tblTranHistory.setPossible(possible);
        }
        return Result.ok(historyList);
    }

    @RequestMapping("/remark")
    public Result getAllRemark(String tranId){
        List<TblTranRemark> allRemark = tblTranService.getAllRemark(tranId);
        return Result.ok(allRemark);
    }

    @RequestMapping("/delRemark")
    public Result delRemark(String id){
        tblTranService.delRemark(id);
        return Result.ok();
    }

    @RequestMapping("/editRemark")
    public Result editRemark(String id){
        TblTranRemark tblTranRemark = tblTranService.editRemark(id);
        return Result.ok(tblTranRemark);
    }

    @RequestMapping(value = "/updateRemark", method = RequestMethod.POST)
    public Result updateRemark(TblTranRemark tblTranRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblTranRemark.setEditby(name);
        tblTranService.updateRemark(tblTranRemark);
        return Result.ok();
    }

    @RequestMapping(value = "/addRemark", method = RequestMethod.POST)
    public Result addRemark(TblTranRemark tblTranRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblTranRemark.setCreateby(name);
        tblTranService.addRemark(tblTranRemark);
        return Result.ok();
    }
}
