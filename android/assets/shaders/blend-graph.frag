#version 330

precision mediump float;

uniform sampler2D u_texture;
uniform sampler2D u_background;

in vec4 v_color;
in vec2 v_texCoords;

out vec4 fragColor;

vec4 blendColors(vec4 src, vec4 dest)
{
    return vec4(src.rgb * src.a + dest.rgb * dest.a * (1.0 - src.a), src.a + dest.a * (1.0 - src.a));
}

void main()
{
    vec4 backgroundColor = texture(u_background, v_texCoords);
    vec4 textureColor = texture(u_texture, v_texCoords);

    fragColor = v_color * vec4(blendColors(textureColor, backgroundColor).rgb, 1.0);
}