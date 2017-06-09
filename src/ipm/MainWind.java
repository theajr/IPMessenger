package ipm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class MainWind extends JFrame implements Runnable, ActionListener, CaretListener, ComponentListener {

    public static JPanel left, down, btns;
    public static String nikName, myIP;

    public JScrollPane jspLst, jspMess;
    public static JTextArea message;
    public static JButton send, clear;
    public static JToggleButton lock;
    private ServerSocket SERVER;
    private Socket client;
    private ObjectInputStream iStream;
    public static JTabbedPane chatBox;
    private JMenu file, edit, info;
    private JMenuItem clean1, clean2, about, exM, remTab, remAllTab;
    private JCheckBoxMenuItem onTop;
    private Smileys smiliesP;
    private JSplitPane cnt;
    private JSplitPane hsp;
    public static JCheckBoxMenuItem smileSN, autoScroll;
    public static MatteBorder mbrd;

    public MainWind() {
        // setUndecorated(true);
        setTitle("IPMessenger v1.1");
        setLayout(new BorderLayout(1, 1));
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myIP = getIP();
        if (myIP == null || myIP.startsWith("127.")) {
            JOptionPane.showMessageDialog(this, "Check Your LAN Please...", "Connection Failed!", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        nikName = JOptionPane.showInputDialog(this, "Your Nick Name", myIP);
        if (nikName == null) {
            JOptionPane.showMessageDialog(this, "Don't u have name?", "Nickname fail", JOptionPane.ERROR_MESSAGE);

            //  System.exit(0);
        }
        left = new PalsList(myIP.substring(0, myIP.lastIndexOf(".")));
        mbrd = new MatteBorder(5, 1, 1, 1, Color.LIGHT_GRAY);
        chatBox = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        chatBox.setBorder(BorderFactory.createTitledBorder(mbrd, "History(s)"));
        CBLjointAction cbl=new CBLjointAction(chatBox,PalsList.list);
        chatBox.addChangeListener(cbl);
        left.setBorder(BorderFactory.createTitledBorder(mbrd, "Online"));
        hsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, chatBox);
        hsp.setDividerLocation(133);
       // chatBox.setEnabled(false);
        down = new JPanel(new BorderLayout(1, 1));
        down.add(jspMess = new JScrollPane(message = new JTextArea(3, 10), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        btns = new JPanel(new GridLayout(0, 1, 2, 2));
        message.addCaretListener(this);
        message.setBorder(BorderFactory.createTitledBorder(mbrd, "Your Reply/Message:"));
        message.requestFocusInWindow();
        message.setLineWrap(true);
        message.setAutoscrolls(true);
        btns.add(send = new JButton("Send "));
        send.setMnemonic(KeyEvent.VK_N);
        send.setEnabled(false);
        btns.add(clear = new JButton("Clear"));
        clear.setMnemonic(KeyEvent.VK_C);
        clear.addActionListener(this);
        clear.setEnabled(false);
        btns.add(lock = new JToggleButton("Lock "));
        lock.setMnemonic(KeyEvent.VK_L);

        lock.addActionListener(this);
        down.add(btns, BorderLayout.EAST);

        smiliesP = new Smileys();

        cnt = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, hsp, smiliesP);
        // smiliesP.setBorder(BorderFactory.createTitledBorder(mbrd, ""));

        smiliesP.setPreferredSize(new Dimension(222, 22));
        add(cnt, BorderLayout.CENTER);
        add(down, BorderLayout.SOUTH);
        JMenuBar jmb = new JMenuBar();
        file = new JMenu("File    ");
        file.setMnemonic('F');
        edit = new JMenu("Edit    ");
        edit.setMnemonic('E');
        info = new JMenu("Info    ");
        info.setMnemonic('o');
        smileSN = new JCheckBoxMenuItem("Smileys", true);
        autoScroll = new JCheckBoxMenuItem("Auto Scroll", true);
        autoScroll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
        smileSN.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        file.add(smileSN);
        onTop = new JCheckBoxMenuItem("Always On Top", true);
        onTop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
        onTop.addActionListener(this);
        file.add(onTop);
        file.add(autoScroll);

        exM = new JMenuItem("Exit    ");

        exM.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        file.add(exM);
        remTab = new JMenuItem("Remove chat Tab  ");
        remTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        remTab.addActionListener(this);
        remAllTab = new JMenuItem("Remove All chat Tab  ");
        remAllTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        remAllTab.addActionListener(this);
        clean1 = new JMenuItem("Clean History   ");
        clean1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.SHIFT_MASK));
        clean2 = new JMenuItem("Clean All History's  ");
        clean2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.SHIFT_MASK));

        edit.add(clean1);
        edit.add(clean2);
        edit.addSeparator();
        edit.add(remTab);
        edit.add(remAllTab);

        about = new JMenuItem("About");
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
        info.add(about);

        exM.addActionListener(this);
        clean1.addActionListener(this);
        clean2.addActionListener(this);
        about.addActionListener(this);
        smileSN.addActionListener(this);
        jmb.add(file);
        jmb.add(edit);
        jmb.add(info);
        addComponentListener(this);
        setJMenuBar(jmb);
        setSize(1200, 500);

        setVisible(true);
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        } finally {
            SwingUtilities.updateComponentTreeUI(this);

        }
        new Thread(this).start();
    }

    public static void main(String[] args) throws UnknownHostException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MainWind mainWind = new MainWind();
            }
        });
    }

    @Override
    public void run() {
        try {
            SERVER = new ServerSocket(1596);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Another IPMessenger is Running..", "Already Running..", JOptionPane.ERROR_MESSAGE);
            System.exit(0);

        }

        while (true) {
            try {
                client = SERVER.accept();
                ClientMan cm = new ClientMan(client);
                new Thread(cm).start();

            } catch (IOException e) {
                //  e.printStackTrace();
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent src = (JComponent) e.getSource();
        if (src == clear) {
            message.setText("");
        } else if (src == lock) {
            message.setEditable(!lock.isSelected());
            message.setBackground(lock.isSelected() ? Color.GRAY : Color.WHITE);

        } else if (src == remTab) {
            try {
                chatBox.remove(chatBox.getSelectedIndex());
            } catch (Exception eo) {
            }
        } else if (src == remAllTab) {
            try {
                chatBox.removeAll();
            } catch (Exception ej) {
            }
        } else if (src == onTop) {
            this.setAlwaysOnTop(onTop.isSelected());
        } else if (src == exM) {
            System.exit(0);
        } else if (src == clean1) {
            try {
                int sHi = chatBox.getSelectedIndex();
                if (sHi != -1) {
                    Component cmp = chatBox.getComponentAt(sHi);
                    JViewport jvp = ((JScrollPane) cmp).getViewport();
                    JLabel now = (JLabel) jvp.getView();
                    now.setText("<html></html>");
                }
            } catch (Exception eppp) {
            }

        } else if (src == clean2) {
            try {
                for (int i = 0; i < chatBox.getTabCount(); i++) {
                    Component cmp = chatBox.getComponentAt(i);
                    JViewport jvp = ((JScrollPane) cmp).getViewport();
                    JLabel now = (JLabel) jvp.getView();
                    now.setText("<html></html>");

                }
            } catch (Exception epp) {
            }

        } else if (src == smileSN) {
            smiliesP.setVisible(smileSN.isSelected());
            cnt.setDividerLocation(getWidth() - 300);

        } else if (src == about) {
            JDialog abd = new JDialog(this, "The Designer");
            abd.add(new JLabel("<html><body style=\"padding:30px;border:13px solid white;border-radius:30px;\">"
                    + "<h1 color=red>IPMessenger v1.1</h1>"
                    + "<h2 color=green>AJay Reddy Pathuri</h2>"
                    + "<p>ajayreddy.pathuri@facebook.com</p>"
                    + "<h3>Date:05<sup>th</sup> November, 2014</h3>"
                    + "</body></html>"));
            abd.pack();
            abd.setResizable(false);
            abd.setVisible(true);

        }

    }

    @Override
    public void caretUpdate(CaretEvent e) {
        send.setEnabled(message.getText().length() != 0);
        clear.setEnabled(message.getText().length() != 0);

    }

    private static String getIP() {
        try {
            Process p = Runtime.getRuntime().exec("ifconfig");
            Scanner s = new Scanner(p.getInputStream());
            while (s.hasNextLine()) {
                String lin = s.nextLine();
                if (lin.contains("inet addr:")) {
                    int start = lin.indexOf("inet addr:");
                    return lin.substring(start + 10, lin.indexOf(" ", start + 10));
                }

            }
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public void componentResized(ComponentEvent ce) {

        cnt.setDividerLocation(getWidth() - 300);
        hsp.setDividerLocation(150);
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }

    public static String getFormattedMessage(String m) {
        String smilies[] = new String[]{"bigsmile.png", "stressed.png", "beaten.png", "hi.png", "easymoney.png", "smoking.png", "cool.png", "question.png", "kissed.png", "whistling.png", "study.png", "facepalm.png", "sweat.png", "wornout.png", "music.png", "hungry.png", "happy.png", "sorry.png", "snotty.png", "surprise.png", "call.png", "wink.png", "hypnotic.png", "badly.png", "hug.png", "shock.png", "singing.png", "cocktail.png", "expressionless.png", "yawn.png", "sad.png", "disgust.png", "smile.png", "beuptonogood.png", "giggle.png", "struggle.png", "pirate.png", "scared.png", "crying.png", "beer.png", "sleep.png", "monocle.png", "woo.png", "movie.png", "sweetangel.png", "ninja.png", "angry.png", "waiting.png", "furious.png", "despair.png", "cold.png", "frown.png", "impish.png", "laugh.png", "idea.png", "thumbsdown.png", "pudently.png", "nerd.png", "lol.png", "hysterical.png", "boring.png", "exclamation.png", "aggressive.png", "disappointment.png", "stop.png", "rose.png", "thinking.png", "party.png", "adore.png", "thumbsup.png", "cry.png", "kiss.png", "coffee.png", "dizzy.png", "frustrated.png", "rage.png", "satisfied.png", "stars.png", "sick.png", "bomb.png"};
        for (int i = 0; i < smilies.length; i++) {
            m = m.replaceAll("\\[" + i + "\\]", "<img style=\"line-height:30px;width:30px;vertical-align:middle;\" src=\"" + MainWind.class.getResource("Smileys/" + smilies[i]) + "\"></img>");
        }
        return m;
    }

}
