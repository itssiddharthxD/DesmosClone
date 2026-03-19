package com.desmosclone.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class GraphPanel extends JPanel {
	
    private String equation = "";
    private double scale = 50.0; // Pixels per unit
    private double offsetX = 0.0;
    private double offsetY = 0.0;
    
    private Point dragStart = null;

    public GraphPanel() {
        setBackground(Color.WHITE);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    double dx = e.getX() - dragStart.x;
                    double dy = e.getY() - dragStart.y;
                    offsetX += dx;
                    offsetY += dy;
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });

        // --- Zooming (Mouse Wheel) ---
        addMouseWheelListener(e -> {
            double zoomFactor = 1.1;
            if (e.getWheelRotation() < 0) {
                scale *= zoomFactor; // Zoom in
            } else {
                scale /= zoomFactor; // Zoom out
            }
            repaint();
        });
    }

    public void setEquation(String eq) {
        this.equation = eq;
        repaint();
    }

    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enabling anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Calculating the origin (0,0)
        double originX = width / 2.0 + offsetX;
        double originY = height / 2.0 + offsetY;

        // --- Draw Grid and Numbers ---
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();

        // X-axis grid and labels
        for (int i = 0; originX + i * scale < width; i++) {
            int x = (int)(originX + i * scale);
            g2d.setColor(new Color(230, 230, 230)); // Light gray grid
            g2d.drawLine(x, 0, x, height);
            
            g2d.setColor(Color.DARK_GRAY); // Dark gray text
            if (i != 0) g2d.drawString(String.valueOf(i), x - fm.stringWidth(String.valueOf(i))/2, (int)originY + 15);
        }
        for (int i = 1; originX - i * scale > 0; i++) {
            int x = (int)(originX - i * scale);
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawLine(x, 0, x, height);
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(String.valueOf(-i), x - fm.stringWidth(String.valueOf(-i))/2, (int)originY + 15);
        }

        // Y-axis grid and labels (Positive and Negative)
        for (int i = 1; originY + i * scale < height; i++) {
            int y = (int)(originY + i * scale);
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawLine(0, y, width, y);
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(String.valueOf(-i), (int)originX + 5, y + fm.getAscent()/2);
        }
        for (int i = 1; originY - i * scale > 0; i++) {
            int y = (int)(originY - i * scale);
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawLine(0, y, width, y);
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(String.valueOf(i), (int)originX + 5, y + fm.getAscent()/2);
        }

        // --- Draw Axes ---
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine((int) originX, 0, (int) originX, height); // Y-axis
        g2d.drawLine(0, (int) originY, width, (int) originY); // X-axis
        
        // Drawing the origin
        g2d.drawString("0", (int)originX + 5, (int)originY + 15);

        // Drawing the Function
        if (equation == null || equation.trim().isEmpty()) return;

        g2d.setColor(new Color(41, 128, 185)); // Desmos wala blue
        g2d.setStroke(new BasicStroke(2.5f));

        Integer prevScreenY = null;

        // Iterate pixel by pixel across the screen width
        for (int screenX = 0; screenX < width; screenX++) {
        	
            double mathX = (screenX - originX) / scale;

            try {
                // Evaluating the equation
                double mathY = MathEvaluator.evaluate(equation, mathX);

                if (Double.isNaN(mathY) || Double.isInfinite(mathY)) {
                    prevScreenY = null; 
                    continue;
                }

                // Convert mathematical Y coordinate back to screen Y coordinate
                int screenY = (int) (originY - (mathY * scale));

                // Draw a line from the previous point to the current point
                if (prevScreenY != null) {
                    // Prevent drawing massive vertical lines across asymptotes (like in tan(x))
                    if (Math.abs(screenY - prevScreenY) < height) {
                        g2d.drawLine(screenX - 1, prevScreenY, screenX, screenY);
                    }
                }
                prevScreenY = screenY;

            } catch (Exception ex) {
                
                break;
            }
        }
    }
}