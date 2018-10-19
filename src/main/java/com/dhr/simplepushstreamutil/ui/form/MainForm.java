package com.dhr.simplepushstreamutil.ui.form;

import com.dhr.simplepushstreamutil.bean.LocalDataBean;
import com.dhr.simplepushstreamutil.bean.ServerInfoBean;
import com.dhr.simplepushstreamutil.bean.SourceUrlInfoBean;
import com.dhr.simplepushstreamutil.ui.dialog.*;
import com.dhr.simplepushstreamutil.util.JschUtil;
import com.dhr.simplepushstreamutil.util.JsonUtil;
import com.google.gson.Gson;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainForm extends JFrame {
    private ConfigProxyDialog configProxyDialog;//配置代理的对话框
    private BilibiliAccountDialog bilibiliAccountDialog;
    private ConfigServerPushConfirmDialog configServerPushConfirmDialog;
    private ConfigSchemeDialog configSchemeDialog;
    private AreaSettingDialog areaSettingDialog;

    private SourceUrlInfoDialog resourceUrlInfoDialog;
    private ServerInfoDialog serverInfoDialog;

    private String resourceUrl;
    private String m3u8Url;
    private String targetUrl;


    private JsonUtil jsonUtil = new JsonUtil();
    private Gson gson = new Gson();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private JschUtil jschUtil = new JschUtil();
    private LocalDataBean localDataBean;

    private String serverIp;
    private int serverPort;
    private String userName;
    private String userPassword;

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

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainForm.showConfigProxyDialog();
            }
        });

        mi2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainForm.showBilibiliAccountDialog();
            }
        });

        mi3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainForm.showConfigServerPushConfirmDialog();
            }
        });
        mi4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainForm.showConfigSchemeDialog();
            }
        });
        mi6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainForm.showTipsDialog("随手写的程序，没啥好说的");
            }
        });

        frame.setContentPane(mainForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private MainForm() {
        initView();
        initData();
    }

    private void initView() {
        rbLocal.setSelected(true);
        rbLocal.addChangeListener(rbLocalOrServerChangeListener);

        serverInfoPanel.setVisible(false);

        btnTestConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testServerConnect();
            }
        });

        btnSaveServerInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSaveServerInfoDialog();
            }
        });

        btnSaveSourceUrlInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSaveSourceUrlInfoDialog();
            }
        });

        btnSaveLiveRoomUrlInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSaveLiveRoomUrlInfo();
            }
        });

        btnGetResourceInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSourceUrlInfoDialog();
            }
        });

        btnLoadServerInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showServerInfoDialog();
            }
        });

        btnSaveSourceUrlInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void initData() {
        String localData = jsonUtil.getDatafromFile(LocalDataBean.class.getSimpleName());
        if (null == localData || localData.isEmpty()) {
            localDataBean = new LocalDataBean();
            jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(localDataBean));
        } else {
            localDataBean = gson.fromJson(localData, LocalDataBean.class);
        }
    }

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
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jschUtil.versouSshUtil(serverIp, userName, userPassword, serverPort);
                            jschUtil.runCmd("ls", "UTF-8");
                            addTextToLog("\n连接服务器成功");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            addTextToLog("\n连接服务器失败，请检查输入的信息");
                        }
                    }
                });
            } catch (Exception ex) {
                showTipsDialog("您输入的端口号有误，请检查后重试！（端口号均为整数数字）");
            }
        }
    }

    /**
     * 本地推流和服务器推流的选择变化监听
     */
    private ChangeListener rbLocalOrServerChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (rbLocal.isSelected()) {
                serverInfoPanel.setVisible(false);
            } else {
                serverInfoPanel.setVisible(true);
            }
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

    private void showConfigProxyDialog() {
        if (null == configProxyDialog) {
            configProxyDialog = new ConfigProxyDialog();
            configProxyDialog.pack();
        }
        configProxyDialog.setVisible(true);
    }

    private void showBilibiliAccountDialog() {
        if (null == bilibiliAccountDialog) {
            bilibiliAccountDialog = new BilibiliAccountDialog();
            bilibiliAccountDialog.pack();
        }
        bilibiliAccountDialog.setVisible(true);
    }

    private void showConfigServerPushConfirmDialog() {
        if (null == configServerPushConfirmDialog) {
            configServerPushConfirmDialog = new ConfigServerPushConfirmDialog();
            configServerPushConfirmDialog.pack();
        }
        configServerPushConfirmDialog.setVisible(true);
    }

    private void showConfigSchemeDialog() {
        if (null == configSchemeDialog) {
            configSchemeDialog = new ConfigSchemeDialog();
            configSchemeDialog.pack();
        }
        configSchemeDialog.setVisible(true);
    }

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

    private void showSaveLiveRoomUrlInfo() {
        String strName = JOptionPane.showInputDialog("请输入保存的名称");
    }

    private void showSourceUrlInfoDialog() {
        if (null == resourceUrlInfoDialog) {
            resourceUrlInfoDialog = new SourceUrlInfoDialog(this, new SourceUrlInfoDialog.CallBack() {
                @Override
                public void confirm(String url) {
                    taResourceUrl.setText(url);
                }
            });
            resourceUrlInfoDialog.pack();
        }
        resourceUrlInfoDialog.setVisible(true);
    }

    private void showServerInfoDialog() {
        if (null == serverInfoDialog) {
            serverInfoDialog = new ServerInfoDialog(this, new ServerInfoDialog.CallBack() {
                @Override
                public void confirm(String ip, int port, String userName, String userPassword) {
                    tfServerIp.setText(ip);
                    tfServerPort.setText(String.valueOf(port));
                    tfUserName.setText(userName);
                    tfUserPassword.setText(userPassword);
                }
            });
            serverInfoDialog.pack();
        }
        serverInfoDialog.setVisible(true);
    }

    private void showAreaSettingDialog() {
        if (null == areaSettingDialog) {
            areaSettingDialog = new AreaSettingDialog();
            areaSettingDialog.pack();
        }
        areaSettingDialog.setVisible(true);
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
    private JComboBox comboBox1;
    private JRadioButton 手动填写直播间地址RadioButton;
    private JRadioButton 通过登录信息获取直播间地址RadioButton;
    private JButton btnSaveLiveRoomUrlInfo;
    private JButton 获取保存记录Button1;
    private JRadioButton 画面音频RadioButton;
    private JRadioButton 只音频需全局代理RadioButton;
    private JRadioButton 只画面需全局代理RadioButton;
    private JButton 开启直播间Button;
    private JButton 关闭直播间Button;
    private JButton 打开我的直播间Button;
    private JButton 开始解析Button;
    private JButton 开始推流Button;
    private JButton 结束推流Button;
    private JPanel serverInfoPanel;
}
