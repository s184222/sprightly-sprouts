cmake_minimum_required(VERSION 2.8 FATAL_ERROR)
# Above line indicates to Cmake that minimum version 2.8 is required.
# As you may have noted all lines beginning with hash symbol are treated as comments by Cmake.

SET(sampleName sprightly-sprouts)
# In the above line we declared a variable sampleName and assigned it a value MyApp. We will make use of the same later.project(PrjMyApp)
# here we define the project or solution (for visual studio) name

find_package(OpenGL REQUIRED)
find_package(Threads REQUIRED)

# here we specify the additional include directories for the project. These files come in additional include directories option of VC++
# project.
# either the variable values from package like ${PCL_INCLUDE_DIRS} or absolute paths as shown in second and third line may be used.
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/../../Dependencies/glad")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/../../Dependencies/GLFW/include")

# here we specify the additional library directories for the linker in the project. These files come in additional library directories
# option of VC++ project.
# either the variable values from package like ${PCL_LIBRARY_DIRS} or absolute paths as shown in second and third line may be used.
# An update, link_directories is being pulled out, you may use TARGET_LINK_LIBRARIES instead as shown below
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/../../Dependencies/GLFW/lib-rasppi")

# The following line is very important.
# It specifies the executable name. Here the executable is the first parameter i.e. MyApp and a file MyApp.exe will be created on
# compilation in windows platform.
# Followed by the executable name come all your source and header files.
# All cpp fles will be clubbed into source folder and all .h files will be added to header files folder of the project.
add_executable (sprightly-sprouts main.cpp glad.c)

# There may be some additional dependencies which you may have to specify for the project, which you may do as in following lines.
# Note that first parameter is the executable name.
target_link_libraries (sprightly-sprouts ${OPENGL_LIBRARIES} libglfw3.a ${CMAKE_DL_LIBS} ${CMAKE_THREAD_LIBS_INIT})

# Add the required libraries for linking:
TARGET_LINK_LIBRARIES(${sampleName})

# End of CMakeLists.Txt file. Save it as a plain text file and the name should be exactly CMakeLists.Txt