# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.8)

set(SERVICE_COMPONENTS sesv2 rds-data)

# This example uses the poco library to implement an http server.
# Poco can be installed automatically using the Conan package manager.
# See the accompanying ReadMe for instructions.
set(USING_CONAN TRUE)  # True if using Conan.

# Set this project's name.
project("ServerlessAurora")
set(EXECUTABLE run_serverless_aurora)

#Set the location of where Windows can find the installed libraries of the SDK.
if(MSVC)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Enable CTest for testing these code examples.
if(BUILD_TESTS)
    include(CTest)
endif()

# Build shared libraries by default.
if(NOT DEFINED BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

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

# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
    add_definitions(-DUSE_IMPORT_EXPORT)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    set(CMAKE_BUILD_TYPE Debug) # Explicitly setting CMAKE_BUILD_TYPE is necessary in Windows to copy DLLs.

    list(APPEND SERVICE_LIST ${SERVICE_COMPONENTS} ${MY_POCO_LIBS})

     AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})  #Choose this line if your executables are in /build/Debug
endif()


# Check whether the target system is Windows, including Win64.
if(WIN32)
    # Check whether the compiler is some version of Microsoft Visual C++, or another compiler simulating C++.
    if(MSVC)
         source_group("Source Files" FILES ${AWSDOC_SOURCE})
    endif(MSVC)
endif()


add_executable(${EXECUTABLE}
        serverless_aurora.cpp
        RDSDataHandler.cpp
        SES3EmailHandler.cpp
        ItemTrackerHTTPHandler.cpp
        PocoHTTPServer.cpp)

target_include_directories(${EXECUTABLE}
        PUBLIC
        /opt/homebrew/include)
target_link_directories(${EXECUTABLE}
        PUBLIC
        /opt/homebrew/lib)
target_link_libraries(${EXECUTABLE}
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
        ${MY_POCO_LIBS}
        ${CONAN_LIBS})




