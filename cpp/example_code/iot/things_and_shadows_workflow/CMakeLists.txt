# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.13)

set(SERVICE_NAME iot)
set(SERVICE_COMPONENTS iot iot-data cloudformation)

# Set this project's name.
project("${SERVICE_NAME}-examples")

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

if (WINDOWS_BUILD AND AWSSDK_INSTALL_AS_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executables' location.

    AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
endif ()

set(EXAMPLE iot_things_and_shadows_workflow)

set(EXAMPLE_EXE run_${EXAMPLE})

add_executable(${EXAMPLE_EXE}
        iot_things_and_shadows_workflow.cpp
        ../create_thing.cpp
        ../delete_thing.cpp
        ../create_keys_and_certificate.cpp
        ../delete_certificate.cpp
        ../attach_thing_principal.cpp
        ../detach_thing_principal.cpp
        ../update_thing.cpp
        ../update_thing_shadow.cpp
        ../get_thing_shadow.cpp
        ../describe_endpoint.cpp
        ../list_certificates.cpp
        ../create_topic_rule.cpp
        ../create_topic_rule.cpp
        ../list_topic_rules.cpp
        ../search_index.cpp
        ../update_indexing_configuration.cpp
        ../delete_topic_rule.cpp
)

target_link_libraries(${EXAMPLE_EXE}
        PRIVATE
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
        )

target_compile_definitions(${EXAMPLE_EXE}
        PRIVATE
        TEMPLATES_PATH="${CMAKE_CURRENT_SOURCE_DIR}"
        EXCLUDE_ACTION_MAIN=1 # This is necessary to exclude multiple main functions.
)
