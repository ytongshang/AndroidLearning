attribute vec4 aPosition;
attribute vec2 aTextureCoordinate;

uniform mat4 uMatrix;
uniform mat4 uTextureCoordMatrix;

varying vec2 vTextureCoordinate;

void main() {
    gl_Position=uMatrix*aPosition;
    vTextureCoordinate=(uTextureCoordMatrix*vec4(aTextureCoordinate,0,1)).xy;
}