precision mediump float;

uniform sampler2D u_Texture;
uniform float u_Saturation;

varying vec2 v_TextureCoordinate;

const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);

void main() {
    vec4 textureColor = texture2D(u_Texture, v_TextureCoordinate);
    float luminance = dot(textureColor.rgb, luminanceWeighting);
    vec3 greyScaleColor = vec3(luminance);
    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, u_Saturation), textureColor.w);
}