# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

cmake_minimum_required(VERSION 3.2)
project(s3-examples)

set (CMAKE_CXX_STANDARD 11)

# Enable CTest
include(CTest)

# Build as dynamic libraries by default
if(NOT BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

# Link to shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
    add_definitions(-DUSE_IMPORT_EXPORT)
endif()

# Locate the aws sdk for c++ package.
find_package(AWSSDK REQUIRED COMPONENTS s3)

# Header files
file(GLOB AWSDOC_S3_HEADERS
    "include/awsdoc/s3/*.h"
)

# Source files
file(GLOB AWSDOC_S3_SOURCE
    "source/*.cpp"
)

if(WIN32)
    if(MSVC)
        source_group("Header Files\\awsdoc\\s3" FILES ${AWSDOC_S3_HEADERS})
        source_group("Source Files" FILES ${AWSDOC_S3_SOURCE})
    endif(MSVC)
endif()

foreach(file ${AWSDOC_S3_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WLE)

    # Build example executables
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE} ${AWSDOC_S3_HEADERS} ${file})
    if(MSVC AND BUILD_SHARED_LIBS)
        target_compile_definitions(${EXAMPLE_EXE} PUBLIC "USE_IMPORT_EXPORT")
        target_compile_definitions(${EXAMPLE_EXE} PRIVATE "AWSDOC_S3_EXPORTS")
    endif()
    target_include_directories(${EXAMPLE_EXE} PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>)
    target_link_libraries(${EXAMPLE_EXE} ${AWSSDK_LINK_LIBRARIES} ${AWSSDK_PLATFORM_DEPS})

    if(BUILD_TESTING)
        enable_testing()
        # Build example libs
        set(EXAMPLE_LIB ${EXAMPLE})

        add_library(${EXAMPLE_LIB} ${AWSDOC_S3_HEADERS} ${file})
        if(MSVC AND BUILD_SHARED_LIBS)
            target_compile_definitions(${EXAMPLE_LIB} PUBLIC "USE_IMPORT_EXPORT")
            target_compile_definitions(${EXAMPLE_LIB} PRIVATE "AWSDOC_S3_EXPORTS")
        endif()
        target_include_directories(${EXAMPLE_LIB} PUBLIC
            $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
            $<INSTALL_INTERFACE:include>)
        target_link_libraries(${EXAMPLE_LIB} ${AWSSDK_LINK_LIBRARIES} ${AWSSDK_PLATFORM_DEPS})

        # Build example tests
        set(EXAMPLE_TEST test_${EXAMPLE})

        add_executable(${EXAMPLE_TEST} tests/test_${EXAMPLE}.cpp)
        target_include_directories(${EXAMPLE_TEST} PUBLIC
            $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
            $<INSTALL_INTERFACE:include>)
        target_link_libraries(${EXAMPLE_TEST} ${EXAMPLE_LIB})
        add_test(${EXAMPLE_TEST} ${EXAMPLE_TEST})
    endif()
endforeach()

