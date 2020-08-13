package com.powernode.workbench.service;

import java.util.List;
import java.util.Map;

public interface EchartsService {

    Map<String, Object> getOwnerCount();

    List<List<Integer>> getActCost();

    Map actAvgCost();

    List<Map<String, String>> getClueSource();

    Map getClueState();

    Map getClueAddress();

    List<Map<String, String>> getContactsSource();

    Map contactsAddress();

    Map<String, Object> getTranMoney();

    Map getTranMoney2();

}
