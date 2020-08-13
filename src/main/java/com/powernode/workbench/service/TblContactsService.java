package com.powernode.workbench.service;

import com.powernode.utils.PageResult;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblContactsRemark;
import com.powernode.workbench.pojo.TblTran;

import java.util.List;

public interface TblContactsService {

    PageResult getAllContacts(int pageNum, int pageSize, String owner, String fullname, String customerid, String source, String birth);

    void addContacts(TblContacts tblContacts);

    TblContacts getContactsById(String contactsId);

    void updateSave(TblContacts tblContacts);

    void delContacts(String[] ids);

    List<TblContactsRemark> getAllRemark(String contactsId);

    void addRemark(TblContactsRemark tblContactsRemark);

    TblContactsRemark editRemark(String id);

    void updateRemark(TblContactsRemark tblContactsRemark);

    void delRemark(String id);

    List<TblTran> getAllTran(String contactsId);

    List<TblActivity> getAllActivity(String contactsId);

    void delActivity(String activityId);

    List<TblActivity> unActivity(String activityId);

    void relation(String[] aids, String contactsId);
}
