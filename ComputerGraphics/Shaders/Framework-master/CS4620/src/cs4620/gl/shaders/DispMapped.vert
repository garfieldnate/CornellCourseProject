#version 120

// Note: We multiply a vector with a matrix from the left side (M * v)!
// mProj * mView * mWorld * pos

// RenderCamera Input
uniform mat4 mViewProjection;

// RenderObject Input
uniform mat4 mWorld;
uniform mat3 mWorldIT;

// RenderMesh Input
attribute vec4 vPosition; // Sem (POSITION 0)
attribute vec3 vNormal; // Sem (NORMAL 0)
attribute vec2 vUV; // Sem (TEXCOORD 0)

// Shading Information
uniform float dispMagnitude;

varying vec2 fUV;
varying vec3 fN; // normal at the vertex
varying vec4 worldPos; // vertex position in world-space coordinates

void main() {
	// TODO A4: Implement displacement mapping vertex shader
	//Compute height map value
	vec3 height = getNormalColor(vUV).xyz;
	float heightMap = (height.x + height.y + height.z) / 3 * dispMagnitude;
	//implement height map
	vec4 trans = vec4(heightMap * vNormal.x, heightMap * vNormal.y, heightMap * vNormal.z, 0);
	vec4 Position = vPosition + trans;
	// Calculate Point In World Space
    worldPos = mWorld * Position;
    // Calculate Projected Point
    gl_Position = mViewProjection * worldPos;

    // We have to use the inverse transpose of the world transformation matrix for the normal
    fN = normalize((mWorldIT * vNormal).xyz);
    fUV = vUV;
}
