# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.13)

set(SERVICE_COMPONENTS s3 transfer)

# Set this project's name.
project("s3_object_integrity_workflow")

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 17)

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})

if (WINDOWS_BUILD)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executable's location.

    AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR} ${BIN_SUB_DIR})
endif ()

set(EXAMPLE_EXE run_${PROJECT_NAME})

add_executable(${EXAMPLE_EXE}
        s3_object_integrity_workflow.cpp
        main.cpp
        ../create_bucket.cpp
        ../delete_objects.cpp
        ../list_objects.cpp
        ../delete_bucket.cpp
)

target_compile_definitions(${EXAMPLE_EXE}
        PRIVATE
        EXCLUDE_MAIN_FUNCTION
        SRC_DIR="${CMAKE_CURRENT_SOURCE_DIR}"
        LARGE_FILE_DIR="${CMAKE_CURRENT_SOURCE_DIR}/../../../../resources/sample_files/"
)

target_include_directories(${EXAMPLE_EXE}
        PRIVATE
        "${CMAKE_CURRENT_SOURCE_DIR}/.."
)

target_link_libraries(${EXAMPLE_EXE}
        PRIVATE
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
)
