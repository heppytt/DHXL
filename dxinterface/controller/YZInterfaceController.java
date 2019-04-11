package com.dhxl.web.controller.dxinterface.controller;

import com.alibaba.fastjson.JSON;
import com.dhxl.common.core.domain.AjaxResult;
import com.dhxl.common.utils.StringUtils;
import com.dhxl.system.domain.YzTrail;
import com.dhxl.system.service.IDeviceUserService;
import com.dhxl.system.service.IYzTrailService;
import com.dhxl.web.controller.dxinterface.entity.YzDataModel;
import com.dhxl.web.controller.dxinterface.util.DxServiceThread;
import com.dhxl.web.controller.dxinterface.util.YzDataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/yzData")
@Api("YZ对接接口服务")
@Slf4j
public class YZInterfaceController {

    @Autowired
    private IYzTrailService mIYzTrailService;
    @Autowired
    private IDeviceUserService deviceUserService;

    @ResponseBody
    @RequestMapping(value = "/postPosition", method = RequestMethod.POST)
    @ApiOperation("获取云智提供的位置数据")
    public AjaxResult postPosition(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();    //获取开始时间
        AjaxResult mAjaxResult = null;
        try {
            String s = IOUtils.toString(request.getInputStream(), "utf-8");
            if (StringUtils.isNotEmpty(s)) {
                String sjon = JSON.toJSON(s).toString();
                YzDataModel mYzDataModel = JSON.parseObject(sjon, YzDataModel.class);
                Map<String, Object> map = YzDataUtils.dataHander(mYzDataModel);
                long endTime = System.currentTimeMillis();    //获取结束时间
                log.info("程序预处理数据运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
                List<YzTrail> lList = (List<YzTrail>) map.get("data");
                String did = (String) map.get("did");
                if (null != lList && !lList.isEmpty()) {
                    mIYzTrailService.batchInsertData(lList);
                    long endTime2 = System.currentTimeMillis();    //获取结束时间
                    log.info("程序保存地理数据运行时间：" + (endTime2 - endTime) + "ms");    //输出程序运行时间
                    //返回数据
                    mAjaxResult = AjaxResult.success("写入成功");
                    Map<String, String> deceiveMap = deviceUserService.getAllDeceiveMap();
                    if (deceiveMap.containsKey(did)) {
                        Thread mThread = new Thread(new DxServiceThread(lList));
                        mThread.start();//启动线程
//                        String returnMessage = YzDataUtils.sendDataToDx(lList, deceiveMap);
//                        long endTime3 = System.currentTimeMillis();    //获取结束时间
//                        log.info("程序访问电信接口运行时间：" + (endTime3 - endTime2) + "ms");
//                        mAjaxResult = AjaxResult.success(returnMessage);
                    }
                } else {
                    mAjaxResult = AjaxResult.success("解析数据失败");
                    log.info("解析数据失败");
                }
            } else {
                mAjaxResult = AjaxResult.success("获取数据为空");
            }
        } catch (Exception e) {
            String message = e.getMessage();
            log.info(message);
            AjaxResult.success(message);
        }
        return mAjaxResult;
    }

    public static void main(String[] args) {
        YzDataModel mYzDataModel = new YzDataModel();
        mYzDataModel.setTime(1542697143064L);
        mYzDataModel.setType("TRACE-02");
        mYzDataModel.setDid("101010020111");
        String[] date = {"113931338,22541998,40,1542696106,-1.0,0.0,0.0,-1,1542697105,0", "-11,24,40,1009,-1.0,-1.0,0.0,-1,10,1"};
//        mYzDataModel.setData(date);
        String sjon = JSON.toJSON(mYzDataModel).toString();
        System.out.println(sjon);
        YzDataModel m2YzDataModel = JSON.parseObject(sjon, YzDataModel.class);
        System.out.println("m2YzDataModel");
    }

}
