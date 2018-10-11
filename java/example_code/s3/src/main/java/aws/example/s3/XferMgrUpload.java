 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.s3;
import aws.example.s3.XferMgrProgress;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Upload objects to an Amazon S3 bucket using S3 TransferManager.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class XferMgrUpload
{
    public static void uploadDir(String dir_path, String bucket_name,
            String key_prefix, boolean recursive, boolean pause)
    {
        System.out.println("directory: " + dir_path + (recursive ?
                    " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadDirectory(bucket_name,
                    key_prefix, new File(dir_path), recursive);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void uploadFileList(String[] file_paths, String bucket_name,
            String key_prefix, boolean pause)
    {
        System.out.println("file list: " + Arrays.toString(file_paths) +
                (pause ? " (pause)" : ""));
        // convert the file paths to a list of File objects (required by the
        // uploadFileList method)
        ArrayList<File> files = new ArrayList<File>();
        for (String path : file_paths) {
            files.add(new File(path));
        }

        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadFileList(bucket_name,
                    key_prefix, new File("."), files);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void uploadFile(String file_path, String bucket_name,
            String key_prefix, boolean pause)
    {
        System.out.println("file: " + file_path +
                (pause ? " (pause)" : ""));

        String key_name = null;
        if (key_prefix != null) {
            key_name = key_prefix + '/' + file_path;
        } else {
            key_name = file_path;
        }

        File f = new File(file_path);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            Upload xfer = xfer_mgr.upload(bucket_name, key_name, f);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            //  or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    XferMgrUpload [--recursive] [--pause] <s3_path> <local_paths>\n\n" +
            "Where:\n" +
            "    --recursive - Only applied if local_path is a directory.\n" +
            "                  Copies the contents of the directory recursively.\n\n" +
            "    --pause     - Attempt to pause+resume the upload. This may not work for\n" +
            "                  small files.\n\n" +
            "    s3_path     - The S3 destination (bucket/path) to upload the file(s) to.\n\n" +
            "    local_paths - One or more local paths to upload to S3. These can be files\n" +
            "                  or directories. Globs are permitted (*.xml, etc.)\n\n" +
            "Examples:\n" +
            "    XferMgrUpload public_photos/cat_happy.png my_photos/funny_cat.png\n" +
            "    XferMgrUpload public_photos my_photos/cat_sad.png\n" +
            "    XferMgrUpload public_photos my_photos/cat*.png\n" +
            "    XferMgrUpload public_photos my_photos\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        int cur_arg = 0;
        boolean recursive = false;
        boolean pause = false;

        // first, parse any switches
        while (args[cur_arg].startsWith("--")) {
           if (args[cur_arg].equals("--recursive")) {
              recursive = true;
           } else if (args[cur_arg].equals("--pause")) {
              pause = true;
           } else {
              System.out.println("Unknown argument: " + args[cur_arg]);
              System.out.println(USAGE);
              System.exit(1);
           }
           cur_arg += 1;
        }

        // only the first '/' character is of interest to get the bucket name.
        // Subsequent ones are part of the key name.
        String s3_path[] = args[cur_arg].split("/", 2);
        cur_arg += 1;

        // Any remaining args are assumed to be local paths to copy.
        // They may be directories, arrays, or a mix of both.
        ArrayList<String> dirs_to_copy = new ArrayList<String>();
        ArrayList<String> files_to_copy = new ArrayList<String>();

        while (cur_arg < args.length) {
           // check to see if local path is a directory or file...
           File f = new File(args[cur_arg]);
           if (f.exists() == false) {
              System.out.println("Input path doesn't exist: " + args[cur_arg]);
              System.exit(1);
           }
           else if (f.isDirectory()) {
              dirs_to_copy.add(args[cur_arg]);
           } else {
              files_to_copy.add(args[cur_arg]);
           }
           cur_arg += 1;
        }

        String bucket_name = s3_path[0];
        String key_prefix = null;
        if (s3_path.length > 1) {
            key_prefix = s3_path[1];
        }

        // Upload any directories in the list.
        for (String dir_path : dirs_to_copy) {
            uploadDir(dir_path, bucket_name, key_prefix, recursive, pause);
        }

        // If there's more than one file in the list, upload it as a file list.
        // Otherwise, upload it as a single file.
        if (files_to_copy.size() > 1) {
            uploadFileList(files_to_copy.toArray(new String[0]), bucket_name,
                    key_prefix, pause);
        } else if (files_to_copy.size() == 1) {
            uploadFile(files_to_copy.get(0), bucket_name, key_prefix, pause);
        } // else: nothing to do.
    }
}

