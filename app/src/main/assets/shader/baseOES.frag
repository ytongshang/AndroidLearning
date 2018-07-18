#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uOESTexture;
varying vec2 vTextureCoordinate;
void main() {
    gl_FragColor = texture2D(uOESTexture, vTextureCoordinate);
}