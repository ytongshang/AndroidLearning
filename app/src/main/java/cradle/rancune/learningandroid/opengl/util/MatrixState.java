package cradle.rancune.learningandroid.opengl.util;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by Rancune@126.com 2018/7/17.
 */
public class MatrixState {
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMatrix = new float[16];

    private final Stack<float[]> mStack = new Stack<>();

    public MatrixState() {
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    public void pushModelMatrix() {
        mStack.push(Arrays.copyOf(mModelMatrix, 16));
    }

    public void popModelMatrix() {
        mModelMatrix = mStack.pop();
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(mModelMatrix, 0, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(mModelMatrix, 0, x, y, z);
    }

    public void setCamera(float eyeX, float eyeY, float eyeZ,
                          float centerX, float centerY, float centerZ,
                          float upX, float upY, float upZ) {
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void frustum(float left, float right, float bottom, float top,
                        float near, float far) {
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void ortho(float left, float right, float bottom, float top,
                      float near, float far) {
        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void perspective(float fovy, float aspect, float zNear, float zFar) {
        Matrix.perspectiveM(mProjectionMatrix, 0, fovy, aspect, zNear, zFar);
    }

    public float[] getFinalMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0);
        return mMatrix;
    }
}
