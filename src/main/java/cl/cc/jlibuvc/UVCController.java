package cl.cc.jlibuvc;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FunctionPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.annotation.ByPtrPtr;
import org.bytedeco.javacpp.annotation.ByVal;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Const;
import org.bytedeco.javacpp.annotation.MemberGetter;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Opaque;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

/**
 *
 * @author CyberCastle
 */
@Properties({ @Platform(include = { "libuvc/libuvc.h" }, link = "uvc") })
public class UVCController implements InfoMapper {

    public static interface UVCError {

        public static final int UVC_SUCCESS = 0;
        public static final int UVC_ERROR_IO = -1;
        public static final int UVC_ERROR_INVALID_PARAM = -2;
        public static final int UVC_ERROR_ACCESS = -3;
        public static final int UVC_ERROR_NO_DEVICE = -4;
        public static final int UVC_ERROR_NOT_FOUND = -5;
        public static final int UVC_ERROR_BUSY = -6;
        public static final int UVC_ERROR_TIMEOUT = -7;
        public static final int UVC_ERROR_OVERFLOW = -8;
        public static final int UVC_ERROR_PIPE = -9;
        public static final int UVC_ERROR_INTERRUPTED = -10;
        public static final int UVC_ERROR_NO_MEM = -11;
        public static final int UVC_ERROR_NOT_SUPPORTED = -12;
        public static final int UVC_ERROR_INVALID_DEVICE = -50;
        public static final int UVC_ERROR_INVALID_MODE = -51;
        public static final int UVC_ERROR_CALLBACK_EXISTS = -52;
        public static final int UVC_ERROR_OTHER = -99;
    };

    public static interface UVCFrameFormat {

        public static final int UVC_FRAME_FORMAT_UNKNOWN = 0;
        public static final int UVC_FRAME_FORMAT_ANY = 0;
        public static final int UVC_FRAME_FORMAT_UNCOMPRESSED = 1;
        public static final int UVC_FRAME_FORMAT_COMPRESSED = 2;
        public static final int UVC_FRAME_FORMAT_YUYV = 3;
        public static final int UVC_FRAME_FORMAT_UYVY = 4;
        public static final int UVC_FRAME_FORMAT_RGB = 5;
        public static final int UVC_FRAME_FORMAT_BGR = 6;
        public static final int UVC_FRAME_FORMAT_MJPEG = 7;
        public static final int UVC_FRAME_FORMAT_GRAY8 = 8;
        public static final int UVC_FRAME_FORMAT_BY8 = 9;
        public static final int UVC_FRAME_FORMAT_COUNT = 10;
    };

    /**
     * VideoStreaming interface descriptor subtype (A.6)
     */
    public static interface UVCVideoStreamingDescSubtype {

        public static final int UVC_VS_UNDEFINED = 0x00;
        public static final int UVC_VS_INPUT_HEADER = 0x01;
        public static final int UVC_VS_OUTPUT_HEADER = 0x02;
        public static final int UVC_VS_STILL_IMAGE_FRAME = 0x03;
        public static final int UVC_VS_FORMAT_UNCOMPRESSED = 0x04;
        public static final int UVC_VS_FRAME_UNCOMPRESSED = 0x05;
        public static final int UVC_VS_FORMAT_MJPEG = 0x06;
        public static final int UVC_VS_FRAME_MJPEG = 0x07;
        public static final int UVC_VS_FORMAT_MPEG2TS = 0x0a;
        public static final int UVC_VS_FORMAT_DV = 0x0c;
        public static final int UVC_VS_COLORFORMAT = 0x0d;
        public static final int UVC_VS_FORMAT_FRAME_BASED = 0x10;
        public static final int UVC_VS_FRAME_FRAME_BASED = 0x11;
        public static final int UVC_VS_FORMAT_STREAM_BASED = 0x12;
    };

    public static interface UVCReqCode {

        public static final int UVC_RC_UNDEFINED = 0x00;
        public static final int UVC_SET_CUR = 0x01;
        public static final int UVC_GET_CUR = 0x81;
        public static final int UVC_GET_MIN = 0x82;
        public static final int UVC_GET_MAX = 0x83;
        public static final int UVC_GET_RES = 0x84;
        public static final int UVC_GET_LEN = 0x85;
        public static final int UVC_GET_INFO = 0x86;
        public static final int UVC_GET_DEF = 0x87;
    };

    public static interface UVCStatusClass {

