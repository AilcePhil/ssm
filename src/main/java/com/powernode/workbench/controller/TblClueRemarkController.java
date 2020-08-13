package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblClueRemark;
import com.powernode.workbench.service.TblClueRemarkService;
import com.powernode.workbench.service.TblRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/clueRemark")
public class TblClueRemarkController {

    @Value("${sessionUser}")
    private String SESSION_USER;
    @Autowired
    private TblClueRemarkService tblClueRemarkService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addClueRemarks(TblClueRemark tblClueRemark, HttpServletRequest request) {
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblClueRemark.setCreateby(name);
        try {
            tblClueRemarkService.addRemark(tblClueRemark);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1500, e);
        }
    }

    @RequestMapping("/remarks")
    public Result getAllClueRemark(String clueid){
        try {
            List<TblClueRemark> clueRemarks = tblClueRemarkService.selectAllReamark(clueid);
            return Result.ok(clueRemarks);
        } catch (ResultException e) {
            return Result.build(1404,e);
        }
    }

    @RequestMapping("/edit")
    public Result editClueRemark(String id){
        try {
            TblClueRemark tblClueRemark = tblClueRemarkService.selectRemarkById(id);
            return Result.ok(tblClueRemark);
        } catch (ResultException e) {
            return Result.build(1404,e);
        }
    }

    @RequestMapping("/update")
    public Result updateClueRemark(TblClueRemark tblClueRemark, HttpServletRequest request){
        String name = ((TblUser) request.getSession().getAttribute(SESSION_USER)).getName();
        tblClueRemark.setEditby(name);
        try {
            tblClueRemarkService.updateClueRemark(tblClueRemark);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1500,e);
        }
    }

    @RequestMapping("/del")
    public Result delClueRemark(String id){
        try {
            tblClueRemarkService.delRemarkById(id);
            return Result.ok();
        } catch (ResultException e) {
            return Result.build(1500,e);
        }
    }
}