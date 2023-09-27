# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

set(EXAMPLE_SERVICE_NAME PAM)
set(CURRENT_TARGET "${EXAMPLE_SERVICE_NAME}_gtest")
set(CURRENT_TARGET_AWS_DEPENDENCIES  s3 dynamodb rekognition sns)

# Set this project's name.
project("${EXAMPLE_SERVICE_NAME}-examples-gtests" )

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 14)

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

enable_testing()

find_package(GTest)

if(NOT GTest_FOUND)
    include(FetchContent)
    FetchContent_Declare(
            googletest
            GIT_REPOSITORY https://github.com/google/googletest.git
            GIT_TAG release-1.12.1
    )

    # For Windows: Prevent overriding the parent project's compiler/linker settings.
    set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
    FetchContent_MakeAvailable(googletest)
endif()


# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${CURRENT_TARGET_AWS_DEPENDENCIES})
find_package(libzip REQUIRED)

add_executable(
        ${CURRENT_TARGET}
)

target_sources(
        ${CURRENT_TARGET}
        PUBLIC
        ../cpp_lambda_functions.cpp
        cpp_lambda_tests.cpp
        test_main.cpp
        pam_gtests.cpp
)

target_include_directories(
        ${CURRENT_TARGET}
        PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/..>
        $<INSTALL_INTERFACE:..>
)

target_compile_definitions(
        ${CURRENT_TARGET}
        PUBLIC
        TESTING_BUILD
        SOURCE_DIR="${CMAKE_CURRENT_SOURCE_DIR}"
)

target_link_libraries(
        ${CURRENT_TARGET}
        GTest::gtest
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
        libzip::zip
)

include(GoogleTest)

gtest_add_tests(
        TARGET
        ${CURRENT_TARGET}
)