        public static final int UVC_STATUS_CLASS_CONTROL = 0x10;
        public static final int UVC_STATUS_CLASS_CONTROL_CAMERA = 0x11;
        public static final int UVC_STATUS_CLASS_CONTROL_PROCESSING = 0x12;
    };

    public static interface UVCStatusAttribute {

        public static final int UVC_STATUS_ATTRIBUTE_VALUE_CHANGE = 0x00;
        public static final int UVC_STATUS_ATTRIBUTE_INFO_CHANGE = 0x01;
        public static final int UVC_STATUS_ATTRIBUTE_FAILURE_CHANGE = 0x02;
        public static final int UVC_STATUS_ATTRIBUTE_UNKNOWN = 0xff;
    };

    public static interface UVCAutoExposure {

        public final static byte UVC_AUTO_EXPOSURE_MODE_MANUAL = 1;
        public final static byte UVC_AUTO_EXPOSURE_MODE_AUTO = 2;
        public final static byte UVC_AUTO_EXPOSURE_MODE_SHUTTER_PRIORITY = 4;
        public final static byte UVC_AUTO_EXPOSURE_MODE_APERTURE_PRIORITY = 8;
    };

    static {
        Loader.load();
    }

    @Override
    public void map(InfoMap infoMap) {
        // @formatter:off
        infoMap.put(new Info("libusb_context").pointerTypes("LibUSBContext"))
                .put(new Info("uvc_device").pointerTypes("UVCDevice"))
                .put(new Info("uvc_device_handle").pointerTypes("UVCDeviceHandle"))
                .put(new Info("uvc_context").pointerTypes("UVCContext"))
                .put(new Info("uvc_streaming_interface").pointerTypes("UVCStreamingInterface"))
                .put(new Info("uvc_device_descriptor").pointerTypes("UVCDeviceDescriptor"))
                .put(new Info("uvc_stream_ctrl_t").pointerTypes("UVCStreamCtrl"))
                .put(new Info("timeval").pointerTypes("TimeVal"))
                .put(new Info("uvc_frame").pointerTypes("UVCFrame"))
                .put(new Info("uvc_frame_desc").pointerTypes("UVCFrameDesc"))
                .put(new Info("uvc_format_desc").pointerTypes("UVCFormatDesc"));
        // @formatter:on
    }

    @Opaque
    @Name("libusb_context")
    public static class LibUSBContext extends Pointer {

        public LibUSBContext() {
        }

        public LibUSBContext(Pointer p) {
            super(p);
        }
    }

    @Opaque
    @Name("uvc_device")
    public static class UVCDevice extends Pointer {

        public UVCDevice() {
        }

        public UVCDevice(Pointer p) {
            super(p);
        }
    }

    @Opaque
    @Name("uvc_device_handle")
    public static class UVCDeviceHandle extends Pointer {

        public UVCDeviceHandle() {
        }

        public UVCDeviceHandle(Pointer p) {
            super(p);
        }
    }

    @Opaque
    @Name("uvc_context")
    public static class UVCContext extends Pointer {

        public UVCContext() {
        }

        public UVCContext(Pointer p) {
            super(p);
        }
    }

    @Opaque
    @Name("uvc_streaming_interface")
    public static class UVCStreamingInterface extends Pointer {

        public UVCStreamingInterface() {
        }

        public UVCStreamingInterface(Pointer p) {
            super(p);
        }
    }

    /**
     * Structure representing a UVC device descriptor.
     *
     * (This isn't a standard structure.)
     */
    @Name("uvc_device_descriptor")
    public static class UVCDeviceDescriptor extends Pointer {

        static {
            Loader.load();
        }

        public UVCDeviceDescriptor() {
            allocate();
        }

        public UVCDeviceDescriptor(int size) {
            allocateArray(size);
        }

        public UVCDeviceDescriptor(Pointer p) {
            super(p);
        }

        private native void allocate();

        private native void allocateArray(int size);

        /**
         * Vendor ID
         *
         * @return
         */
        public native @Cast("uint16_t") short idVendor();

        public native UVCDeviceDescriptor idVendor(short idVendor);

        /**
         * Product ID
         * 
         * @return
         */
        public native @Cast("uint16_t") short idProduct();

        public native UVCDeviceDescriptor idProduct(short idProduct);

        /**
         * UVC compliance level, e.g. 0x0100 (1.0), 0x0110
         * 
         * @return
         */
        public native @Cast("uint16_t") short bcdUVC();

        public native UVCDeviceDescriptor bcdUVC(short bcdUVC);

