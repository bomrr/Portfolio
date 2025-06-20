#version 430

layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec2 tex_coord;
layout (location = 2) in vec3 vertNormal;

out vec3 varyingNormal;
out vec3 varyingLightDir;
out vec3 varyingVertPos;
out vec3 varyingHalfVector;

out vec2 tc;

struct PositionalLight
{	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec3 position;
};
struct Material
{	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	float shininess;
};

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 mv_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;

void main(void)
{	varyingVertPos = (mv_matrix * vec4(vertPos,1.0)).xyz;
	varyingLightDir = light.position - varyingVertPos;
	varyingNormal = (norm_matrix * vec4(vertNormal,1.0)).xyz;
	
	varyingHalfVector =
		normalize(normalize(varyingLightDir)
		+ normalize(-varyingVertPos)).xyz;

	tc = tex_coord;
	gl_Position = p_matrix * mv_matrix * vec4(vertPos,1.0);
}
