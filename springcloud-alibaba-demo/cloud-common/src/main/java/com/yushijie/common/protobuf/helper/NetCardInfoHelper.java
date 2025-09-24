/*
 * Copyright(c) 2020-2021 KEPTDATA Software Technology Co., Limited.All rights reserved.
 *
 * KEPTDATA Software Technology Co., Limited claims this computer program as an unpublished work. Claim of copyright
 * does not imply waiver of other rights.
 *
 * NOTICE OF PROPRIETARY RIGHTS
 *
 * This program is a confidential trade secret and the property of KEPTDATA Software Technology Co., Limited.Use,
 * examination, reproduction, disassembly, decompiling, transfer and or disclosure to others of all or any part of this
 * software program are strictly prohibited except by express written agreement with KEPTDATA Software Technology Co.,
 * Limited.
 */

package com.yushijie.common.protobuf.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.keptdata.one2data.header.common.dto.IpAddressPurposesDTO;
import cn.keptdata.one2data.header.common.enumeration.IpPurposeEnum;
import cn.keptdata.one2data.header.common.enumeration.NicPurpose;
import cn.keptdata.one2data.header.message.protobuf.common.Node;
import cn.keptdata.one2data.header.recovery.dto.PEReportNodeInfoDTO;
import cn.keptdata.one2data.header.recovery.entity.RecoveryTargetDeviceNic;
import cn.keptdata.one2data.util.common.JSONUtil;
import cn.keptdata.one2data.util.net.IPUtils;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 网卡信息辅助类
 * @date 2021/7/14 13:53
 */
public class NetCardInfoHelper {

    /**
     * @param netCardList
     * @return void
     * @author Bob.Yang
     * @description 获取有效的网络设备[去除回环地址，192.168.122.1 等地址]
     * @date 2021-10-30 12:58
     */
    public static List<Node.NetCardInfo> getValidNetInterface(List<Node.NetCardInfo> netCardList) {

        List<Node.NetCardInfo> netCardInfoList = new ArrayList<>(8);

        Iterator<Node.NetCardInfo> iterator = netCardList.iterator();
        while (iterator.hasNext()) {
            Node.NetCardInfo netCardInfo = iterator.next();
            if (IPUtils.isMeaningfulIP(netCardInfo.getIp())) {
                netCardInfoList.add(netCardInfo);
            }
        }

        return netCardInfoList;
    }

    /**
     * @param netCardList
     * @return java.lang.String
     * @author Bob.Yang
     * @description 将节点上报的网卡转换为字符串
     * @date 2021/4/10 18:11
     */
    public static String getNodeIPAsJson(List<Node.NetCardInfo> netCardList) {
        List<Node.NetCardInfo> validNetInterface = getValidNetInterface(netCardList);
        return doGetIpAddress(validNetInterface);
    }

    public static String getNodeIPAsJsonFromPEReportNodeInfoDTO(List<PEReportNodeInfoDTO.NetCardInfo> netCardList) {
        List<PEReportNodeInfoDTO.NetCardInfo> netCardInfoList = new ArrayList<>(8);

        Iterator<PEReportNodeInfoDTO.NetCardInfo> iterator = netCardList.iterator();
        while (iterator.hasNext()) {
            PEReportNodeInfoDTO.NetCardInfo netCardInfo = iterator.next();
            if (IPUtils.isMeaningfulIP(netCardInfo.getIp())) {
                netCardInfoList.add(netCardInfo);
            }
        }

        IpAddressPurposesDTO ipAddressPurposesDTO = new IpAddressPurposesDTO();
        List<IpAddressPurposesDTO.IpPurpose> manageIpList = new ArrayList<>(8);
        List<IpAddressPurposesDTO.IpPurpose> unknownIpList = new ArrayList<>(8);

        for (int i = 0; i < netCardInfoList.size(); i++) {
            String ip = netCardList.get(i).getIp();

            if (i == 0) {
                IpAddressPurposesDTO.IpPurpose purpose = new IpAddressPurposesDTO.IpPurpose();
                purpose.setIp(ip);
                purpose.setPurpose(NicPurpose.MANAGE.getCode());
                manageIpList.add(purpose);
            } else {
                IpAddressPurposesDTO.IpPurpose purpose = new IpAddressPurposesDTO.IpPurpose();
                purpose.setIp(ip);
                purpose.setPurpose("");
                unknownIpList.add(purpose);
            }

        }
        // 管理网IP
        ipAddressPurposesDTO.appendIpPurpose(manageIpList);

        // 业务网IP
        ipAddressPurposesDTO.appendIpPurpose(unknownIpList);

        return JSONUtil.toJsonStr(ipAddressPurposesDTO);
    }

