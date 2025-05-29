#version 430

in vec3 varyingNormal, varyingLightDir, varyingVertPos, varyingHalfVec;
in vec4 shadow_coord;
out vec4 fragColor;

in vec3 varyingTangent;
in vec3 originalVertex;
in vec3 varyingHalfVector;

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
layout (binding = 2) uniform sampler2D t;

vec3 calcNewNormal()
{
	vec3 normal = normalize(varyingNormal);
	vec3 tangent = normalize(varyingTangent);
	tangent = normalize(tangent - dot(tangent, normal) * normal);
	vec3 bitangent = cross(tangent, normal);
	mat3 tbn = mat3(tangent, bitangent, normal);
	vec3 retrievedNormal = texture(t,tc).xyz;
	retrievedNormal = retrievedNormal * 2.0 - 1.0;
	vec3 newNormal = tbn * retrievedNormal;
	newNormal = normalize(newNormal);
	return newNormal;
}

void main(void)
{	vec3 L = normalize(varyingLightDir);
	vec3 V = normalize(-v_matrix[3].xyz * mat3(v_matrix) - varyingVertPos);
	vec3 H = normalize(varyingHalfVec);

    vec3 N = calcNewNormal();

    // compute light reflection vector, with respect N:
	vec3 R = normalize(reflect(-L, N));

	// get the angle between the light and surface normal:
	float cosTheta = dot(L,N);

	// angle between the view vector and reflected light:
	float cosPhi = dot(V,R);
	
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
