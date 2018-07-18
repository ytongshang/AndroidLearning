precision mediump float;

uniform sampler2D uTexture;
uniform float uSaturation;

varying vec2 vTextureCoordinate;

const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);

void main() {
    vec4 textureColor = texture2D(uTexture, vTextureCoordinate);
    float luminance = dot(textureColor.rgb, luminanceWeighting);
    vec3 greyScaleColor = vec3(luminance);
    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, uSaturation), textureColor.w);
}