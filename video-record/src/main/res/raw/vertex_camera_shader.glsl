attribute vec4 aPosition;
uniform mat4 uTextureMatrix;
attribute vec4 aTextureCoordinate;
varying vec2 vTextureCoordinate;

void main() {
    gl_Position = aPosition;
    vTextureCoordinate = (uTextureMatrix * aTextureCoordinate).xy;
}