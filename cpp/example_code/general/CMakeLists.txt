#Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#SPDX-License-Identifier: Apache-2.0
# snippet-start:[general.cpp.starter.cmakelists]
cmake_minimum_required(VERSION 3.3)
# For the AWS C++ SDK build use shared libraries.
option(BUILD_SHARED_LIBS "Build shared libraries" ON)
# C++ standard version 11.
set(CMAKE_CXX_STANDARD 11)
project(app LANGUAGES CXX)
#Set the location of where to find the installed libraries of the SDK.
list(APPEND CMAKE_PREFIX_PATH "C:\\Program Files (x86)\\aws-cpp-sdk-all\\lib\\cmake")
list(APPEND CMAKE_PREFIX_PATH "C:\\Program Files (x86)\\aws-cpp-sdk-all\\lib\\aws-c-event-stream\\cmake")
list(APPEND CMAKE_PREFIX_PATH "C:\\Program Files (x86)\\aws-cpp-sdk-all\\lib\\aws-c-common\\cmake")
list(APPEND CMAKE_PREFIX_PATH "C:\\Program Files (x86)\\aws-cpp-sdk-all\\lib\\aws-checksums\\cmake")
#Load required components: This basic example uses S3.
find_package(AWSSDK CONFIG COMPONENTS s3 REQUIRED)
# The executable name and its sourcefiles.
add_executable(${PROJECT_NAME} "main.cpp")
#This if clause copies the SDK DLLs from the installation location to the place where this project's executable is located.
#Without this you'll need to copy the install /bin folder to the exe location (app.exe in this case) to prevent runtime errors.
if(MSVC AND BUILD_SHARED_LIBS)
    target_compile_definitions(${PROJECT_NAME} PUBLIC "USE_IMPORT_EXPORT")
    add_definitions(-DUSE_IMPORT_EXPORT)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    list(APPEND SERVICE_LIST s3)
    #For IDE's like Xcode and Visual Studio this line will be ignored because Release/Debug switched internally, but necessary for non-IDE builds.
    set(CMAKE_BUILD_TYPE Debug)
    #Note:Choose appropriate one of the following two lines, based on your build output.
    AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})  #Choose this line if your .exe is in /build/Debug.
    #AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR})  #Choose this line for Visual Studio and possibly other IDEs.
    message(STATUS ">>CMAKE_CURRENT_BINARY_DIR: ${CMAKE_CURRENT_BINARY_DIR}")
    message(STATUS ">>CMAKE_BUILD_TYPE: ${CMAKE_BUILD_TYPE}")
endif()
set_compiler_flags(${PROJECT_NAME})
set_compiler_warnings(${PROJECT_NAME})
message(STATUS ">>AWSSDK_INCLUDE_DIRS: ${AWSSDK_INCLUDE_DIRS}")
target_include_directories(${PROJECT_NAME} PRIVATE ${AWSSDK_INCLUDE_DIRS})
# The libraries used by your executable.
target_link_libraries(${PROJECT_NAME} PRIVATE ${AWSSDK_LIBRARIES})
# snippet-end:[general.cpp.starter.cmakelists]


