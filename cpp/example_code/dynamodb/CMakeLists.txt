# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.8)

set(SERVICE_NAME dynamodb)
set(SERVICE_COMPONENTS dynamodb)

# Set this project's name.
project("${SERVICE_NAME}-examples")

#Set the location of where Windows can find the installed libraries of the SDK.
if(MSVC)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Enable CTest for testing these code examples.
if(BUILD_TESTS)
    include(CTest)
endif()

# Build shared libraries by default.
if(NOT BUILD_SHARED_LIBS)
    set(BUILD_SHARED_LIBS ON)
endif()

# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})

find_package(ZLIB)
set(ADDITIONAL_LIBS )
set(ADDITIONAL_INCLUDE_DIR)
set(ADDITIONAL_DEFINES)

if (ZLIB_FOUND)
    set(ADDITIONAL_LIBS ${ZLIB_LIBRARIES})
    set(ADDITIONAL_INCLUDE_DIR ${ZLIB_INCLUDE_DIRS})
    set(ADDITIONAL_DEFINES _HAS_ZLIB_)
endif()
# If the compiler is some version of Microsoft Visual C++, or another compiler simulating C++,
# and building as shared libraries, then dynamically link to those shared libraries.
if(MSVC AND BUILD_SHARED_LIBS)
    add_definitions(-DUSE_IMPORT_EXPORT)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    set(CMAKE_BUILD_TYPE Debug) #Explicitly setting CMAKE_BUILD_TYPE is necessary in windows to copy dlls.

    list(APPEND SERVICE_LIST ${SERVICE_COMPONENTS})

     AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_BUILD_TYPE})  #Choose this line if your executables are in /build/Debug
endif()


# AWSDOC_${SERVICE_COMPONENTS}_SOURCE can be defined in the command line to limit the files in a build, for example to one action.
if(NOT DEFINED AWSDOC_SOURCE)
    file(GLOB AWSDOC_SOURCE
        "*.cpp"
    )
endif()

# Check whether the target system is Windows, including Win64.
if(WIN32)
    list(FILTER AWSDOC_SOURCE EXCLUDE REGEX "/list_buckets_disabling_dns_cache.cpp$") # Not supported in windows, see file for details

    # Check whether the compiler is some version of Microsoft Visual C++, or another compiler simulating C++.
    if(MSVC)
         source_group("Source Files" FILES ${AWSDOC_SOURCE})
    endif(MSVC)
endif()

foreach(file ${AWSDOC_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WE)

    # Build the code example executables.
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE} ${file})

    target_include_directories(${EXAMPLE_EXE} PUBLIC 
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>
            ${ADDITIONAL_INCLUDE_DIR})
    target_link_libraries(${EXAMPLE_EXE} ${AWSSDK_LINK_LIBRARIES} 
        ${AWSSDK_PLATFORM_DEPS}
            ${ADDITIONAL_LIBS})
    target_compile_definitions(${EXAMPLE_EXE}
            PRIVATE
            ${ADDITIONAL_DEFINES})

 endforeach()

if(BUILD_TESTS)
    add_subdirectory(gtests)
endif()

