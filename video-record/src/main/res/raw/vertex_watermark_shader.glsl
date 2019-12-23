attribute vec4 aPosition;
uniform mat4 uPositionMatrix;
attribute vec2 aTextureCoordinate;
varying vec2 vTextureCoordinate;

void main() {
    gl_Position = uPositionMatrix * aPosition;
    vTextureCoordinate = aTextureCoordinate;
}