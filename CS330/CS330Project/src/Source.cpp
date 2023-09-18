#include <iostream>
#include <random>
#include <cstdlib>
#include <GL/glew.h>
#include <GLFW/glfw3.h>

#define STB_IMAGE_IMPLEMENTATION
#include <stb_image.h>

#include <glm/glm.hpp>
#include <glm/gtx/transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include "meshes.h"

// Unnamed namespace holds local variables, window variables, and GLMesh struct
namespace {
	// GLFW window parameters
	const char* const WINDOW_TITLE = "CS300-Project";
	const int WINDOW_WIDTH = 800;
	const int WINDOW_HEIGHT = 600;
	// Vectors hold camera location, up vector, and front vector
	glm::vec3 gCameraPos = glm::vec3(0.0f, 0.0f, 3.0f);
	glm::vec3 gCameraFront = glm::vec3(0.0f, 0.0f, -1.0f);
	glm::vec3 gCameraUp = glm::vec3(0.0f, 1.0f, 0.0f);
	// Variables are used to calculate time change to regulate update speeds
	float gDeltaTime = 0.0f;
	float gLastFrame = 0.0f;
	// Variables used to update camera position based on mouse input
	float gLastX = WINDOW_WIDTH / 2.0f;
	float gLastY = WINDOW_HEIGHT / 2.0f;
	float gPitch = 0.0f, gYaw = -90.0f;
	bool gFirstMouse = true;
	float cameraSpeed = 2.5f;
	bool perspective = true;
	// Variables are used to load texture
	GLuint woodTextureID;
	GLuint mouseTextureID;
	GLuint mouseWheelTextureID;
	GLuint tabletFrontTextureID;
	GLuint tabletBackTextureID;
	GLuint blackTextureID;
	GLuint pencilHolderTextureID;
	GLuint whiteMarkerTextureID;
	const char* woodFileName = "wood-top.jpg";
	const char* mouseFileName = "mouse-texture.jpg";
	const char* mouseWheelFileName = "mouse-wheel.jpg";
	const char* tabletFrontFileName = "tablet.png";
	const char* tabletBackFileName = "silver-texture.jpg";
	const char* blackTextureFileName = "black-texture.jpg";
	const char* pencilHolderFileName = "pencil-holder.png";
	const char* whiteMarkerFileName = "white-marker.jpg";
	// Local Objects
	GLFWwindow* gWindow = nullptr;
	GLuint gProgramID;
	GLuint gLightShaderID;
	Meshes meshes;
	// Light Source Variables
	glm::vec3 gLightColor0(1.0f, 1.0f, 1.0f);
	glm::vec3 gLightPosition0(4.0f, 7.0f, 10.0f);
	glm::vec3 gLightColor1(0.461f, 0.960f, 0.902f);
	glm::vec3 gLightPosition1(2.5f, 1.5f, -1.499f);
}

// Function Declarations
bool UInitialize(int argc, char* argv[], GLFWwindow** window);
void UProcessInput(GLFWwindow* window);
void UResizeWindow(GLFWwindow* window, int width, int height);
bool UCreateShaderProgram(const char* vtxShaderSource, const char* fragShaderSource, GLuint& programID);
void UDestroyShaderProgram(GLuint programID);
void URender();
bool UTextureInit();
void UMouseScrollCallback(GLFWwindow* window, double xoffset, double yoffset);
void UMouseCallback(GLFWwindow* window, double mousexpos, double mouseypos);
void UKeyCallback(GLFWwindow* window, int key, int scancode, int action, int mods);
bool UCreateTexture(const char* filename, GLuint& textureID);
void flipImageVertically(unsigned char* image, int width, int height, int channels);

// Vertex Shader for all objects other than light source
// Inputs vertex position, normal vectors, and texture positions.
// Outputs vertex location based on matrix transformations and forwards normal/texture data to fragment shader
const char* vertexShaderSource =
"#version 440 core\n"
"layout (location = 0) in vec3 aPos;\n"
"layout (location = 1) in vec3 aNormal;\n"
"layout (location = 2) in vec2 texturePos;\n"
"out vec2 texPos;\n"
"out vec3 normal;\n"
"out vec3 vertexFragmentPos;\n"
"uniform mat4 model;\n"
"uniform mat4 view;\n"
"uniform mat4 projection;\n"
"void main()\n"
"{\n"
"   gl_Position = projection * view * model * vec4(aPos.x, aPos.y, aPos.z, 1.0);\n"
"   texPos = texturePos;\n"
"   vertexFragmentPos = vec3(model * vec4(aPos, 1.0f));\n"
"   normal = mat3(transpose(inverse(model))) * aNormal;\n"
"}\n\0";

