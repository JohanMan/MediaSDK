precision mediump float;

uniform sampler2D uTextureSampler;
varying vec2 vTextureCoordinate;

void main() {
    gl_FragColor = texture2D(uTextureSampler, vTextureCoordinate);
}