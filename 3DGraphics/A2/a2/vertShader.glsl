#version 430

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;
layout (binding=0) uniform sampler2D samp;

out vec4 varyingColor;
out vec2 tc;

void main(void)
{
	gl_Position = p_matrix * mv_matrix * vec4(position,1.0);

	tc = texCoord;
	
	varyingColor = vec4(position,1.0)*0.5 + vec4(0.5, 0.5, 0.5, 0.5);
}
