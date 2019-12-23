attribute vec4 aPosition;
attribute vec2 aTextureCoordinate;
varying vec2 vTextureCoordinate;

void main() {
    gl_Position = aPosition;
    vTextureCoordinate = aTextureCoordinate;
}