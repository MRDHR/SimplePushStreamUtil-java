package com.dhr.simplepushstreamutil.ui.form;

import com.dhr.simplepushstreamutil.ui.dialog.ConfigProxyDialog;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class MainForm extends JFrame {
    private ConfigProxyDialog configProxyDialog;//配置代理的对话框

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

        frame.setContentPane(mainForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private MainForm() {
        initView();
    }

    private void initView() {
        rbLocal.setSelected(true);
        rbLocal.addChangeListener(rbLocalOrServerChangeListener);

        serverInfoPanel.setVisible(false);
        btnTestConnect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        });
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

    private JPanel panel1;
    private JRadioButton rbLocal;
    private JRadioButton rbServer;
    private JTextArea taLog;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton btnTestConnect;
    private JButton btnSaveServerInfo;
    private JButton btnLoadServerInfo;
    private JButton 保存直播源地址Button;
    private JButton 获取保存记录Button;
    private JTextArea textArea1;
    private JComboBox comboBox1;
    private JRadioButton 手动填写直播间地址RadioButton;
    private JRadioButton 通过登录信息获取直播间地址RadioButton;
    private JButton 保存直播间地址Button;
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
