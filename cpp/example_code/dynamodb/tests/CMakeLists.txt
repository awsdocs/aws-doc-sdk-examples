# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

set(EXAMPLE_SERVICE_NAME dynamodb)
set(CURRENT_TARGET "${EXAMPLE_SERVICE_NAME}_gtest")
set(CURRENT_TARGET_AWS_DEPENDENCIES dynamodb s3)

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
    AWSSDK_CPY_DYN_LIBS(
            CURRENT_TARGET_AWS_DEPENDENCIES
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

# GTEST_SOURCE_FILES can be defined in the command line to limit the files in a build, for example to one action.
if (NOT DEFINED GTEST_SOURCE_FILES)
    file(
            GLOB
            GTEST_SOURCE_FILES
            "gtest_*.cpp"
    )
endif()

# Check whether the target system is Windows, including Win64.
if(WIN32)
    list(FILTER GTEST_SOURCE_FILES EXCLUDE REGEX "/gtest_list_buckets_disabling_dns_cache.cpp$") # Not supported in windows, see file for details

    # Check whether the compiler is some version of Microsoft Visual C++, or another compiler simulating C++.
    if(MSVC)
        source_group("Source Files" FILES ${GTEST_SOURCE_FILES})
    endif(MSVC)
endif()

enable_testing()

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
        ../dynamodb_utils.cpp
        ${EXAMPLE_SERVICE_NAME}_gtests.cpp
)

target_include_directories(
        ${CURRENT_TARGET}
        PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/..>
        ${AWSSDK_INCLUDE_DIR}/aws
)

target_compile_definitions(
        ${CURRENT_TARGET}
        PUBLIC
        TESTING_BUILD
        MOVIE_FILE_PATH="${MOVIE_FILE_PATH}"
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
