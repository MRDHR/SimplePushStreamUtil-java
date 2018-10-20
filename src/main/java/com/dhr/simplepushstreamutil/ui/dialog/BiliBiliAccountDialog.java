package com.dhr.simplepushstreamutil.ui.dialog;

import com.dhr.simplepushstreamutil.bean.LocalDataBean;
import com.dhr.simplepushstreamutil.ui.form.MainForm;
import com.dhr.simplepushstreamutil.util.JsonUtil;
import com.google.gson.Gson;
import com.hiczp.bilibili.api.BilibiliAPI;
import com.hiczp.bilibili.api.BilibiliAccount;
import com.hiczp.bilibili.api.passport.entity.LoginResponseEntity;
import com.hiczp.bilibili.api.passport.exception.CaptchaMismatchException;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BiliBiliAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton btnRemove;
    private JButton buttonCancel;
    private JButton btnSave;
    private JButton btnTestLogin;
    private JTextField tfUserName;
    private JTextField tfPassword;

    private MainForm mainForm;
    private String userName;
    private String password;
    private BilibiliAccount bilibiliAccount;
    private JsonUtil jsonUtil = new JsonUtil();
    private Gson gson = new Gson();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public BiliBiliAccountDialog(MainForm mainForm) {
        this.mainForm = mainForm;
        setContentPane(contentPane);
        setSize(330, 200);
        setPreferredSize(new Dimension(330, 200));
        setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(btnRemove);
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
        btnTestLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userName = tfUserName.getText();
                password = tfPassword.getText();
                if (userName.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            BiliBiliAccountDialog.this,
                            "账号不能为空，请输入后重试",
                            "温馨提示：",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            BiliBiliAccountDialog.this,
                            "密码不能为空，请输入后重试",
                            "温馨提示：",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    testLogin();
                }
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveLoginInfo();
            }
        });
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeLoginInfo();
            }
        });
    }

    private void testLogin() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                BilibiliAPI bilibiliAPI = new BilibiliAPI();
                try {
                    LoginResponseEntity loginResponseEntity = bilibiliAPI.login(userName, password);
                    int code = loginResponseEntity.getCode();
                    if (0 == code) {
                        bilibiliAccount = loginResponseEntity.toBilibiliAccount();
                        JOptionPane.showMessageDialog(
                                BiliBiliAccountDialog.this,
                                "登录成功",
                                "温馨提示：",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            BiliBiliAccountDialog.this,
                            "网络异常",
                            "温馨提示：",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (LoginException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            BiliBiliAccountDialog.this,
                            "用户名密码错误，请重新输入后再试",
                            "温馨提示：",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (CaptchaMismatchException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            BiliBiliAccountDialog.this,
                            "需要输入验证码，请联系本人",
                            "温馨提示：",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
    }

    private void saveLoginInfo() {
        if (null == bilibiliAccount) {
            JOptionPane.showMessageDialog(
                    this,
                    "尚未进行登录，请登录后重试",
                    "温馨提示：",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            if (null == jsonUtil) {
                jsonUtil = new JsonUtil();
            }
            if (null == gson) {
                gson = new Gson();
            }
            mainForm.getLocalDataBean().setBilibiliAccount(bilibiliAccount);
            jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(mainForm.getLocalDataBean()));
            JOptionPane.showMessageDialog(
                    this,
                    "登录信息保存成功",
                    "温馨提示：",
                    JOptionPane.INFORMATION_MESSAGE
            );
            mainForm.showOrHideLiveRoomPanel();
        }
    }

    private void removeLoginInfo() {
        BilibiliAccount bilibiliAccount = new BilibiliAccount("", "", 0L, 0L, 0L);
        mainForm.getLocalDataBean().setBilibiliAccount(bilibiliAccount);
        jsonUtil.saveDataToFile(LocalDataBean.class.getSimpleName(), gson.toJson(mainForm.getLocalDataBean()));
        JOptionPane.showMessageDialog(
                this,
                "登录信息删除成功",
                "温馨提示：",
                JOptionPane.INFORMATION_MESSAGE
        );
        mainForm.showOrHideLiveRoomPanel();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
