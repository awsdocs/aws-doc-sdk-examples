# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)
set(SERVICE_COMPONENTS sesv2 rds-data)

set(BUILD_SHARED_LIBS ON)

# Set this project's name.
project("ServerlessAuroraGTest")
set(EXECUTABLE serverless_aurora_gtest)

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location for Windows to find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_BUILD_TYPE Debug)

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
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})

add_executable(${EXECUTABLE})

if (WINDOWS_BUILD)
    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executables' location.

    if (AWSSDK_INSTALL_AS_SHARED_LIBS)
        # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.
        AWSSDK_CPY_DYN_LIBS(
                CURRENT_TARGET_AWS_DEPENDENCIES
                ""
                ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR}
        )
    endif ()

    add_custom_command(
            TARGET
            ${CURRENT_TARGET}
            POST_BUILD
            COMMAND ${CMAKE_COMMAND} -E copy
            ${CMAKE_BINARY_DIR}/${CMAKE_INSTALL_BINDIR}${BIN_SUB_DIR}/gtest.dll
            ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR}
    )
endif ()

enable_testing()

target_sources(
        ${EXECUTABLE}
        PUBLIC
        ../RDSDataHandler.cpp
        ../SESV2EmailHandler.cpp
        ../ItemTrackerHTTPHandler.cpp
        serverless_aurora_gtests.cpp
        gtest_serverless_aurora.cpp
)


target_include_directories(
        ${EXECUTABLE}
        PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/..>
)

target_link_libraries(${EXECUTABLE}
        GTest::gtest_main
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS})


include(GoogleTest)
gtest_add_tests(
        TARGET
        ${EXECUTABLE}
)
