#version 430

in vec4 redGreenBlue;
out vec4 color;
in vec2 tc;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;
layout (binding=0) uniform sampler2D samp;

void main(void)
{	
    color = redGreenBlue;
}
