# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.14)

set(EXAMPLE_SERVICE_NAME "s3")
set(CURRENT_TARGET "${EXAMPLE_SERVICE_NAME}_gtest")
set(CURRENT_TARGET_AWS_DEPENDENCIES s3 sts iam)

# Set this project's name.
project("${EXAMPLE_SERVICE_NAME}-examples-gtests")

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 14)

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

find_package(GTest)


if (NOT GTest_FOUND)
    include(FetchContent)
    FetchContent_Declare(
            googletest
            GIT_REPOSITORY https://github.com/google/googletest.git
            GIT_TAG release-1.12.1
    )

    # For Windows: Prevent overriding the parent project's compiler/linker settings.
    set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
    FetchContent_MakeAvailable(googletest)
endif ()

# CURL package is optional to test pre-signed url code.
find_package(CURL)
if (NOT CURL_FOUND)
    unset(CURL_INCLUDE_DIRS)
    unset(CURL_LIBRARIES)
    unset(CURL_LIBRARY)
endif ()


# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location for Windows to find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${CURRENT_TARGET_AWS_DEPENDENCIES})

add_executable(
        ${CURRENT_TARGET}
)

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

# GTEST_SOURCE_FILES can be defined in the command line to limit the files in a build. For example,
# you can limit files to one action.
if (NOT DEFINED GTEST_SOURCE_FILES)
    file(
            GLOB
            GTEST_SOURCE_FILES
            "gtest_*.cpp"
    )
endif ()

# Handle special case of list_buckets_disabling_dns_cache.cpp on Windows.
if (WINDOWS_BUILD)
    list(FIND AWSSDK_CLIENT_LIBS "curl" CONTAINS_CURL)
    if (CONTAINS_CURL EQUAL -1)
        # remove list_buckets_disabling_dns_cache.cpp libs not using curl
        list(FILTER GTEST_SOURCE_FILES EXCLUDE REGEX "/gtest_list_buckets_disabling_dns_cache.cpp$") # Not supported in windows without curl, see file for details
    else ()
        if (NOT CURL_FOUND) # find_package did not set these.
            set(CURL_INCLUDE_DIRS "c:/curl/include") # Update this with correct curl install location.
            set(CURL_LIBRARIES "c:/curl/lib") # Update this with correct  curl install location.
        endif ()
    endif ()
endif ()

enable_testing()


foreach (TEST_FILE ${GTEST_SOURCE_FILES})
    string(REPLACE "gtest_" "../" SOURCE_FILE ${TEST_FILE})
    if (EXISTS ${SOURCE_FILE})
        list(APPEND GTEST_SOURCE ${SOURCE_FILE} ${TEST_FILE})
    else ()
        message("Error: no associated source file found for ${TEST_FILE}")
    endif ()
endforeach ()

target_sources(
        ${CURRENT_TARGET}
        PUBLIC
        ${GTEST_SOURCE}
        test_main.cpp
        S3_GTests.cpp
)

target_include_directories(
        ${CURRENT_TARGET}
        PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/../include>
        $<INSTALL_INTERFACE:../include>
)

target_include_directories(
        ${CURRENT_TARGET}
        SYSTEM PUBLIC
        ${CURL_INCLUDE_DIRS}
)

target_compile_definitions(
        ${CURRENT_TARGET}
        PUBLIC
        TESTING_BUILD
        SRC_DIR="${CMAKE_CURRENT_SOURCE_DIR}"
)

if (CURL_FOUND)
    target_compile_definitions(${CURRENT_TARGET}
            PUBLIC
            HAS_CURL=1)
endif ()

target_link_libraries(
        ${CURRENT_TARGET}
        GTest::gtest
        ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS}
        ${CURL_LIBRARIES}
)

target_link_directories(${CURRENT_TARGET}
        PUBLIC
        ${CURL_LIBRARIES})

include(GoogleTest)
gtest_add_tests(
        TARGET
        ${CURRENT_TARGET}
)
