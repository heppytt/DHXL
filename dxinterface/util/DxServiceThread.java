package com.dhxl.web.controller.dxinterface.util;

import com.dhxl.system.domain.YzTrail;

import java.util.List;

public class DxServiceThread implements Runnable {

    private List<YzTrail> lList;

    public DxServiceThread(List<YzTrail> lList) {
        this.lList = lList;
    }

    @Override
    public void run() {
        YzDataUtils.sendDataToDx(lList);
    }
}
