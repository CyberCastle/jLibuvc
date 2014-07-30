package cl.cc.jlibuvc.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;

/**
 *
 * @author CyberCastle, Based in CanvasFrame.java, written by Samuel Audet (https://github.com/bytedeco/javacv)

 Make sure OpenGL or XRender is enabled to get low latency, something like
 export _JAVA_OPTIONS=-Dsun.java2d.opengl=True export
 _JAVA_OPTIONS=-Dsun.java2d.xrender=True
 */
public class ImagePanel extends JPanel {

    private static final long serialVersionUID = -7523388842376668141L;

    private Canvas canvas = null;
    private double initialScale = 1.0;
    private Color color = null;
    private Image image = null;
    private BufferedImage buffer = null;

    public ImagePanel() {
        init();
    }

    private void init() {
        canvas = new Canvas() {
            private static final long serialVersionUID = -4832292154872445719L;

            @Override
            public void update(Graphics g) {
                paint(g);
            }

            @Override
            public void paint(Graphics g) {
                // Calling BufferStrategy.show() here sometimes throws
                // NullPointerException or IllegalStateException,
                // but otherwise seems to work fine.
                try {
                    BufferStrategy strategy = canvas.getBufferStrategy();
                    do {
                        do {
                            g = strategy.getDrawGraphics();
                            if (color != null) {
                                g.setColor(color);
                                g.fillRect(0, 0, getWidth(), getHeight());
                            }
                            if (image != null) {
                                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                            }
                            if (buffer != null) {
                                g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
                            }
                            g.dispose();
                        } while (strategy.contentsRestored());
                        strategy.show();
                    } while (strategy.contentsLost());
                } catch (NullPointerException | IllegalStateException e) {
                }
            }
        };
        canvas.setSize(getSize());
        add(canvas);
        //canvas.createBufferStrategy(2);
        //canvas.setIgnoreRepaint(true);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Dimension getCanvasSize() {
        return canvas.getSize();
    }

    public void setCanvasSize(final int width, final int height) {
        Dimension d = getCanvasSize();
        if (d.width == width && d.height == height) {
            return;
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // There is apparently a bug in Java code for Linux, and what happens goes like this:
                // 1. Canvas gets resized, checks the visible area (has not changed) and updates
                // BufferStrategy with the same size. 2. pack() resizes the frame and changes
                // the visible area 3. We call Canvas.setSize() with different dimensions, to make
                // it check the visible area and reallocate the BufferStrategy almost correctly
                // 4. Finally, we resize the Canvas to the desired size... phew!
                canvas.setSize(width, height);
                canvas.setSize(width + 1, height + 1);
                canvas.setSize(width, height);
            }
        };

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException ex) {
            }
        }
    }

    public double getCanvasScale() {
        return initialScale;
    }

    public void setCanvasScale(double initialScale) {
        this.initialScale = initialScale;
    }

    public Graphics2D createGraphics() {
        if (buffer == null || buffer.getWidth() != canvas.getWidth() || buffer.getHeight() != canvas.getHeight()) {
            BufferedImage newbuffer = canvas.getGraphicsConfiguration().createCompatibleImage(
                    canvas.getWidth(), canvas.getHeight(), Transparency.TRANSLUCENT);
            if (buffer != null) {
                Graphics g = newbuffer.getGraphics();
                g.drawImage(buffer, 0, 0, null);
                g.dispose();
            }
            buffer = newbuffer;
        }
        return buffer.createGraphics();
    }

    public void releaseGraphics(Graphics2D g) {
        g.dispose();
        canvas.paint(null);
    }

    public void showColor(Color color) {
        this.color = color;
        this.image = null;
        canvas.paint(null);
    }

    public void renderImage(Image image) {
        if (image == null) {
            return;
        }
        int w = (int) Math.round(image.getWidth(null) * initialScale);
        int h = (int) Math.round(image.getHeight(null) * initialScale);
        setCanvasSize(w, h);
        this.color = null;
        this.image = image;
        canvas.paint(null);
    }
    
    public void createBuffer() {
        this.canvas.createBufferStrategy(2);
    }
}
