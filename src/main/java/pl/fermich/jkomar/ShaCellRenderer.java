/*
 * $Id: ShaCellRenderer.java,v 1.2 2007/06/16 09:03:06 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;

class ShaCellRenderer extends JPanel implements ListCellRenderer {

    private JLabel fileLabel;
    private JProgressBar progres;
    private Color proBack;
    private Color proFore;
    private Color listBack;

    public ShaCellRenderer() {
        setLayout(new BorderLayout());
        setOpaque(true);
        fileLabel = null;
        progres = null;
        proBack = new Color(238, 238, 238);
        proFore = new Color(163, 184, 204);
        listBack = new Color(184, 207, 229);
    }


    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (fileLabel != null)
            remove(fileLabel);

        if (progres != null)
            remove(progres);

        fileLabel = ((FilePacketReader) value).getFileLabel();
        progres = ((FilePacketReader) value).getProgressBar();

        progres.setPreferredSize(new Dimension(100, 10));

        add(fileLabel, BorderLayout.WEST);    //JLabel
        add(new JLabel("    "), BorderLayout.CENTER);
        add(progres, BorderLayout.EAST);    //JProgressBar
        //add(Box.createHorizontalGlue());

        //setBorder(BorderFactory.createLineBorder(proFore.darker()));
        setBackground(isSelected ? listBack : proBack);
        progres.setBackground(isSelected ? listBack : proBack);
        progres.setForeground(isSelected ? proFore.darker() : proFore);
        return this;
    }
}
