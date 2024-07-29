// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ecr.scenario

// snippet-start:[ecr.kotlin_scenario.parent.main]
import java.util.Scanner

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 *
 * This code example requires an IAM Role that has permissions to interact with the Amazon ECR service.
 *
 * To create an IAM role, see:
 *
 * https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html
 *
 * This code example requires a local docker image named echo-text. Without a local image,
 * this program will not successfully run. For more information including how to create the local
 * image, see:
 *
 * /getting_started_scenarios/ecr_scenario/README
 *
 */

val DASHES = String(CharArray(80)).replace("\u0000", "-")

suspend fun main(args: Array<String>) {
    val usage =
        """
        Usage: <iamRoleARN> <accountId>

        Where:
           iamRoleARN - The IAM role ARN that has the necessary permissions to access and manage the Amazon ECR repository.
           accountId - Your AWS account number. 
        
        """.trimIndent()

    // if (args.size != 2) {
    //     println(usage)
    //     return
    // }

    var iamRole = "arn:aws:iam::814548047983:role/Admin"
    var localImageName: String
    var accountId = "814548047983"
    val ecrActions = ECRActions()
    val scanner = Scanner(System.`in`)

    println(
        """
        The Amazon Elastic Container Registry (ECR) is a fully-managed Docker container registry 
        service provided by AWS. It allows developers and organizations to securely 
        store, manage, and deploy Docker container images. 
        ECR provides a simple and scalable way to manage container images throughout their lifecycle, 
        from building and testing to production deployment. 
                        
        The `EcrClient` service client that is part of the AWS SDK for Kotlin provides a set of methods to 
        programmatically interact with the Amazon ECR service. This allows developers to 
        automate the storage, retrieval, and management of container images as part of their application 
        deployment pipelines. With ECR, teams can focus on building and deploying their 
        applications without having to worry about the underlying infrastructure required to 
        host and manage a container registry.
            
        This scenario walks you through how to perform key operations for this service.  
        Let's get started...
        
         You have two choices:
            1 - Run the entire program.
            2 - Delete an existing Amazon ECR repository named echo-text (created from a previous execution of 
            this program that did not complete).
          
        """.trimIndent(),
    )

    while (true) {
        val input = scanner.nextLine()
        if (input.trim { it <= ' ' }.equals("1", ignoreCase = true)) {
            println("Continuing with the program...")
            println("")
            break
        } else if (input.trim { it <= ' ' }.equals("2", ignoreCase = true)) {
            val repoName = "echo-text"
            ecrActions.deleteECRRepository(repoName)
            return
        } else {
            // Handle invalid input.
            println("Invalid input. Please try again.")
        }
    }

    waitForInputToContinue(scanner)
    println(DASHES)
    println(
        """
        1. Create an ECR repository.
         
        The first task is to ensure we have a local Docker image named echo-text. 
        If this image exists, then an Amazon ECR repository is created. 
        
        An ECR repository is a private Docker container repository provided 
        by Amazon Web Services (AWS). It is a managed service that makes it easy 
        to store, manage, and deploy Docker container images. 
        
        """.trimIndent(),
    )

    // Ensure that a local docker image named echo-text exists.
    val doesExist = ecrActions.listLocalImages()
    val repoName: String
    if (!doesExist) {
        println("The local image named echo-text does not exist")
        return
    } else {
        localImageName = "echo-text"
        repoName = "echo-text"
    }

    val repoArn = ecrActions.createECRRepository(repoName).toString()
    println("The ARN of the ECR repository is $repoArn")
    waitForInputToContinue(scanner)

    println(DASHES)
    println(
        """
        2. Set an ECR repository policy.
        
        Setting an ECR repository policy using the `setRepositoryPolicy` function is crucial for maintaining
        the security and integrity of your container images. The repository policy allows you to 
        define specific rules and restrictions for accessing and managing the images stored within your ECR 
        repository.    
        
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    ecrActions.setRepoPolicy(repoName, iamRole)
    waitForInputToContinue(scanner)

    println(DASHES)
    println(
        """
        3. Display ECR repository policy.
        
        Now we will retrieve the ECR policy to ensure it was successfully set.   
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    val policyText = ecrActions.getRepoPolicy(repoName)
    println("Policy Text:")
    println(policyText)
    waitForInputToContinue(scanner)

    println(DASHES)
    println(
        """
        4. Retrieve an ECR authorization token.
        
        You need an authorization token to securely access and interact with the Amazon ECR registry. 
        The `getAuthorizationToken` method of the `EcrAsyncClient` is responsible for securely accessing 
        and interacting with an Amazon ECR repository. This operation is responsible for obtaining a 
        valid authorization token, which is required to authenticate your requests to the ECR service. 
        
        Without a valid authorization token, you would not be able to perform any operations on the 
        ECR repository, such as pushing, pulling, or managing your Docker images.    
        
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    ecrActions.getAuthToken()
    waitForInputToContinue(scanner)

    println(DASHES)
    println(
        """
        5. Get the ECR Repository URI.
                    
        The URI  of an Amazon ECR repository is important. When you want to deploy a container image to 
        a container orchestration platform like Amazon Elastic Kubernetes Service (EKS) 
        or Amazon Elastic Container Service (ECS), you need to specify the full image URI, 
        which includes the ECR repository URI. This allows the container runtime to pull the 
        correct container image from the ECR repository.    
        
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    val repositoryURI: String? = ecrActions.getRepositoryURI(repoName)
    println("The repository URI is $repositoryURI")
    waitForInputToContinue(scanner)

    println(DASHES)
    println(
        """
        6. Set an ECR Lifecycle Policy.
                    
        An ECR Lifecycle Policy is used to manage the lifecycle of Docker images stored in your ECR repositories. 
        These policies allow you to automatically remove old or unused Docker images from your repositories, 
        freeing up storage space and reducing costs.    
        
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    val pol = ecrActions.setLifeCyclePolicy(repoName)
    println(pol)
    waitForInputToContinue(scanner)

    println(DASHES)
    println(
        """
        7. Push a docker image to the Amazon ECR Repository.
            
        The `pushImageCmd()` method pushes a local Docker image to an Amazon ECR repository.
        It sets up the Docker client by connecting to the local Docker host using the default port.
        It then retrieves the authorization token for the ECR repository by making a call to the AWS SDK.
            
        The method uses the authorization token to create an `AuthConfig` object, which is used to authenticate
        the Docker client when pushing the image. Finally, the method tags the Docker image with the specified
        repository name and image tag, and then pushes the image to the ECR repository using the Docker client.
        If the push operation is successful, the method prints a message indicating that the image was pushed to ECR.
        
        """.trimIndent(),
    )

    waitForInputToContinue(scanner)
    ecrActions.pushDockerImage(repoName, localImageName)
    waitForInputToContinue(scanner)

    println(DASHES)
    println("8. Verify if the image is in the ECR Repository.")
    waitForInputToContinue(scanner)
    ecrActions.verifyImage(repoName, localImageName)
    waitForInputToContinue(scanner)

    println(DASHES)
    println("9. As an optional step, you can interact with the image in Amazon ECR by using the CLI.")
    println("Would you like to view instructions on how to use the CLI to run the image? (y/n)")
    val ans = scanner.nextLine().trim()
    if (ans.equals("y", true)) {
        val instructions = """
        1. Authenticate with ECR - Before you can pull the image from Amazon ECR, you need to authenticate with the registry. You can do this using the AWS CLI:
        
            aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $accountId.dkr.ecr.us-east-1.amazonaws.com
        
        2. Describe the image using this command:
        
           aws ecr describe-images --repository-name $repoName --image-ids imageTag=$localImageName
        
        3. Run the Docker container and view the output using this command:
        
           docker run --rm $accountId.dkr.ecr.us-east-1.amazonaws.com/$repoName:$localImageName
        """
        println(instructions)
    }
    waitForInputToContinue(scanner)

    println(DASHES)
    println("10. Delete the ECR Repository.")
    println(
        """
        If the repository isn't empty, you must either delete the contents of the repository 
        or use the force option (used in this scenario) to delete the repository and have Amazon ECR delete all of its contents 
        on your behalf.
        
        """.trimIndent(),
    )
    println("Would you like to delete the Amazon ECR Repository? (y/n)")
    val delAns = scanner.nextLine().trim { it <= ' ' }
    if (delAns.equals("y", ignoreCase = true)) {
        println("You selected to delete the AWS ECR resources.")
        waitForInputToContinue(scanner)
        ecrActions.deleteECRRepository(repoName)
    }

    println(DASHES)
    println("This concludes the Amazon ECR SDK scenario")
    println(DASHES)
}

private fun waitForInputToContinue(scanner: Scanner) {
    while (true) {
        println("")
        println("Enter 'c' followed by <ENTER> to continue:")
        val input = scanner.nextLine()
        if (input.trim { it <= ' ' }.equals("c", ignoreCase = true)) {
            println("Continuing with the program...")
            println("")
            break
        } else {
            // Handle invalid input.
            println("Invalid input. Please try again.")
        }
    }
}
// snippet-end:[ecr.kotlin_scenario.parent.main]
