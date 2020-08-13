package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.service.TblActivityService;
import com.powernode.workbench.service.TblRemarkService;
import com.sun.prism.shader.Mask_TextureSuper_AlphaTest_Loader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/remark")
public class TblRemarkController {

    @Autowired
    private TblRemarkService tblRemarkService;
    @Autowired
    private TblActivityService tblActivityService;
    @Value("${sessionUser}")
    private String SESSION_USER;

    //查询备注
    @RequestMapping("/remarks")
    public Result getAllRemark(String activityId){
        try {
            List<TblActivityRemark> remarkList = tblRemarkService.selectAllRemark(activityId);
            TblActivity tblActivity = tblActivityService.selectActById(activityId);
            Map map = new HashMap();
            map.put("remarkList",remarkList);
            map.put("tblActivity",tblActivity);
            return Result.ok(map);
        } catch (ResultException e) {
            return Result.build(1006,e);
        }

    }


    //增加备注
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addRemarkById(TblActivityRemark tblActivityRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblActivityRemark.setCreateby(name);
        try {
            tblRemarkService.addRemark(tblActivityRemark);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1005,e);
        }

    }

    //删除备注
    @RequestMapping("/del")
    public Result delRemarkById(String id){
        try {
            tblRemarkService.delRemark(id);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1200,e);
        }
    }

    //修改备注
    @RequestMapping("/edit")
    public Result editRemarkById(String id){
        try {
            TblActivityRemark tblActivityRemark = tblRemarkService.editRemark(id);
            return Result.ok(tblActivityRemark);
        } catch (ResultException e) {
            return Result.build(1404,e);
        }

    }

    //保存修改备注
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result editRemarkSave(TblActivityRemark tblActivityRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblActivityRemark.setEditby(name);
        try {
            tblRemarkService.editSaveRemark(tblActivityRemark);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1500,e);
        }


    }
}
