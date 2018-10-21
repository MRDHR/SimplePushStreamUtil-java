package com.dhr.simplepushstreamutil.ui.form;

import com.dhr.simplepushstreamutil.bean.*;
import com.dhr.simplepushstreamutil.runnable.*;
import com.dhr.simplepushstreamutil.ui.dialog.*;
import com.dhr.simplepushstreamutil.util.JschUtil;
import com.dhr.simplepushstreamutil.util.JsonUtil;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliAPI;
import com.hiczp.bilibili.api.BilibiliAccount;
import com.hiczp.bilibili.api.web.BilibiliWebAPI;
import com.hiczp.bilibili.api.web.live.LiveService;
import com.hiczp.bilibili.api.web.live.entity.*;
import okhttp3.Cookie;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainForm extends JFrame {
    private ConfigProxyDialog configProxyDialog;//配置代理的对话框
    private boolean getFormatListProxy;
    private boolean openLiveRoomProxy;
    private String proxyIp;
    private int proxyPort;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BiliBiliAccountDialog bilibiliAccountDialog;
    private ConfigServerPushConfirmDialog configServerPushConfirmDialog;
    private ConfigSchemeDialog configSchemeDialog;
    private AreaSettingDialog areaSettingDialog;

    private SourceUrlInfoDialog resourceUrlInfoDialog;
    private ServerInfoDialog serverInfoDialog;
    private LiveRoomUrlInfoDialog liveRoomUrlInfoDialog;

    private StartLiveEntity.DataBean.RtmpBean rtmp;

    private String resourceUrl;
    private String m3u8Url;
    private String liveRoomUrl;

    private String[] command = {"cmd"};
    private String userDirPath = new File(System.getProperty("user.dir")).getPath();
    private File file1 = new File(userDirPath + "\\ffmpeg\\bin\\");
    private File file2 = new File(userDirPath + "\\python\\");
    private File file3 = new File(userDirPath + "\\streamlink\\");
    private String windowsEnvPath = file1.getPath() + "\\;" + file2.getPath() + "\\;" + file3.getPath() + "\\;";
    private Process process = null;
    private PrintWriter stdin;

    private JsonUtil jsonUtil = new JsonUtil();
    private Gson gson = new Gson();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private JschUtil jschUtil = new JschUtil();
    private LocalDataBean localDataBean;

    private String serverIp;
    private int serverPort;
    private String userName;
    private String userPassword;

    private int controlPanelHeight;

    private boolean isLocalFile = false;
    private boolean needBackGround = false;

    private List<ResolutionBean> listResolutions = new ArrayList<>();

    private BilibiliAPI bilibiliAPI;
    private BilibiliWebAPI bilibiliWebAPI;
    private Map<String, List<Cookie>> cookiesMap;
    private LiveService liveService;
    private LiveService liveServiceWithProxy;
    private String roomId;
    private String csrfToken = "";

    public static void main(String[] args) {
        try {
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);
        } catch (Exception e) {
        }
        JFrame frame = new JFrame("简易推流工具");
        frame.setResizable(false);
        frame.setSize(800, 600);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        MainForm mainForm = new MainForm();

        JMenuBar mb = new JMenuBar();                 //实例菜单栏
        JMenu config = new JMenu("配置");                //实例一个菜单项
        JMenu more = new JMenu("更多");
        JMenuItem mi1 = new JMenuItem("全局代理");         //实例子目录
        JMenuItem mi2 = new JMenuItem("配置B站账号");
        JMenuItem mi3 = new JMenuItem("配置服务器推流方式");
        JMenuItem mi4 = new JMenuItem("配置解析方案");

        JMenuItem mi5 = new JMenuItem("帮助");
        JMenuItem mi6 = new JMenuItem("关于");

        frame.setJMenuBar(mb);                        //设置菜单栏
        mb.add(config);                               //添加菜单项
        mb.add(more);                               //添加菜单项
        config.add(mi1);                             //加入子菜单
        config.add(mi2);                             //加入子菜单
        config.add(mi3);                             //加入子菜单
        config.add(mi4);                             //加入子菜单

        more.add(mi5);                              //添加子目录
        more.add(mi6);

        mi1.addActionListener(e -> mainForm.showConfigProxyDialog());

        mi2.addActionListener(e -> mainForm.showBilibiliAccountDialog());

        mi3.addActionListener(e -> mainForm.showConfigServerPushConfirmDialog());
        mi4.addActionListener(e -> mainForm.showConfigSchemeDialog());
        mi6.addActionListener(e -> mainForm.showTipsDialog("随手写的程序，没啥好说的"));

        frame.setContentPane(mainForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private MainForm() {
        initView();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        rbBoth.setSelected(true);
        rbOnlyAudio.setEnabled(false);
        rbOnlyImage.setEnabled(false);
        btnCloseLiveRoom.setEnabled(false);
        btnToMyLiveRoom.setEnabled(false);

        rbLocal.addItemListener(itemListener);
        rbServer.addItemListener(itemListener);
        rbLocal.setSelected(true);
        serverInfoPanel.setVisible(false);
        rbInputLiveRoomUrl.setSelected(true);
        rbInputLiveRoomUrl.addItemListener(itemListener);
        rbGetLiveRoomUrl.addItemListener(itemListener);
        contentScrollPanel.getVerticalScrollBar().setUnitIncrement(20);

        btnTestConnect.addActionListener(e -> testServerConnect());

        btnSaveServerInfo.addActionListener(e -> showSaveServerInfoDialog());

        btnSaveSourceUrlInfo.addActionListener(e -> showSaveSourceUrlInfoDialog());

        btnSaveLiveRoomUrlInfo.addActionListener(e -> showSaveLiveRoomUrlInfo());

        btnGetResourceInfo.addActionListener(e -> showSourceUrlInfoDialog());

        btnLoadServerInfo.addActionListener(e -> showServerInfoDialog());

        btnGetLiveRoomInfo.addActionListener(e -> showLiveRoomUrlInfoDialog());

        btnGetFormatList.addActionListener(e -> getFormatList());

        btnPushStream.addActionListener(e -> pushStreamPerformed());

        btnStopStream.addActionListener(e -> stopStream());

        btnOpenLiveRoom.addActionListener(e -> openLiveRoom());

        btnCloseLiveRoom.addActionListener(e -> closeLiveRoom());

        btnToMyLiveRoom.addActionListener(e -> toMyLIveRoom());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        String localData = jsonUtil.getDatafromFile(LocalDataBean.class.getSimpleName());
        if (null == localData || localData.isEmpty()) {
            localDataBean = new LocalDataBean();
            jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
        } else {
            localDataBean = gson.fromJson(localData, LocalDataBean.class);
        }
        if (null == localDataBean.getConfigSchemeBean()) {
            ConfigSchemeBean configSchemeBean = new ConfigSchemeBean();
            configSchemeBean.setSchemeType(0);
            localDataBean.setConfigSchemeBean(configSchemeBean);
            jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
        }
        if (null == localDataBean.getConfigLinuxPushBean()) {
            ConfigLinuxPushBean configLinuxPushBean = new ConfigLinuxPushBean();
            configLinuxPushBean.setStatus(0);
            localDataBean.setConfigLinuxPushBean(configLinuxPushBean);
            jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
        }
        showOrHideLiveRoomControlPanel();
    }

    /**
     * 测试服务器连接
     */
    private void testServerConnect() {
        serverIp = tfServerIp.getText();
        String cacheServerPort = tfServerPort.getText();
        userName = tfUserName.getText();
        userPassword = tfUserPassword.getText();
        if (null == serverIp || serverIp.isEmpty()) {
            showTipsDialog("服务器ip不能为空");
        } else if (null == cacheServerPort || cacheServerPort.isEmpty()) {
            showTipsDialog("端口号不能为空");
        } else if (null == userName || userName.isEmpty()) {
            showTipsDialog("用户名不能为空");
        } else if (null == userPassword || userPassword.isEmpty()) {
            showTipsDialog("用户密码不能为空");
        } else {
            taLog.setText("开始测试连接服务器");
            try {
                serverPort = Integer.parseInt(cacheServerPort);
                executorService.execute(() -> {
                    try {
                        jschUtil.versouSshUtil(serverIp, userName, userPassword, serverPort);
                        jschUtil.runCmd("ls", "UTF-8");
                        addTextToLog("\n连接服务器成功");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        addTextToLog("\n连接服务器失败，请检查输入的信息");
                    }
                });
            } catch (Exception ex) {
                showTipsDialog("您输入的端口号有误，请检查后重试！（端口号均为整数数字）");
            }
        }
    }

    /**
     * 停止推流
     */
    private void stopStream() {
        if (rbLocal.isSelected()) {
            stopPushStreamInWindows();
        } else {
            stopPushStreamInLinux();
        }
    }

    /**
     * 获取分辨率列表
     */
    private void getFormatList() {
        if (rbServer.isSelected()) {
            serverIp = tfServerIp.getText();
            String cacheServerPort = tfServerPort.getText();
            userName = tfUserName.getText();
            userPassword = tfUserPassword.getText();
            if (null == serverIp || serverIp.isEmpty()) {
                showTipsDialog("服务器ip不能为空");
                return;
            } else if (null == cacheServerPort || cacheServerPort.isEmpty()) {
                showTipsDialog("端口号不能为空");
                return;
            } else if (null == userName || userName.isEmpty()) {
                showTipsDialog("用户名不能为空");
                return;
            } else if (null == userPassword || userPassword.isEmpty()) {
                showTipsDialog("用户密码不能为空");
                return;
            } else {
                try {
                    serverPort = Integer.parseInt(cacheServerPort);
                } catch (Exception ex) {
                    showTipsDialog("您输入的端口号有误，请检查后重试！（端口号均为整数数字）");
                    return;
                }
            }
        }
        resourceUrl = new String(taResourceUrl.getText().getBytes(), StandardCharsets.UTF_8);
        if (resourceUrl.isEmpty()) {
            showTipsDialog("请输入直播源地址后重试");
        } else {
            String message = "";
            switch (localDataBean.getConfigSchemeBean().getSchemeType()) {
                case 0:
                    message = "该地址是否需要使用youtube-dl进行解析？\n（如填入的为m3u8地址或本地视频文件地址，请选否）";
                    break;
                case 1:
                    message = "该地址是否需要使用streamlink进行解析？\n（如填入的为m3u8地址或本地视频文件地址，请选否）";
                    break;
            }

            int result = JOptionPane.showConfirmDialog(
                    MainForm.this, message, "温馨提示：",
                    JOptionPane.YES_NO_OPTION
            );
            switch (result) {
                case 0:
                    //是
                    cbFormatList.removeAllItems();
                    isLocalFile = false;
                    if (rbLocal.isSelected()) {
                        getFormatListInWindows();
                    } else {
                        getFormatListInLinux();
                    }
                    break;
                case 1:
                    //否
                    cbFormatList.removeAllItems();
                    isLocalFile = true;
                    m3u8Url = resourceUrl;
                    break;
            }
        }
    }

    /**
     * 推流按钮点击
     */
    private void pushStreamPerformed() {
        if (rbGetLiveRoomUrl.isSelected()) {
            liveRoomUrl = rtmp.getAddr() + rtmp.getCode();
            if (liveRoomUrl.isEmpty()) {
                showTipsDialog("请先打开直播间");
            } else {
                pushStream();
            }
        } else {
            //手动填写直播间地址
            liveRoomUrl = taLiveRoomUrl.getText();
            if (null == liveRoomUrl || liveRoomUrl.isEmpty()) {
                showTipsDialog("直播间地址为空，请输入后重试。");
            } else {
                pushStream();
            }
        }
    }

    /**
     * 通过B站账号信息获取推流地址
     */
    private void openLiveRoom() {
        BilibiliAccount bilibiliAccount = localDataBean.getBilibiliAccount();
        if (null == bilibiliAccount || null == bilibiliAccount.getAccessToken() || bilibiliAccount.getAccessToken().isEmpty()) {
            showTipsDialog("登录信息为空，请在菜单栏中登录B站账号信息后重试");
        } else {
            getAreaList(bilibiliAccount);
        }
    }

    /**
     * 获取分区列表
     *
     * @param bilibiliAccount
     */
    private void getAreaList(BilibiliAccount bilibiliAccount) {
        executorService.execute(() -> {
            addTextToLog("\n\n开始获取分区列表信息，请稍候...");
            bilibiliAPI = new BilibiliAPI(bilibiliAccount);
            try {
                cookiesMap = bilibiliAPI.toCookies();
                bilibiliWebAPI = new BilibiliWebAPI(cookiesMap);
                liveService = bilibiliWebAPI.getLiveService();
                if (openLiveRoomProxy) {
                    liveServiceWithProxy = bilibiliWebAPI.getLiveService(proxyIp, proxyPort);
                }
                LiveAreaListEntity liveAreaListEntity = liveService.getLiveAreaList().execute().body();
                if (null != liveAreaListEntity && 0 == liveAreaListEntity.getCode()) {
                    addTextToLog("\n\n获取分区列表信息成功");
                    if (null == areaSettingDialog) {
                        areaSettingDialog = new AreaSettingDialog(this::updateTitleAndOpenLiveRoom);
                    }
                    areaSettingDialog.setData(liveAreaListEntity.getData());
                    areaSettingDialog.setVisible(true);
                } else {
                    showTipsDialog("获取分区列表失败，请稍后再试");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 更新房间标题并打开直播间
     *
     * @param roomName
     * @param areaId
     */
    private void updateTitleAndOpenLiveRoom(String roomName, String areaId) {
        executorService.execute(() -> {
            try {
                addTextToLog("\n\n开始获取开启直播相关参数，请稍候...");
                LiveInfoEntity liveInfoEntity = liveService.getLiveInfo().execute().body();
                if (null != liveInfoEntity && 0 == liveInfoEntity.getCode()) {
                    roomId = liveInfoEntity.getData().getRoomid();
                    List<Cookie> cookies = cookiesMap.get("bilibili.com");
                    for (Cookie cookie : cookies) {
                        if ("bili_jct".equals(cookie.name())) {
                            csrfToken = cookie.value();
                            break;
                        }
                    }
                    addTextToLog("\n\n获取开启直播参数成功，开始修改房间标题...");
                    UpdateRoomTitleEntity updateRoomTitleEntity = liveService.updateRoomTitle(roomId, roomName, csrfToken).execute().body();
                    if (null != updateRoomTitleEntity && 0 == updateRoomTitleEntity.getCode()) {
                        addTextToLog("\n\n修改房间标题成功，正在开启直播...");
                        StartLiveEntity startLiveEntity;
                        if (openLiveRoomProxy) {
                            startLiveEntity = liveServiceWithProxy.startLive(roomId, "pc", areaId, csrfToken).execute().body();
                        } else {
                            startLiveEntity = liveService.startLive(roomId, "pc", areaId, csrfToken).execute().body();
                        }
                        if (null != startLiveEntity && 0 == startLiveEntity.getCode()) {
                            addTextToLog("\n\n开启直播成功");
                            btnCloseLiveRoom.setEnabled(true);
                            btnToMyLiveRoom.setEnabled(true);
                            rtmp = startLiveEntity.getData().getRtmp();
                            //打开直播成功
                        } else {
                            showTipsDialog("开启直播失败，请稍后再试");
                        }
                    } else {
                        showTipsDialog("更新房间标题失败，请稍后再试");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 关闭直播间
     */
    private void closeLiveRoom() {
        executorService.execute(() -> {
            BilibiliAccount bilibiliAccount = localDataBean.getBilibiliAccount();
            if (null == bilibiliAccount || null == bilibiliAccount.getAccessToken() || bilibiliAccount.getAccessToken().isEmpty()) {
                showTipsDialog("登录信息为空，请在菜单栏中登录B站账号信息后重试");
            } else {
                try {
                    StopLiveEntity stopLiveEntity = liveService.stopLive(roomId, "pc", csrfToken).execute().body();
                    if (null != stopLiveEntity && 0 == stopLiveEntity.getCode()) {
                        addTextToLog("\n\n关闭直播间成功");
                        btnCloseLiveRoom.setEnabled(false);
                        btnToMyLiveRoom.setEnabled(false);
                    } else {
                        addTextToLog("\n\n关闭直播间失败，请自行去B站停止直播");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * 打开我的直播间
     */
    private void toMyLIveRoom() {
        if (null != roomId && !roomId.isEmpty()) {
            String site = "https://live.bilibili.com/" + roomId;
            try {
                Desktop desktop = Desktop.getDesktop();
                if (Desktop.isDesktopSupported()
                        && desktop.isSupported(Desktop.Action.BROWSE)) {
                    URI uri = new URI(site);
                    desktop.browse(uri);
                }
            } catch (IOException | URISyntaxException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * 显示隐藏直播间操作面板
     */
    public void showOrHideLiveRoomControlPanel() {
        BilibiliAccount bilibiliAccount = localDataBean.getBilibiliAccount();
        if (0 == controlPanelHeight) {
            controlPanelHeight = controlPanel.getPreferredSize().height;
        }
        int liveRoomControlPanelHeight = liveRoomControlPanel.getPreferredSize().height;
        if (null == bilibiliAccount || null == bilibiliAccount.getAccessToken() || bilibiliAccount.getAccessToken().isEmpty()) {
            if (liveRoomControlPanel.isVisible()) {
                liveRoomControlPanel.setVisible(false);
                controlPanel.setPreferredSize(new Dimension(0, controlPanelHeight - liveRoomControlPanelHeight));
                controlPanel.setSize(0, controlPanelHeight - liveRoomControlPanelHeight);
            }
        } else {
            if (!liveRoomControlPanel.isVisible()) {
                liveRoomControlPanel.setVisible(true);
                controlPanel.setPreferredSize(new Dimension(0, controlPanelHeight + liveRoomControlPanelHeight));
                controlPanel.setSize(0, controlPanelHeight + liveRoomControlPanelHeight);
            }
        }
        controlPanelHeight = controlPanel.getPreferredSize().height;
    }

    /**
     * 单选按钮选中状态变化监听
     */
    private ItemListener itemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (0 == controlPanelHeight) {
                controlPanelHeight = controlPanel.getPreferredSize().height;
            }
            int serverInfoPanelHeight = serverInfoPanel.getPreferredSize().height;
            int liveRoomPanelHeight = liveRoomPanel.getPreferredSize().height;
            if (e.getSource() == rbLocal && rbLocal.isSelected()) {
                if (serverInfoPanel.isVisible()) {
                    serverInfoPanel.setVisible(false);
                    controlPanel.setPreferredSize(new Dimension(0, controlPanelHeight - serverInfoPanelHeight));
                    controlPanel.setSize(0, controlPanelHeight - serverInfoPanelHeight);
                }
            } else if (e.getSource() == rbServer && rbServer.isSelected()) {
                if (!serverInfoPanel.isVisible()) {
                    serverInfoPanel.setVisible(true);
                    controlPanel.setPreferredSize(new Dimension(0, controlPanelHeight + serverInfoPanelHeight));
                    controlPanel.setSize(0, controlPanelHeight + serverInfoPanelHeight);
                }
            } else if (e.getSource() == rbInputLiveRoomUrl && rbInputLiveRoomUrl.isSelected()) {
                if (!liveRoomPanel.isVisible()) {
                    liveRoomPanel.setVisible(true);
                    controlPanel.setPreferredSize(new Dimension(0, controlPanelHeight + liveRoomPanelHeight));
                    controlPanel.setSize(0, controlPanelHeight + liveRoomPanelHeight);
                }
            } else if (e.getSource() == rbGetLiveRoomUrl && rbGetLiveRoomUrl.isSelected()) {
                if (liveRoomPanel.isVisible()) {
                    liveRoomPanel.setVisible(false);
                    controlPanel.setPreferredSize(new Dimension(0, controlPanelHeight - liveRoomPanelHeight));
                    controlPanel.setSize(0, controlPanelHeight - liveRoomPanelHeight);
                }
            }
            controlPanelHeight = controlPanel.getPreferredSize().height;
        }
    };

    /**
     * 显示提示对话框
     *
     * @param content
     */
    private void showTipsDialog(String content) {
        JOptionPane.showMessageDialog(this, content, "温馨提示：", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示配置代理对话框
     */
    private void showConfigProxyDialog() {
        if (null == configProxyDialog) {
            configProxyDialog = new ConfigProxyDialog((get, open, ip, port) -> {
                getFormatListProxy = get;
                openLiveRoomProxy = open;
                proxyIp = ip;
                proxyPort = port;
                if (openLiveRoomProxy && null != ip && !ip.isEmpty()) {
                    rbOnlyAudio.setEnabled(true);
                    rbOnlyImage.setEnabled(true);
                } else {
                    rbBoth.setSelected(true);
                    rbOnlyAudio.setEnabled(false);
                    rbOnlyImage.setEnabled(false);
                }
            });
            configProxyDialog.pack();
        }
        configProxyDialog.setVisible(true);
    }

    /**
     * 显示bilibili账号对话框
     */
    private void showBilibiliAccountDialog() {
        if (null == bilibiliAccountDialog) {
            bilibiliAccountDialog = new BiliBiliAccountDialog(this);
            bilibiliAccountDialog.pack();
        }
        bilibiliAccountDialog.setVisible(true);
    }

    /**
     * 显示配置推流确认对话框的对话框
     */
    private void showConfigServerPushConfirmDialog() {
        if (null == configServerPushConfirmDialog) {
            configServerPushConfirmDialog = new ConfigServerPushConfirmDialog(this);
            configServerPushConfirmDialog.pack();
        }
        configServerPushConfirmDialog.setVisible(true);
    }

    /**
     * 显示配置解析方案对话框
     */
    private void showConfigSchemeDialog() {
        if (null == configSchemeDialog) {
            configSchemeDialog = new ConfigSchemeDialog(this);
            configSchemeDialog.pack();
        }
        configSchemeDialog.setVisible(true);
    }

    /**
     * 显示保存服务器信息的输入框
     */
    private void showSaveServerInfoDialog() {
        String saveName = JOptionPane.showInputDialog("请输入保存的名称");
        if (null != saveName) {
            if (saveName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "保存的名称不能为空，请输入后重试", "温馨提示：", JOptionPane.INFORMATION_MESSAGE);
            } else {
                serverIp = tfServerIp.getText();
                String cacheServerPort = tfServerPort.getText();
                userName = tfUserName.getText();
                userPassword = tfUserPassword.getText();
                if (null == serverIp || serverIp.isEmpty()) {
                    showTipsDialog("服务器ip不能为空");
                } else if (null == cacheServerPort || cacheServerPort.isEmpty()) {
                    showTipsDialog("端口号不能为空");
                } else if (null == userName || userName.isEmpty()) {
                    showTipsDialog("用户名不能为空");
                } else if (null == userPassword || userPassword.isEmpty()) {
                    showTipsDialog("用户密码不能为空");
                } else {
                    try {
                        serverPort = Integer.parseInt(cacheServerPort);
                        List<ServerInfoBean> serverInfoBeans = localDataBean.getServerInfoBeans();
                        if (null == serverInfoBeans) {
                            serverInfoBeans = new ArrayList<>();
                        }
                        ServerInfoBean serverInfoBean = new ServerInfoBean();
                        serverInfoBean.setSaveName(saveName);
                        serverInfoBean.setIp(serverIp);
                        serverInfoBean.setPort(serverPort);
                        serverInfoBean.setUserName(userName);
                        serverInfoBean.setUserPassword(userPassword);
                        serverInfoBeans.add(serverInfoBean);
                        localDataBean.setServerInfoBeans(serverInfoBeans);
                        jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
                        showTipsDialog("服务器信息保存记录成功");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showTipsDialog("您输入的端口号有误，请检查后重试！（端口号均为整数数字）");
                    }

                }
            }
        }
    }

    /**
     * 显示保存直播源信息的输入框
     */
    private void showSaveSourceUrlInfoDialog() {
        String saveName = JOptionPane.showInputDialog("请输入保存的名称");
        if (null != saveName) {
            if (saveName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "保存的名称不能为空，请输入后重试", "温馨提示：", JOptionPane.INFORMATION_MESSAGE);
            } else {
                resourceUrl = taResourceUrl.getText();
                if (resourceUrl.isEmpty()) {
                    showTipsDialog("直播源地址不能为空");
                } else {
                    List<SourceUrlInfoBean> sourceUrlInfoBeans = localDataBean.getSourceUrlInfoBeans();
                    if (null == sourceUrlInfoBeans) {
                        sourceUrlInfoBeans = new ArrayList<>();
                    }
                    SourceUrlInfoBean sourceUrlInfoBean = new SourceUrlInfoBean();
                    sourceUrlInfoBean.setSaveName(saveName);
                    sourceUrlInfoBean.setUrl(resourceUrl);
                    sourceUrlInfoBeans.add(sourceUrlInfoBean);
                    localDataBean.setSourceUrlInfoBeans(sourceUrlInfoBeans);
                    jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
                    showTipsDialog("直播源信息保存记录成功");
                }
            }
        }
    }

    /**
     * 显示保存直播间地址的输入框
     */
    private void showSaveLiveRoomUrlInfo() {
        String saveName = JOptionPane.showInputDialog("请输入保存的名称");
        if (null != saveName) {
            if (saveName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "保存的名称不能为空，请输入后重试", "温馨提示：", JOptionPane.INFORMATION_MESSAGE);
            } else {
                liveRoomUrl = taLiveRoomUrl.getText();
                if (liveRoomUrl.isEmpty()) {
                    showTipsDialog("直播间地址不能为空");
                } else {
                    List<LiveRoomUrlInfoBean> liveRoomUrlInfoBeans = localDataBean.getLiveRoomUrlInfoBeans();
                    if (null == liveRoomUrlInfoBeans) {
                        liveRoomUrlInfoBeans = new ArrayList<>();
                    }
                    LiveRoomUrlInfoBean liveRoomUrlInfoBean = new LiveRoomUrlInfoBean();
                    liveRoomUrlInfoBean.setSaveName(saveName);
                    liveRoomUrlInfoBean.setUrl(liveRoomUrl);
                    liveRoomUrlInfoBeans.add(liveRoomUrlInfoBean);
                    localDataBean.setLiveRoomUrlInfoBeans(liveRoomUrlInfoBeans);
                    jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
                    showTipsDialog("直播间信息保存记录成功");
                }
            }
        }
    }

    /**
     * 显示本地保存的服务器信息对话框
     */
    private void showServerInfoDialog() {
        if (null == serverInfoDialog) {
            serverInfoDialog = new ServerInfoDialog(this, (ip, port, userName, userPassword) -> {
                tfServerIp.setText(ip);
                tfServerPort.setText(String.valueOf(port));
                tfUserName.setText(userName);
                tfUserPassword.setText(userPassword);
            });
            serverInfoDialog.pack();
        }
        serverInfoDialog.setVisible(true);
    }

    /**
     * 显示本地保存的直播源地址信息对话框
     */
    private void showSourceUrlInfoDialog() {
        if (null == resourceUrlInfoDialog) {
            resourceUrlInfoDialog = new SourceUrlInfoDialog(this, url -> taResourceUrl.setText(url));
            resourceUrlInfoDialog.pack();
        }
        resourceUrlInfoDialog.setVisible(true);
    }

    /**
     * 显示本地保存的直播间地址信息对话框
     */
    private void showLiveRoomUrlInfoDialog() {
        if (null == liveRoomUrlInfoDialog) {
            liveRoomUrlInfoDialog = new LiveRoomUrlInfoDialog(this, url -> taLiveRoomUrl.setText(url));
            liveRoomUrlInfoDialog.pack();
        }
        liveRoomUrlInfoDialog.setVisible(true);
    }

    /**
     * windows环境获取分辨率列表
     */
    private void getFormatListInWindows() {
        executorService.execute(() -> {
            taLog.setText("开始获取分辨率列表");
            try {
                process = Runtime.getRuntime().exec(command);
                stdin = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"), true);
                new Thread(new GetFormatListRunnable(process.getErrorStream(), getFormatListCallBack, localDataBean.getConfigSchemeBean().getSchemeType())).start();
                new Thread(new GetFormatListRunnable(process.getInputStream(), getFormatListCallBack, localDataBean.getConfigSchemeBean().getSchemeType())).start();
                setWindowsProxy();
                stdin.println("set path=%path%;" + windowsEnvPath);
                String cmd = "";
                switch (localDataBean.getConfigSchemeBean().getSchemeType()) {
                    case 0:
                        cmd = "youtube-dl --list-formats " + resourceUrl;
                        break;
                    case 1:
                        cmd = "streamlink " + resourceUrl;
                        break;
                }
                stdin.println(cmd);
                stdin.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * linux服务器环境获取分辨率列表
     */
    private void getFormatListInLinux() {
        executorService.execute(() -> {
            taLog.setText("开始获取分辨率列表");
            try {
                jschUtil.versouSshUtil(serverIp, userName, userPassword, serverPort);
                String cmd = "";
                switch (localDataBean.getConfigSchemeBean().getSchemeType()) {
                    case 0:
                        cmd = "youtube-dl --list-formats " + resourceUrl;
                        break;
                    case 1:
                        cmd = "streamlink " + resourceUrl;
                        break;
                }
                jschUtil.runCmd(cmd, "UTF-8", getFormatListCallBack, localDataBean.getConfigSchemeBean().getSchemeType());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 获取分辨率列表的回调
     */
    private GetFormatListRunnable.GetFormatListCallBack getFormatListCallBack = new GetFormatListRunnable.GetFormatListCallBack() {
        @Override
        public void getFormatListSuccess(List<ResolutionBean> listResolutions) {
            MainForm.this.listResolutions = listResolutions;
            cbFormatList.removeAllItems();
            for (ResolutionBean resolution : listResolutions) {
                String result = "";
                if (null != resolution.getResolutionPx() && !resolution.getResolutionPx().isEmpty()) {
                    result += resolution.getResolutionPx();
                } else {
                    result += "无分辨率参数";
                }
                if (null != resolution.getFps() && !resolution.getFps().isEmpty()) {
                    result += " " + resolution.getFps();
                }
                cbFormatList.addItem(result);
            }
            addTextToLog("\n\n" + "获取分辨率列表成功，请选择推送分辨率，检查直播间地址是否有误，检查无误后点击开始推流按钮");
            jschUtil.close();
        }

        @Override
        public void getFormatListFail(List<String> errLog) {
            cbFormatList.removeAllItems();
            addTextToLog("\n\n" + "获取分辨率列表失败");
            for (String str : errLog) {
                addTextToLog("\n" + str);
            }
            jschUtil.close();
        }
    };

    /**
     * 设置windows的cmd代理
     */
    private void setWindowsProxy() {
        if (getFormatListProxy) {
            stdin.println("set http_proxy=http://" + proxyIp + ":" + proxyPort);
            stdin.println("set https_proxy=https://" + proxyIp + ":" + proxyPort);
        }
    }

    /**
     * 推流
     */
    private void pushStream() {
        if (isLocalFile) {
            //直推m3u8或本地视频文件
            if (rbLocal.isSelected()) {
                pushStreamToLiveRoomInWindows();
            } else {
                showBackGroundPushStreamConfirmDialog();
            }
        } else {
            if (rbLocal.isSelected()) {
                if (0 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    startGetM3u8Url();
                } else if (1 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    pushStreamToLiveRoomInWindows();
                }
            } else {
                if (0 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    startGetM3u8Url();
                } else if (1 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    showBackGroundPushStreamConfirmDialog();
                }
            }
        }
    }

    /**
     * 显示是否需要后台推流的对话框
     */
    private void showBackGroundPushStreamConfirmDialog() {
        ConfigLinuxPushBean configLinuxPushBean = localDataBean.getConfigLinuxPushBean();
        switch (configLinuxPushBean.getStatus()) {
            case 0:
                int result = JOptionPane.showConfirmDialog(
                        MainForm.this,
                        "您当前选择的是服务器推流模式，是否需要开启后台推送？\n（开启后台推送后无法获取当前推流日志）",
                        "温馨提示：",
                        JOptionPane.YES_NO_OPTION
                );
                switch (result) {
                    case 0:
                        //是
                        needBackGround = true;
                        pushStreamToLiveRoom();
                        break;
                    case 1:
                        //否
                        needBackGround = false;
                        pushStreamToLiveRoom();
                        break;
                }
                break;
            case 1:
                //是
                needBackGround = true;
                pushStreamToLiveRoom();
                break;
            case 2:
                //否
                needBackGround = false;
                pushStreamToLiveRoom();
                break;
        }
    }

    /**
     * 推流到直播间
     */
    private void pushStreamToLiveRoom() {
        if (rbLocal.isSelected()) {
            pushStreamToLiveRoomInWindows();
        } else {
            pushStreamToLiveRoomInLinux();
        }

    }

    /**
     * windows平台推流
     */
    private void pushStreamToLiveRoomInWindows() {
        executorService.execute(() -> {
            try {
                addTextToLog("\n\n开始组装推流参数即将开始推流，请稍候...");
                process = Runtime.getRuntime().exec(command);
                stdin = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"), true);
                stdin.println("set path=%path%;" + windowsEnvPath);
                new Thread(new PushStreamRunnable(process.getErrorStream(), pushStreamCallBack)).start();
                new Thread(new PushStreamRunnable(process.getInputStream(), pushStreamCallBack)).start();
                setWindowsProxy();
                String videoParams = null;
                if (rbBoth.isSelected()) {
                    videoParams = " -c:v copy -c:a aac -strict -2 -f flv ";
                } else if (rbOnlyAudio.isSelected()) {
                    videoParams = " -vn -c:a aac -strict -2 -f flv ";
                } else if (rbOnlyImage.isSelected()) {
                    videoParams = " -c:v copy -an -strict -2 -f flv ";
                }
                if (0 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String cache = "ffmpeg -thread_queue_size 1024 -i " + m3u8Url + videoParams + "\"" + liveRoomUrl + "\"";
                    stdin.println(cache);
                    stdin.close();
                } else if (1 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String cache;
                    String resolutionPx = listResolutions.get(cbFormatList.getSelectedIndex()).getResolutionPx();
                    if (resolutionPx.contains("(")) {
                        resolutionPx = resolutionPx.substring(0, resolutionPx.lastIndexOf("("));
                    }
                    cache = "streamlink -O " + resourceUrl + " " + resolutionPx + " | ffmpeg -thread_queue_size 1024 -i pipe:0 " + videoParams + "\"" + liveRoomUrl + "\"";
                    stdin.println(cache);
                    stdin.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * linux平台推流
     */
    private void pushStreamToLiveRoomInLinux() {
        executorService.execute(() -> {
            try {
                addTextToLog("\n\n开始组装推流参数即将开始推流，请稍候...");
                jschUtil.versouSshUtil(serverIp, userName, userPassword, serverPort);
                String videoParams = null;
                if (rbBoth.isSelected()) {
                    videoParams = " -c:v copy -c:a aac -strict -2 -f flv ";
                } else if (rbOnlyAudio.isSelected()) {
                    videoParams = " -vn -c:a aac -strict -2  -f flv ";
                } else if (rbOnlyImage.isSelected()) {
                    videoParams = " -c:v copy -an -strict -2  -f flv ";
                }
                if (0 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String cache;
                    if (needBackGround) {
                        cache = "screen -dmS SimplePushStreamUtil ffmpeg -thread_queue_size 1024 -i " + m3u8Url + videoParams + "\"" + liveRoomUrl + "\"";
                    } else {
                        cache = "ffmpeg -thread_queue_size 1024 -i " + m3u8Url + videoParams + "\"" + liveRoomUrl + "\"";
                    }
                    System.out.println(cache);
                    jschUtil.runCmd(cache, "UTF-8", pushStreamCallBack);
                } else if (1 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String cache;
                    String resolutionPx = listResolutions.get(cbFormatList.getSelectedIndex()).getResolutionPx();
                    if (resolutionPx.contains("(")) {
                        resolutionPx = resolutionPx.substring(0, resolutionPx.lastIndexOf("("));
                    }
                    if (needBackGround) {
                        cache = "nohup streamlink -O " + resourceUrl + " " + resolutionPx + " | ffmpeg -thread_queue_size 1024 -i pipe:0 " + videoParams + "\"" + liveRoomUrl + "\"" + ";" + "jobs -l";
                    } else {
                        cache = "streamlink -O " + resourceUrl + " " + resolutionPx + " | ffmpeg -thread_queue_size 1024 -i pipe:0 " + videoParams + "\"" + liveRoomUrl + "\"";
                    }
                    System.out.println(cache);
                    jschUtil.runCmd(cache, "UTF-8", pushStreamCallBack);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 推流的回调
     */
    private PushStreamRunnable.PushStreamCallBack pushStreamCallBack = new PushStreamRunnable.PushStreamCallBack() {
        @Override
        public void pushing(String size, String time, String bitrate) {
            BigDecimal bigDecimal = new BigDecimal(size);
            int i = bigDecimal.intValue();
            String result1;
            if (i >= 1000) {
                result1 = bigDecimal.divide(new BigDecimal(1024)).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "MB";
            } else {
                result1 = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "KB";
            }
            String result2 = new BigDecimal(bitrate).divide(new BigDecimal(8)).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
            addTextToLog("\n\n" + simpleDateFormat.format(new Date()) + "\n" + "已推送文件大小：" + result1 + "\u3000已推流时长："
                    + time.substring(0, time.lastIndexOf(".")) + "\u3000上传速度：" + result2 + "KB/S");
        }

        @Override
        public void pushFail(String reason) {
            addTextToLog("\n\n" + reason);
        }
    };

    /**
     * 开始获取m3u8地址
     */
    private void startGetM3u8Url() {
        if (rbLocal.isSelected()) {
            getM3u8UrlInWindows();
        } else {
            getM3u8UrlInLinux();
        }
    }

    /**
     * windows平台的获取m3u8地址
     */
    private void getM3u8UrlInWindows() {
        executorService.execute(() -> {
            addTextToLog("\n\n开始获取直播源，请稍候...");
            try {
                process = Runtime.getRuntime().exec(command);
                stdin = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"), true);
                new Thread(new GetM3u8UrlRunnable(process.getErrorStream(), getM3u8UrlCallBack)).start();
                new Thread(new GetM3u8UrlRunnable(process.getInputStream(), getM3u8UrlCallBack)).start();
                setWindowsProxy();
                stdin.println("set path=%path%;" + windowsEnvPath);
                if (0 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    //通过youtube-dl获取m3u8地址
                    String resolutionNo = listResolutions.get(cbFormatList.getSelectedIndex()).getResolutionNo();
                    stdin.println("youtube-dl -f " + resolutionNo + " -g " + resourceUrl);
                    stdin.close();
                } else if (1 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String resolutionPx = listResolutions.get(cbFormatList.getSelectedIndex()).getResolutionPx();
                    if (resolutionPx.contains("(")) {
                        resolutionPx = resolutionPx.substring(0, resolutionPx.lastIndexOf("("));
                    }
                    stdin.println("streamlink --stream-url " + resourceUrl + " " + resolutionPx);
                    stdin.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * linux平台的获取m3u8地址
     */
    private void getM3u8UrlInLinux() {
        executorService.execute(() -> {
            addTextToLog("\n\n开始获取直播源，请稍候...");
            try {
                jschUtil.versouSshUtil(serverIp, userName, userPassword, serverPort);
                if (0 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String resolutionNo = listResolutions.get(cbFormatList.getSelectedIndex()).getResolutionNo();
                    //通过youtube-dl获取m3u8地址
                    jschUtil.runCmd("youtube-dl -f " + resolutionNo + " -g " + resourceUrl, "UTF-8", getM3u8UrlCallBack);
                } else if (1 == localDataBean.getConfigSchemeBean().getSchemeType()) {
                    String resolutionPx = listResolutions.get(cbFormatList.getSelectedIndex()).getResolutionPx();
                    if (resolutionPx.contains("(")) {
                        resolutionPx = resolutionPx.substring(0, resolutionPx.lastIndexOf("("));
                    }
                    jschUtil.runCmd("streamlink --stream-url " + resourceUrl + " " + resolutionPx, "UTF-8", getM3u8UrlCallBack);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 获取m3u8地址的回调
     */
    private GetM3u8UrlRunnable.GetM3u8UrlCallBack getM3u8UrlCallBack = new GetM3u8UrlRunnable.GetM3u8UrlCallBack() {
        @Override
        public void GetM3u8UrlSuccess(String m3u8Url) {
            MainForm.this.m3u8Url = m3u8Url;
            addTextToLog("\n\n获取直播源成功");
            if (rbLocal.isSelected()) {
                pushStreamToLiveRoom();
            } else {
                showBackGroundPushStreamConfirmDialog();
            }
        }

        @Override
        public void GetM3u8UrlFail(List<String> errLog) {
            addTextToLog("\n\n获取直播源失败");
            for (String str : errLog) {
                addTextToLog("\n" + str);
            }
            jschUtil.close();
        }
    };

    /**
     * windows平台的停止推流
     */
    private void stopPushStreamInWindows() {
        try {
            process = Runtime.getRuntime().exec(command);
            new Thread(new FindFfmpegPidRunnable(process.getErrorStream(), System.err, findFfmpegCallBack, liveRoomUrl)).start();
            new Thread(new FindFfmpegPidRunnable(process.getInputStream(), System.out, findFfmpegCallBack, liveRoomUrl)).start();
            stdin = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"), true);
            /** 以下可以输入自己想输入的cmd命令 */
            stdin.println("wmic process where name=\"ffmpeg.exe\" get CommandLine,processid,executablepath");//通过进程名获得pid、路径
            stdin.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 查找ffmpeg进程的回调
     */
    private FindFfmpegPidRunnable.FindFfmpegCallBack findFfmpegCallBack = pid -> {
        if (null != pid && pid.length() > 0) {
            killFfmpegProcess(pid);
        }
    };

    /**
     * 杀死ffmpeg进程
     *
     * @param pid
     */
    private void killFfmpegProcess(String pid) {
        try {
            process = Runtime.getRuntime().exec(command);
            new Thread(new KillFfmpegRunnable(process.getErrorStream(), System.err, killFfmpegCallBack)).start();
            new Thread(new KillFfmpegRunnable(process.getInputStream(), System.out, killFfmpegCallBack)).start();
            stdin = new PrintWriter(process.getOutputStream());
            /** 以下可以输入自己想输入的cmd命令 */
            stdin.println("wmic process where processid=\"" + pid + "\" call terminate");//通过进程名获得pid、路径
            stdin.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 杀死ffmpeg的回调
     */
    private KillFfmpegRunnable.KillFfmpegCallBack killFfmpegCallBack = new KillFfmpegRunnable.KillFfmpegCallBack() {
        @Override
        public void killProcessSuccess() {
            addTextToLog("\n\n结束ffmpeg进程成功。（为保证彻底关闭推流，请进入任务管理查看是否存在ffmpeg的进程）\n\n");
        }

        @Override
        public void killProcessFail(String reason) {
            addTextToLog("\n\n结束ffmpeg进程失败" + reason);
        }
    };

    /**
     * linux平台的停止推流
     */
    private void stopPushStreamInLinux() {
        executorService.execute(() -> {
            try {
                jschUtil.versouSshUtil(serverIp, userName, userPassword, serverPort);
                List<String> ls = jschUtil.runCmd("ps -aux|grep " + "\"" + liveRoomUrl + "\"" + "| grep -v grep | awk '{print $2}'", "UTF-8");
                for (String str : ls) {
                    jschUtil.runCmd("kill -9 " + str, "UTF-8");
                }
                jschUtil.close();
                addTextToLog("\n\n结束推流成功");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 设置日志
     *
     * @param log
     */
    private void addTextToLog(String log) {
        if (taLog.getText().length() > 8000) {
            taLog.setText(log);
        } else {
            taLog.append(log);
        }
        taLog.selectAll();
    }

    public LocalDataBean getLocalDataBean() {
        return localDataBean;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public Gson getGson() {
        return gson;
    }

    private JPanel panel1;
    private JRadioButton rbLocal;
    private JRadioButton rbServer;
    private JTextArea taLog;
    private JTextField tfServerIp;
    private JTextField tfServerPort;
    private JTextField tfUserName;
    private JTextField tfUserPassword;
    private JButton btnTestConnect;
    private JButton btnSaveServerInfo;
    private JButton btnLoadServerInfo;
    private JButton btnSaveSourceUrlInfo;
    private JButton btnGetResourceInfo;
    private JTextArea taResourceUrl;
    private JComboBox cbFormatList;
    private JRadioButton rbInputLiveRoomUrl;
    private JRadioButton rbGetLiveRoomUrl;
    private JButton btnSaveLiveRoomUrlInfo;
    private JButton btnGetLiveRoomInfo;
    private JRadioButton rbOnlyAudio;
    private JRadioButton rbOnlyImage;
    private JButton btnOpenLiveRoom;
    private JButton btnCloseLiveRoom;
    private JButton btnToMyLiveRoom;
    private JButton btnGetFormatList;
    private JButton btnPushStream;
    private JButton btnStopStream;
    private JPanel serverInfoPanel;
    private JPanel controlPanel;
    private JPanel liveRoomPanel;
    private JScrollPane contentScrollPanel;
    private JTextArea taLiveRoomUrl;
    private JRadioButton rbBoth;
    private JPanel liveRoomControlPanel;
}
