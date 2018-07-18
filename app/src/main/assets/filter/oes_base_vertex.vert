attribute vec4 a_Position;
attribute vec2 a_TextureCoordinate;
uniform mat4 u_Matrix;
uniform mat4 u_CoordMatrix;
varying vec2 v_TextureCoordinate;

void main(){
    gl_Position = u_Matrix*a_Position;
    v_TextureCoordinate = (u_CoordMatrix*vec4(a_TextureCoordinate,0,1)).xy;
}