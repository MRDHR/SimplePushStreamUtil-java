package com.dhr.simplepushstreamutil.bean;

import java.util.List;

public class LocalDataBean {
    private ConfigLinuxPushBean configLinuxPushBean;
    private ConfigSchemeBean configSchemeBean;
    private LoginInfoBean loginInfoBean;
    private List<SourceUrlInfoBean> sourceUrlInfoBeans;
    private List<ServerInfoBean> serverInfoBeans;
    private List<LiveRoomUrlInfoBean> liveRoomUrlInfoBeans;

    public ConfigLinuxPushBean getConfigLinuxPushBean() {
        return configLinuxPushBean;
    }

    public void setConfigLinuxPushBean(ConfigLinuxPushBean configLinuxPushBean) {
        this.configLinuxPushBean = configLinuxPushBean;
    }

    public ConfigSchemeBean getConfigSchemeBean() {
        return configSchemeBean;
    }

    public void setConfigSchemeBean(ConfigSchemeBean configSchemeBean) {
        this.configSchemeBean = configSchemeBean;
    }

    public LoginInfoBean getLoginInfoBean() {
        return loginInfoBean;
    }

    public void setLoginInfoBean(LoginInfoBean loginInfoBean) {
        this.loginInfoBean = loginInfoBean;
    }

    public List<SourceUrlInfoBean> getSourceUrlInfoBeans() {
        return sourceUrlInfoBeans;
    }

    public void setSourceUrlInfoBeans(List<SourceUrlInfoBean> sourceUrlInfoBeans) {
        this.sourceUrlInfoBeans = sourceUrlInfoBeans;
    }

    public List<ServerInfoBean> getServerInfoBeans() {
        return serverInfoBeans;
    }

    public void setServerInfoBeans(List<ServerInfoBean> serverInfoBeans) {
        this.serverInfoBeans = serverInfoBeans;
    }

    public List<LiveRoomUrlInfoBean> getLiveRoomUrlInfoBeans() {
        return liveRoomUrlInfoBeans;
    }

    public void setLiveRoomUrlInfoBeans(List<LiveRoomUrlInfoBean> liveRoomUrlInfoBeans) {
        this.liveRoomUrlInfoBeans = liveRoomUrlInfoBeans;
    }

}
