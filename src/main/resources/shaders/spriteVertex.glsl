#version 330 core

layout (location = 0) in vec4 vertex; // <vec2 position, vec2 texCoords>

out vec2 TexCoords;

uniform mat4 projection;
uniform mat4 model;

uniform sampler2D spriteTexture;
uniform vec4 textureOffset;

void main()
{
	TexCoords = textureOffset.xy + vec2(textureOffset.z * vertex.z, textureOffset.w * vertex.w);
	gl_Position = projection * model * vec4(vertex.xy, 0.0, 1.0);
}