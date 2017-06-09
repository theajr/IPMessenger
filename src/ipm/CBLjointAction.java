/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ipm;

import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


class CBLjointAction implements ChangeListener{
    private final JList lis;
    private final JTabbedPane cbx;
   
    
    public CBLjointAction(JTabbedPane chatBox, JList list) {
        cbx=chatBox;
        lis=list;
        cbx.addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        lis.setSelectedValue(cbx.getTitleAt(cbx.getSelectedIndex()), true);
    }
    
}
