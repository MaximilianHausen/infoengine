#version 460

layout(location = 0) in vec2 size;
layout(location = 1) in mat4 modelMatrix;

layout(location = 0) out vec2 fragTexCoord;

layout(push_constant) uniform constants {
    mat4 view;
    mat4 proj;
} cameraMatrices;

vec2 positions[6] = vec2[](
vec2(-1, -1), vec2(1, -1), vec2(1, 1),
vec2(1, 1), vec2(-1, 1), vec2(-1, -1)
);

vec2 texCoords[6] = vec2[](
vec2(0, 0), vec2(1, 0), vec2(1, 1),
vec2(1, 1), vec2(0, 1), vec2(0, 0)
);

void main() {
    //gl_Position = cameraMatrices.proj * cameraMatrices.view * modelMatrix * vec4(positions[gl_VertexIndex] * size, 0.0, 1.0);
    gl_Position = vec4(positions[gl_VertexIndex] * vec2(0.1, 0.1), 0.0, 1.0);

    fragTexCoord = texCoords[gl_VertexIndex];
}