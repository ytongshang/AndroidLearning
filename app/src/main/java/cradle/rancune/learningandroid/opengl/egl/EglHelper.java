package cradle.rancune.learningandroid.opengl.egl;

import android.annotation.TargetApi;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;


/**
 * Created by Rancune@126.com 2018/7/23.
 */
@SuppressWarnings("WeakerAccess")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class EglHelper {
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private static final Chooser sDefaultChooser;

    static {
        sDefaultChooser = new Chooser()
                .setRedBit(8)
                .setGreenBit(8)
                .setBlueBit(8)
                .setAlphaBit(8)
                .setStencilBit(0)
                .setWithDepth(true);
    }

    private final Chooser mChooser;
    private EGLDisplay mDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLSurface mSurface = EGL14.EGL_NO_SURFACE;
    private EGLContext mContext = EGL14.EGL_NO_CONTEXT;
    private EGLConfig mConfig;

    public static Chooser getDefaultChooser() {
        return sDefaultChooser;
    }

    public static void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + " : egl error: 0x"
                    + Integer.toHexString(error));
        }
    }

    public EglHelper(Chooser chooser) {
        if (chooser == null) {
            chooser = sDefaultChooser;
        }
        mChooser = chooser;
    }

    public void init() {
        mDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("could not get egl14 display");
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mDisplay, version, 0, version, 1)) {
            throw new RuntimeException("could not initialize egl14");
        }
        int[] attrs = {
                EGL14.EGL_RED_SIZE, mChooser.mRedBit,
                EGL14.EGL_GREEN_SIZE, mChooser.mGreenBit,
                EGL14.EGL_BLUE_SIZE, mChooser.mBlueBit,
                EGL14.EGL_ALPHA_SIZE, mChooser.mAlphaBit,
                EGL14.EGL_DEPTH_SIZE, mChooser.mWithDepth ? 16 : 0,
                EGL14.EGL_STENCIL_SIZE, mChooser.mStencilBit,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL_RECORDABLE_ANDROID, 1,
                EGL14.EGL_NONE};
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        EGL14.eglChooseConfig(
                mDisplay, attrs, 0,
                configs, 0, configs.length,
                numConfigs, 0);
        checkEglError("eglChooseConfig");
        mConfig = configs[0];

        int[] contextAttrs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE};
        mContext = EGL14.eglCreateContext(mDisplay, mConfig, EGL14.EGL_NO_CONTEXT,
                contextAttrs, 0);
        checkEglError("eglCreateContext");
    }

    public void resumeWindowSurface(Surface surfaceObject) {
        if (mSurface != EGL14.EGL_NO_SURFACE) {
            return;
        }
        if (surfaceObject == null || !surfaceObject.isValid()) {
            throw new RuntimeException("eglCreateWindowSurface, but surface object is null");
        }
        int[] surfaceAttrs = {EGL14.EGL_NONE};
        mSurface = EGL14.eglCreateWindowSurface(mDisplay, mConfig, surfaceObject, surfaceAttrs, 0);
        checkEglError("eglCreateWindowSurface");

        EGL14.eglMakeCurrent(mDisplay, mSurface, mSurface, mContext);
        checkEglError("eglMakeCurrent");
    }

    public void resumePbufferSurface(Surface surfaceObject, int width, int height) {
        if (mSurface != EGL14.EGL_NO_SURFACE) {
            return;
        }
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("eglCreatePbufferSurface, but width or height < 0");
        }
        int[] pbAttrs = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE};
        mSurface = EGL14.eglCreatePbufferSurface(mDisplay, mConfig, pbAttrs, 0);
        checkEglError("eglCreatePbufferSurface");

        EGL14.eglMakeCurrent(mDisplay, mSurface, mSurface, mContext);
        checkEglError("eglMakeCurrent");
    }

    public void pause() {
        if (mSurface == EGL14.EGL_NO_SURFACE) {
            return;
        }
        EGL14.eglMakeCurrent(mDisplay, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroySurface(mDisplay, mSurface);
        mSurface = EGL14.EGL_NO_SURFACE;
    }

    public void destroy() {
        pause();
        EGL14.eglDestroyContext(mDisplay, mContext);
        EGL14.eglTerminate(mDisplay);
    }

    public void swapBuffers() {
        if (mDisplay != null && mSurface != null) {
            EGL14.eglSwapBuffers(mDisplay, mSurface);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setPresentationTime(long presentationTime) {
        if (null != mDisplay && null != mSurface) {
            EGLExt.eglPresentationTimeANDROID(mDisplay, mSurface, presentationTime);
        }
    }

    public static class Chooser {
        int mRedBit;
        int mGreenBit;
        int mBlueBit;
        int mAlphaBit;
        int mStencilBit;
        boolean mWithDepth;

        public Chooser() {

        }

        public Chooser setRedBit(int redBit) {
            mRedBit = redBit;
            return this;
        }

        public Chooser setGreenBit(int greenBit) {
            mGreenBit = greenBit;
            return this;
        }

        public Chooser setBlueBit(int blueBit) {
            mBlueBit = blueBit;
            return this;
        }

        public Chooser setAlphaBit(int alphaBit) {
            mAlphaBit = alphaBit;
            return this;
        }

        public Chooser setStencilBit(int stencilBit) {
            mStencilBit = stencilBit;
            return this;
        }

        public Chooser setWithDepth(boolean withDepth) {
            mWithDepth = withDepth;
            return this;
        }
    }
}
