/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.photo

import com.google.gson.Gson
import java.util.Map

class PhotoApplicationResources {

    companion object {
        val LABELS_TABLE = "lam200-Java-PAM-PamTablesLabelsTable6998F453-1AY3UH5OVH24H"
        val STORAGE_BUCKET = "lam200-java-pam-pambucketsstoragebucket08049213-1h3fcp6kz7kjf"
        val WORKING_BUCKET = "lam200-java-pam-pambucketsworkingbucket9940330b-17j4gk7djl1sz"
        val TOPIC_ARN = "arn:aws:sns:us-east-1:814548047983:lam200-Java-PAM-PamNotifier9D8DBE72-hIIAi9PvosRe"

        val CORS_HEADER_MAP = Map.of(
            "Access-Control-Allow-Origin", "*"
        )
        val gson = Gson()
    }
}
