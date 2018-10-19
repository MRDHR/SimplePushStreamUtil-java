package com.dhr.simplepushstreamutil;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MainForm {
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

        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private MainForm() {
        initView();

    }

    private void initView() {
        rbLocal.setSelected(true);

        rbLocal.addChangeListener(changeListener);
    }

    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (rbLocal.isSelected()) {
                taLog.setText("本机推流已选中");
            } else {
                taLog.setText("服务器推流已选中");
            }
        }
    };

    private JPanel panel1;
    private JRadioButton rbLocal;
    private JRadioButton rbServer;
    private JTextArea taLog;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton 测试连接Button;
    private JButton 保存本地Button;
    private JButton 获取本地Button;
}
