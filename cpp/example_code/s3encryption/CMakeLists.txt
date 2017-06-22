cmake_minimum_required(VERSION 3.2)
project(myTestS3Encryption)

# On all platforms, after installation of AWS SDK for C++, CMake should be able to locate the correct path of AWSSDKConfig.cmake
# On Windows, AWSSDK_DIR or CMAKE_INSTALL_PREFIX should be provided to CMake by users
# On other Unix like systems, the default installation dir of AWSSDKConfig.cmake is /usr/local/lib/cmake/
find_package(AWSSDK)

# After correctly loading AWSSDKConfig.cmake, serveral variables and macros should be available for user to easily use our SDK
Message(STATUS "AWSSDK_LIB_DIR: ${AWSSDK_LIB_DIR}")
Message(STATUS "AWSSDK_INC_DIR: ${AWSSDK_INCLUDE_DIR}")
Message(STATUS "AWSSDK_BIN_DIR: ${AWSSDK_BIN_DIR}")

# This is a test only code, you can commented it out. It's useful for copying  
# all libs specified in SERVICE_LIST and all dependent libs to destination, usually where binary sits.
# The second arg is compile time configuration: "Debug" "Release" and so on.
SET(SERVICE_LIST sqs s3 transfer dynamodb s3-encryption apigateway)
AWSSDK_CPY_DYN_LIBS(SERVICE_LIST "" ./)


# This macro is able to return all dependent libs of high level services
# In our test code, only s3-encryption is actually used, you can remove the other two.
SET(SERVICE s3-encryption transfer apigateway)
AWSSDK_LIB_DEPS(SERVICE DEPS)
Message(STATUS "${SERVICE} depends on libs: ${DEPS}")

# This macro is able to find out all the libs needed to link the target
# include the libs from SERVICE(s) and all of their dependent libs.
AWSSDK_DETERMINE_LIBS_TO_LINK(SERVICE OUTPUT)

Message(STATUS "All linked libs: ${OUTPUT}")

if (NOT WIN32) # Normally on Windows system, pkg-config tool is not available
	find_package(PkgConfig)
	pkg_check_modules(DEPS REQUIRED ${OUTPUT}) 
endif()

if (APPLE)
	add_definitions(-DUNDER_MACOS)
endif()

link_directories("${AWSSDK_LIB_DIR}")
add_executable(myTestS3Encryption src/main.cpp)

if (WIN32) # We can use variables and macros from AWSSDKConfig.cmake
	# This is required by core sdk to use lib on dll_sementics platform
	add_definitions(-DUSE_IMPORT_EXPORT)
	target_link_libraries(myTestS3Encryption ${OUTPUT})
	target_include_directories(myTestS3Encryption PUBLIC ${AWSSDK_INCLUDE_DIR})
else()  # We can either use facilities from PkgConfig module or use variables and macros from AWSSDKConfig.cmake directly
	target_link_libraries(myTestS3Encryption ${DEPS_LIBRARIES})
	target_include_directories(myTestS3Encryption PRIVATE ${DEPS_INCLUDE_DIRS})
	target_compile_options(myTestS3Encryption PRIVATE ${DEPS_CFLAGS_OTHER})
endif()

