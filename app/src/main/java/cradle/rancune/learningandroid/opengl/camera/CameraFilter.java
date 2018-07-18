package cradle.rancune.learningandroid.opengl.camera;

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

    private int mOesTexture;

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
        createFromAssets("filter/oes_base_vertex.vert", "filter/oes_base_fragment.frag");
        mVertexPosition = getAttributeLocation("a_Position");
        mCoordPosition = getAttributeLocation("a_TextureCoordinate");
        mMatrixPosition = getUniformLocation("u_Matrix");
        mCoordMatrixPosition = getUniformLocation("u_CoordMatrix");
        mTexturePosition = getUniformLocation("u_Texture");

        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        mOesTexture = texture[0];

        mCoordBuffer.clear();
        mCoordBuffer.put(sCameraCoords);
        mCoordBuffer.position(0);
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
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOesTexture);
        GLES20.glUniform1i(mTexturePosition, 0);

        GLES20.glEnableVertexAttribArray(mVertexPosition);
        GLES20.glVertexAttribPointer(mVertexPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mCoordMatrixPosition);
        GLES20.glVertexAttribPointer(mCoordPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mVertexPosition);
        GLES20.glDisableVertexAttribArray(mCoordPosition);
    }

    public int getTextureId() {
        return mOesTexture;
    }
}
