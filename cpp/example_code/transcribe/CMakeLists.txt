# CMakeLists.txt
cmake_minimum_required(VERSION 3.13)
set(CMAKE_CXX_STANDARD 11)
project(get_transcript LANGUAGES CXX)
set(SERVICE_COMPONENTS transcribe;transcribestreaming)

# Build shared libraries by default.
set(BUILD_SHARED_LIBS ON)

# Use the MSVC variable to determine if this is a Windows build.
set(WINDOWS_BUILD ${MSVC})

# Set the location of where Windows can find the installed libraries of the SDK.
if (WINDOWS_BUILD)
    string(REPLACE ";" "/aws-cpp-sdk-all;" SYSTEM_MODULE_PATH "${CMAKE_SYSTEM_PREFIX_PATH}/aws-cpp-sdk-all")
    list(APPEND CMAKE_PREFIX_PATH ${SYSTEM_MODULE_PATH})
endif ()

find_package(AWSSDK COMPONENTS ${SERVICE_COMPONENTS})

if (WINDOWS_BUILD AND AWSSDK_INSTALL_AS_SHARED_LIBS)
    # Copy relevant AWS SDK for C++ libraries into the current binary directory for running and debugging.

    # set(BIN_SUB_DIR "/Debug") # If you are building from the command line, you may need to uncomment this
    # and set the proper subdirectory to the executables' location.

    AWSSDK_CPY_DYN_LIBS(SERVICE_COMPONENTS "" ${CMAKE_CURRENT_BINARY_DIR}${BIN_SUB_DIR})
endif ()


add_executable(${PROJECT_NAME} "get_transcript.cpp" )

target_link_libraries(${PROJECT_NAME} PRIVATE ${AWSSDK_LINK_LIBRARIES})

target_compile_definitions(${PROJECT_NAME}
        PRIVATE
        MEDIA_DIR="${CMAKE_CURRENT_SOURCE_DIR}/.media" )

