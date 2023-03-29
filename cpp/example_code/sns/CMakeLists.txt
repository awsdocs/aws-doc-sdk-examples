# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.8)

set(SERVICE_NAME sns)
set(SERVICE_COMPONENTS sns)

# Set this project's name.
project("${SERVICE_NAME}-examples")

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Set the location of where Windows can find the installed libraries of the SDK.
if (MSVC)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})

# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if (MSVC)
    set(CMAKE_BUILD_TYPE Debug) # Explicitly setting CMAKE_BUILD_TYPE is necessary in Windows to copy DLLs.

    list(APPEND SERVICE_LIST ${SERVICE_COMPONENTS})

    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})
endif ()

# AWSDOC_SOURCE can be defined in the command line to limit the files in a build. For example,
# you can limit files to one action.
if (NOT DEFINED AWSDOC_SOURCE)
    file(GLOB AWSDOC_SOURCE
            "*.cpp"
            )
endif ()

foreach (file ${AWSDOC_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WE)

    # Build the code example executables.
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE} ${file})

    target_link_libraries(${EXAMPLE_EXE} ${AWSSDK_LINK_LIBRARIES}
            ${AWSSDK_PLATFORM_DEPS})

endforeach ()


if (BUILD_TESTS)
    add_subdirectory(tests)
endif ()