        /**
         * Serial number (null if unavailable)
         * 
         * @return
         */
        public native @Cast("char*") BytePointer serialNumber();

        public native UVCDeviceDescriptor serialNumber(BytePointer serialNumber);

        /**
         * Device-reported manufacturer name (or null)
         * 
         * @return
         */
        public native @Cast("char*") BytePointer manufacturer();

        public native UVCDeviceDescriptor manufacturer(BytePointer manufacturer);

        /**
         * Device-reporter product name (or null)
         * 
         * @return
         */
        public native @Cast("char*") BytePointer product();

        public native UVCDeviceDescriptor product(BytePointer product);
    };

    /**
     * Streaming mode, includes all information needed to select stream
     * 
     * @ingroup streaming
     */
    @Name("uvc_stream_ctrl_t")
    public static class UVCStreamCtrl extends Pointer {

        static {
            Loader.load();
        }

        public UVCStreamCtrl() {
            allocate();
        }

        public UVCStreamCtrl(int size) {
            allocateArray(size);
        }

        public UVCStreamCtrl(Pointer p) {
            super(p);
        }

        private native void allocate();

        private native void allocateArray(int size);

        public native @Cast("uint16_t") short bmHint();

        public native UVCStreamCtrl bmHint(short bmHint);

        public native @Cast("uint8_t") byte bFormatIndex();

        public native UVCStreamCtrl bFormatIndex(byte bFormatIndex);

        public native @Cast("uint8_t") byte bFrameIndex();

        public native UVCStreamCtrl bFrameIndex(byte bFrameIndex);

        public native @Cast("uint32_t") int dwFrameInterval();

        public native UVCStreamCtrl dwFrameInterval(int wKeyFrameRate);

        public native @Cast("uint16_t") short wKeyFrameRate();

        public native UVCStreamCtrl wKeyFrameRate(short wKeyFrameRate);

        public native @Cast("uint16_t") short wPFrameRate();

        public native UVCStreamCtrl wPFrameRate(short wPFrameRate);

        public native @Cast("uint16_t") short wCompQuality();

        public native UVCStreamCtrl wCompQuality(short wCompQuality);

        public native @Cast("uint16_t") short wCompWindowSize();

        public native UVCStreamCtrl wCompWindowSize(short wCompWindowSize);

        public native @Cast("uint32_t") int wDelay();

        public native UVCStreamCtrl wDelay(int wDelay);

        public native @Cast("uint32_t") int dwMaxVideoFrameSize();

        public native UVCStreamCtrl dwMaxVideoFrameSize(int dwMaxVideoFrameSize);

        public native @Cast("uint32_t") int dwMaxPayloadTransferSize();

        public native UVCStreamCtrl dwMaxPayloadTransferSize(int dwMaxPayloadTransferSize);

        public native @Cast("uint32_t") int dwClockFrequency();

        public native UVCStreamCtrl dwClockFrequency(int dwClockFrequency);

        public native @Cast("uint8_t") byte bmFramingInfo();

        public native UVCStreamCtrl bmFramingInfo(byte bmFramingInfo);

        public native @Cast("uint8_t") byte bPreferredVersion();

        public native UVCStreamCtrl bPreferredVersion(byte bPreferredVersion);

        public native @Cast("uint8_t") byte bMinVersion();

        public native UVCStreamCtrl bMinVersion(byte bMinVersion);

        public native @Cast("uint8_t") byte bMaxVersion();

        public native UVCStreamCtrl bMaxVersion(byte bMaxVersion);

        public native @Cast("uint8_t") byte bInterfaceNumber();

        public native UVCStreamCtrl bInterfaceNumber(byte bInterfaceNumber);
        /**
         * @todo add UVC 1.1 parameters
         */
    }

    @Name("timeval")
    public static class TimeVal extends Pointer {

        static {
            Loader.load();
        }

        public TimeVal() {
            allocate();
        }

        public TimeVal(int size) {
            allocateArray(size);
        }

        public TimeVal(Pointer p) {
            super(p);
        }

        private native void allocate();

        private native void allocateArray(int size);

        public native long tv_sec();

        public native TimeVal tv_sec(long tv_sec);

        public native long tv_usec();

        public native TimeVal tv_usec(long tv_usec);
    }

    /**
     * An image frame received from the UVC device
     * 
     * @ingroup streaming
     */
    @Name("uvc_frame")
    public static class UVCFrame extends Pointer {

        static {
            Loader.load();
        }

