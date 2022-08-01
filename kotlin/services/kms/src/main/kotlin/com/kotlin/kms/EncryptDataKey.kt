// snippet-sourcedescription:[EncryptDataKey.kt demonstrates how to encrypt and decrypt data by using an AWS Key Management Service (KMS) key.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Key Management Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_encrypt_data.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.DecryptRequest
import aws.sdk.kotlin.services.kms.model.EncryptRequest
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_encrypt_data.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <keyId> <path>
        Where:
            keyId - A key id value to describe (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). 
            path - The path of a text file where the data is written to (for example, C:\AWS\TextFile.txt). 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val keyId = args[0]
    val path = args[1]
    val encryptedData = encryptData(keyId)
    decryptData(encryptedData, keyId, path)
}

// snippet-start:[kms.kotlin_encrypt_data.main]
suspend fun encryptData(keyIdValue: String): ByteArray? {

    val text = "This is the text to encrypt by using the AWS KMS Service"
    val myBytes: ByteArray = text.toByteArray()

    val encryptRequest = EncryptRequest {
        keyId = keyIdValue
        plaintext = myBytes
    }

    KmsClient { region = "us-west-2" }.use { kmsClient ->
        val response = kmsClient.encrypt(encryptRequest)
        val algorithm: String = response.encryptionAlgorithm.toString()
        println("The encryption algorithm is $algorithm")

        // Return the encrypted data.
        return response.ciphertextBlob
    }
}

suspend fun decryptData(encryptedDataVal: ByteArray?, keyIdVal: String?, path: String) {

    val decryptRequest = DecryptRequest {
        ciphertextBlob = encryptedDataVal
        keyId = keyIdVal
    }
    KmsClient { region = "us-west-2" }.use { kmsClient ->
        val decryptResponse = kmsClient.decrypt(decryptRequest)
        val myVal = decryptResponse.plaintext

        // Write the decrypted data to a file.
        if (myVal != null) {
            File(path).writeBytes(myVal)
        }
    }
}
// snippet-end:[kms.kotlin_encrypt_data.main]
