package com.dhr.simplepushstreamutil.ui.dialog;

import com.dhr.simplepushstreamutil.bean.ConfigLinuxPushBean;
import com.dhr.simplepushstreamutil.bean.LocalDataBean;
import com.dhr.simplepushstreamutil.ui.form.MainForm;
import com.dhr.simplepushstreamutil.util.JsonUtil;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigServerPushConfirmDialog extends JDialog {
    private MainForm mainForm;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton rbConfirmEveryTime;
    private JRadioButton rbBackgroundEveryTime;
    private JRadioButton rbFrontEveryTime;

    private JsonUtil jsonUtil = new JsonUtil();
    private Gson gson = new Gson();
    private ConfigLinuxPushBean configLinuxPushBean;


    public ConfigServerPushConfirmDialog(MainForm mainForm) {
        this.mainForm = mainForm;
        setContentPane(contentPane);
        setSize(242, 220);
        setPreferredSize(new Dimension(242, 220));
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

        initView();
    }

    private void initView() {
        configLinuxPushBean = mainForm.getLocalDataBean().getConfigLinuxPushBean();
        if (null == configLinuxPushBean) {
            configLinuxPushBean = new ConfigLinuxPushBean();
        }
        switch (configLinuxPushBean.getStatus()) {
            case 0:
                rbConfirmEveryTime.setSelected(true);
                break;
            case 1:
                rbBackgroundEveryTime.setSelected(true);
                break;
            case 2:
                rbFrontEveryTime.setSelected(true);
                break;
        }
    }

    private void onOK() {
        if (rbConfirmEveryTime.isSelected()) {
            configLinuxPushBean.setStatus(0);
        } else if (rbBackgroundEveryTime.isSelected()) {
            configLinuxPushBean.setStatus(1);
        } else if (rbFrontEveryTime.isSelected()) {
            configLinuxPushBean.setStatus(2);
        }
        mainForm.getLocalDataBean().setConfigLinuxPushBean(configLinuxPushBean);
        jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(mainForm.getLocalDataBean()));
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
