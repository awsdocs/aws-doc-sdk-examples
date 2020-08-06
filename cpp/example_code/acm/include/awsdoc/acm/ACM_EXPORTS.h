// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once

#ifdef _MSC_VER
    // Disable Windows warning about max template size.
    #pragma warning (disable : 4503)
#endif // _MSC_VER

#if defined (_WIN32)
    #ifdef _MSC_VER
        #pragma warning (disable : 4251)
    #endif // _MSC_VER

    #ifdef USE_IMPORT_EXPORT
        #ifdef AWSDOC_ACM_EXPORTS
            #define AWSDOC_ACM_API __declspec(dllexport)
        #else
            #define AWSDOC_ACM_API __declspec(dllimport)
        #endif // AWSDOC_ACM_EXPORTS
    #else
        #define AWSDOC_ACM_API
    #endif // USE_IMPORT_EXPORT
#else // defined (WIN32)
    #define AWSDOC_ACM_API
#endif // defined (WIN32)