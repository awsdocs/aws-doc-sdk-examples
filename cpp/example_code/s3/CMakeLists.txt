# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Set the minimum required version of CMake for this project.
cmake_minimum_required(VERSION 3.13)

set(SERVICE_NAME s3)
set(SERVICE_COMPONENTS iam s3 sts)

# Set this project's name.
project("${SERVICE_NAME}-examples")

# Set the C++ standard to use to build this target.
set(CMAKE_CXX_STANDARD 11)

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if(WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif()

# CURL package is optional to test pre-signed url code.
find_package(CURL)

if (NOT CURL_FOUND) 
    unset(CURL_INCLUDE_DIRS) 
    unset(CURL_LIBRARIES) 
    unset(CURL_LIBRARY)
endif()


# Find the AWS SDK for C++ package.
find_package(AWSSDK REQUIRED COMPONENTS ${SERVICE_COMPONENTS})

if(WINDOWS_BUILD AND AWSSDK_INSTALL_AS_SHARED_LIBS)
     # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

     # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this 
                                    # and set the proper subdirectory to the executables' location.

     AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
 endif()

# Add the code example-specific header files.
file(GLOB AWSDOC_S3_HEADERS
    "include/awsdoc/s3/*.h"
)

# AWSDOC_S3_SOURCE can be defined in the command line to limit the files in a build. For example,
# you can limit files to one action.
if(NOT DEFINED AWSDOC_S3_SOURCE)
    file(GLOB AWSDOC_S3_SOURCE
        "*.cpp"
    )
endif()

# Handle special case of list_buckets_disabling_dns_cache.cpp on Windows.
if(WINDOWS_BUILD)
    list(FIND AWSSDK_CLIENT_LIBS "curl" CONTAINS_CURL)
    if (CONTAINS_CURL EQUAL -1)
            # Remove list_buckets_disabling_dns_cache.cpp when not using curl library for http.
        list(FILTER AWSDOC_S3_SOURCE EXCLUDE REGEX "/list_buckets_disabling_dns_cache.cpp$") # Not supported in windows without curl, see file for details
    else()
        if (NOT CURL_FOUND) # find_package did not set these.
            set(CURL_INCLUDE_DIRS "c:/curl/include") # Update this with correct curl install location.
            set(CURL_LIBRARIES "c:/curl/lib") # Update this with correct  curl install location.
        endif()
    endif()
endif()


foreach(file ${AWSDOC_S3_SOURCE})
    get_filename_component(EXAMPLE ${file} NAME_WE)

    # Build the code example executables.
    set(EXAMPLE_EXE run_${EXAMPLE})

    add_executable(${EXAMPLE_EXE} ${AWSDOC_S3_HEADERS} ${file})

    target_include_directories(${EXAMPLE_EXE} PUBLIC 
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>
        )
  
        
    target_include_directories(${EXAMPLE_EXE} SYSTEM PUBLIC 
        ${CURL_INCLUDE_DIRS})

    target_link_libraries(${EXAMPLE_EXE} 
        ${AWSSDK_LINK_LIBRARIES} 
        ${AWSSDK_PLATFORM_DEPS}
            ${CURL_LIBRARIES})


    if(CURL_FOUND)
        target_compile_definitions(${EXAMPLE_EXE}
        PUBLIC
        HAS_CURL=1)
    endif()

 endforeach()

if(BUILD_TESTS)
    add_subdirectory(tests)
endif()