// Fragment shader for all objects other than light source
// Inputs texture position, normal values, and light source data
// Outputs fragment color based on texture values and light calculations
const char* fragmentShaderSource =
"#version 440 core\n"
"struct Material {\n"
"   float ambient;\n"
"   float diffuse;\n"
"   float specular;\n"
"   float shininess;\n"
"};\n"
"uniform Material material;\n"
"out vec4 FragColor;\n"
"in vec2 texPos;\n"
"in vec3 normal;\n"
"in vec3 vertexFragmentPos;\n"
"uniform sampler2D uTexture;\n"
"uniform vec3 lightColor0;\n"
"uniform vec3 lightPos0;\n"
"uniform vec3 lightColor1;\n"
"uniform vec3 lightPos1;\n"
"uniform vec3 viewPosition;\n"
"void main()\n"
"{\n"
"   vec3 ambient = material.ambient * lightColor0;\n"

"   vec3 norm = normalize(normal);\n"
"   vec3 lightDirection = normalize(lightPos0 - vertexFragmentPos);\n"
"   float impact = max(dot(norm, lightDirection), 0.0);\n"
"   vec3 diffuse0 = (impact * material.diffuse) * lightColor0;\n"

"   vec3 viewDir = normalize(viewPosition - vertexFragmentPos);\n"
"   vec3 reflectDir = reflect(-lightDirection, norm);\n"
"   float specularComponent = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);\n"
"   vec3 specular0 = (material.specular * specularComponent) * lightColor0;\n"

"   norm = normalize(normal);\n"
"   lightDirection = normalize(lightPos1 - vertexFragmentPos);\n"
"   impact = max(dot(norm, lightDirection), 0.0);\n"
"   vec3 diffuse1 = (impact * material.diffuse) * lightColor1;\n"

"   viewDir = normalize(viewPosition - vertexFragmentPos);\n"
"   reflectDir = reflect(-lightDirection, norm);\n"
"   specularComponent = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);\n"
"   vec3 specular1 = (material.specular * specularComponent) * lightColor1;\n"

"   vec4 texColor = texture(uTexture, texPos);\n"
"   if (texColor.a < 0.1)\n"
"      discard;\n"
"   FragColor = vec4((ambient + diffuse0 + diffuse1 + specular0 + specular1) * texColor.rgb, texColor.a);\n"
"}\n\0";

// Vertex Shader light sources
// Inputs vertex position
// Outputs vertex location based on matrix transformations
const char* lightVertexShader =
"#version 440 core\n"
"layout (location = 0) in vec3 aPos;\n"
"uniform mat4 model;\n"
"uniform mat4 view;\n"
"uniform mat4 projection;\n"
"void main()\n"
"{\n"
"   gl_Position = projection * view * model * vec4(aPos.x, aPos.y, aPos.z, 1.0);\n"
"}\n\0";

// Fragment shader for light sources
// Outputs Bright white light for all fragments
const char* lightFragmentShader =
"#version 440 core\n"
"out vec4 FragColor;\n"
"void main()\n"
"{\n"
"   FragColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);\n"
"}\n\0";

// Entry point of application
int main(int argc, char* argv[])
{
	// Call to UInitialize to create GLFW window and initialize GLEW
	if (!UInitialize(argc, argv, &gWindow)) //
	{
		return EXIT_FAILURE;
	}
	if (UTextureInit() == EXIT_FAILURE)
	{
		return EXIT_FAILURE;
	}
	// Adds uniform value of texture to fragment shader
	glUniform1i(glGetUniformLocation(gProgramID, "uTexture"), 0);
	// Instantiate meshes object, which holds blueprint meshes for objects such as cylinders, spheres, planes, etc...
	meshes.CreateMeshes();

	// Enables depth testing for rendering fragments based on distance from camera
	glEnable(GL_DEPTH_TEST);
	// Render Loop: iterates for each output frame
	while (!glfwWindowShouldClose(gWindow))
	{
		// Calculates time between frames
		float currentFrame = glfwGetTime();
		gDeltaTime = currentFrame - gLastFrame;
		gLastFrame = currentFrame;
		// Process peripheral inputs
		UProcessInput(gWindow);

		// Render background and triangles to screen
		URender();

		// Checks for callback events such as changing the screen size
		glfwPollEvents();
	}
	// Destroy all meshes instantiated earlier in GLFW context
	meshes.DestroyMeshes();
	// Exits the application gracefully
	exit(EXIT_SUCCESS);
	return 0;
}

