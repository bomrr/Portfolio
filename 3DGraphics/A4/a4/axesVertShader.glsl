#version 430

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;

const vec4 vertices[6] = vec4[6]
	(vec4(0.0,0.0,0.0, 1.0), // Midpoint
	vec4( 3.0,0.0,0.0, 1.0), // X
	vec4( 0.0,0.0,0.0, 1.0), // Midpoint
	vec4( 0.0,3.0,0.0, 1.0), // Y
	vec4( 0.0,0.0,0.0, 1.0), // Midpoint
	vec4( 0.0,0.0,3.0, 1.0)  // Z
);

const vec4 axesColors[6] = vec4[6] (
	vec4(1.0, 0.0, 0.0, 1.0), // Red
    vec4(1.0, 0.0, 0.0, 1.0), // Red
	vec4(0.0, 1.0, 0.0, 1.0), // Green
	vec4(0.0, 1.0, 0.0, 1.0), // Green
	vec4(0.0, 0.0, 1.0, 1.0), // Blue
	vec4(0.0, 0.0, 1.0, 1.0) // Blue
);

layout (binding=0) uniform sampler2D samp;

out vec4 redGreenBlue;
out vec2 tc;

void main(void)
{
	gl_Position = p_matrix * mv_matrix * vertices[gl_VertexID];

	redGreenBlue = axesColors[gl_VertexID];
}
