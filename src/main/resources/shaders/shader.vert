#version 460

layout(location = 0) in int in_texIndex;
layout(location = 1) in vec2 in_size;
layout(location = 2) in mat4 in_modelMatrix;

layout(location = 0) flat out int out_texIndex;
layout(location = 1) out vec2 out_texCoord;

layout(push_constant) uniform constants {
    mat4 view;
    mat4 proj;
} cameraMatrices;

vec2 positions[6] = vec2[](
vec2(-1.0, -1.0), vec2(1.0, -1.0), vec2(1.0, 1.0),
vec2(1.0, 1.0), vec2(-1.0, 1.0), vec2(-1.0, -1.0)
);

vec2 texCoords[6] = vec2[](
vec2(0.0, 0.0), vec2(1.0, 0.0), vec2(1.0, 1.0),
vec2(1.0, 1.0), vec2(0.0, 1.0), vec2(0.0, 0.0)
);

void main() {
    gl_Position = cameraMatrices.proj * cameraMatrices.view * in_modelMatrix * vec4(positions[gl_VertexIndex] * in_size, 0.0, 1.0);

    out_texIndex = in_texIndex;
    out_texCoord = texCoords[gl_VertexIndex];
}
