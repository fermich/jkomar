/*
 * $Id: ChatPanel.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class ChatPanel extends JPanel {


    private JTextArea talkArea;
    private JTextArea msgArea;
    private HistoryBuff histBuff;
    private ChatInputBuffer cib;


    public void addMessage(String m) {
        talkArea.append("> " + m + "\n\n");
        talkArea.setCaretPosition(talkArea.getDocument().getLength());
    }


    public ChatPanel(ChatInputBuffer ciB) {
        histBuff = new HistoryBuff(100);

        cib = ciB;

        talkArea = new JTextArea("", 6, 20);
        talkArea.setLineWrap(true);
        msgArea = new JTextArea("", 2, 20);
        msgArea.setLineWrap(true);

        //talkArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        talkArea.setEditable(false);

        msgArea.addKeyListener(new KeyListener() {
                                   public void keyPressed(KeyEvent e) {
                                       if (KeyEvent.VK_UP == e.getKeyCode()) {
                                           histBuff.prevQuery();
                                           msgArea.setText(histBuff.getHistory());
                                       } else if (KeyEvent.VK_DOWN == e.getKeyCode()) {
                                           histBuff.forwQuery();
                                           msgArea.setText(histBuff.getHistory());
                                       } else if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                                           String msg = msgArea.getText();
                                           talkArea.append("< " + msg + "\n\n");
                                           talkArea.setCaretPosition(talkArea.getDocument().getLength());
                                           cib.addMessage(msg);
                                           histBuff.toHistory(msg);
                                       }
                                   }

                                   public void keyReleased(KeyEvent e) {
                                       if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                                           msgArea.setText("");
                                       }
                                   }

                                   public void keyTyped(KeyEvent e) {
                                   }
                               }
        );

        BorderLayout bl = new BorderLayout();
        bl.setVgap(3);

        setLayout(bl);
        JScrollPane tasp = new JScrollPane(talkArea);
        tasp.setWheelScrollingEnabled(true);
        JScrollPane masp = new JScrollPane(msgArea);
        masp.setWheelScrollingEnabled(true);

        add(tasp, BorderLayout.CENTER);
        add(masp, BorderLayout.SOUTH);
    }
}
