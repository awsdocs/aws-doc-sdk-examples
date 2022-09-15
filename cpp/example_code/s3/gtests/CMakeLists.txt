# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

set(EXAMPLE_SERVICE_NAME "s3")
set(CURRENT_TARGET "${EXAMPLE_SERVICE_NAME}_gtest")
set(CURRENT_TARGET_AWS_DEPENDENCIES s3 sts)

# Set this project's name.
project("${EXAMPLE_SERVICE_NAME}-examples-gtests" )

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 14)

include(FetchContent)
FetchContent_Declare(
        googletest
        GIT_REPOSITORY https://github.com/google/googletest.git
        GIT_TAG release-1.12.1
)
# For Windows: Prevent overriding the parent project's compiler/linker settings.
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

# Set the location for Windows to find the installed libraries of the SDK.
if(MSVC)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# Build shared libraries by default.
if(NOT BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

set(CMAKE_BUILD_TYPE Debug)

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${CURRENT_TARGET_AWS_DEPENDENCIES})

add_executable(
        ${CURRENT_TARGET}
)

# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
    add_definitions(-DUSE_IMPORT_EXPORT)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    list(APPEND SERVICE_LIST ${CURRENT_TARGET_AWS_DEPENDENCIES})
    #For IDE's like Xcode and Visual Studio this line will be ignored because Release/Debug 
    #  is switched internally, but this is necessary for non-IDE builds.

	
    #TODO:Choose which of the following two lines to use to copy to the same folder where your executables are.
    AWSSDK_CPY_DYN_LIBS(
        SERVICE_LIST
        ""
        ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE}
    )  #Choose this line if your executables are in /build/Debug

    add_custom_command(
        TARGET
        ${CURRENT_TARGET}
        POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
                ${CMAKE_BINARY_DIR}/${CMAKE_INSTALL_BINDIR}/${CMAKE_BUILD_TYPE}/gtest.dll
                ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE}
    )
 endif()

# Check whether the target system is Windows, including Win64.
if(WIN32)
    # Check whether the compiler is some version of Microsoft Visual C++, or another compiler simulating C++.
    if(MSVC)
        source_group("Header Files\\awsdoc\\s3" FILES ${AWSDOC_S3_HEADERS})
        source_group("Source Files" FILES ${AWSDOC_S3_SOURCE})
    endif(MSVC)
endif()

enable_testing()

file(
     GLOB
     GTEST_SOURCE_FILES
    "gtest_*.cpp"
)

foreach(TEST_FILE ${GTEST_SOURCE_FILES})
    string(REPLACE "gtest_" "../" SOURCE_FILE ${TEST_FILE})
     if (EXISTS ${SOURCE_FILE})
        list(APPEND GTEST_SOURCE ${SOURCE_FILE} ${TEST_FILE})
    else()
        message("Error: no associated source file found for ${TEST_FILE}")
    endif()
endforeach()

target_sources(
        ${CURRENT_TARGET}
        PUBLIC
        ${GTEST_SOURCE}
        test_main.cpp
)

target_include_directories(
        ${CURRENT_TARGET}
        PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/../include>
        $<INSTALL_INTERFACE:../include>
)

target_compile_definitions(
        ${CURRENT_TARGET}
        PUBLIC
        TESTING_BUILD
)

target_link_libraries(
        ${CURRENT_TARGET}
        GTest::gtest
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
)

include(GoogleTest)
gtest_add_tests(
        TARGET
        ${CURRENT_TARGET}
)
