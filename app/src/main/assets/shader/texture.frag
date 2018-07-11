precision mediump float;
uniform sampler2D sampleTexture;
varying vec2 vCoordinate;
void main() {
  gl_FragColor=texture2D(sampleTexture,vCoordinate);
}