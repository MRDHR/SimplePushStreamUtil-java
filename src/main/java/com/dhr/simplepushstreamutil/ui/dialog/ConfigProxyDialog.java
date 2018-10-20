package com.dhr.simplepushstreamutil.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigProxyDialog extends JDialog {
    private CallBack callBack;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox cbEnableProxy;
    private JTextField tfProxyIp;
    private JTextField tfProxyPort;

    public ConfigProxyDialog(CallBack callBack) {
        this.callBack = callBack;
        setContentPane(contentPane);
        setResizable(false);
        setSize(242, 200);
        setPreferredSize(new Dimension(242, 200));
        setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (cbEnableProxy.isSelected()) {
            String ip = tfProxyIp.getText();
            String port = tfProxyPort.getText();
            if (null == ip || ip.isEmpty()) {
                // 消息对话框无返回, 仅做通知作用
                JOptionPane.showMessageDialog(
                        this,
                        "代理ip不能为空",
                        "温馨提示：",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else if (null == port || port.isEmpty()) {
                // 消息对话框无返回, 仅做通知作用
                JOptionPane.showMessageDialog(
                        this,
                        "端口号不能为空",
                        "温馨提示：",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                try {
                    int cachePort = Integer.parseInt(port);
                    callBack.confirm(ip, cachePort);
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "端口号错误",
                            "温馨提示：",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        } else {
            callBack.confirm("", 0);
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public interface CallBack {
        void confirm(String ip, int port);
    }

}
