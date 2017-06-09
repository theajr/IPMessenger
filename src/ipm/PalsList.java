package ipm;

import static ipm.PalsList.list;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PalsList extends JPanel implements Runnable, ListSelectionListener {

    public String IPSeries;
    private DefaultListModel listmodel;
    public static JList list;
    private JScrollPane jspLst;
    private Socket selSock;

    public PalsList(String ipSeries) {
        super(new BorderLayout());

        this.IPSeries = ipSeries;
        listmodel = new DefaultListModel();
        list = new JList(listmodel);
        jspLst = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jspLst.setPreferredSize(new Dimension(125, 55));
        list.addListSelectionListener(this);
        add(jspLst, BorderLayout.CENTER);
        new Thread(this).start();
    }

    public static JList getListt() {
        return list;
    }

    @Override
    public void run() {

        while (true) {

            for (int i = 2; i < 256; i++) {

                String ip = IPSeries + "." + i;
                IPC cipc = new IPC(ip, listmodel, list);
                new Thread(cipc).start();
            }

        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selected = (String) list.getSelectedValue();
            try {
                if (!selected.startsWith("127.")) {
                    selSock = new Socket(selected, 1596);
                    SendMessage sms = new SendMessage(selSock);
                    new Thread(sms).start();
                }

            } catch (IOException ex) {
            }
        }
    }
}

class IPC implements Runnable {

    private final JList JLI;
    private final DefaultListModel DFL;
    private final String ipN;

    public IPC(String ipn, DefaultListModel dfl, JList jli) {
        ipN = ipn;
        DFL = dfl;
        JLI = jli;
    }

    @Override
    public void run() {
        try {
            if (InetAddress.getByName(ipN).isReachable(10)) {
                try {
                    Socket sock = new Socket(ipN, 1596);
                    sock.close();
                    if (!DFL.contains(ipN)) {
                        DFL.addElement(ipN);
                    }
                } catch (Exception e) {
                    if (DFL.contains(ipN)) {
                        if (!list.getSelectedValue().equals(ipN)) {
                            DFL.removeElement(ipN);
                            /*  if (MainWind.chatBox.indexOfTab(ipN) != -1) {
                             MainWind.chatBox.remove(MainWind.chatBox.indexOfTab(ipN));
                             }*/
                        }
                    }

                }
            } else {
                if (DFL.contains(ipN)) {
                    if (!list.getSelectedValue().equals(ipN)) {
                        DFL.removeElement(ipN);
                        /*  if (MainWind.chatBox.indexOfTab(ipN) != -1) {
                         MainWind.chatBox.remove(MainWind.chatBox.indexOfTab(ipN));
                         }*/
                    }
                }

            }
        } catch (Exception e) {
            if (DFL.contains(ipN)) {
                if (!list.getSelectedValue().equals(ipN)) {
                    DFL.removeElement(ipN);
                    /*  if (MainWind.chatBox.indexOfTab(ipN) != -1) {
                     MainWind.chatBox.remove(MainWind.chatBox.indexOfTab(ipN));
                     }*/
                }
            }

        }

    }

}
