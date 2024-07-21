#version 330 core

layout (location = 0) in vec4 vertex; // <vec2 position, vec2 texCoords>

out vec2 TexCoords;

uniform mat4 projection;
uniform mat4 model;

uniform vec2 atlasCoordinates;
uniform vec2 atlasCellDimensions;

void main()
{
	TexCoords = atlasCoordinates + vec2(atlasCellDimensions.x * vertex.z, atlasCellDimensions.y * vertex.w);
	gl_Position = projection * model * vec4(vertex.xy, 0.0, 1.0);
}