        public UVCFrame() {
            allocate();
        }

        public UVCFrame(int size) {
            allocateArray(size);
        }

        public UVCFrame(Pointer p) {
            super(p);
        }

        private native void allocate();

        private native void allocateArray(int size);

        /** Image data for this frame */
        public native Pointer data();

        public native UVCFrame data(Pointer data);

        /** Size of image data buffer */
        public native @Cast("size_t") int data_bytes();

        public native UVCFrame data_bytes(int data_bytes);

        /** Width of image in pixels */
        public native @Cast("uint32_t") int width();

        public native UVCFrame width(int width);

        /** Height of image in pixels */
        public native @Cast("uint32_t") int height();

        public native UVCFrame height(int height);

        /** Pixel data format */
        public native @Cast("uvc_frame_format") int frame_format();

        public native UVCFrame frame_format(int frame_format);

        /** Number of bytes per horizontal line (undefined for compressed format) */
        public native @Cast("size_t") int step();

        public native UVCFrame step(int step);

        /** Frame number (may skip, but is strictly monotonically increasing) */
        public native @Cast("uint32_t") int sequence();

        public native UVCFrame sequence(int sequence);

        /** Estimate of system time when the device started capturing the image */
        public native @ByVal TimeVal capture_time();

        public native UVCFrame capture_time(@ByVal TimeVal capture_time);

        /**
         * Handle on the device that produced the image.
         * 
         * @warning You must not call any uvc_* functions during a callback.
         */
        public native UVCDeviceHandle source();

        public native UVCFrame source(UVCDeviceHandle source);

        /**
         * Is the data buffer owned by the library? If 1, the data buffer can be arbitrarily reallocated by frame conversion functions. If
         * 0, the data buffer will not be reallocated or freed by the library. Set this field to zero if you are supplying the buffer.
         */
        public native @Cast("uint8_t") byte library_owns_data();

        public native UVCFrame library_owns_data(byte library_owns_data);
    }

    @Name("uvc_frame_desc")
    public static class UVCFrameDesc extends Pointer {

        static {
            Loader.load();
        }

        public UVCFrameDesc() {
            allocate();
        }

        public UVCFrameDesc(int size) {
            allocateArray(size);
        }

        public UVCFrameDesc(Pointer p) {
            super(p);
        }

        private native void allocate();

        private native void allocateArray(int size);

        public native UVCFormatDesc parent();

        public native UVCFrameDesc parent(UVCFormatDesc parent);

        public native UVCFrameDesc prev();

        public native UVCFrameDesc prev(UVCFrameDesc prev);

        public native UVCFrameDesc next();

        public native UVCFrameDesc next(UVCFrameDesc next);

        /** Type of frame, such as JPEG frame or uncompressed frame */
        public native @Cast("uvc_vs_desc_subtype") int bDescriptorSubtype();

        public native UVCFrameDesc bDescriptorSubtype(int bDescriptorSubtype);

        /** Index of the frame within the list of specs available for this format */
        public native @Cast("uint8_t") byte bFrameIndex();

        public native UVCFrameDesc bFrameIndex(byte bFrameIndex);

        public native @Cast("uint8_t") byte bmCapabilities();

        public native UVCFrameDesc bmCapabilities(byte bmCapabilities);

        /** Image width */
        public native @Cast("uint16_t") short wWidth();

        public native UVCFrameDesc wWidth(short wWidth);

        /** Image height */
        public native @Cast("uint16_t") short wHeight();

        public native UVCFrameDesc wHeight(short wHeight);

        /** Bitrate of corresponding stream at minimal frame rate */
        public native @Cast("uint32_t") int dwMinBitRate();

        public native UVCFrameDesc dwMinBitRate(int dwMinBitRate);

        /** Bitrate of corresponding stream at maximal frame rate */
        public native @Cast("uint32_t") int dwMaxBitRate();

        public native UVCFrameDesc dwMaxBitRate(int dwMaxBitRate);

        /** Maximum number of bytes for a video frame */
        public native @Cast("uint32_t") int dwMaxVideoFrameBufferSize();

        public native UVCFrameDesc dwMaxVideoFrameBufferSize(int dwMaxVideoFrameBufferSize);

        /** Default frame interval (in 100ns units) */
        public native @Cast("uint32_t") int dwDefaultFrameInterval();

        public native UVCFrameDesc dwDefaultFrameInterval(int dwDefaultFrameInterval);