    public static String getNodeIPAsJsonFromRecoveryTargetDeviceNic(List<RecoveryTargetDeviceNic> netCardList) {
        List<RecoveryTargetDeviceNic> netCardInfoList = new ArrayList<>(8);

        Iterator<RecoveryTargetDeviceNic> iterator = netCardList.iterator();
        while (iterator.hasNext()) {
            RecoveryTargetDeviceNic netCardInfo = iterator.next();
            if (IPUtils.isMeaningfulIP(netCardInfo.getNicIp())) {
                netCardInfoList.add(netCardInfo);
            }
        }

        IpAddressPurposesDTO ipAddressPurposesDTO = new IpAddressPurposesDTO();
        List<IpAddressPurposesDTO.IpPurpose> manageIpList = new ArrayList<>(8);
        List<IpAddressPurposesDTO.IpPurpose> unknownIpList = new ArrayList<>(8);

        for (int i = 0; i < netCardInfoList.size(); i++) {
            String ip = netCardList.get(i).getNicIp();

            if (i == 0) {
                IpAddressPurposesDTO.IpPurpose purpose = new IpAddressPurposesDTO.IpPurpose();
                purpose.setIp(ip);
                purpose.setPurpose(NicPurpose.MANAGE.getCode());
                manageIpList.add(purpose);
            } else {
                IpAddressPurposesDTO.IpPurpose purpose = new IpAddressPurposesDTO.IpPurpose();
                purpose.setIp(ip);
                purpose.setPurpose("");
                unknownIpList.add(purpose);
            }

        }
        // 管理网IP
        ipAddressPurposesDTO.appendIpPurpose(manageIpList);

        // 业务网IP
        ipAddressPurposesDTO.appendIpPurpose(unknownIpList);

        return JSONUtil.toJsonStr(ipAddressPurposesDTO);
    }

    /**
     * @param netCardList
     * @return java.lang.String
     * @author yanhaoyuan
     * @description 返回ip字符串
     * @date 2023/1/4 16:58
     */
    private static String doGetIpAddress(List<Node.NetCardInfo> netCardList) {
        IpAddressPurposesDTO ipAddressPurposesDTO = new IpAddressPurposesDTO();
        List<IpAddressPurposesDTO.IpPurpose> manageIpList = new ArrayList<>(8);
        List<IpAddressPurposesDTO.IpPurpose> unknownIpList = new ArrayList<>(8);

        for (int i = 0; i < netCardList.size(); i++) {
            String ip = netCardList.get(i).getIp();

            if (i == 0) {
                IpAddressPurposesDTO.IpPurpose purpose = new IpAddressPurposesDTO.IpPurpose();
                purpose.setIp(ip);
                purpose.setPurpose(NicPurpose.MANAGE.getCode());
                manageIpList.add(purpose);
            } else {
                IpAddressPurposesDTO.IpPurpose purpose = new IpAddressPurposesDTO.IpPurpose();
                purpose.setIp(ip);
                purpose.setPurpose("");
                unknownIpList.add(purpose);
            }

        }
        // 管理网IP
        ipAddressPurposesDTO.appendIpPurpose(manageIpList);

        // 业务网IP
        ipAddressPurposesDTO.appendIpPurpose(unknownIpList);

        return JSONUtil.toJsonStr(ipAddressPurposesDTO);
    }

    public static String getNodeAsIpPurposesJson(List<Node.NetCardInfo> netCardList) {
        List<IpAddressPurposesDTO.IpPurpose> purposes = new ArrayList<>(8);
        for (int i = 0; i < netCardList.size(); i++) {
            String ip = netCardList.get(i).getIp();
            if (i == 0) {
                IpAddressPurposesDTO.IpPurpose manage =
                    IpAddressPurposesDTO.IpPurpose.builder().ip(ip).purpose(IpPurposeEnum.MANAGE.getCode()).build();
                purposes.add(manage);
            } else {
                IpAddressPurposesDTO.IpPurpose unknow =
                    IpAddressPurposesDTO.IpPurpose.builder().ip(ip).purpose(IpPurposeEnum.UNKONW.getCode()).build();
                purposes.add(unknow);
            }
        }
        IpAddressPurposesDTO ipAddressPurposesDTO = IpAddressPurposesDTO.builder().purposes(purposes).build();
        return JSONUtil.toJsonStr(ipAddressPurposesDTO);
    }

    public static String getNodeAsIpPurposesJsonForMainIp(List<Node.NetCardInfo> netCardList, String mainIp) {
        List<IpAddressPurposesDTO.IpPurpose> purposes = new ArrayList<>(8);
        for (int i = 0; i < netCardList.size(); i++) {
            String nicIp = netCardList.get(i).getIp();
            // 一个网卡可能有多个ip
            String[] splitIPs = nicIp.split(",");
            for (String ip : splitIPs) {
                // 替换掉所有空格
                ip = ip.replaceAll("\\s+", "");
                if (ip.equals(mainIp)) {
                    IpAddressPurposesDTO.IpPurpose manage =
                        IpAddressPurposesDTO.IpPurpose.builder().ip(ip).purpose(IpPurposeEnum.MANAGE.getCode()).build();
                    purposes.add(manage);
                } else {
                    IpAddressPurposesDTO.IpPurpose unknow =
                        IpAddressPurposesDTO.IpPurpose.builder().ip(ip).purpose(IpPurposeEnum.UNKONW.getCode()).build();
                    purposes.add(unknow);
                }
            }
        }
        IpAddressPurposesDTO ipAddressPurposesDTO = IpAddressPurposesDTO.builder().purposes(purposes).build();
        return JSONUtil.toJsonStr(ipAddressPurposesDTO);
    }

}
