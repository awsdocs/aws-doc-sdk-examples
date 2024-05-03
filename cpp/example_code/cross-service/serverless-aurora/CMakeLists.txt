# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.13)

set(SERVICE_COMPONENTS sesv2 rds-data)

# This example uses the poco library to implement an HTTP server.
# Poco can be installed automatically using the Conan package manager.
# See the accompanying ReadMe for instructions.
set(USING_CONAN TRUE)  # True if using Conan.

# Set this project's name.
project("ServerlessAurora")
set(EXECUTABLE run_serverless_aurora)

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})

if (USING_CONAN)
    include(${CMAKE_BINARY_DIR}/conanbuildinfo.cmake)
    conan_basic_setup()
else()
    find_package(POCO REQUIRED Net Util)
    list(APPEND MY_POCO_LIBS
            PocoNet
            PocoUtil
            PocoFoundation)
endif()

if (WINDOWS_BUILD AND AWSSDK_INSTALL_AS_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executables' location.

    AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
endif ()

add_executable(${EXECUTABLE}
        serverless_aurora.cpp
        RDSDataHandler.cpp
        SESV2EmailHandler.cpp
        ItemTrackerHTTPHandler.cpp
        PocoHTTPServer.cpp)

target_link_libraries(${EXECUTABLE}
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
        ${MY_POCO_LIBS}
        ${CONAN_LIBS})



