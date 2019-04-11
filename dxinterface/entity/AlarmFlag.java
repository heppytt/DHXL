package com.dhxl.web.controller.dxinterface.entity;

import lombok.Data;

@Data
public class AlarmFlag {

    private Integer emergency = 0;
    private Integer overSpeed = 0;
    private Integer fatigueDriving = 0;
    private Integer dangerWarning = 0;
    private Integer gnssModuleFailure = 0;
    private Integer gnssAntennaUnconnected = 0;
    private Integer gnssAntennaShortCircuit = 0;
    private Integer mainPowerUndervoltage = 0;
    private Integer mainPowerDisconnect = 0;
    private Integer lcdFailure = 0;
    private Integer ttsFailure = 0;
    private Integer cameraFailure = 0;
    private Integer icCardModelFailure = 0;
    private Integer speedingWarning = 0;
    private Integer fatigueWarning = 0;
    private Integer overtimeDayDriving = 0;
    private Integer overtimeParking = 0;
    private Integer inOutArea = 0;
    private Integer inOutLine = 0;
    private Integer overtimeInLine = 0;
    private Integer routeDeviation = 0;
    private Integer vssFailure = 0;
    private Integer oilAbnormality = 0;
    private Integer theft = 0;
    private Integer illegalIgnition = 0;
    private Integer illegalMoving = 0;
    private Integer collisionWarning = 0;
    private Integer rolloverWarning = 0;
    private Integer illegalDoorOpen = 0;

}
