precision mediump float;

varying mediump vec2 vTextureCoordinate;
uniform sampler2D uTextureSampler;

uniform mediump float uWidth;
uniform mediump float uHeight;
uniform mediump float uOpacity;
uniform mediump float uBrightness;
uniform mediump float uTone;

const highp vec3 grayMatrix = vec3(0.299, 0.587, 0.114);
vec2 blurCoordinates[20];

const highp mat3 saturateMatrix = mat3(
            1.1102, -0.0598, -0.061,
            -0.0774, 1.0826, -0.1186,
            -0.0228, -0.0228, 1.1772
            );

float hardLight(float color) {
    if(color <= 0.5)
        color = color * color * 2.0;
    else
        color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);
    return color;
}

void main() {

    vec3 centralColor = texture2D(uTextureSampler, vTextureCoordinate).rgb;

    vec2 singleStepOffset = vec2(2.0 / uWidth, 2.0 / uHeight);
    blurCoordinates[0] = vTextureCoordinate.xy + singleStepOffset * vec2(0.0, -10.0);
    blurCoordinates[1] = vTextureCoordinate.xy + singleStepOffset * vec2(0.0, 10.0);
    blurCoordinates[2] = vTextureCoordinate.xy + singleStepOffset * vec2(-10.0, 0.0);
    blurCoordinates[3] = vTextureCoordinate.xy + singleStepOffset * vec2(10.0, 0.0);
    blurCoordinates[4] = vTextureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = vTextureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);
    blurCoordinates[6] = vTextureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[7] = vTextureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);
    blurCoordinates[8] = vTextureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = vTextureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = vTextureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = vTextureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = vTextureCoordinate.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = vTextureCoordinate.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = vTextureCoordinate.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = vTextureCoordinate.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = vTextureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = vTextureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = vTextureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);
    blurCoordinates[19] = vTextureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);

    float sampleColor = centralColor.g * 20.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[0]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[1]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[2]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[3]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[4]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[5]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[6]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[7]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[8]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[9]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[10]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[11]).g;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[12]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[13]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[14]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[15]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[16]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[17]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[18]).g * 2.0;
    sampleColor += texture2D(uTextureSampler, blurCoordinates[19]).g * 2.0;

    sampleColor = sampleColor / 48.0;

    float highPass = centralColor.g - sampleColor + 0.5;

    for(int i = 0; i < 5; i++) {
        highPass = hardLight(highPass);
    }
    float luminance = dot(centralColor, grayMatrix);

    float alpha = pow(luminance, uOpacity);

    vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;

    // 模糊
    vec4 color;

    if (uOpacity < 0.05) {
        color = vec4(centralColor, 1.0);
    } else {
        color = vec4(mix(smoothColor.rgb, max(smoothColor, centralColor), alpha), 1.0);
    }

    vec3 satcolor = color.rgb * saturateMatrix;

    if(uTone > 0.05) {
        // 色调
        color = vec4(mix(color.rgb, satcolor, uTone), color.a);
    }

    // 明亮
    color = vec4((color.rgb + vec3(uBrightness)), color.a);

    gl_FragColor = color;

}