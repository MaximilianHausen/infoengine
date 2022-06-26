#version 460

#define MAX_TEXTURES 128

layout(binding = 0) uniform sampler2D texSampler[MAX_TEXTURES];

layout(location = 0) flat in int in_texIndex;
layout(location = 1) in vec2 in_texCoord;

layout(location = 0) out vec4 fragColor;

void main() {
    fragColor = texture(texSampler[in_texIndex], in_texCoord);
}
