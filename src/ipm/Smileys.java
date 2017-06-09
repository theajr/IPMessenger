package ipm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Smileys extends JPanel implements ActionListener {

    public Smileys() {
        setLayout(new BorderLayout(3, 3));

        JPanel tp = new JPanel(new GridLayout(0, 3));
        tp.setBorder(BorderFactory.createTitledBorder(MainWind.mbrd, "Smileys"));

        tp.setBackground(Color.WHITE);
        String smilies[] = new String[]{"bigsmile.png", "stressed.png", "beaten.png", "hi.png", "easymoney.png", "smoking.png", "cool.png", "question.png", "kissed.png", "whistling.png", "study.png", "facepalm.png", "sweat.png", "wornout.png", "music.png", "hungry.png", "happy.png", "sorry.png", "snotty.png", "surprise.png", "call.png", "wink.png", "hypnotic.png", "badly.png", "hug.png", "shock.png", "singing.png", "cocktail.png", "expressionless.png", "yawn.png", "sad.png", "disgust.png", "smile.png", "beuptonogood.png", "giggle.png", "struggle.png", "pirate.png", "scared.png", "crying.png", "beer.png", "sleep.png", "monocle.png", "woo.png", "movie.png", "sweetangel.png", "ninja.png", "angry.png", "waiting.png", "furious.png", "despair.png", "cold.png", "frown.png", "impish.png", "laugh.png", "idea.png", "thumbsdown.png", "pudently.png", "nerd.png", "lol.png", "hysterical.png", "boring.png", "exclamation.png", "aggressive.png", "disappointment.png", "stop.png", "rose.png", "thinking.png", "party.png", "adore.png", "thumbsup.png", "cry.png", "kiss.png", "coffee.png", "dizzy.png", "frustrated.png", "rage.png", "satisfied.png", "stars.png", "sick.png", "bomb.png"};
        for (int i = 0; i < smilies.length; i++) {
            JButton pb = new JButton("<html><img src=\"" + MainWind.class.getResource("Smileys/" + smilies[i]) + "\"></img></html>");
            pb.setBorderPainted(false);
            pb.setToolTipText(smilies[i].split("\\.")[0] + "#[" + i + "]");
            pb.setBackground(Color.white);
            pb.addActionListener(this);
            tp.add(pb);
        }
        setPreferredSize(new Dimension(222, 2));

        add(new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selSm = e.getActionCommand().split("\"")[1].split("\"")[0];
        String snm = selSm.substring(selSm.lastIndexOf("/") + 1);
        MainWind.message.append(((JComponent) e.getSource()).getToolTipText().split("#")[1]);
    }

}
