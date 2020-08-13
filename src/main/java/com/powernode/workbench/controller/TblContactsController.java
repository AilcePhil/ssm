package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblContactsRemark;
import com.powernode.workbench.pojo.TblTran;
import com.powernode.workbench.service.TblActivityService;
import com.powernode.workbench.service.TblContactsService;
import com.powernode.workbench.service.TblTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class TblContactsController {

    @Autowired
    private TblContactsService tblContactsService;
    @Autowired
    private TblActivityService tblActivityService;
    @Autowired
    private TblTranService tblTranService;

    @Value("${sessionUser}")
    private String SESSION_USER;

    @RequestMapping("/contacts")
    public Result pageCustomers(@RequestParam(value = "pageNum", required = true) int pageNum,
                                @RequestParam(value = "pageSize", required = true) int pageSize,
                                @RequestParam(value = "owner", required = false) String owner,
                                @RequestParam(value = "fullname", required = false) String fullname,
                                @RequestParam(value = "customerid", required = false) String customerid,
                                @RequestParam(value = "source", required = false) String source,
                                @RequestParam(value = "birth", required = false) String birth,
                                HttpServletRequest request
    ){
        Map map = new HashMap();
        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
        map.put("source", dicMap.get("source"));

        PageResult allContacts = tblContactsService.getAllContacts(pageNum, pageSize, owner, fullname, customerid, source, birth);
        map.put("contacts",allContacts );
        return Result.ok(map);
    }

    @RequestMapping("/options")
    public Result getOptions(HttpServletRequest request){
        Map map = new HashMap();

        List<TblUser> allUsers = tblActivityService.getAllUsers();
        map.put("user", allUsers);

        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
        map.put("source", dicMap.get("source"));
        map.put("appellation",dicMap.get("appellation"));

        return Result.ok(map);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addContacts(TblContacts tblContacts, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblContacts.setCreateby(name);
        tblContactsService.addContacts(tblContacts);
        return Result.ok();
    }

    @RequestMapping("/update")
    public Result updateContacts(String contactsId, HttpServletRequest request){
        Map map = new HashMap();

        TblContacts contacts = tblContactsService.getContactsById(contactsId);
        map.put("contacts", contacts);

        List<TblUser> allUsers = tblActivityService.getAllUsers();
        map.put("user", allUsers);

        Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
        map.put("source", dicMap.get("source"));
        map.put("appellation",dicMap.get("appellation"));

        return Result.ok(map);
    }

    @RequestMapping("/updateSave")
    public Result updateSave(TblContacts tblContacts, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblContacts.setEditby(name);
        tblContactsService.updateSave(tblContacts);
        return Result.ok();
    }

    @RequestMapping("/del")
    public Result dalContacts(String[] ids){
        tblContactsService.delContacts(ids);
        return Result.ok();
    }

    @RequestMapping("/detail")
    public Result getContactById(String contactsId){
        TblContacts contacts = tblContactsService.getContactsById(contactsId);
        return Result.ok(contacts);
    }

    @RequestMapping("/remarks")
    public Result gatAllRemark(String contactsId){
        List<TblContactsRemark> allRemark = tblContactsService.getAllRemark(contactsId);
        return Result.ok(allRemark);
    }

    @RequestMapping(value = "/addRemark", method = RequestMethod.POST)
    public Result addRemark(TblContactsRemark tblContactsRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblContactsRemark.setCreateby(name);
        tblContactsService.addRemark(tblContactsRemark);
        return Result.ok();
    }

    @RequestMapping("/editRemark")
    public Result editRemark(String id){
        TblContactsRemark tblContactsRemark = tblContactsService.editRemark(id);
        return Result.ok(tblContactsRemark);
    }

    @RequestMapping(value = "/updateRemark", method = RequestMethod.POST)
    public Result updateRemark(TblContactsRemark tblContactsRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblContactsRemark.setEditby(name);
        tblContactsService.updateRemark(tblContactsRemark);
        return Result.ok();
    }

    @RequestMapping("/delRemark")
    public Result delRemark(String id){
        tblContactsService.delRemark(id);
        return Result.ok();
    }

    @RequestMapping("/tran")
    public Result getAllTran(String contactsId, HttpServletRequest request){
        Map pMap = (Map)request.getSession().getServletContext().getAttribute("pMap");
        List<TblTran> allTran = tblContactsService.getAllTran(contactsId);
        for (TblTran tblTran : allTran) {
            String possible = (String)pMap.get(tblTran.getStage());
            tblTran.setPossible(possible);
        }
        return Result.ok(allTran);
    }

    @RequestMapping("/delTran")
    public Result delTran(String id){
        String[] ids = {id};
        tblTranService.delTranById(ids);
        return Result.ok();
    }

    @RequestMapping("/activity")
    public Result getActivity(String contactsId){
        List<TblActivity> allActivity = tblContactsService.getAllActivity(contactsId);
        return Result.ok(allActivity);
    }

    @RequestMapping("/delActivity")
    public Result delActivity(String activityId){
        tblContactsService.delActivity(activityId);
        return Result.ok();
    }

    @RequestMapping("/unActivity")
    public Result unActivity(String contactsId){
        List<TblActivity> activityList = tblContactsService.unActivity(contactsId);
        return Result.ok(activityList);
    }

    @RequestMapping("/relation")
    public Result relation(String[] aids, String contactsId){
        tblContactsService.relation(aids, contactsId);
        return Result.ok();
    }

}
