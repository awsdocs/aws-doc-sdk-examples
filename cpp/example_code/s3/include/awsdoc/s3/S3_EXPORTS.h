// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0 
//This header file is required for Windows build due to the inclusion of the unit tests.  The unit tests
//are an external library, so for them to be able to access the project's methods in order to test them,
//the methods must be exported.
#pragma once

#ifdef _MSC_VER
	// Disable Windows complaining about max template size.
	#pragma warning (disable : 4503)
#endif // _MSC_VER

#if defined (_WIN32)
	#ifdef _MSC_VER
		#pragma warning (disable : 4251)
	#endif // _MSC_VER

	#ifdef USE_IMPORT_EXPORT
		#ifdef AWSDOC_S3_EXPORTS
			#define AWSDOC_S3_API __declspec(dllexport)
		#else
			#define AWSDOC_S3_API __declspec(dllimport)
		#endif // AWSDOC_S3_EXPORTS
	#else
		#define AWSDOC_S3_API
	#endif // USE_IMPORT_EXPORT
#else // defined (WIN32)
	#define AWSDOC_S3_API
#endif // defined (WIN32)