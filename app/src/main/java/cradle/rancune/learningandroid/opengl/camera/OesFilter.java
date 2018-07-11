package cradle.rancune.learningandroid.opengl.camera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cradle.rancune.learningandroid.opengl.GLProgram;
import cradle.rancune.learningandroid.opengl.renderer.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.util.GLHelper;

/**
 * Created by Rancune@126.com 2018/7/11.
 */
public class OesFilter extends SimpleRenderer {

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

    private GLProgram mGLProgram;
    private int mPositionIndex;
    private int mCoordIndex;
    private int mMatrixIndex;
    private int mCoordMatrixIndex;
    private int mTexture;



    public OesFilter(Context context) {
        super(context);
        mVertexBuffer = GLHelper.createFloatBuffer(mVertices);
        mCoordBuffer = GLHelper.createFloatBuffer(mCoords);
        mGLProgram = GLProgram.of(GLHelper.readFromAssets(mContext, "shader/oes_base_vertex.vert"),
                GLHelper.readFromAssets(mContext, "shader/oes_base_fragment.frag"));
        mPositionIndex = mGLProgram.getAttributeLocation("vPosition");
        mCoordIndex = mGLProgram.getAttributeLocation("vCoord");
        mMatrixIndex = mGLProgram.getUniformLocation("vMatrix");
        mCoordMatrixIndex = mGLProgram.getUniformLocation("vCoordMatrix");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        int texture = createTextureId();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
    }

    @Override
    public void draw() {
        super.draw();
    }

    private int createTextureId() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
