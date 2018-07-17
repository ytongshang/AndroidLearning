package cradle.rancune.learningandroid.opengl.util;

import android.opengl.Matrix;

/**
 * Created by Rancune@126.com 2018/7/17.
 */
public class MatrixUtils {

    private MatrixUtils() {

    }

    public enum ScaleTye {
        FIT_XY,
        FIT_START,
        FIT_END,
        CENTER_CROP,
        CENTER_INSIDE
    }

    public static void getMatrix(float[] matrix, ScaleTye type,
                                 int imgWidth, int imgHeight,
                                 int viewWidth, int viewHeight) {
        float[] projection = new float[16];
        float[] camera = new float[16];
        if (type == ScaleTye.FIT_XY) {
            Matrix.orthoM(projection, 0, -1, 1, -1, 1, 1, 3);
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
        float sWhView = (float) viewWidth / viewHeight;
        float sWhImg = (float) imgWidth / imgHeight;
        if (sWhImg > sWhView) {
            switch (type) {
                case CENTER_CROP:
                    Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                    break;
                case CENTER_INSIDE:
                    Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                    break;
                case FIT_START:
                    Matrix.orthoM(projection, 0, -1, 1, 1 - 2 * sWhImg / sWhView, 1, 1, 3);
                    break;
                case FIT_END:
                    Matrix.orthoM(projection, 0, -1, 1, -1, 2 * sWhImg / sWhView - 1, 1, 3);
                    break;
            }
        } else {
            switch (type) {
                case CENTER_CROP:
                    Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                    break;
                case CENTER_INSIDE:
                    Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                    break;
                case FIT_START:
                    Matrix.orthoM(projection, 0, -1, 2 * sWhView / sWhImg - 1, -1, 1, 1, 3);
                    break;
                case FIT_END:
                    Matrix.orthoM(projection, 0, 1 - 2 * sWhView / sWhImg, 1, -1, 1, 1, 3);
                    break;
            }
        }
        Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
    }

    public static void flip(float[] matrix, boolean flipX, boolean flipY, boolean flipZ) {
        if (flipX || flipY || flipZ) {
            Matrix.scaleM(matrix, 0,
                    flipX ? -1 : 1,
                    flipY ? -1 : 1,
                    flipZ ? -1 : 1);
        }
    }
}
