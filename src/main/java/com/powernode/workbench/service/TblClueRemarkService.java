package com.powernode.workbench.service;

import com.powernode.workbench.pojo.TblClueRemark;

import java.util.List;

public interface TblClueRemarkService {
    void addRemark(TblClueRemark tblClueRemark);

    List<TblClueRemark> selectAllReamark(String clueid);

    TblClueRemark selectRemarkById(String id);

    void updateClueRemark(TblClueRemark tblClueRemark);

    void delRemarkById(String id);
}
