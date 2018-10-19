package com.dhr.simplepushstreamutil.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BilibiliAccountDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton 保存信息Button;
    private JButton 登录测试Button;
    private JTextField textField1;
    private JTextField textField2;

    public BilibiliAccountDialog() {
        setContentPane(contentPane);
        setSize(330, 200);
        setPreferredSize(new Dimension(330, 200));
        setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
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


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