// Initialize GLFW, GLFW window, and GLEW
// Provides error checking if software does link correctly
bool UInitialize(int argc, char* argv[], GLFWwindow** window)
{
	// Initialize GLFW, sets current version (4.4), and sets current profile (Core)
	glfwInit();
	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
	glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
	glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

	// Helps apple users start GLFW window
#ifdef __APPLE__
	glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif
	// Create new GLFW window with initialized dimensions/title and assigns object to window pointer
	* window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
	// Report failure to initialze GLFW window
	if (*window == NULL)
	{
		std::cout << "Failed to create GLFW window" << std::endl;
		glfwTerminate();
		return false;
	}
	// Applies GLFW context to current thread for context change function calls
	glfwMakeContextCurrent(*window);
	// Changes to the frame buffer size (window size) calls the UResizeWindow function
	glfwSetFramebufferSizeCallback(*window, UResizeWindow);
	glfwSetInputMode(*window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	glfwSetScrollCallback(*window, UMouseScrollCallback);
	glfwSetCursorPosCallback(*window, UMouseCallback);
	glfwSetKeyCallback(*window, UKeyCallback);

	// Initialize GLEW
	glewExperimental = GL_TRUE;
	GLenum GlewInitResult = glewInit();
	// Report failure to initialize GLEW
	if (GLEW_OK != GlewInitResult)
	{
		std::cerr << glewGetErrorString(GlewInitResult) << std::endl;
		return false;
	}
	// Outputs current OpenGL version to console
	std::cout << "INFO: OpenGL Version: " << glGetString(GL_VERSION) << std::endl;
	return true;
}

// Process user input through GLFW keyboard enumerations
void UProcessInput(GLFWwindow* window)
{
	// Decision branching checks if window/key combination is pressed
	if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
	{
		glfwSetWindowShouldClose(window, true);
	}
	float cameraOffset = cameraSpeed * gDeltaTime;
	// Allows for camera movement
	if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
		gCameraPos += cameraOffset * gCameraFront;
	if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
		gCameraPos -= cameraOffset * gCameraFront;
	if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
		gCameraPos -= glm::normalize(glm::cross(gCameraFront, gCameraUp)) * cameraOffset;
	if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
		gCameraPos += glm::normalize(glm::cross(gCameraFront, gCameraUp)) * cameraOffset;
	if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
		gCameraPos += cameraOffset * gCameraUp;
	if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
		gCameraPos -= cameraOffset * gCameraUp;
}

bool UTextureInit() 
{
	// Produces ShaderProgram for use with triangles
	if (!UCreateShaderProgram(vertexShaderSource, fragmentShaderSource, gProgramID))
	{
		return EXIT_FAILURE;
	}
	// Produces ShaderProgram for light sources
	if (!UCreateShaderProgram(lightVertexShader, lightFragmentShader, gLightShaderID))
	{
		return EXIT_FAILURE;
	}
	// Creates texture object for use on objects
	if (!UCreateTexture(woodFileName, woodTextureID))
	{
		std::cout << "Failed to load texture " << woodFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(mouseFileName, mouseTextureID))
	{
		std::cout << "Failed to load texture " << mouseFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(mouseWheelFileName, mouseWheelTextureID))
	{
		std::cout << "Failed to load texture " << mouseWheelFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(tabletFrontFileName, tabletFrontTextureID))
	{
		std::cout << "Failed to load texture " << tabletFrontFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(tabletBackFileName, tabletBackTextureID))
	{
		std::cout << "Failed to load texture " << tabletBackFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(blackTextureFileName, blackTextureID))
	{
		std::cout << "Failed to load texture " << blackTextureFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(pencilHolderFileName, pencilHolderTextureID))
	{
		std::cout << "Failed to load texture " << blackTextureFileName << std::endl;
		return EXIT_FAILURE;
	}
	if (!UCreateTexture(whiteMarkerFileName, whiteMarkerTextureID))
	{
		std::cout << "Failed to load texture " << whiteMarkerFileName << std::endl;
		return EXIT_FAILURE;
	}
	return 0;
}

// Resize GLFW window viewport dimensions
void UResizeWindow(GLFWwindow* window, int width, int height)
{
	glViewport(0, 0, width, height);
}

// Function creates a ShaderProgram and provides a reference ID to the programID parameter
// This function creates a vertex shader and a fragment shader. The shaders a linked under a new ShaderProgram
// Checks are made for the creation of the vertexShader/fragmentShader and the linkage of the ShaderProgram
bool UCreateShaderProgram(const char* vtxShaderSource, const char* fragShaderSource, GLuint& programID)
{
	// Variables used for error checking
	int success = 0; // Value indicates whether compilation/linkage was successful
	char infoLog[512]; // Stores error log
	// Create ShaderProgram object and store ID in programID
	programID = glCreateProgram();
	// Create vertex/fragment shader objects and store them in respective ID's
	GLuint vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
	GLuint fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
	// Bind vertexShaderSource code/fragmentShaderSource code to respective shader objects
	glShaderSource(vertexShaderID, 1, &vtxShaderSource, NULL);
	glShaderSource(fragmentShaderID, 1, &fragShaderSource, NULL);
	// Compile vertexShader
	glCompileShader(vertexShaderID);
	// Check if compilation was successful and output error log
	glGetShaderiv(vertexShaderID, GL_COMPILE_STATUS, &success);
	if (!success)
	{
		glGetShaderInfoLog(vertexShaderID, 512, NULL, infoLog);
		std::cout << "ERROR::SHADER::VERTEX::COMPILATION_FAILED\n" << infoLog << std::endl;

		return false;
	}
	// Compile fragmentShader
	glCompileShader(fragmentShaderID);
	// Check if compilation was successful and output error log
	glGetShaderiv(fragmentShaderID, GL_COMPILE_STATUS, &success);
	if (!success)
	{
		glGetShaderInfoLog(fragmentShaderID, sizeof(infoLog), NULL, infoLog);
		std::cout << "ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n" << infoLog << std::endl;

		return false;
	}
	// Attach vertexShader and fragmentShader to ShaderProgram
	glAttachShader(programID, vertexShaderID);
	glAttachShader(programID, fragmentShaderID);
	// Link vertex/fragment shaders together for ShaderProgram
	glLinkProgram(programID);
	// Check if linkage was successful and output error log
	glGetProgramiv(programID, GL_LINK_STATUS, &success);
	if (!success)
	{
		glGetProgramInfoLog(programID, sizeof(infoLog), NULL, infoLog);
		std::cout << "ERROR::SHADER::PROGRAM::LINKING_FAILED\n" << infoLog << std::endl;

		return false;
	}
	// Delete vertex/fragment shaders to free memory
	glDeleteShader(vertexShaderID);
	glDeleteShader(fragmentShaderID);
	// Set ShaderProgram as current program for GLFW context
	glUseProgram(programID);
	return true;
}

// Function deletes the ShaderProgram specified by the parameter programID
void UDestroyShaderProgram(GLuint programID)
{
	glDeleteProgram(programID);
}

// Function renders the background color and the triangle objects to the screen
void URender()
{
	// Define local Variables
	glm::mat4 scale, rotation, translation, model, view, projection;
	GLint modelLoc, viewLoc, projLoc, colorLoc;
	// Specify current background color, clear current values in buffer, and replace buffer with new background color
	glClearColor(0.750f, 0.716f, 0.623f, 1.0f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	// Lighting Effects
	glUseProgram(gProgramID);
	GLint lightColorLoc0 = glGetUniformLocation(gProgramID, "lightColor0");
	glUniform3f(lightColorLoc0, gLightColor0.r, gLightColor0.g, gLightColor0.b);
	GLint lightPositionLoc0 = glGetUniformLocation(gProgramID, "lightPos0");
	glUniform3f(lightPositionLoc0, gLightPosition0.x, gLightPosition0.y, gLightPosition0.z);
	GLint lightColorLoc1 = glGetUniformLocation(gProgramID, "lightColor1");
	glUniform3f(lightColorLoc1, gLightColor1.r, gLightColor1.g, gLightColor1.b);
	GLint lightPositionLoc1 = glGetUniformLocation(gProgramID, "lightPos1");
	glUniform3f(lightPositionLoc1, gLightPosition1.x, gLightPosition1.y, gLightPosition1.z);
	GLint viewPositionLoc = glGetUniformLocation(gProgramID, "viewPosition");
	glUniform3f(viewPositionLoc, gCameraPos.x, gCameraPos.y, gCameraPos.z);

	// First Object: Tabletop
	
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, woodTextureID);
	// Orthographic or Perspective
	if (perspective)
	{
		projection = glm::perspective(glm::radians(45.0f), (GLfloat)WINDOW_WIDTH / (GLfloat)WINDOW_HEIGHT, 0.1f, 100.0f);
	}
	else
	{
		projection = glm::ortho(-5.0f, 5.0f, -3.75f, 3.75f, 0.1f, 100.0f);
	}
	// Scale object size
	scale = glm::scale(glm::vec3(4.0f, 0.2f, 3.0f));
	// Rotate object around selected axis in degrees
	rotation = glm::rotate(0.0f, glm::vec3(1.0f, 0.0f, 0.0f));
	// Translate object in specified direction
    translation = glm::translate(glm::vec3(0.0f, 0.0f, 0.0f));
	// Matrix represents object transition from object space to world space
	model = translation * rotation * scale;
	// Matrix represents location of camera
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	// Retrive uniform variable locations for the model, view, projection, and color.
	modelLoc = glGetUniformLocation(gProgramID, "model");
	viewLoc = glGetUniformLocation(gProgramID, "view");
	projLoc = glGetUniformLocation(gProgramID, "projection");
	// Send uniform matrix data to vertex shader
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	// Material Color Values
	GLint materialAmbientLoc = glGetUniformLocation(gProgramID, "material.ambient");
	GLint materialDiffuseLoc = glGetUniformLocation(gProgramID, "material.diffuse");
	GLint materialSpecularLoc = glGetUniformLocation(gProgramID, "material.specular");
	GLint materialShininess = glGetUniformLocation(gProgramID, "material.shininess");
	glUniform1f(materialAmbientLoc, 0.25f);
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.5f);
	glUniform1f(materialShininess, 32.0f);
	// Bind vertex array object for triangles
	glBindVertexArray(meshes.gPlaneMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gPlaneMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	// Unbind the VBO
	glBindVertexArray(0);

	// Second Object: Mouse
		
	// Mouse Body texture
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, mouseTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(0.847f, 0.363f, 0.484f));
	rotation = glm::rotate(glm::radians(80.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	translation = glm::translate(glm::vec3(0.95f, 0.0f, 1.7f));
	model = translation * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.01f);
	glUniform1f(materialShininess, 4.0f);
	glBindVertexArray(meshes.gSphereMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gSphereMesh.nIndices / 2, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);

	// Mouse wheel
    // Similar process to first object's code
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, mouseTextureID);
	scale = glm::scale(glm::vec3(0.242f, 0.121f, 0.242f));
	rotation = glm::rotate(glm::radians(90.0f), glm::vec3(1.0f, 0.0f, 0.0f));
	glm::mat4 rotation2 = glm::rotate(glm::radians(80.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	translation = glm::translate(glm::vec3(0.95f, 0.16f, 1.38f));
	model = translation * rotation2 * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.01f);
	glUniform1f(materialShininess, 4.0f);
	glBindVertexArray(meshes.gCylinderMesh.vao);
	glDrawArrays(GL_TRIANGLE_FAN, 0, 36);		//bottom
	glDrawArrays(GL_TRIANGLE_FAN, 36, 72);		//top
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, mouseWheelTextureID);
	glDrawArrays(GL_TRIANGLE_STRIP, 72, 146);	//sides
	glBindVertexArray(0);

	// Third Object: Tablet

	// Tablet Face
    // Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, tabletFrontTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(2.5f, 3.0f, 0.06f));
	rotation = glm::rotate(glm::radians(180.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	rotation2 = glm::rotate(glm::radians(-45.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	translation = glm::translate(glm::vec3(2.5f, 1.5f, -1.5f));
	model = translation * rotation2 * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.9f);
	glUniform1f(materialSpecularLoc, 0.8f);
	glUniform1f(materialShininess, 32.0f);
	glBindVertexArray(meshes.gBoxMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gPlaneMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);

	// Tablet Body
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, mouseTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(2.5f, 3.0f, 0.06f));
	rotation = glm::rotate(glm::radians(180.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	rotation2 = glm::rotate(glm::radians(-45.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	translation = glm::translate(glm::vec3(2.5f, 1.5f, -1.5f));
	model = translation * rotation2 * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialAmbientLoc, 0.85f);
	glUniform1f(materialDiffuseLoc, 0.9f);
	glUniform1f(materialSpecularLoc, 0.8f);
	glUniform1f(materialShininess, 32.0f);
	glBindVertexArray(meshes.gBoxMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gBoxMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);
	glUniform1f(materialAmbientLoc, 0.25f);

	// Fourth Object: Keyboard

	// Main Keyboard Body
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, mouseTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(5.875f, 0.15f, 2.5f));
	rotation = glm::rotate(glm::radians(30.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	rotation2 = glm::rotate(glm::radians(0.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	translation = glm::translate(glm::vec3(-1.2f, 0.06f, -0.6f));
	model = translation * rotation2 * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.5f);
	glUniform1f(materialShininess, 32.0f);
	glBindVertexArray(meshes.gBoxMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gBoxMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);

	// Keyboard hand rest
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, blackTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(5.875f, 0.15f, 1.0f));
	rotation = glm::rotate(glm::radians(30.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	rotation2 = glm::rotate(glm::radians(8.0f), glm::vec3(1.0f, 0.0f, 0.0f));
	translation = glm::translate(glm::vec3(-0.329f, 0.004f, 0.91f));
	model = translation * rotation * rotation2 * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.01f);
	glUniform1f(materialShininess, 4.0f);
	glBindVertexArray(meshes.gBoxMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gBoxMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);

	// Keyboard keys
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, blackTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(0.21f, 0.21f, 0.21f));
	rotation = glm::rotate(glm::radians(30.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.01f);
	glUniform1f(materialShininess, 4.0f);
	glBindVertexArray(meshes.gBoxMesh.vao);
	// Render multiple keys in an array
	for (int j= 0; j < 6; j++)
	{
		for (int i = 0; i < 11; i++)
		{
			translation = glm::translate(glm::vec3(-3.9f + (i * 0.449f) + (j * .16), 0.1f, -0.1f + (i * -0.247f) + (j * .27)));
			model = translation * rotation * scale;
			glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
			glDrawElements(GL_TRIANGLES, meshes.gBoxMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
		}
	}
	glBindVertexArray(0);

	// Sixth Object: Pencil Holder
	
	// Main pencil container
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, blackTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(0.65f, 1.7f, 0.65f));
	rotation = glm::rotate(glm::radians(0.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	translation = glm::translate(glm::vec3(-2.7f, 0.03f, -1.9f));
	model = translation * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.5f);
	glUniform1f(materialShininess, 32.0f);
	glBindVertexArray(meshes.gCylinderMesh.vao);
	glDrawArrays(GL_TRIANGLE_FAN, 0, 36);		//bottom
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, pencilHolderTextureID);
	glDrawArrays(GL_TRIANGLE_STRIP, 72, 146);	//sides
	glBindVertexArray(0);

	// Container dividers
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, pencilHolderTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(0.65f, 1.7f, 0.95f));
	rotation = glm::rotate(glm::radians(90.0f), glm::vec3(1.0f, 0.0f, 0.0f));
	translation = glm::translate(glm::vec3(-2.7f, 0.78f, -1.9f));
	model = translation * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.8f);
	glUniform1f(materialSpecularLoc, 0.5f);
	glUniform1f(materialShininess, 32.0f);
	glBindVertexArray(meshes.gPlaneMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gPlaneMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	scale = glm::scale(glm::vec3(0.95f, 0.75f, 0.325f));
	rotation = glm::rotate(glm::radians(90.0f), glm::vec3(0.0f, 0.0f, 1.0f));
	translation = glm::translate(glm::vec3(-2.7f, 0.78f, -1.58f));
	model = translation * rotation  * scale;
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glDrawElements(GL_TRIANGLES, meshes.gPlaneMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);

	// Markers
	// Bind current texture to openGL context
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, whiteMarkerTextureID);
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(0.07f, 1.0f, 0.07f));
	rotation = glm::rotate(glm::radians(0.0f), glm::vec3(0.0f, 1.0f, 0.0f));
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glUniform1f(materialDiffuseLoc, 0.85f);
	glUniform1f(materialSpecularLoc, 0.8f);
	glUniform1f(materialShininess, 32.0f);
	glBindVertexArray(meshes.gCylinderMesh.vao);
	int i = 0;
	while (i < 10)
	{
		switch (i) 
		{
		case 0:
			translation = glm::translate(glm::vec3(-2.5f, 0.03f, -1.7f));
			break;
		case 1:
			translation = glm::translate(glm::vec3(-2.2f, 0.03f, -1.7f));
			break;
		case 2:
			translation = glm::translate(glm::vec3(-2.5f, 0.03f, -1.4f));
			break;
		case 3:
			translation = glm::translate(glm::vec3(-2.9f, 0.03f, -1.7f));
			break;
		case 4:
			translation = glm::translate(glm::vec3(-3.2f, 0.03f, -1.7f));
			break;
		case 5:
			translation = glm::translate(glm::vec3(-2.9f, 0.03f, -1.4f));
			break;
		case 6:
			translation = glm::translate(glm::vec3(-2.7f, 0.03f, -2.2f));
			break;
		case 7:
			translation = glm::translate(glm::vec3(-2.4f, 0.03f, -2.2f));
			break;
		case 8:
			translation = glm::translate(glm::vec3(-3.0f, 0.03f, -2.2f));
			break;
		case 9:
			translation = glm::translate(glm::vec3(-2.7f, 0.03f, -2.5f));
			break;
		default:
			translation = glm::translate(glm::vec3(-2.7f, 0.03f, -1.9f));
			break;
		}
		model = translation * rotation * scale;
		glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
		glDrawArrays(GL_TRIANGLE_FAN, 0, 36);		//bottom
		glDrawArrays(GL_TRIANGLE_FAN, 36, 72);		//top
		glDrawArrays(GL_TRIANGLE_STRIP, 72, 146);	//sides
		i++;
	}
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, blackTextureID);
	scale = glm::scale(glm::vec3(0.07f, 0.6f, 0.07f));
	i = 0;
	while (i < 10)
	{
		switch (i)
		{
		case 0:
			translation = glm::translate(glm::vec3(-2.5f, 1.03f, -1.7f));
			break;											  
		case 1:												  
			translation = glm::translate(glm::vec3(-2.2f, 1.03f, -1.7f));
			break;											  
		case 2:												  
			translation = glm::translate(glm::vec3(-2.5f, 1.03f, -1.4f));
			break;
		case 3:
			translation = glm::translate(glm::vec3(-2.9f, 1.03f, -1.7f));
			break;
		case 4:
			translation = glm::translate(glm::vec3(-3.2f, 1.03f, -1.7f));
			break;
		case 5:
			translation = glm::translate(glm::vec3(-2.9f, 1.03f, -1.4f));
			break;
		case 6:
			translation = glm::translate(glm::vec3(-2.7f, 1.03f, -2.2f));
			break;
		case 7:
			translation = glm::translate(glm::vec3(-2.4f, 1.03f, -2.2f));
			break;
		case 8:
			translation = glm::translate(glm::vec3(-3.0f, 1.03f, -2.2f));
			break;
		case 9:
			translation = glm::translate(glm::vec3(-2.7f, 1.03f, -2.5f));
			break;
		default:
			translation = glm::translate(glm::vec3(-2.7f, 1.03f, -1.9f));
			break;
		}
		model = translation * rotation * scale;
		glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
		glDrawArrays(GL_TRIANGLE_FAN, 0, 36);		//bottom
		glDrawArrays(GL_TRIANGLE_FAN, 36, 72);		//top
		glDrawArrays(GL_TRIANGLE_STRIP, 72, 146);	//sides
		i++;
	}
	glBindVertexArray(0);

	// Seventh Object: Light Sphere, represents mouse body

	// Bind current texture to openGL context
	glUseProgram(gLightShaderID);
	// Orthographic or Perspective
	if (perspective)
	{
		projection = glm::perspective(glm::radians(45.0f), (GLfloat)WINDOW_WIDTH / (GLfloat)WINDOW_HEIGHT, 0.1f, 100.0f);
	}
	else
	{
		projection = glm::ortho(-5.0f, 5.0f, -3.75f, 3.75f, 0.1f, 100.0f);
	}
	// Retrive uniform variable locations for the model, view, projection, and color.
	modelLoc = glGetUniformLocation(gLightShaderID, "model");
	viewLoc = glGetUniformLocation(gLightShaderID, "view");
	projLoc = glGetUniformLocation(gLightShaderID, "projection");
	// Similar process to first object's code
	scale = glm::scale(glm::vec3(0.2f, 0.2f, 0.2f));
	rotation = glm::rotate(0.0f, glm::vec3(1.0f, 0.0f, 0.0f));
	translation = glm::translate(glm::vec3(gLightPosition0.x, gLightPosition0.y, gLightPosition0.z));
	model = translation * rotation * scale;
	view = glm::lookAt(gCameraPos, gCameraPos + gCameraFront, gCameraUp);
	glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));
	glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
	glUniformMatrix4fv(projLoc, 1, GL_FALSE, glm::value_ptr(projection));
	glBindVertexArray(meshes.gSphereMesh.vao);
	glDrawElements(GL_TRIANGLES, meshes.gSphereMesh.nIndices, GL_UNSIGNED_INT, (void*)0);
	glBindVertexArray(0);

	// Swap buffers to show current frame. Prevents screen tearing.
	glfwSwapBuffers(gWindow);
}

// Instantiates a new texture object
bool UCreateTexture(const char* filename, GLuint& textureID)
{
	// Local Variables
	int width, height, channels;
	// Load image file from local directory
	unsigned char* image = stbi_load(filename, &width, &height, &channels, 0);
	if (image)
	{
		// Orients image so the height is pointing up instead of down
		flipImageVertically(image, width, height, channels);
		// Creates texture object and binds it to current openGL context
		glGenTextures(1, &textureID);
		glBindTexture(GL_TEXTURE_2D, textureID);
		// Sets wrapping style
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// Sets magnification/minimization filters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		// Sends texture data to the GPU
		if (channels == 3)
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
		}
		else if (channels == 4)
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		}
		else
		{
			std::cout << "Not implemented to handle image with " << channels << " channels" << std::endl;
			return false;
		}
		// Create Mipmap for magnification/minimization
		glGenerateMipmap(GL_TEXTURE_2D);
		// Free memory of image
		stbi_image_free(image);
		// Unbinds the texture from current context
		glBindTexture(GL_TEXTURE_2D, 0);

		return true;
	}
	else
	{
		return false;
	}
}

