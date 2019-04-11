package com.dhxl.web.controller.dxinterface.entity;

import lombok.Data;

@Data
public class StatusFlag {
    private Integer acc = 1;
    private Integer isLocation = 1;
    private Integer isSouthLatitude = 0;
    private Integer isWestLongitude = 0;
    private Integer isSuspended = 0;
    private Integer isPositionEncryption = 0;
    private Integer halfLoad = 0;
    private Integer fullLoad = 0;
    private Integer isOilWayDisconnect = 0;
    private Integer isEleWayDisconnect = 0;
    private Integer isDoorLocked = 0;
    private Integer frontDoorOpened = 0;
    private Integer middleDoorOpened = 0;
    private Integer rearDoorOpened = 0;
    private Integer driverDoorOpened = 0;
    private Integer customDoorOpened = 0;
    private Integer useGPSSatellite = 1;
    private Integer useBeiDouSatellite = 0;
    private Integer useGLONASSSatellite = 0;
    private Integer useGalileoSatellite = 0;

}
