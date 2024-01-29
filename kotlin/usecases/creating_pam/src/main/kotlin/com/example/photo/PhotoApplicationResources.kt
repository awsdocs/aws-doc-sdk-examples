// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.photo

import com.google.gson.Gson
import java.util.Map

class PhotoApplicationResources {

    companion object {
        val LABELS_TABLE = "<Enter value>"
        val STORAGE_BUCKET = "<Enter value>"
        val WORKING_BUCKET = "<Enter value>"
        val TOPIC_ARN = "<Enter value>"
        val CORS_HEADER_MAP = Map.of("Access-Control-Allow-Origin", "*")
        val gson = Gson()
    }
}
