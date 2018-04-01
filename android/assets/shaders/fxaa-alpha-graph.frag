#version 330

precision mediump float;

uniform sampler2D u_texture;
uniform vec2 u_texSize;
uniform float u_spanMax;
uniform float u_reduceMin;
uniform float u_reduceMul;

in vec4 v_color;
in vec2 v_texCoords;

out vec4 fragColor;

void main()
{
    vec2 texelSize = vec2(1.0 / u_texSize.x, 1.0 / u_texSize.y);

    vec4 textureM = texture(u_texture, v_texCoords);
    vec4 textureTL = texture(u_texture, v_texCoords + vec2(-1.0, -1.0) * texelSize);
    vec4 textureTR = texture(u_texture, v_texCoords + vec2(1.0, -1.0) * texelSize);
    vec4 textureBL = texture(u_texture, v_texCoords + vec2(-1.0, 1.0) * texelSize);
    vec4 textureBR = texture(u_texture, v_texCoords + vec2(1.0, 1.0) * texelSize);

    vec2 dir;
    dir.x = -(textureTL.a + textureTR.a) + (textureBL.a + textureBR.a);
    dir.y = (textureTL.a + textureBL.a) - (textureTR.a + textureBR.a);

    float avg = max((textureTL.a + textureTR.a + textureBL.a + textureBR.a) * u_reduceMul * 0.25, u_reduceMin);
    float mul = 1.0 / (min(abs(dir.x), abs(dir.y)) + avg);

    dir = clamp(dir * mul, vec2(-u_spanMax, -u_spanMax), vec2(u_spanMax, u_spanMax)) * texelSize;

    vec4 result1 = 0.5 * (texture(u_texture, v_texCoords + dir * vec2(1.0 / 3.0 - 0.5)) +
                    texture(u_texture, v_texCoords + dir * vec2(2.0 / 3.0 - 0.5)));

    vec4 result2 = 0.5 * (texture(u_texture, v_texCoords + dir * vec2(0.0 / 3.0 - 0.5)) +
                    texture(u_texture, v_texCoords + dir * vec2(3.0 / 3.0 - 0.5)));

    vec4 result = 0.5 * (result1 + result2);

    float alphaMin = min(textureM.a, min(min(textureTL.a, textureTR.a), min(textureBL.a, textureBR.a)));
    float alphaMax = max(textureM.a, max(max(textureTL.a, textureTR.a), max(textureBL.a, textureBR.a)));
    float alphaResult = result.a;

    fragColor = (alphaResult < alphaMin || alphaResult > alphaMax) ? result1 : result2;
}