#version 430

in vec3 varyingNormal, varyingLightDir, varyingVertPos, varyingHalfVec;
in vec4 shadow_coord;
out vec4 fragColor;

in vec2 tc;
 
struct PositionalLight
{	vec4 ambient, diffuse, specular;
	vec3 position;
};

struct Material
{	vec4 ambient, diffuse, specular;
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
uniform mat4 shadowMVP;

layout (binding = 0) uniform sampler2DShadow shadowTex;
layout(binding = 1) uniform sampler2D s;

void main(void)
{	vec3 L = normalize(varyingLightDir);
	vec3 N = normalize(varyingNormal);
	vec3 V = normalize(-v_matrix[3].xyz * mat3(v_matrix) - varyingVertPos); // Corrected line pulled from the professor's instructions
	vec3 H = normalize(varyingHalfVec);

	// get the angle between the light and surface normal:
	float cosTheta = dot(L,N);
	// get angle between the normal and the halfway vector
	float cosPhi = dot(H,N);
	
	float notInShadow = textureProj(shadowTex, shadow_coord);
	
	vec4 texColor = texture(s, tc);
	vec3 ambient = (globalAmbient + light.ambient).xyz * material.ambient.xyz;
	fragColor = vec4(texColor.rgb * ambient, texColor.a);

	// For testing
	//notInShadow = 1.0;

	if (notInShadow == 1.0) {
		// compute ADS contributions (per pixel):
		vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
		vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta,0.0);
		vec3 specular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi,0.0), material.shininess);

		vec3 resultColor = texColor.rgb * (ambient + diffuse) + specular;

		fragColor = vec4(resultColor, texColor.a);
	}
}
