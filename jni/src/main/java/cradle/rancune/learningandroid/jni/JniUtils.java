package cradle.rancune.learningandroid.jni;

/**
 * Created by Rancune@126.com 2018/9/5.
 */
public class JniUtils {
    public static native String getStringFromNative();

    public int num = 10;
    public native int addNum();

    public String str = "world";
    public native void setNormalField();

    public static String staticStr = "static world";
    public static native void setStaticField();
}
