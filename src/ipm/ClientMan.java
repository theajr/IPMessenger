package ipm;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import sun.misc.BASE64Decoder;

public class ClientMan implements Runnable {

    private Socket SOCKET;
    private ObjectInputStream iStream;
    private JTabbedPane chatty;
    private ObjectOutputStream oStream;
    private BASE64Decoder bsd;

    public ClientMan(Socket sock) {
        SOCKET = sock;
        chatty = MainWind.chatBox;
        try {
            iStream = new ObjectInputStream(SOCKET.getInputStream());
            //   oStream = new ObjectOutputStream(SOCKET.getOutputStream());
        } catch (IOException e) {
            //      e.printStackTrace();

        }
    }

    @Override
    public void run() {
        while (SOCKET != null) {
            try {
                String recieved = (String) iStream.readObject(); // FROM#TO#NIKNAME#MESSAGE#
                //  System.out.println("encoded=" + recieved);

                bsd = new BASE64Decoder();
                recieved = new String(bsd.decodeBuffer(recieved));
                String[] param = recieved.split("#1596#");
                //  System.out.println("decoded=" + recieved);

                if (param.length >= 3) {
                    String senderIP = param[0];
                    String sentTo = param[1];
                    String nickName = param[2];
                    String message = MainWind.getFormattedMessage(param[3]);

                    String time = param[4];
                    if (sentTo.equals(MainWind.myIP)) {
                        int index = chatty.indexOfTab(senderIP);
                        if (index == -1) {

                            JLabel hist = new JLabel("<html>"
                                    + "<p style=\""
                                    + "text-align:left;"
                                    + "border:1px solid purple;"
                                    + "padding:10px;"
                                    + "background:#95FF90;"
                                    + "width:\"" + (chatty.getWidth() - 300) + "\""
                                    + "word-wrap:break-word;\">"
                                    + "<i color=orange>" + nickName + "</i>:-"
                                    + message + "<br>"
                                    + "<i align=right>" + time + "<i>"
                                    + "</p><br>"
                                    + "</html>");

                            JScrollPane histSP = new JScrollPane(hist, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                            chatty.add(senderIP, histSP);

                        } else {

                            final Component cmp = chatty.getComponentAt(chatty.indexOfTab(senderIP));
                            JViewport jvp = ((JScrollPane) cmp).getViewport();
                            final JLabel now = (JLabel) jvp.getView();

                            now.setText("<html>" + now.getText().substring(6, now.getText().length() - 7)
                                    + "<p style=\"text-align:left;border:1px solid green;padding:10px;background:#95FF90;width:" + (cmp.getWidth() - 300) + "px;word-wrap:break-word;;\">"
                                    + "<i color=orange>" + nickName + "</i>:-"
                                    + message + "<br>"
                                    + "<i align=right color=navy>"
                                    + time + "<i>"
                                    + "</p><br>"
                                    + "</html>");

                            ((JScrollPane) cmp).getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

                                @Override
                                public void adjustmentValueChanged(AdjustmentEvent ae) {
                                    if (MainWind.autoScroll.isSelected()) {
                                        ((JScrollPane) cmp).getVerticalScrollBar().setValue(now.getText().length() * 100);
                                    }
                                }
                            });

                        }
                        if (index != chatty.getSelectedIndex()) {
                            UIManager.put("OptionPane.yesButtonText", "Show Chat");
                            UIManager.put("OptionPane.noButtonText", "Ignore");

                            UIManager.put("OptionPane.cancelButtonText", "(^!^)");
                            int varr = JOptionPane.showConfirmDialog(chatty, "New message from: " + senderIP + "\nDo you wanna see?", "New Message!", JOptionPane.INFORMATION_MESSAGE);
                            if (varr == 0) {
                                PalsList.list.setSelectedValue(chatty.getTitleAt(chatty.getSelectedIndex()), true);
                            }

                        }

                    } else {
                        //System.out.println(senderIP + " says " + message);

                    }
                }

            } catch (Exception e) {
                SOCKET = null;
            }

        }
    }

}
