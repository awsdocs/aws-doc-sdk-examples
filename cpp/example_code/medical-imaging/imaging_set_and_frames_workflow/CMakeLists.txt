# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.13)

set(SERVICE_NAME medical-imaging)
set(SERVICE_COMPONENTS medical-imaging cloudformation s3 sts)

# Set this project's name.
project("${SERVICE_NAME}-examples")

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 17)

# Used by gzip-hpp to unzip the image set metadata.
# See the README for installation instructions.
find_path(GZIP_HPP_INCLUDE_DIRS "gzip/compress.hpp")

# Used by gzip-hpp to unzip the image set metadata.
# See the README for installation instructions.
find_package(ZLIB REQUIRED)


find_path(JSONCONS_HPP_INCLUDE_DIRS "jsoncons/json.hpp")

# Package enabling decoding of HTJ2K image frames.
# See the README for installation instructions.
find_package(OpenJPEG CONFIG REQUIRED)


# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})


if (WINDOWS_BUILD AND AWSSDK_INSTALL_AS_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executables' location.

    AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
endif ()

set(EXAMPLE medical_image_sets_and_frames_workflow)

set(EXAMPLE_EXE run_${EXAMPLE})

add_executable(${EXAMPLE_EXE}
        medical_image_sets_and_frames_workflow.cpp
        ../delete_image_set.cpp
        ../get_dicom_import_job.cpp
        ../get_image_set_metadata.cpp
        ../search_image_sets.cpp
        ../start_dicom_import_job.cpp)

target_link_libraries(${EXAMPLE_EXE}
        PRIVATE
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
        openjp2
        ZLIB::ZLIB)

target_include_directories(${EXAMPLE_EXE}
        PRIVATE
        ${GZIP_HPP_INCLUDE_DIRS}
        ${JSONCONS_HPP_INCLUDE_DIRS}
        ${CMAKE_CURRENT_SOURCE_DIR}/..)

target_compile_definitions(${EXAMPLE_EXE}
        PRIVATE
        TEMPLATES_PATH="${CMAKE_CURRENT_SOURCE_DIR}"
        TESTING_BUILD=1
)

