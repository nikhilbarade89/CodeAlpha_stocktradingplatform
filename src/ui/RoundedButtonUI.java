package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class RoundedButtonUI extends BasicButtonUI {

    private static final int RADIUS = 20;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton b = (AbstractButton) c;
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        paintBackground(g, b);
        super.paint(g, c);
    }

    private void paintBackground(Graphics g, AbstractButton b) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (b.getModel().isPressed())
            g2.setColor(b.getBackground().darker());
        else if (b.getModel().isRollover())
            g2.setColor(b.getBackground().brighter());
        else
            g2.setColor(b.getBackground());

        g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), RADIUS, RADIUS);
        g2.dispose();
    }
}