package cl.cc.example;

import cl.cc.jlibuvc.gui.ImagePanel;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author CyberCastle
 */
public class DemoFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 7534330708637208215L;

    /**
     * Creates new form DemoFrame
     */
    public DemoFrame() {
        initComponents();
    }

    DemoFrame(String title) {
        super(title);
        initComponents();
        this.setVisible(true);
        ((ImagePanel) this.imgPanel).createBuffer();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        imgPanel = new ImagePanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setName("DemoFrame"); // NOI18N
        getContentPane().setLayout(new GridLayout(1, 1));

        imgPanel.setName("imgPanel"); // NOI18N

        GroupLayout imgPanelLayout = new GroupLayout(imgPanel);
        imgPanel.setLayout(imgPanelLayout);
        imgPanelLayout.setHorizontalGroup(
            imgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );
        imgPanelLayout.setVerticalGroup(
            imgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );

        getContentPane().add(imgPanel);

        pack();
    }//GEN-END:initComponents

    public void showImage(Image img) {
        ((ImagePanel) this.imgPanel).renderImage(img);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel imgPanel;
    // End of variables declaration//GEN-END:variables
}
