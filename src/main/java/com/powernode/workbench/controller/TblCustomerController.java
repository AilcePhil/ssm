package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblCustomer;
import com.powernode.workbench.pojo.TblCustomerRemark;
import com.powernode.workbench.pojo.TblTran;
import com.powernode.workbench.service.TblActivityService;
import com.powernode.workbench.service.TblCustomerService;
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

@RestController()
@RequestMapping("/customer")
public class TblCustomerController {

    @Autowired
    private TblCustomerService tblCustomerService;
    @Autowired
    private TblActivityService tblActivityService;
    @Autowired
    private TblTranService tblTranService;


    @Value("${sessionUser}")
    private String SESSION_USER;

    @RequestMapping("/customers")
    public Result pageCustomers(@RequestParam(value = "pageNum", required = true) int pageNum,
                                @RequestParam(value = "pageSize", required = true) int pageSize,
                                @RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "owner", required = false) String owner,
                                @RequestParam(value = "phone", required = false) String phone,
                                @RequestParam(value = "website", required = false) String website
                                ){
        PageResult pageResult = tblCustomerService.selectAllCust(pageNum, pageSize, name, owner, phone, website);
        return Result.ok(pageResult);
    }

    @RequestMapping("/options")
    public Result getOptions(){
        List<TblUser> users = tblActivityService.getAllUsers();
        return Result.ok(users);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result createCustomer(TblCustomer tblCustomer, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblCustomer.setCreateby(name);
        tblCustomerService.addCustomer(tblCustomer);
        return Result.ok();
    }

    @RequestMapping("/update")
    public Result getUpdate(String customerId){
        List<TblUser> allUsers = tblActivityService.getAllUsers();
        TblCustomer tblCustomer = tblCustomerService.updateCustomer(customerId);

        Map<String, Object> map = new HashMap<>();
        map.put("user",allUsers);
        map.put("customer", tblCustomer);
        return Result.ok(map);

    }

    @RequestMapping("/updateSave")
    public Result updateSave(TblCustomer tblCustomer, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblCustomer.setEditby(name);
        tblCustomerService.updateSave(tblCustomer);
        return Result.ok();
    }

    @RequestMapping("/del")
    public Result delCustomer(String[] ids){
        tblCustomerService.deleteCustomer(ids);
        return Result.ok();
    }

    @RequestMapping("/detail")
    public Result getAllDetail(String customerId){
        TblCustomer tblCustomer = tblCustomerService.selectById(customerId);
        return Result.ok(tblCustomer);
    }

    @RequestMapping("/remarks")
    public Result getAllRemark(String customerId){
        List<TblCustomerRemark> allRemark = tblCustomerService.getAllRemark(customerId);
        return Result.ok(allRemark);
    }

    @RequestMapping(value = "/addRemark", method = RequestMethod.POST)
    public Result addRemark(TblCustomerRemark tblCustomerRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblCustomerRemark.setCreateby(name);
        tblCustomerService.addRemark(tblCustomerRemark);
        return Result.ok();
    }

    @RequestMapping("/editRemark")
    public Result editRemark(String id){
        TblCustomerRemark tblCustomerRemark = tblCustomerService.editRemark(id);
        return Result.ok(tblCustomerRemark);
    }

    @RequestMapping("/delRemark")
    public Result delRemark(String id){
        tblCustomerService.delRemark(id);
        return Result.ok();
    }

    @RequestMapping(value = "/updateRemark", method = RequestMethod.POST)
    public Result updateRemark(TblCustomerRemark tblCustomerRemark,HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblCustomerRemark.setEditby(name);
        tblCustomerService.updateRemark(tblCustomerRemark);
        return Result.ok();
    }

    @RequestMapping("/tran")
    public Result getAllTran(String customerId, HttpServletRequest request){
        Map pMap = (Map)request.getSession().getServletContext().getAttribute("pMap");
        List<TblTran> allTran = tblCustomerService.getAllTran(customerId);
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

    @RequestMapping("/contacts")
    public Result getAllContacts(String customerId){
        List<TblContacts> allContacts = tblCustomerService.getAllContacts(customerId);
        return Result.ok(allContacts);
    }

    @RequestMapping("/delContacts")
    public Result delContacts(String id){

        return null;
    }

}
