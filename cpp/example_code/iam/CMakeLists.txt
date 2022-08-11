# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.8)

# Set this project's name.
project("iam-examples")

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_BUILD_TYPE Debug) 


# Build shared libraries by default.
if(NOT DEFINED BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

# Enable CTest for testing these code examples.
if(BUILD_TESTS)
    include(CTest)
endif()

set(MSVC_DEBUG TRUE)  # this is for debugging and should not be in the pull request


# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS iam s3 sts)

# Add the code example-specific header files.
file(GLOB AWSDOC_S3_HEADERS
        "include/awsdoc/s3/*.h"
        )

# Add the code example-specific source files if the source input is not already defined.
if(NOT DEFINED AWSDOC_S3_SOURCE)
    file(GLOB AWSDOC_S3_SOURCE
            "*.cpp"
            )
endif()


# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
  #  add_definitions(-DUSE_IMPORT_EXPORT)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    list(APPEND SERVICE_LIST iam s3 sts)
    #For IDE's like Xcode and Visual Studio, this line is ignored. This is because Release/Debug
    #  is switched internally, but this is necessary for non-IDE builds.

    AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})  #Choose this line if your executables are in /build/Debug

    message(STATUS ">>CMAKE_CURRENT_BINARY_DIR: ${CMAKE_CURRENT_BINARY_DIR}")
    message(STATUS ">>CMAKE_BUILD_TYPE: ${CMAKE_BUILD_TYPE}")

 #   source_group("Header Files\\awsdoc\\s3" FILES ${AWSDOC_S3_HEADERS})
#/   source_group("Source Files" FILES ${AWSDOC_S3_SOURCE})
endif()


foreach(file ${AWSDOC_S3_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WE)

    # Build the code example executables.
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE} ${file})

    target_link_libraries(${EXAMPLE_EXE} ${AWSSDK_LINK_LIBRARIES}
            ${AWSSDK_PLATFORM_DEPS})

endforeach()

#if(BUILD_TESTS)
#    add_subdirectory(gtests)
#endif()

