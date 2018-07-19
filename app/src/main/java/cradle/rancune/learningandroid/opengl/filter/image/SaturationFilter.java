package cradle.rancune.learningandroid.opengl.filter.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import cradle.rancune.learningandroid.opengl.filter.Filter;
import cradle.rancune.learningandroid.opengl.util.MatrixUtils;

/**
 * Created by Rancune@126.com 2018/7/18.
 */
public class SaturationFilter extends Filter {
    private int mVertexPosition;
    private int mTextureCoordPosition;
    private int mMatrixPosition;
    private int mCoordMatrixPosition;
    private int mTexturePosition;
    private int mSaturationPosition;

    private float mSaturation;
    private Bitmap mBitmap;

    private int mViewWidth;
    private int mViewHeight;

    public SaturationFilter(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        createFromAssets("shader/baseTexture2D.vert", "shader/saturation.frag");
        mVertexPosition = getAttributeLocation("aPosition");
        mTextureCoordPosition = getAttributeLocation("aTextureCoordinate");
        mMatrixPosition = getUniformLocation("uMatrix");
        mCoordMatrixPosition = getUniformLocation("uTextureCoordMatrix");
        mTexturePosition = getUniformLocation("uTexture");
        mSaturationPosition = getUniformLocation("uSaturation");
    }

    @Override
    public void onSizeChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        if (mBitmap != null) {
            MatrixUtils.getMatrix(mMatrix, MatrixUtils.ScaleTye.CENTER_CROP,
                    mBitmap.getWidth(), mBitmap.getHeight(),
                    mViewWidth, mViewHeight);
        }
    }

    @Override
    public void onDraw() {
        if (mBitmap == null) {
            return;
        }
        GLES20.glUniform1f(mSaturationPosition, mSaturation);
        GLES20.glUniformMatrix4fv(mMatrixPosition, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(mCoordMatrixPosition, 1, false, mTextureMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        GLES20.glUniform1i(mTexturePosition, 0);

        GLES20.glEnableVertexAttribArray(mVertexPosition);
        GLES20.glVertexAttribPointer(mVertexPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordPosition);
        GLES20.glVertexAttribPointer(mTextureCoordPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mTextureCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mVertexPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordPosition);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        if (mViewHeight != 0 && mViewWidth != 0) {
            MatrixUtils.getMatrix(mMatrix, MatrixUtils.ScaleTye.CENTER_CROP,
                    mBitmap.getWidth(), mBitmap.getHeight(),
                    mViewWidth, mViewHeight);
        }
    }

    public void setSaturation(float saturation) {
        mSaturation = saturation;
    }
}
