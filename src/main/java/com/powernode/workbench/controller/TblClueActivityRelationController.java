package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.utils.PageResult;
import com.powernode.utils.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClue;
import com.powernode.workbench.service.TblActivityService;
import com.powernode.workbench.service.TblClueActivityRelationService;
import com.powernode.workbench.service.TblClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/caRelation")
public class TblClueActivityRelationController {

    @Autowired
    private TblClueActivityRelationService relationService;
    @Autowired
    private TblClueService tblClueService;


    @RequestMapping("/find")
    public Result findAllActivity(@RequestParam(value = "activityname", required = false) String activityname,
                                  @RequestParam(value = "clueid", required = true) String clueid
                                  ){
        try {
            List<TblActivity> tblActivities = relationService.selectByName(activityname, clueid);
            return Result.ok(tblActivities);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }

    @RequestMapping("/findPage")
    public Result pageList(@RequestParam(value = "activityname", required = false) String activityname,
                           @RequestParam(value = "clueid", required = true) String clueid,
                           @RequestParam(value = "pageNo", required = true) int pageNo,
                           @RequestParam(value = "pageSize", required = true) int pageSize
                           ){
        try {
            PageResult pageResult = relationService.selectByName(activityname, clueid, pageNo, pageSize);
            return Result.ok(pageResult);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }

   @RequestMapping("/relation")
   public Result getCARelation(String clueid){
       try {
           List<Map<String, Object>> relaActivity = relationService.getRelaActivity(clueid);
           return Result.ok(relaActivity);
       } catch (ResultException e) {
           return Result.build(1500,e);
       }
   }

   @RequestMapping("/pageRelation")
   public Result getPageCARelation(@RequestParam(value = "clueId", required = true) String clueId,
                                   @RequestParam(value = "activityName", required = false) String activityName,
                                   @RequestParam(value = "pageNo", required = true) int pageNo,
                                   @RequestParam(value = "pageSize", required = true) int pageSize
                                   ){
       PageResult pageResult = relationService.selectById(clueId, activityName, pageNo, pageSize);
       return Result.ok(pageResult);
   }

   @RequestMapping("/del")
   public Result delCARelation(String id){
       try {
           relationService.delRelation(id);
           return Result.ok();
       } catch (ResultException e) {
           return Result.build(1500,e);
       }
   }

   @RequestMapping("/add")
   public Result addCARelation(String[] aids, String clueid){
        try {
           relationService.addRelation(aids, clueid);
           return Result.ok();
       } catch (ResultException e) {
           return Result.build(1500,e);
       }
   }

   @RequestMapping("/clue")
   public Result getClueById(String clueid){
       TblClue tblClue = tblClueService.selectClueById(clueid);
       return Result.ok(tblClue);
   }

   @RequestMapping("/stage")
   public Result getStage(HttpServletRequest request){
       Map dicMap = (Map)request.getSession().getServletContext().getAttribute("dicMap");
       Object stage = dicMap.get("stage");
       return Result.ok(stage);
   }
}
