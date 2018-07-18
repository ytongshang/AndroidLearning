package cradle.rancune.learningandroid.opengl.renderer.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLHelper;
import cradle.rancune.learningandroid.opengl.util.MatrixUtils;

/**
 * Created by Rancune@126.com 2018/7/9.
 */
public class Sample2D extends SimpleRenderer {

    // 顶点
    private final float[] mVertices = {
            -1.0f, 1.0f, // 左上
            -1.0f, -1.0f, // 左下
            1.0f, 1.0f, // 右上
            1.0f, -1.0f, // 右下
    };
    private FloatBuffer mVertexBuffer;

    // 纹理坐标
    private final float[] mCoords = {
            0.0f, 0.0f, // 左上
            0.0f, 1.0f, // 左下
            1.0f, 0.0f, // 右上
            1.0f, 1.0f // 右下
    };
    private FloatBuffer mCoordBuffer;

    private final GLProgram mGLProgram;

    private float[] mMatrix = new float[16];
    private float[] mTextureCoordMatrix = new float[16];

    private Bitmap mBitmap;
    private int mTexture;

    private int mVertexPosition;
    private int mMatrxPosition;
    private int mTextureCoordPosition;
    private int mTextureCoordMatrixPosition;
    private int mTexturePosition;

    public Sample2D(Context context) {
        super(context);
        mVertexBuffer = GLHelper.createFloatBuffer(mVertices);
        mCoordBuffer = GLHelper.createFloatBuffer(mCoords);
        Matrix.setIdentityM(mTextureCoordMatrix, 0);
        Matrix.setIdentityM(mMatrix, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lenna, options);
        mTexture = GLHelper.load2DTexture();
        mGLProgram = GLProgram.of(GLHelper.readFromAssets(context, "shader/baseTexture2D.vert"),
                GLHelper.readFromAssets(context, "shader/baseTexture2D.frag"));
        mVertexPosition = mGLProgram.getAttributeLocation("aPosition");
        mTextureCoordPosition = mGLProgram.getAttributeLocation("aTextureCoordinate");
        mMatrxPosition = mGLProgram.getUniformLocation("uMatrix");
        mTextureCoordMatrixPosition = mGLProgram.getUniformLocation("uTextureCoordMatrix");
        mTexturePosition = mGLProgram.getUniformLocation("uTexture");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        GLES20.glViewport(0, 0, width, height);
        MatrixUtils.getMatrix(mMatrix, MatrixUtils.ScaleTye.CENTER_INSIDE,
                mBitmap.getWidth(), mBitmap.getHeight(), width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        draw();
    }

    @Override
    public void draw() {
        super.draw();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mGLProgram.getProgram());

        GLES20.glUniformMatrix4fv(mMatrxPosition, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(mTextureCoordMatrixPosition, 1, false, mTextureCoordMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        GLES20.glUniform1i(mTexturePosition, 0);

        GLES20.glEnableVertexAttribArray(mVertexPosition);
        GLES20.glVertexAttribPointer(mVertexPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordPosition);
        GLES20.glVertexAttribPointer(mTextureCoordPosition, 2, GLES20.GL_FLOAT, false, 2 * 4, mCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mVertexPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordPosition);
    }
}
