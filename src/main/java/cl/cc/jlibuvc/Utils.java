package cl.cc.jlibuvc;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

/**
 *
 * @author CyberCastle, Based in CanvasFrame.java, written by Samuel Audet (https://github.com/bytedeco/javacv)
 */
public class Utils {

    public static BufferedImage getImage(UVCController.UVCFrame frame) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        // raster in "BGR" order like OpenCV..
        int[] offsets = new int[]{2, 1, 0};

        ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        ComponentSampleModel csm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, frame.width(), frame.height(), 3, frame.width() * 3, offsets);
        WritableRaster wr = Raster.createWritableRaster(csm, null);

        BufferedImage bImage = new BufferedImage(cm, wr, false, null);
        DataBufferByte out = (DataBufferByte) bImage.getRaster().getDataBuffer();
        ByteBuffer buff = frame.data().capacity(frame.data_bytes()).asByteBuffer();

        copyByteBuffer(buff, frame.width() * 3, ByteBuffer.wrap(out.getData()), frame.width() * 3);
        return bImage;
    }

    private static void copyByteBuffer(ByteBuffer srcBuf, int srcStep, ByteBuffer dstBuf, int dstStep) {
        assert srcBuf != dstBuf;
        int channels = 3;
        int w = Math.min(srcStep, dstStep);
        int srcLine = srcBuf.position(), dstLine = dstBuf.position();
        byte[] buffer = new byte[channels];
        while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
            srcBuf.position(srcLine);
            dstBuf.position(dstLine);
            w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
            for (int x = 0; x < w; x += channels) {
                for (int z = 0; z < channels; z++) {
                    int in = srcBuf.get();
                    byte out;
                    out = (byte) in;
                    buffer[z] = out;
                }
                for (int z = channels - 1; z >= 0; z--) {
                    dstBuf.put(buffer[z]);
                }
            }
            srcLine += srcStep;
            dstLine += dstStep;
        }
    }
}
