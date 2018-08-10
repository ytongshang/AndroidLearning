package cradle.rancune.learningandroid.record.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Rancune@126.com 2018/7/26.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoConfig {

    public enum Quality {
        SHD(1280, 720, 1000 * 2000),
        HD(640, 480, 1000 * 1000),
        SD(480, 360, 1000 * 500);
        private int width;
        private int height;
        private int bitRate;

        Quality(int width, int height, int bitRate) {
            this.width = width;
            this.height = height;
            this.bitRate = bitRate;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public int bitRate() {
            return bitRate;
        }
    }

    /**
     * H.264/AVC video
     */
    private String mime = "video/avc";
    private int width = 1280;
    private int height = 720;
    private int dpi = 1;
    private int frameRate = 24;
    /**
     * 关键帧
     */
    private int iFrameRate = 2;
    private int bitRate = 1000 * 2000;
    private int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
    private int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    /**
     * 屏幕长宽比,这里必须大于1
     */
    private float screenRatio = -1;

    public VideoConfig(Context context) {
        calculateRatio(context);
    }

    private float calculateRatio(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int heightPixels = dm.heightPixels;
        int widthPixels = dm.widthPixels;
        float density = dm.density;
        int screenPortraitHeight = Math.max(heightPixels, widthPixels);
        int screenPortraitWidth = Math.min(heightPixels, widthPixels);
        return (screenPortraitHeight * density) / (screenPortraitWidth * density);
    }

    public String getMime() {
        return mime;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDpi() {
        return dpi;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public int getiFrameRate() {
        return iFrameRate;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getColorFormat() {
        return colorFormat;
    }

    private boolean aligned = false;
    private boolean alignDevice = true;

    public VideoConfig alignDevice(boolean alignDevice) {
        this.alignDevice = alignDevice;
        return this;
    }

    private void alignDevice() {
        if (!alignDevice) {
            return;
        }
        if (screenRatio < 1) {
            throw new IllegalArgumentException("screenRatio must be > 1, like 1280/720");
        }

        if (aligned) {
            return;
        }
        int oldWidth = getWidth();
        int oldHeight = getHeight();

        int newHeight = oldHeight;
        int newWidth;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            newWidth = (int) (oldHeight * screenRatio);
        } else {
            newWidth = (int) (oldHeight / screenRatio);
        }

        // 16-align for device compatibility
        newWidth = (newWidth + 15) / 16 * 16;
        newHeight = (newHeight + 15) / 16 * 16;
        this.width = newWidth;
        this.height = newHeight;
        aligned = true;
    }

    public VideoConfig orientation(int orientation) {
        this.orientation = orientation;
        int oldWidth = getWidth();
        int oldHeight = getHeight();
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            this.width = Math.max(oldWidth, oldHeight);
            this.height = Math.min(oldWidth, oldHeight);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.width = Math.min(oldWidth, oldHeight);
            this.height = Math.max(oldWidth, oldHeight);
        }

        alignDevice();
        return this;
    }


}
