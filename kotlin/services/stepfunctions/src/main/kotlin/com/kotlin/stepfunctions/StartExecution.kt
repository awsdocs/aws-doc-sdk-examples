//snippet-sourcedescription:[StartExecution.kt demonstrates how to start a state machine execution for AWS Step Functions.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Step Functions]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.start_execute.import]
import  aws.sdk.kotlin.services.sfn.SfnClient
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import aws.sdk.kotlin.services.sfn.model.StartExecutionRequest
import org.json.simple.parser.ParseException
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.start_execute.import]

suspend fun main(args:Array<String>) {

    val usage = """
      Usage:
         <stateMachineName> <jsonFile>
      Where:
         stateMachineArn - The ARN of the state machine.
         jsonFile - A JSON file that contains the values to pass to the workflow.

    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val stateMachineARN = args[0]
    val jsonFile = args[1]
    val exeArn = startWorkflow(stateMachineARN, jsonFile)
    println("The execution ARN is $exeArn")
}

// snippet-start:[stepfunctions.kotlin.start_execute.main]
suspend fun startWorkflow(stateMachineArnVal: String?, jsonFile: String): String? {
        val json = getJSONString(jsonFile)

        // Specify the name of the execution by using a GUID value.
        val uuid = UUID.randomUUID()
        val uuidValue = uuid.toString()
        val request = StartExecutionRequest {
            input  = json
            stateMachineArn = stateMachineArnVal
            name = uuidValue
        }

        SfnClient { region = "us-east-1" }.use { sfnClient ->
            val response = sfnClient.startExecution(request)
            return response.executionArn
        }
     }

    private fun getJSONString(path: String): String {

        try {
            val parser = JSONParser()
            val data = parser.parse(FileReader(path)) as JSONObject //path to the JSON file.
            return data.toJSONString()

        } catch (e: IOException) {
            print(e.message)
        } catch (e: ParseException) {
            print(e.message)
        }
        return ""
 }
// snippet-end:[stepfunctions.kotlin.start_execute.main]