        /** Minimum frame interval for continuous mode (100ns units) */
        public native @Cast("uint32_t") int dwMinFrameInterval();

        public native UVCFrameDesc dwMinFrameInterval(int dwMinFrameInterval);

        /** Maximum frame interval for continuous mode (100ns units) */
        public native @Cast("uint32_t") int dwMaxFrameInterval();

        public native UVCFrameDesc dwMaxFrameInterval(int dwMaxFrameInterval);

        /** Granularity of frame interval range for continuous mode (100ns) */
        public native @Cast("uint32_t") int dwFrameIntervalStep();

        public native UVCFrameDesc dwFrameIntervalStep(int dwFrameIntervalStep);

        /** Frame intervals */
        public native @Cast("uint8_t") byte bFrameIntervalType();

        public native UVCFrameDesc bFrameIntervalType(byte bFrameIntervalType);

        /** number of bytes per line */
        public native @Cast("uint32_t") int dwBytesPerLine();

        public native UVCFrameDesc dwBytesPerLine(int dwBytesPerLine);

        /** Available frame rates, zero-terminated (in 100ns units) */
        public native @Cast("uint32_t *") IntBuffer intervals();

        public native UVCFrameDesc intervals(IntBuffer intervals);
    }

    /**
     * Format descriptor
     *
     * A "format" determines a stream's image type (e.g., raw YUYV or JPEG) and includes many "frame" configurations.
     */
    @Name("uvc_format_desc")
    public static class UVCFormatDesc extends Pointer {

        static {
            Loader.load();
        }

        public UVCFormatDesc() {
            allocate();
        }

        public UVCFormatDesc(int size) {
            allocateArray(size);
        }

        public UVCFormatDesc(Pointer p) {
            super(p);
        }

        private native void allocate();

        private native void allocateArray(int size);

        public native UVCStreamingInterface parent();

        public native UVCFormatDesc parent(UVCStreamingInterface parent);

        public native UVCFormatDesc prev();

        public native UVCFormatDesc prev(UVCFormatDesc prev);

        public native UVCFormatDesc next();

        public native UVCFormatDesc next(UVCFormatDesc next);

        /** Type of image stream, such as JPEG or uncompressed. */
        public native @Cast("uvc_vs_desc_subtype") int bDescriptorSubtype();

        public native UVCFormatDesc bDescriptorSubtype(int bDescriptorSubtype);

        /** Identifier of this format within the VS interface's format list */
        public native @Cast("uint8_t") byte bFormatIndex();

        public native UVCFormatDesc bFormatIndex(byte bFormatIndex);

        public native @Cast("uint8_t") byte bNumFrameDescriptors();

        public native UVCFormatDesc bNumFrameDescriptors(byte bNumFrameDescriptors);

        /** Format specifier Union */
        public native @Cast("uint8_t") byte guidFormat(int pos);

        public native UVCFormatDesc guidFormat(int pos, byte guidFormat);

        @MemberGetter
        public native @Cast("uint8_t *") BytePointer guidFormat();

        public native @Cast("uint8_t") byte fourccFormat(int pos);

        public native UVCFormatDesc fourccFormat(int pos, byte fourccFormat);

        @MemberGetter
        public native @Cast("uint8_t *") BytePointer fourccFormat();

        /** Format-specific data Union */
        /** BPP for uncompressed stream */
        public native @Cast("uint8_t") byte bBitsPerPixel();

        public native UVCFormatDesc bBitsPerPixel(byte bBitsPerPixel);

        /** Flags for JPEG stream */
        public native @Cast("uint8_t") byte bmFlags();

        public native UVCFormatDesc bmFlags(byte bmFlags);

        /** Default {uvc_frame_desc} to choose given this format */
        public native @Cast("uint8_t") byte bDefaultFrameIndex();

        public native UVCFormatDesc bDefaultFrameIndex(byte bDefaultFrameIndex);

        public native @Cast("uint8_t") byte bAspectRatioX();

        public native UVCFormatDesc bAspectRatioX(byte bAspectRatioX);

        public native @Cast("uint8_t") byte bAspectRatioY();

        public native UVCFormatDesc bAspectRatioY(byte bAspectRatioY);

        public native @Cast("uint8_t") byte bmInterlaceFlags();

        public native UVCFormatDesc bmInterlaceFlags(byte bmInterlaceFlags);

        public native @Cast("uint8_t") byte bCopyProtect();

        public native UVCFormatDesc bCopyProtect(byte bCopyProtect);

