#include<GLFW/glfw3.h>

int main() {
	if (glfwInit() != GLFW_TRUE)
		return 1;

	GLFWwindow* window = glfwCreateWindow(100, 100, "Hello world", nullptr, nullptr);
	if (!window) {
		glfwTerminate();
		return 1;
	}

	glfwMakeContextCurrent(window);

	glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
	while (glfwWindowShouldClose(window) != GL_TRUE) {
		glClear(GL_COLOR_BUFFER_BIT);
		glfwSwapBuffers(window);
		glfwPollEvents();
	}

	glfwTerminate();

	return 0;
}