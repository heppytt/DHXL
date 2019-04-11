package com.dhxl.web.controller.dxinterface.util;

import com.alibaba.fastjson.JSONObject;
import com.dhxl.common.utils.bean.BeanUtils;
import com.dhxl.system.domain.YzTrail;
import com.dhxl.web.controller.dxinterface.entity.PostPositionVO;
import com.dhxl.web.controller.dxinterface.entity.YzDataModel;
import com.dhxl.web.socket.util.BatchNumUtil;
import com.dhxl.web.socket.util.LocationUtils;
import com.dhxl.web.socket.vo.DataTypeConstants;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class YzDataUtils {


    public static Map<String, Object> dataHander(YzDataModel msYzDataModel) {
        //存放数据
        Map<String, Object> map = new HashMap<>();
        List<YzTrail> lYzTrail = new ArrayList<>();
        if (null != msYzDataModel) {
            String type = msYzDataModel.getType();
            String did = msYzDataModel.getDid();
            map.put("did", did);
            Long ltime = msYzDataModel.getTime();
            String[] liststr = msYzDataModel.getData();
            List<String> list = new ArrayList<>();
            /**
             * 放入list里面进行流式编程
             */
            for (int i = 0; i < liststr.length; i++) {
                list.add(liststr[i]);
            }
            String batchNum = BatchNumUtil.RandomBatchNum();
            YzTrail msYzTrail = new YzTrail();
            msYzTrail.setBatchNum(batchNum);
            msYzTrail.setType(type);
            if (null != ltime) {
                msYzTrail.setTime(LongToDate(ltime));
            } else {
                msYzTrail.setTime(new Date());
            }
            msYzTrail.setDid(did);
            AtomicReference<Long> init_lng = new AtomicReference<>(0L);
            AtomicReference<Long> init_lat = new AtomicReference<>(0L);
            AtomicReference<Long> init_time = new AtomicReference<>(0L);
            AtomicReference<Long> init_systime = new AtomicReference<>(0L);
            AtomicBoolean flag = new AtomicBoolean(true);
            AtomicBoolean time_flag = new AtomicBoolean(true);
            if (null != list && !list.isEmpty()) {
                list.stream().map(data -> {
                    String[] dataArr = data.split(",");
                    YzTrail mYzTrail = new YzTrail();
                    try {
                        BeanUtils.copyProperties(mYzTrail, msYzTrail);
                        if (DataTypeConstants.DATA_TRACE_01.equals(type)) {
                            String lng = dataArr[0];
                            String lat = dataArr[1];
                            //处理精度和维度
                            handleLngAndLat(lng, lat, flag, init_lng, init_lat, mYzTrail);
                        } else if (DataTypeConstants.DATA_TRACE_02.equals(type)) {
                            String lng = dataArr[0];
                            String lat = dataArr[1];
                            String radius = dataArr[2];
                            String time = dataArr[3];
                            String altitude = dataArr[4];
                            String northPt = dataArr[5];
                            String speed = dataArr[6];
                            String sateliteNum = dataArr[7];
                            String systime = dataArr[8];
                            String accFlag = dataArr[9];
                            handleLngAndLat(lng, lat, flag, init_lng, init_lat, mYzTrail);
                            //处理时间数据
                            handleTime(time, systime, time_flag, init_time, init_systime, mYzTrail);
                            mYzTrail.setRadius(Float.parseFloat(radius));
                            mYzTrail.setAltitude(Float.parseFloat(altitude));
                            mYzTrail.setNorthPt(Float.parseFloat(northPt));
                            mYzTrail.setSpeed(Float.parseFloat(speed));
                            mYzTrail.setSatelliteNum(Float.parseFloat(sateliteNum));
                            mYzTrail.setAccFlag(Integer.parseInt(accFlag));
                        }
                        lYzTrail.add(mYzTrail);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return lYzTrail;
                }).collect(Collectors.toList());
            }
        }
        map.put("data", lYzTrail);
        return map;
    }

    public static String sendDataToDx(List<YzTrail> lList) {

        try {
            Map<String, String> map = new HashMap<>();
            map.put("username", "18907181584");
            map.put("password", "1111");
            map.put("expiresSliding", "30");
            map.put("expiresAbsoulute", "30");
            String url = "http://58.53.196.217:11080/api/OAuth/GetToken?username={username}&password={password}&expiresSliding={expiresSliding}&expiresAbsoulute={expiresAbsoulute}";
            String token = RestFulClientUtil.sendGetRest(url, map);
            String postPositionUrl = "http://58.53.196.217:11080/api/v1/TerminalService/PostPosition";
            DozerBeanMapper mapper = new DozerBeanMapper();
            lList.stream().map(mYzTrail -> {
                PostPositionVO mPostPositionVO = mapper.map(mYzTrail, PostPositionVO.class);
//                Date gpsTime = mPostPositionVO.getGpsTime();
                Float altitude = mPostPositionVO.getAltitude();// 电信数据 高程不能为负
//                if (null == gpsTime) {
//                    //百度经度纬度 首点时间 为gps时间
//                    mPostPositionVO.setGpsTime(mYzTrail.getTime());
//                }
                if (null == altitude || altitude < 0L) {
                    mPostPositionVO.setAltitude(0L);
                }
                String str = JSONObject.toJSONStringWithDateFormat(mPostPositionVO, "yyyy-MM-dd'T'HH:mm:ss");
                RestFulClientUtil.sendPostRest3(postPositionUrl, str);
//                log.info("DX返回结果------>"+returnMessage);
                return mPostPositionVO;
            }).collect(Collectors.toList());
//            for (PostPositionVO mPostPositionVO : lPostPositionVO) {
//                String str = JSONObject.toJSONStringWithDateFormat(mPostPositionVO, "yyyy-MM-dd'T'HH:mm:ss");
//                RestFulClientUtil.sendPostRest3(postPositionUrl, str);
//            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "同步数据成功";
    }

    /**
     * long换成时间
     *
     * @param time
     * @return
     */
    public static Date LongToDate(Long time) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = date.atZone(zone).toInstant();
        Date date2 = Date.from(instant);
        return date2;
    }

    /**
     * 处理精度和维度
     *
     * @param lng
     * @param lat
     * @param flag
     * @param init_lng
     * @param init_lat
     * @param mYzTrail
     */
    public static void handleLngAndLat(String lng, String lat, AtomicBoolean flag, AtomicReference<Long> init_lng, AtomicReference<Long> init_lat, YzTrail mYzTrail) {
        Long flng = Long.parseLong(lng);
        Long flat = Long.parseLong(lat);
        Long flng_befour = 0L;
        Long flat_befour = 0L;
        if (flag.get()) {
            flag.set(false);
            mYzTrail.setRelativeMileage(0L);
        } else {
            flng_befour = init_lng.get();
            flat_befour = init_lat.get();
            flng = flng_befour + flng;
            flat = flat_befour + flat;
            //算两点间距离
            Long rm = LocationUtils.getDistance(flat_befour, flng_befour, flat, flng);
            mYzTrail.setRelativeMileage(rm);
        }
        init_lng.set(flng);
        init_lat.set(flat);
        mYzTrail.setLng(flng);
        mYzTrail.setLat(flat);

    }

    /**
     * 处理时间数据
     *
     * @param time
     * @param systime
     * @param time_flag
     * @param init_time
     * @param init_systime
     * @param mYzTrail
     */
    public static void handleTime(String time, String systime, AtomicBoolean time_flag, AtomicReference<Long> init_time, AtomicReference<Long> init_systime, YzTrail mYzTrail) {
        Long timel = Long.parseLong(time);
        Long systimel = Long.parseLong(systime);
        Long time_befour = 0L;
        Long systime_befour = 0L;
        if (time_flag.get()) {
            time_flag.set(false);
        } else {
            time_befour = init_time.get();
            systime_befour = init_systime.get();
            timel = time_befour + timel;
            systimel = systime_befour + systimel;
        }
        init_time.set(timel);
        init_systime.set(systimel);
        if (timel != null) {
            mYzTrail.setDataTime(LongToDate(timel*1000));
        }
        if (systimel != null) {
            mYzTrail.setSystime(LongToDate(systimel*1000));
        }
    }

}
