# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

set(EXAMPLE_SERVICE_NAME "ses")
set(CURRENT_TARGET "${EXAMPLE_SERVICE_NAME}_gtest")
set(CURRENT_TARGET_AWS_DEPENDENCIES email)

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

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location for Windows to find the installed libraries of the SDK.
if(WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${CURRENT_TARGET_AWS_DEPENDENCIES})

add_executable(
        ${CURRENT_TARGET}
)

if(WINDOWS_BUILD)
      # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this 
                                    # and set the proper subdirectory to the executables' location.

    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
    AWSSDK_CPY_DYN_LIBS(
            CURRENT_TARGET_AWS_DEPENDENCIES
        ""
        ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR}
    )

    add_custom_command(
        TARGET
        ${CURRENT_TARGET}
        POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
                ${CMAKE_BINARY_DIR}/${CMAKE_INSTALL_BINDIR}${BIN_SUB_DIR}/gtest.dll
                ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR}
    )
endif()

# GTEST_SOURCE_FILES can be defined in the command line to limit the files in a build, for example to one action.
if (NOT DEFINED GTEST_SOURCE_FILES)
    file(
            GLOB
            GTEST_SOURCE_FILES
            "gtest_*.cpp"
    )
endif()

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
        SRC_DIR="${CMAKE_CURRENT_SOURCE_DIR}"
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
