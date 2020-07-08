package cl.cc;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.bytedeco.javacpp.PointerPointer;

import cl.cc.jlibuvc.UVCController;
import cl.cc.jlibuvc.UVCController.UVCContext;
import cl.cc.jlibuvc.UVCController.UVCDevice;
import cl.cc.jlibuvc.UVCController.UVCDeviceDescriptor;
import cl.cc.jlibuvc.UVCController.UVCDeviceHandle;
import cl.cc.jlibuvc.UVCController.UVCFormatDesc;
import cl.cc.jlibuvc.UVCController.UVCFrame;
import cl.cc.jlibuvc.UVCController.UVCFrameCallback;
import cl.cc.jlibuvc.UVCController.UVCFrameFormat;
import cl.cc.jlibuvc.UVCController.UVCStreamCtrl;
import cl.cc.jlibuvc.Utils;
import cl.cc.jlibuvc.gui.ImagePanel;

/**
 *
 * @author CyberCastle
 */
public class Run {

    public static void main(String... arg) throws InterruptedException {

        UVCDevice dev = new UVCController.UVCDevice();
        UVCDeviceHandle devh = new UVCController.UVCDeviceHandle();
        UVCContext ctx = new UVCController.UVCContext();
        UVCStreamCtrl sctrl = new UVCController.UVCStreamCtrl();

        /* Abrimos la cámara */
        System.out.println("Init Result: " + UVCController.uvc_init(ctx, null));
        System.out.println("Find Result: " + UVCController.uvc_find_device(ctx, dev, 0, 0, null)); // Detección Automática
        System.out.println("Open Result: " + UVCController.uvc_open(dev, devh));

        // Obtención de la info de la cámara
        PointerPointer<UVCController.UVCDeviceDescriptor> descp = new PointerPointer<>(new UVCController.UVCDeviceDescriptor());
        System.out.println(UVCController.uvc_get_device_descriptor(dev, descp));
        UVCDeviceDescriptor desc = descp.get(UVCController.UVCDeviceDescriptor.class);
        System.out.println("Serial Number: " + desc.serialNumber().getString());
        System.out.println("Manufacturer: " + desc.manufacturer());
        System.out.println("Product Description: " + desc.product().getString());
        UVCController.uvc_free_device_descriptor(desc);

        // Obtención de la descripción del formato
        UVCFormatDesc frmtDesc = UVCController.uvc_get_format_descs(devh);

        System.out.println("Descriptor Subtype: " + frmtDesc.bDescriptorSubtype());
        System.out.println("Bits Per Pixel: " + frmtDesc.bBitsPerPixel());
        System.out.println("Aspect Ratio X: " + frmtDesc.bAspectRatioX());
        System.out.println("Aspect Ratio Y: " + frmtDesc.bAspectRatioY());
        System.out.println("Default Frame Index: " + frmtDesc.bDefaultFrameIndex());
        System.out.println("Frame Descriptors: " + frmtDesc.bNumFrameDescriptors());
        System.out.println("Interlace Flags: " + frmtDesc.bmInterlaceFlags());

        int res = UVCController.uvc_get_stream_ctrl_format_size(devh, sctrl, UVCFrameFormat.UVC_FRAME_FORMAT_MJPEG, 1280, 720, 30);
        System.out.println("Get Stream Control Result: " + res);

        if (res != 0) {
            System.out.println("Configuración no Permitida");
            return;
        }

        System.out.println("Frame Index: " + sctrl.bFrameIndex());
        System.out.println("Max Video Frame Buffer Size: " + sctrl.dwMaxVideoFrameSize());
        System.out.println("Frame Interval: " + sctrl.dwFrameInterval());
        System.out.println("Interface Number: " + sctrl.bInterfaceNumber());

        final DemoFrame windows = new DemoFrame("Sonria!!!!!! :-)");
        windows.setSize(1280, 720);
        UVCFrameCallback callback = new UVCFrameCallback() {

            @Override
            public void call(UVCFrame frame) {

                try {
                    UVCFrame cframe = UVCController.uvc_allocate_frame(frame.width() * frame.height());
                    int convres = UVCController.uvc_mjpeg2rgb(frame, cframe);
                    if (convres != 0) {
                        return;
                    }

                    windows.showImage(Utils.getImage(cframe));

                    UVCController.uvc_free_frame(cframe);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }

        };

        UVCController.uvc_set_focus_auto(devh, (byte) 0);
        UVCController.uvc_start_streaming(devh, sctrl, callback, (byte) 0);

        // Thread.sleep(5000); /* 5 segundos de video */
        while (windows.isActive()) {
        }

        UVCController.uvc_stop_streaming(devh);
        windows.dispose();

        /* Cerramos la cámara */
        UVCController.uvc_close(devh);
        UVCController.uvc_unref_device(dev);
        UVCController.uvc_exit(ctx);
    }

    static class DemoFrame extends javax.swing.JFrame {

        private static final long serialVersionUID = 7534330708637208215L;
        private JPanel imgPanel;

        DemoFrame(String title) {
            super(title);
            initComponents();
            this.setVisible(true);
            ((ImagePanel) this.imgPanel).createBuffer();
        }

        private void initComponents() {

            imgPanel = new ImagePanel();

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setName("DemoFrame"); // NOI18N
            getContentPane().setLayout(new GridLayout(1, 1));

            imgPanel.setName("imgPanel");

            GroupLayout imgPanelLayout = new GroupLayout(imgPanel);
            imgPanel.setLayout(imgPanelLayout);
            imgPanelLayout
                    .setHorizontalGroup(imgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 576, Short.MAX_VALUE));
            imgPanelLayout
                    .setVerticalGroup(imgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 445, Short.MAX_VALUE));

            getContentPane().add(imgPanel);

            pack();
        }

        public void showImage(Image img) {
            ((ImagePanel) this.imgPanel).renderImage(img);
        }

    }
}
