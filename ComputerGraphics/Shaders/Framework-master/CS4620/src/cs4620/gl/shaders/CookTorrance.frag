#version 120

// You May Use The Following Functions As RenderMaterial Input
// vec4 getDiffuseColor(vec2 uv)
// vec4 getNormalColor(vec2 uv)
// vec4 getSpecularColor(vec2 uv)

// Lighting Information
#define M_PI 3.1415926535897932384626433832795
#define euler 2.71828182845904523536
const int MAX_LIGHTS = 16;
uniform int numLights;
uniform vec3 lightIntensity[MAX_LIGHTS];
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 ambientLightIntensity;

// Camera Information
uniform vec3 worldCam;
uniform float exposure;

// Shading Information
// 0 : smooth, 1: rough
uniform float roughness;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world coordinates

void main()
{
    // TODO A4: Implement reflection mapping fragment shader
    // interpolating normals will change the length of the normal, so renormalize the normal.
    vec3 N = normalize(fN);
    vec3 V = normalize(worldCam - worldPos.xyz);
    
    vec4 finalColor = vec4(0.0, 0.0, 0.0, 0.0);

    for (int i = 0; i < numLights; i++) {
      float r = length(lightPosition[i] - worldPos.xyz);
      vec3 L = normalize(lightPosition[i] - worldPos.xyz); 
      vec3 H = normalize(L + V);

      // calculate diffuse term
      vec4 Idiff = getDiffuseColor(fUV) * max(dot(N, L), 0.0);
      Idiff = clamp(Idiff, 0.0, 1.0);

      // calculate specular term
      float Fre = 0.04 + (1.0 - 0.04) * pow(1.0 - dot(V, H), 5.0);//Fresnel term
      float Dis = exp((pow(dot(N, H), 2.0) - 1.0) / (pow(roughness, 2.0) * pow(dot(N, H), 2.0))) / (pow(roughness, 2.0) * pow(dot(N, H), 4.0));//Microfacet distribution
      float Geo = min(min(1.0, 2.0 * dot(N, H) * dot(N, V) / dot(V, H)), 2.0 * dot(N, H) * dot(N, L) / dot(V, H));//Geometric attenuation
      float Tem = Fre * Dis * Geo / (M_PI * dot(N, V) * dot(N, L));
      vec4 Ispec = getSpecularColor(fUV) * Tem * max(dot(N, L), 0.0);
      Ispec = clamp(Ispec, 0.0, 1.0);
      
      // calculate ambient term
      vec4 Iamb = getDiffuseColor(fUV);
      Iamb = clamp(Iamb, 0.0, 1.0);

      finalColor += vec4(lightIntensity[i], 0.0) * (Idiff + Ispec) / (r*r) + vec4(ambientLightIntensity, 0.0) * Iamb;
    }

    gl_FragColor = finalColor * exposure; 
}