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

    private String mime = "video/avc";// 编码视频格式
    private boolean alignDevice = true;
    private int width = 1280;   // 编码器编码视频宽
    private int height = 720;   // 编码器编码视频高
    private int dpi = 1;        // dpi
    private int frameRate = 24; // 1s内视频帧 fps
    private int iFrameRate = 2; // 2s内1个关键帧
    private int bitRate = 1000 * 2000; // 比特率/码率 2000kbps
    private int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;
    private int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private float screenRatio = -1; // 屏幕长宽比 1280 : 720 = 16 : 9

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


    public int getDpi() {
        return dpi;
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
}
