package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.service.TblActivityService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity")
public class TblActivityController {

    @Autowired
    private TblActivityService tblActivityService;
    @Value("${sessionUser}")
    private String SESSION_USER;

    //获得用户
    @RequestMapping("/users")
    public Result getAllUsers(){
        try {
            List<TblUser> allUsers = tblActivityService.getAllUsers();
            return Result.ok(allUsers);
        } catch (RuntimeException e) {
            return Result.build(false,1004,e.getMessage());
        }

    }

    //增加市场活动
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addActivity(@RequestBody TblActivity tblActivity, HttpServletRequest request){
        //获得当前创建者的名字
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblActivity.setCreateby(name);
        try {
            tblActivityService.addActivity(tblActivity);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1010,e);
        }
    }

    //分页搜索
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public Result pageFind(@RequestParam(value = "pageSize", required = true) int pageSize,
                           @RequestParam(value = "pageNo", required = true) int pageNo,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "owner", required = false) String owner,
                           @RequestParam(value = "startdate", required = false) String startdate,
                           @RequestParam(value = "enddate", required = false) String enddate){

        try {
            PageResult pageResult = tblActivityService.pageFindActivity(pageSize, pageNo, name, owner, startdate, enddate);
            return Result.ok(pageResult);
        } catch (ResultException e) {
            return Result.build(404,e.getMessage());
        }


    }

    //批量删除
    @RequestMapping("/del")
    public Result delByIds(String[] ids){
        System.out.println(ids);
        try {
            tblActivityService.delActivity(ids);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1004,e);
        }
    }

    //根据id获取编辑信息
    @RequestMapping("/id")
    public Result selectById(String id){
        try {
            List<TblUser> allUsers  = tblActivityService.getAllUsers();
            TblActivity tblActivity = tblActivityService.selectActById(id);
            Map map = new HashMap();
            map.put("allUser",allUsers);
            map.put("tblActivity",tblActivity);
            return Result.ok(map);
        } catch (ResultException e) {
            return Result.build(e);
        }
    }

    //保存修改信息
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateSave(TblActivity tblActivity, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblActivity.setEditby(name);
        try {
            tblActivityService.updateActivity(tblActivity);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(e);
        }
    }

    //查询detail详细信息
    @RequestMapping("/detail")
    public Result selectDetailById(String id){
        try {
            TblActivity tblActivity = tblActivityService.detailById(id);
            return Result.ok(tblActivity);
        } catch (ResultException e) {
            return Result.build(e);
        }
    }


}
