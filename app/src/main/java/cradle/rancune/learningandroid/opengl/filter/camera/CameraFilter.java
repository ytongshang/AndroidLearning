package cradle.rancune.learningandroid.opengl.filter.camera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import cradle.rancune.learningandroid.opengl.filter.Filter;
import cradle.rancune.learningandroid.opengl.util.MatrixUtils;

/**
 * Created by Rancune@126.com 2018/7/16.
 */
public class CameraFilter extends Filter {

    private int mVertexPosition;
    private int mCoordPosition;
    private int mMatrixPosition;
    private int mCoordMatrixPosition;
    private int mTexturePosition;

    private int mPreviewWidth;
    private int mPreviewHeight;

    private int mViewWidth;
    private int mViewHeight;

    public CameraFilter(Context context) {
        super(context);
    }

    public void setPreviewSize(int width, int height) {
        mPreviewWidth = width;
        mPreviewHeight = height;
    }

    @Override
    public void onCreate() {
        createFromAssets("shader/baseTexture2D.vert", "shader/baseOES.frag");
        mVertexPosition = getAttributeLocation("aPosition");
        mCoordPosition = getAttributeLocation("aTextureCoordinate");
        mMatrixPosition = getUniformLocation("uMatrix");
        mCoordMatrixPosition = getUniformLocation("uTextureCoordMatrix");
        mTexturePosition = getUniformLocation("uOESTexture");

        mTextureCoordBuffer.clear();
        mTextureCoordBuffer.put(sCameraCoords);
        mTextureCoordBuffer.position(0);
    }

    @Override
    public void onSizeChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        MatrixUtils.getMatrix(mMatrix, MatrixUtils.ScaleTye.CENTER_CROP, mPreviewWidth, mPreviewHeight, mViewWidth, mPreviewHeight);
    }

    @Override
    public void onDraw() {
        GLES20.glUniformMatrix4fv(mMatrixPosition, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(mCoordMatrixPosition, 1, false, mTextureMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
        GLES20.glUniform1i(mTexturePosition, 0);

        GLES20.glEnableVertexAttribArray(mVertexPosition);
        GLES20.glVertexAttribPointer(mVertexPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mCoordMatrixPosition);
        GLES20.glVertexAttribPointer(mCoordPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mTextureCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mVertexPosition);
        GLES20.glDisableVertexAttribArray(mCoordPosition);
    }
}
