package cl.cc.example;

import cl.cc.jlibuvc.UVCController;
import cl.cc.jlibuvc.UVCController.UVCContext;
import cl.cc.jlibuvc.UVCController.UVCDevice;
import cl.cc.jlibuvc.UVCController.UVCDeviceHandle;
import cl.cc.jlibuvc.UVCController.UVCFrame;
import cl.cc.jlibuvc.UVCController.UVCFrameCallback;
import cl.cc.jlibuvc.UVCController.UVCFrameFormat;
import cl.cc.jlibuvc.UVCController.UVCStreamCtrl;
import cl.cc.jlibuvc.Utils;
import org.bytedeco.javacpp.Pointer;

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
        System.out.println(UVCController.uvc_init(ctx, null));
        System.out.println(UVCController.uvc_find_device(ctx, dev, 0, 0, null)); //Detección Automática
        System.out.println(UVCController.uvc_open(dev, devh));

        // Obtención de la info de la cámara
//        PointerPointer<UVCController.UVCDeviceDescriptor> descp = new PointerPointer<>(new UVCController.UVCDeviceDescriptor());
//        System.out.println(UVCController.uvc_get_device_descriptor(dev, descp));
//        UVCDeviceDescriptor desc = descp.get(UVCController.UVCDeviceDescriptor.class);
//        System.out.println(desc.serialNumber().getString());
//        System.out.println(desc.product());
//        UVCController.uvc_free_device_descriptor(desc);
        
        int res = UVCController.uvc_get_stream_ctrl_format_size(devh, sctrl, UVCFrameFormat.UVC_FRAME_FORMAT_MJPEG, 1280, 720, 30);
        System.out.println(res);

        if (res != 0) {
            System.out.println("Configuración no Permitida");
            return;
        }

        System.out.println("Frame Index: " + sctrl.bFrameIndex());
        System.out.println("Max Video Frame Buffer Size: " + sctrl.dwMaxVideoFrameSize());
        System.out.println("Frame Interval: " + sctrl.dwFrameInterval());

        final DemoFrame windows = new DemoFrame("Sonria!!!!!! :-)");
        windows.setSize(1280, 720);
        UVCFrameCallback callback = new UVCFrameCallback() {

            @Override
            public void call(UVCFrame frame, Pointer user_ptr) {

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

        UVCController.uvc_set_ae_mode(devh, (byte) 3);
        UVCController.uvc_start_streaming(devh, sctrl, callback, new Pointer(), (byte) 0);

        //Thread.sleep(5000); /* 5 segundos de video */
        while (windows.isActive()) {
        }

        UVCController.uvc_stop_streaming(devh);
        windows.dispose();

        /* Cerramos la cámara */
        UVCController.uvc_close(devh);
        UVCController.uvc_unref_device(dev);
        UVCController.uvc_exit(ctx);
    }
}
