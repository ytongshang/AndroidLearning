package cradle.rancune.learningandroid.opengl.ui;

import cradle.rancune.learningandroid.opengl.SimpleRenderer;
import cradle.rancune.learningandroid.opengl.renderer.BasicRenderer;

/**
 * Created by Rancune@126.com 2018/7/3.
 */
public class OpenGL01Activity extends OpenGLBaseActivity {

    @Override
    protected Class<? extends SimpleRenderer> createRenderer() {
        return BasicRenderer.class;
    }
}
