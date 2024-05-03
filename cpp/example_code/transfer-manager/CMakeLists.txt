# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.13)

# Set this project's name.
project("tm-examples")

set(SERVICE_COMPONENTS transfer)

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Enable CTest for testing these code examples.
include(CTest)

# Build shared libraries by default.
if(NOT BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS}) #add relevant packages here and below @list(APPEND ...

if (WINDOWS_BUILD AND AWSSDK_INSTALL_AS_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executables' location.

    AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
endif ()

# Add the code example-specific source files.
file(GLOB AWSDOC_EX_SOURCE
    "*.cpp"
)

foreach(file ${AWSDOC_EX_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WE)

    # Build the code example executables.
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE}  ${file})

    if(MSVC AND BUILD_SHARED_LIBS)
        target_compile_definitions(${EXAMPLE_EXE} PUBLIC "USE_IMPORT_EXPORT")
        target_compile_definitions(${EXAMPLE_EXE} PRIVATE "AWSDOC_EX_EXPORTS")
    endif()

    target_include_directories(${EXAMPLE_EXE} PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>)
    target_link_libraries(${EXAMPLE_EXE} ${AWSSDK_LINK_LIBRARIES}
        ${AWSSDK_PLATFORM_DEPS})

    if(BUILD_TESTING)
        # Enable testing for this directory and below.
        enable_testing()

        # Build the code example libraries.
        set(EXAMPLE_LIB ${EXAMPLE})

        add_library(${EXAMPLE_LIB} ${file} )

        if(MSVC AND BUILD_SHARED_LIBS)
            target_compile_definitions(${EXAMPLE_LIB} PUBLIC "USE_IMPORT_EXPORT")
            target_compile_definitions(${EXAMPLE_LIB} PRIVATE "AWSDOC_EX_EXPORTS")
        endif()

        target_include_directories(${EXAMPLE_LIB} PUBLIC
            $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
            $<INSTALL_INTERFACE:include>)
        target_link_libraries(${EXAMPLE_LIB} ${AWSSDK_LINK_LIBRARIES}
            ${AWSSDK_PLATFORM_DEPS})

        # Build the code example unit tests.
        set(EXAMPLE_TEST test_${EXAMPLE})
        set(EXAMPLE_TEST_FILE ${CMAKE_CURRENT_SOURCE_DIR}/tests/test_${EXAMPLE}.cpp)

        if(EXISTS ${EXAMPLE_TEST_FILE})
            add_executable(${EXAMPLE_TEST} ${EXAMPLE_TEST_FILE} )

            target_include_directories(${EXAMPLE_TEST} PUBLIC
                $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
                $<INSTALL_INTERFACE:include>)
            target_link_libraries(${EXAMPLE_TEST} ${EXAMPLE_LIB} )
            add_test(${EXAMPLE_TEST} ${EXAMPLE_TEST})
        endif()

    endif()
endforeach()



