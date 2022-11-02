# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

set(EXAMPLE_SERVICE_NAME "iam")
set(CURRENT_TARGET "${EXAMPLE_SERVICE_NAME}_gtest")
set(CURRENT_TARGET_AWS_DEPENDENCIES iam s3 sts)

# Set this project's name.
project("${EXAMPLE_SERVICE_NAME}-examples-gtests" )

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

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${CURRENT_TARGET_AWS_DEPENDENCIES})

add_executable(
        ${CURRENT_TARGET}
)

# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    AWSSDK_CPY_DYN_LIBS(
        SERVICE_LIST
        ""
        ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE}
    )

    add_custom_command(
        TARGET
        ${CURRENT_TARGET}
        POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
                ${CMAKE_BINARY_DIR}/${CMAKE_INSTALL_BINDIR}/${CMAKE_BUILD_TYPE}/gtest.dll
                ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE}
    )
 endif()

# GTEST_SOURCE_FILES can be defined in the command line to limit the files in a build. For example,
# you can limit files to one action.
if (NOT DEFINED GTEST_SOURCE_FILES)
    file(
            GLOB
            GTEST_SOURCE_FILES
            "gtest_*.cpp"
    )
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
        message("Error: no associated source file ${SOURCE_FILE} found for ${TEST_FILE}")
    endif()
endforeach()

target_sources(
        ${CURRENT_TARGET}
        PUBLIC
        ${GTEST_SOURCE}
        test_main.cpp
        ${EXAMPLE_SERVICE_NAME}_gtests.cpp
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