// Images are loaded with Y axis going down, but OpenGL's Y axis goes up, so let's flip it
void flipImageVertically(unsigned char* image, int width, int height, int channels)
{
	for (int j = 0; j < height / 2; ++j)
	{
		int index1 = j * width * channels;
		int index2 = (height - 1 - j) * width * channels;

		for (int i = width * channels; i > 0; --i)
		{
			unsigned char tmp = image[index1];
			image[index1] = image[index2];
			image[index2] = tmp;
			++index1;
			++index2;
		}
	}
}

// Function updates camera speed when mouse's scroll wheel updates its value
void UMouseScrollCallback(GLFWwindow* window, double xoffset, double yoffset)
{
	// Limits the highest and lowest possible camera speeds
	cameraSpeed += (float)yoffset;
	if (cameraSpeed < 1.0f)
		cameraSpeed = 1.0f;
	if (cameraSpeed > 80.0f)
		cameraSpeed = 80.0f;
}

// Function updates camera direction when mouse is moved
void UMouseCallback(GLFWwindow* window, double mousexpos, double mouseypos)
{
	// Help avoid glitches during initialization
	if (gFirstMouse) // initially set to true
	{
		gLastX = mousexpos;
		gLastY = mouseypos;
		gFirstMouse = false;
	}
	// Update xoffset/yoffset based on changes in mouse input
	float xoffset = mousexpos - gLastX;
	float yoffset = gLastY - mouseypos;
	gLastX = mousexpos;
	gLastY = mouseypos;

	const float mouseSensitivity = 0.1f;
	xoffset *= mouseSensitivity;
	yoffset *= mouseSensitivity;

	// gPitch and gYaw are updated based on chnage in xoffset/yoffset
	gYaw += xoffset;
	gPitch += yoffset;
	if (gPitch > 89.0f)
		gPitch = 89.0f;
	if (gPitch < -89.0f)
		gPitch = -89.0f;
	// Direction of camera is updated based on changes to gYaw and gPitch
	glm::vec3 direction;
	direction.x = cos(glm::radians(gYaw)) * cos(glm::radians(gPitch));
	direction.y = sin(glm::radians(gPitch));
	direction.z = sin(glm::radians(gYaw)) * cos(glm::radians(gPitch));
	gCameraFront = glm::normalize(direction);
}
// This function takes in key press events and processes them
void UKeyCallback(GLFWwindow* window, int key, int scancode, int action, int mods)
{
	// Switch between orthographic and perspective view
	if (key == GLFW_KEY_P && action == GLFW_PRESS)
	{
		if (perspective)
		{
			perspective = false;
			gCameraFront = glm::vec3(0.0f, 0.0f, -1.0f);
			gCameraPos = glm::vec3(0.0f, 0.0f, 3.0f);
		}
		else
		{
			perspective = true;
		}
	}
}