        public native @Cast("uint8_t") byte bVariableSize();

        public native UVCFormatDesc bVariableSize(byte bVariableSize);

        /** Available frame specifications for this format */
        public native UVCFrameDesc frame_descs();

        public native UVCFormatDesc frame_descs(UVCFrameDesc frame_descs);
    }

    /**
     * A callback function to handle incoming assembled UVC frames
     * 
     * @ingroup streaming
     */
    // typedef void(uvc_frame_callback_t)(struct uvc_frame *frame, void *user_ptr);
    public static abstract class UVCFrameCallback extends FunctionPointer {

        static {
            Loader.load();
        }

        public UVCFrameCallback() {
            allocate();
        }

        public UVCFrameCallback(Pointer p) {
            super(p);
        }

        private native void allocate();

        protected void call(UVCFrame frame, Pointer user_ptr) {
            this.call(frame);
        }

        public abstract void call(UVCFrame frame);
    }

    /* Métodos para detectar y abrir un dispositivo UVC */
    public static native int uvc_init(@ByPtrPtr UVCContext ctx, LibUSBContext usb_ctx);

    public static native int uvc_find_device(UVCContext ctx, @ByPtrPtr UVCDevice dev, int vid, int pid, String sn);

    public static native void uvc_ref_device(UVCDevice dev);

    public static native int uvc_open(UVCDevice dev, @ByPtrPtr UVCDeviceHandle devh);

    /* Métodos para obtener la descripción de un dispositivo UVC */
    // change RH parameterized raw pointer type to avoid alert in eclipse
    public static native int uvc_get_device_descriptor(UVCDevice dev, @Cast("uvc_device_descriptor**") PointerPointer<?> desc);

    public static native void uvc_free_device_descriptor(UVCDeviceDescriptor desc);

    public static native @Const UVCFormatDesc uvc_get_format_descs(UVCDeviceHandle devh);

    /* Métodos para cerrar un dispositivo UVC */
    public static native void uvc_close(UVCDeviceHandle devh);

    public static native void uvc_unref_device(UVCDevice dev);

    public static native void uvc_exit(UVCContext ctx);

    /* Métodos para el manejo del Streaming */
    public static native int uvc_get_stream_ctrl_format_size(UVCDeviceHandle devh, UVCStreamCtrl ctrl, @Cast("uvc_frame_format") int format,
            int width, int height, int fps);

    public static native int uvc_probe_stream_ctrl(UVCDeviceHandle devh, UVCStreamCtrl ctrl);

    private static native int uvc_start_streaming(UVCDeviceHandle devh, UVCStreamCtrl ctrl, UVCFrameCallback cb, Pointer user_ptr,
            byte flags);

    public static int uvc_start_streaming(UVCDeviceHandle devh, UVCStreamCtrl ctrl, UVCFrameCallback cb, byte flags) {
        return uvc_start_streaming(devh, ctrl, cb, new Pointer(), flags);
    }

    public static native void uvc_stop_streaming(UVCDeviceHandle devh);

