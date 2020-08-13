package com.powernode.utils;

import com.powernode.settings.pojo.TblDicValue;

import java.util.Comparator;

public class DicComparator implements Comparator<TblDicValue> {
    @Override
    public int compare(TblDicValue o1, TblDicValue o2) {
        int dic1 = Integer.parseInt(o1.getOrderno());
        int dic2 = Integer.parseInt(o2.getOrderno());

        return dic1 - dic2;
    }
}
