// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.ec2

// snippet-start:[ec2.kotlin.allocate_address.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.AllocateAddressRequest
import aws.sdk.kotlin.services.ec2.model.AssociateAddressRequest
import aws.sdk.kotlin.services.ec2.model.DomainType
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.allocate_address.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <instanceId> 

    Where:
        instanceId - An instance id value that you can obtain from the AWS Management Console.  
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val instanceId = args[0]
    val idValue = getAllocateAddress(instanceId)
    println("The id value for the allocated elastic IP address is $idValue")
}

// snippet-start:[ec2.kotlin.allocate_address.main]
suspend fun getAllocateAddress(instanceIdVal: String?): String? {
    val allocateRequest =
        AllocateAddressRequest {
            domain = DomainType.Vpc
        }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val allocateResponse = ec2.allocateAddress(allocateRequest)
        val allocationIdVal = allocateResponse.allocationId

        val request =
            AssociateAddressRequest {
                instanceId = instanceIdVal
                allocationId = allocationIdVal
            }

        val associateResponse = ec2.associateAddress(request)
        return associateResponse.associationId
    }
}
// snippet-end:[ec2.kotlin.allocate_address.main]
