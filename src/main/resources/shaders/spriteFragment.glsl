#version 330 core

in vec2 vertTextureCoordinates;
out vec4 color;

uniform sampler2D spriteSheet;

void main()
{
	color = texture(spriteSheet, vertTextureCoordinates);
}