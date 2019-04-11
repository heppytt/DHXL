package com.dhxl.web.controller.dxinterface.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PostPositionVO {

    private String terminalPhoneNo = "18900000001";
    private Long lat = 0L;
    private Long lng = 0L;
    private float altitude = 0;
    private Integer speed = 0;
    private Integer direction = 0;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date gpsTime; //2019-03-24T07:12:14.857Z
    private Integer mileage = 0;
    private Long relativeMileage = 0L;
    private Integer oil = 0;
    private Integer wiFiSignalStrength = 1;
    private Integer gnssCount = 1;

    private AlarmFlag alarm = new AlarmFlag();
    private StatusFlag status = new StatusFlag();

}
