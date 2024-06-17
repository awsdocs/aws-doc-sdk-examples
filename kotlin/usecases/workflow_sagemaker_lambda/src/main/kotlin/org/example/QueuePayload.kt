// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example

class QueuePayload {

    // The payload job token.
    private var token: String? = null

    // The Amazon Resource Name (ARN) of the pipeline run.
    private var pipelineExecutionArn: String? = null

    // The status of the job.
    private var status: String? = null

    // A dictionary of payload arguments.
    private var arguments: HashMap<String?, String?>? = null

    // Constructor
    fun QueuePayload() {}

    // Getter and Setter methods for token
    fun getToken(): String? = token

    fun setToken(token: String?) {
        this.token = token
    }

    // Getter and Setter methods for pipelineExecutionArn
    fun getPipelineExecutionArn(): String? = pipelineExecutionArn

    fun setPipelineExecutionArn(pipelineExecutionArn: String?) {
        this.pipelineExecutionArn = pipelineExecutionArn
    }

    // Getter and Setter methods for status
    fun getStatus(): String? = status

    fun setStatus(status: String?) {
        this.status = status
    }

    // Getter and Setter methods for arguments
    fun getArguments(): HashMap<String?, String?>? = arguments

    fun setArguments(arguments: HashMap<String?, String?>?) {
        this.arguments = arguments
    }
}