    /* Métodos para el control de la Imagen */
    public static native int uvc_get_scanning_mode(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer mode,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_scanning_mode(UVCDeviceHandle devh, @Cast("uint8_t") byte mode);

    public static native int uvc_get_ae_mode(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer mode, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_ae_mode(UVCDeviceHandle devh, @Cast("uint8_t") byte mode);

    public static native int uvc_get_ae_priority(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer priority,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_ae_priority(UVCDeviceHandle devh, @Cast("uint8_t") byte priority);

    public static native int uvc_get_exposure_abs(UVCDeviceHandle devh, @Cast("uint32_t *") IntBuffer time,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_exposure_abs(UVCDeviceHandle devh, @Cast("uint32_t") int time);

    public static native int uvc_get_exposure_rel(UVCDeviceHandle devh, @Cast("int8_t *") ByteBuffer step,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_exposure_rel(UVCDeviceHandle devh, @Cast("int8_t") byte step);

    public static native int uvc_get_focus_abs(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer focus,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_focus_abs(UVCDeviceHandle devh, @Cast("uint16_t") short focus);

    public static native int uvc_get_focus_rel(UVCDeviceHandle devh, @Cast("int8_t *") ByteBuffer focus_rel,
            @Cast("uint8_t *") ByteBuffer speed, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_focus_rel(UVCDeviceHandle devh, @Cast("int8_t") byte focus_rel, @Cast("uint8_t") byte speed);

    public static native int uvc_get_focus_simple_range(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer focus,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_focus_simple_range(UVCDeviceHandle devh, @Cast("uint8_t") byte focus);

    public static native int uvc_get_focus_auto(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer state,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_focus_auto(UVCDeviceHandle devh, @Cast("uint8_t") byte state);

    public static native int uvc_get_iris_abs(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer iris,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_iris_abs(UVCDeviceHandle devh, @Cast("uint16_t") short iris);

    public static native int uvc_get_iris_rel(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer iris_rel,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_iris_rel(UVCDeviceHandle devh, @Cast("uint8_t") byte iris_rel);

    public static native int uvc_get_zoom_abs(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer focal_length,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_zoom_abs(UVCDeviceHandle devh, @Cast("uint16_t") short focal_length);

    public static native int uvc_get_zoom_rel(UVCDeviceHandle devh, @Cast("int8_t *") ByteBuffer zoom_rel,
            @Cast("uint8_t *") ByteBuffer digital_zoom, @Cast("uint8_t *") ByteBuffer speed, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_zoom_rel(UVCDeviceHandle devh, @Cast("int8_t") byte zoom_rel, @Cast("uint8_t") byte digital_zoom,
            @Cast("uint8_t") byte speed);

    public static native int uvc_get_pantilt_abs(UVCDeviceHandle devh, IntPointer pan, IntPointer tilt, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_pantilt_abs(UVCDeviceHandle devh, @Cast("int32_t") int pan, @Cast("int32_t") int tilt);

    public static native int uvc_get_pantilt_rel(UVCDeviceHandle devh, @Cast("int8_t *") ByteBuffer pan_rel,
            @Cast("uint8_t *") ByteBuffer pan_speed, @Cast("int8_t *") ByteBuffer tilt_rel, @Cast("uint8_t *") ByteBuffer tilt_speed,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_pantilt_rel(UVCDeviceHandle devh, @Cast("int8_t") byte pan_rel, @Cast("uint8_t") byte pan_speed,
            @Cast("int8_t") byte tilt_rel, @Cast("uint8_t") byte tilt_speed);

    public static native int uvc_get_roll_abs(UVCDeviceHandle devh, ShortPointer roll, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_roll_abs(UVCDeviceHandle devh, @Cast("int16_t") short roll);

    public static native int uvc_get_roll_rel(UVCDeviceHandle devh, @Cast("int8_t *") ByteBuffer roll_rel,
            @Cast("uint8_t *") ByteBuffer speed, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_roll_rel(UVCDeviceHandle devh, @Cast("int8_t") byte roll_rel, @Cast("uint8_t") byte speed);

    public static native int uvc_get_privacy(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer privacy,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_privacy(UVCDeviceHandle devh, @Cast("uint8_t") byte privacy);

    public static native int uvc_get_digital_window(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer window_top,
            @Cast("uint16_t *") ShortBuffer window_left, @Cast("uint16_t *") ShortBuffer window_bottom,
            @Cast("uint16_t *") ShortBuffer window_right, @Cast("uint16_t *") ShortBuffer num_steps,
            @Cast("uint16_t *") ShortBuffer num_steps_units, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_digital_window(UVCDeviceHandle devh, @Cast("uint16_t") short window_top,
            @Cast("uint16_t") short window_left, @Cast("uint16_t") short window_bottom, @Cast("uint16_t") short window_right,
            @Cast("uint16_t") short num_steps, @Cast("uint16_t") short num_steps_units);

    public static native int uvc_get_digital_roi(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer roi_top,
            @Cast("uint16_t *") ShortBuffer roi_left, @Cast("uint16_t *") ShortBuffer roi_bottom, @Cast("uint16_t *") ShortBuffer roi_right,
            @Cast("uint16_t *") ShortBuffer auto_controls, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_digital_roi(UVCDeviceHandle devh, @Cast("uint16_t") short roi_top, @Cast("uint16_t") short roi_left,
            @Cast("uint16_t") short roi_bottom, @Cast("uint16_t") short roi_right, @Cast("uint16_t") short auto_controls);

    public static native int uvc_get_backlight_compensation(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer backlight_compensation,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_backlight_compensation(UVCDeviceHandle devh, @Cast("uint16_t") short backlight_compensation);

    public static native int uvc_get_brightness(UVCDeviceHandle devh, ShortPointer brightness, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_brightness(UVCDeviceHandle devh, @Cast("int16_t") short brightness);

    public static native int uvc_get_contrast(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer contrast,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_contrast(UVCDeviceHandle devh, @Cast("uint16_t") short contrast);

    public static native int uvc_get_contrast_auto(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer contrast_auto,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_contrast_auto(UVCDeviceHandle devh, @Cast("uint8_t") byte contrast_auto);

    public static native int uvc_get_gain(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer gain, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_gain(UVCDeviceHandle devh, @Cast("uint16_t") short gain);

    public static native int uvc_get_power_line_frequency(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer power_line_frequency,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_power_line_frequency(UVCDeviceHandle devh, @Cast("uint8_t") byte power_line_frequency);

    public static native int uvc_get_hue(UVCDeviceHandle devh, ShortPointer hue, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_hue(UVCDeviceHandle devh, @Cast("int16_t") short hue);

    public static native int uvc_get_hue_auto(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer hue_auto,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_hue_auto(UVCDeviceHandle devh, @Cast("uint8_t") byte hue_auto);

    public static native int uvc_get_saturation(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer saturation,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_saturation(UVCDeviceHandle devh, @Cast("uint16_t") short saturation);

    public static native int uvc_get_sharpness(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer sharpness,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_sharpness(UVCDeviceHandle devh, @Cast("uint16_t") short sharpness);

    public static native int uvc_get_gamma(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer gamma, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_gamma(UVCDeviceHandle devh, @Cast("uint16_t") short gamma);

    public static native int uvc_get_white_balance_temperature(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer temperature,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_white_balance_temperature(UVCDeviceHandle devh, @Cast("uint16_t") short temperature);

    public static native int uvc_get_white_balance_temperature_auto(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer temperature_auto,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_white_balance_temperature_auto(UVCDeviceHandle devh, @Cast("uint8_t") byte temperature_auto);

    public static native int uvc_get_white_balance_component(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer blue,
            @Cast("uint16_t *") ShortBuffer red, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_white_balance_component(UVCDeviceHandle devh, @Cast("uint16_t") short blue,
            @Cast("uint16_t") short red);

    public static native int uvc_get_white_balance_component_auto(UVCDeviceHandle devh,
            @Cast("uint8_t *") ByteBuffer white_balance_component_auto, @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_white_balance_component_auto(UVCDeviceHandle devh, @Cast("uint8_t") byte white_balance_component_auto);

    public static native int uvc_get_digital_multiplier(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer multiplier_step,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_digital_multiplier(UVCDeviceHandle devh, @Cast("uint16_t") short multiplier_step);

    public static native int uvc_get_digital_multiplier_limit(UVCDeviceHandle devh, @Cast("uint16_t *") ShortBuffer multiplier_step,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_digital_multiplier_limit(UVCDeviceHandle devh, @Cast("uint16_t") short multiplier_step);

    public static native int uvc_get_analog_video_standard(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer video_standard,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_analog_video_standard(UVCDeviceHandle devh, @Cast("uint8_t") byte video_standard);

    public static native int uvc_get_analog_video_lock_status(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer status,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_analog_video_lock_status(UVCDeviceHandle devh, @Cast("uint8_t") byte status);

    public static native int uvc_get_input_select(UVCDeviceHandle devh, @Cast("uint8_t *") ByteBuffer selector,
            @Cast("uvc_req_code") int req_code);

    public static native int uvc_set_input_select(UVCDeviceHandle devh, @Cast("uint8_t") byte selector);

    /* Otros Métodos */
    public static native byte uvc_get_bus_number(UVCDevice dev);

    public static native byte uvc_get_device_address(UVCDevice dev);

    /* Métodos para la manipulación de Frames */
    public static native int uvc_any2rgb(UVCFrame fin, UVCFrame fout);

    /* Hay que tener habilitado libjpeg */
    public static native int uvc_mjpeg2rgb(UVCFrame fin, UVCFrame fout);

    public static native UVCFrame uvc_allocate_frame(@Cast("size_t") int data_bytes);

    public static native void uvc_free_frame(UVCFrame frame);

    // added RH - implemented wrapper for printing a summary of camera's info
    public static native void uvc_print_diag(UVCDeviceHandle devh, @Cast("FILE*") Pointer fd);

    public static native int uvc_get_device_list(UVCContext ctx, @Cast("uvc_device***") @ByPtrPtr PointerPointer<?> uvcDevList);

    public static native void uvc_free_device_list(@Cast("uvc_device**") @ByPtrPtr PointerPointer<?> uvcDevList,
            @Cast("uint8_t") byte unref_devices);
}
