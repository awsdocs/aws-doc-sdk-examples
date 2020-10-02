// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[PutFile.java demonstrates how to upload a file to a branch.]
// snippet-service:[AWS CodeCommit]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CodeCommit]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-30]
// snippet-sourceauthor:[AWS - scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.example.commit;

// snippet-start:[codecommit.java2.put_file.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.PutFileRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.codecommit.model.PutFileResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
// snippet-end:[codecommit.java2.put_file.import]

public class PutFile {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutFile <repoName> <branchName> <filePath> <email> <name> <repoPath> <commitId>\n\n" +
                "Where:\n" +
                "    repoName - the name of the repository,\n" +
                "    branchName -  the name of the branch,\n" +
                "    filePath  - the location of the file on the local drive (i.e., C:\\AWS\\uploadGlacier.txt),\n" +
                "    email -  the email of the user whom uploads the file,\n" +
                "    name -  the name of the user,\n" +
                "    repoPath -  the location in the repo to store the file,\n" +
                "    commitId -  the full commit ID of the head commit in the branch\n" ;

        if (args.length < 7) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String repoName = args[0];
        String branchName = args[1];
        String filePath = args[2];
        String email = args[3];
        String name = args[4];
        String repoPath = args[5];
        String commitId = args[6];

        Region region = Region.US_EAST_1;
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        // Upload the file to the specified branch
        uploadFile(codeCommitClient, filePath, repoName, branchName, email, name, repoPath, commitId);
    }

    // snippet-start:[codecommit.java2.put_file.main]
    public static void uploadFile(CodeCommitClient codeCommitClient,
                                  String filePath,
                                  String repoName,
                                  String branchName,
                                  String email,
                                  String name,
                                  String repoPath,
                                  String commitId ){

        try {
            // Create an SdkBytes object that represents the file to upload
            File myFile = new File(filePath);
            InputStream is = new FileInputStream(myFile);
            SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

            PutFileRequest fileRequest = PutFileRequest.builder()
                .fileContent(fileToUpload)
                .repositoryName(repoName)
                .commitMessage("Uploaded via the Java API")
                .branchName(branchName)
                .filePath(repoPath)
                .parentCommitId(commitId)
                .email(email)
                .name(name)
                .build();

            // Upload file to the branch
            PutFileResponse fileResponse = codeCommitClient.putFile(fileRequest);
            System.out.println("The commit ID is "+fileResponse.commitId());

        } catch (CodeCommitException | FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[codecommit.java2.put_file.main]
}
