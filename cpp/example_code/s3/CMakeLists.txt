# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.8)

# Set this project's name.
project("s3-examples")

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if(WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS s3 sts iam)



if(WINDOWS_BUILD)
     # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

     # set(BIN_SUB_DIR "/Debug") # if you are building from the command line you may need to uncomment this 
                                    # and set the proper subdirectory to the executables' location.

     AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
endif()

# Add the code example-specific header files.
file(GLOB AWSDOC_S3_HEADERS
    "include/awsdoc/s3/*.h"
)

# AWSDOC_S3_SOURCE can be defined in the command line to limit the files in a build. For example,
# you can limit files to one action.
if(NOT DEFINED AWSDOC_S3_SOURCE)
    file(GLOB AWSDOC_S3_SOURCE
        "*.cpp"
    )
endif()

# Check whether the target system is Windows, including Win64.
if(WIN32)
    list(FILTER AWSDOC_S3_SOURCE EXCLUDE REGEX "/list_buckets_disabling_dns_cache.cpp$") # Not supported in windows, see file for details

    # Check whether the compiler is some version of Microsoft Visual C++, or another compiler simulating C++.
    if(MSVC)
        source_group("Header Files\\awsdoc\\s3" FILES ${AWSDOC_S3_HEADERS})
        source_group("Source Files" FILES ${AWSDOC_S3_SOURCE})
    endif(MSVC)
endif()

foreach(file ${AWSDOC_S3_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WE)

    # Build the code example executables.
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE} ${AWSDOC_S3_HEADERS} ${file})

    target_include_directories(${EXAMPLE_EXE} PUBLIC 
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>)
    target_link_libraries(${EXAMPLE_EXE} ${AWSSDK_LINK_LIBRARIES} 
        ${AWSSDK_PLATFORM_DEPS})

 endforeach()

if(BUILD_TESTS)
    add_subdirectory(tests)
endif()

