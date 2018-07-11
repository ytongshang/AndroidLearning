package cradle.rancune.learningandroid.opengl.renderer.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.R;
import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/9.
 */
public class Sample2D extends SimpleRenderer {

    private final float[] mVertices = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };
    private FloatBuffer mVertexBuffer;

    private final float[] mCoords = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };
    private FloatBuffer mCoordBuffer;

    private final GLProgram mGLProgram;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMatrix = new float[16];

    private Bitmap mBitmap;
    private int mTexture;

    public Sample2D(Context context) {
        super(context);
        mVertexBuffer = GLHelper.createFloatBuffer(mVertices);
        mCoordBuffer = GLHelper.createFloatBuffer(mCoords);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lenna, options);
        mTexture = GLHelper.loadTexture(mBitmap);
        Matrix.setIdentityM(mMatrix, 0);
        mGLProgram = GLProgram.of(GLHelper.readFromAssets(context, "shader/texture.vert"),
                GLHelper.readFromAssets(context, "shader/texture.frag"));
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

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
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

        int matrixIndex = mGLProgram.getUniformLocation("uMatrix");
        GLES20.glUniformMatrix4fv(matrixIndex, 1, false, mMatrix, 0);
        int positionIndex = mGLProgram.getAttributeLocation("aPosition");
        GLES20.glEnableVertexAttribArray(positionIndex);
        int coorIndex = mGLProgram.getAttributeLocation("aCoordinate");
        GLES20.glEnableVertexAttribArray(coorIndex);
        int textureIndex = mGLProgram.getUniformLocation("sampleTexture");
        GLES20.glUniform1i(textureIndex, 0);
        mTexture = GLHelper.loadTexture(mBitmap);

        GLES20.glVertexAttribPointer(
                positionIndex,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                mVertexBuffer
        );

        GLES20.glVertexAttribPointer(
                coorIndex,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                mCoordBuffer
        );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
