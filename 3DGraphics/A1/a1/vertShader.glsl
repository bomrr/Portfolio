#version 430

uniform float offsetx;
uniform float offsety;

uniform vec4 v1Size;
uniform vec4 v2Size;
uniform vec4 v3Size;

uniform vec4 v1Color;
uniform vec4 v2Color;
uniform vec4 v3Color;

out vec4 varyingColor;

void main(void)
{ if (gl_VertexID == 0) { 
  gl_Position = v1Size + vec4(offsetx, offsety, 0.0, 0.0);
  varyingColor = v1Color;
  }
  else if (gl_VertexID == 1) {
    gl_Position = v2Size + vec4(offsetx, offsety, 0.0, 0.0);
    varyingColor = v2Color;
    }

  else {
    gl_Position = v3Size +vec4(offsetx, offsety, 0.0, 0.0);
    varyingColor = v3Color;
    }

}