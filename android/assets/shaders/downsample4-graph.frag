#version 330

precision mediump float;

uniform sampler2D u_texture;
uniform vec2 u_texSize;

in vec4 v_color;
in vec2 v_texCoords;

out vec4 fragColor;

void main()
{
    ivec2 quadPosMain = ivec2(round(2.0 * v_texCoords * u_texSize));
    quadPosMain.x = (quadPosMain.x % 2 == 0) ? quadPosMain.x : quadPosMain.x - 1;
    quadPosMain.y = (quadPosMain.y % 2 == 0) ? quadPosMain.y : quadPosMain.y - 1;

    vec4[4] quad = vec4[](texelFetch(u_texture, quadPosMain, 0),
                          texelFetch(u_texture, quadPosMain + ivec2(1.0, 0.0), 0),
                          texelFetch(u_texture, quadPosMain + ivec2(0.0, 1.0), 0),
                          texelFetch(u_texture, quadPosMain + ivec2(1.0, 1.0), 0));
    int counter = 0;

    fragColor = vec4(0.0, 0.0, 0.0, 0.0);

    for (int i = 0; i < 4; i++) {
        if (quad[i].a != 0.0) {
            fragColor += quad[i];
            counter++;
        }
    }

    fragColor.rgb /= (counter != 0) ? counter : 1;
    fragColor.a *= 0.25;
}