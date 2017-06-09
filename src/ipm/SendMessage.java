package ipm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import sun.misc.BASE64Encoder;

class SendMessage implements Runnable, ActionListener {

    private Socket rec;
    private ObjectOutputStream recOS = null;
    private final JTabbedPane chatty2;
    private BASE64Encoder bs;

    SendMessage(Socket selSock) {
        MainWind.send.addActionListener(this);

        rec = selSock;
        try {
            recOS = new ObjectOutputStream(rec.getOutputStream());
        } catch (IOException ex) {

        }
        chatty2 = MainWind.chatBox;

    }

    @Override
    public void run() {
        int index = chatty2.indexOfTab(rec.getInetAddress().getHostAddress());
        if (index == -1) {
            //  JTextArea tjta = new JTextArea();
            JLabel tjta = new JLabel("<html></html>");

            JScrollPane tjsp = new JScrollPane(tjta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            chatty2.add(rec.getInetAddress().getHostAddress(), tjsp);

        }
        chatty2.setSelectedIndex(chatty2.indexOfTab(rec.getInetAddress().getHostAddress()));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == MainWind.send) {
            try {

                bs = new BASE64Encoder();

                String sMsg = MainWind.message.getText();

                if (sMsg.length() != 0) {
                    Date time = new Date();
                    String enc = bs.encode((MainWind.myIP + "#1596#" + rec.getInetAddress().getHostAddress() + "#1596#" + MainWind.nikName + "#1596#" + sMsg + "#1596#" + time).getBytes());
                    recOS.writeObject(enc);
                    recOS.flush();
                    Component cmp = MainWind.chatBox.getComponentAt(MainWind.chatBox.indexOfTab(rec.getInetAddress().getHostAddress()));
                    JViewport jvp = ((JScrollPane) cmp).getViewport();

                    JLabel now = (JLabel) jvp.getView();
                    now.setText("<html>" + now.getText().substring(6, now.getText().length() - 7)
                            + "<p style=\"text-align:right;border:1px solid red;padding:10px;background:#F7D679;width:" + (cmp.getWidth() - 300) + "px;word-wrap:break-word;\">"
                            + "<i font-size=30 color=purple>" + MainWind.nikName + "</i>:-"
                            + "" + MainWind.getFormattedMessage(sMsg) + "<br>"
                            + "<i align=right color=navy>"
                            + time + "<i>"
                            + "</p><br></html>");
                    MainWind.message.setText("");
                }
            } catch (IOException ex) {

            }
        }
    }
}
