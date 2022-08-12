# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

# Set this project's name.
project("iam-examples-gtests")

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_BUILD_TYPE Debug)

include(FetchContent)
FetchContent_Declare(
        googletest
        GIT_REPOSITORY https://github.com/google/googletest.git
        GIT_TAG release-1.12.1
)
# For Windows: Prevent overriding the parent project's compiler/linker settings.
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

set(TEMP_MSVC_REMOVE true)
# Set the location for Windows to find the installed libraries of the SDK.
if(MSVC AND NOT TEMP_MSVC_REMOVE)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# Build shared libraries by default.
if(NOT BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS iam s3 sts)

add_executable(
        iam_gtest
)

# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    list(APPEND SERVICE_LIST iam s3 sts)

    # For IDE's like Xcode and Visual Studio, this line is ignored. This is because Release/Debug
    #  is switched internally, but this is necessary for non-IDE builds.
    AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})

    # copy the gtest shared libraries
    add_custom_command(
        TARGET iam_gtest  POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
                ${CMAKE_BINARY_DIR}/${CMAKE_BUILD_TYPE}/gtest_main.dll
                ${CMAKE_BINARY_DIR}/${CMAKE_BUILD_TYPE}/gtest.dll
                ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})
 endif()


# Check whether the target system is Windows, including Win64.
if(WIN32 AND NOT TEMP_MSVC_REMOVE)
    # Check whether the compiler is some version of Microsoft Visual C++, or another compiler simulating C++.
    if(MSVC)
        source_group("Header Files\\awsdoc\\iam" FILES ${AWSDOC_S3_HEADERS})
        source_group("Source Files" FILES ${AWSDOC_S3_SOURCE})
    endif(MSVC)
endif()

enable_testing()

file(GLOB GTEST_SOURCE_FILES
        "gtest_*.cpp"
)

foreach(TEST_FILE ${GTEST_SOURCE_FILES})
    string(REPLACE "gtest_" "../" SOURCE_FILE ${TEST_FILE})
     if (EXISTS ${SOURCE_FILE})
        list(APPEND GTEST_SOURCE ${SOURCE_FILE} ${TEST_FILE})
    else()
        message("Error: no associated source file ${SOURCE_FILE} found for\n ${TEST_FILE}")
    endif()

endforeach()


target_sources(
        iam_gtest
        PUBLIC
        ${GTEST_SOURCE}
        test_main.cpp
)

target_include_directories(iam_gtest PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/..>
        $<INSTALL_INTERFACE:../include>)

target_compile_definitions(iam_gtest PUBLIC
        TESTING_BUILD)

target_link_libraries(
        iam_gtest
        GTest::gtest
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
)

include(GoogleTest)
gtest_add_tests(TARGET iam_gtest)
