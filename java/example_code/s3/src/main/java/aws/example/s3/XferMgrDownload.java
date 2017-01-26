/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import java.io.File;

/**
 * Download objects to an Amazon S3 bucket using S3 TransferManager.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class XferMgrDownload
{
    public static void downloadDir(String bucket_name, String key_prefix,
          String dir_path, boolean pause)
    {
        System.out.println("downloading to directory: " + dir_path +
              (pause ?  " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager();
        try {
            MultipleFileDownload xfer = xfer_mgr.downloadDirectory(
                    bucket_name, key_prefix, new File(dir_path));
            // loop with xfer.isDone() or block with xfer.waitForCompletion()
            XferMgrProgress.showTransferProgress(xfer);
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void downloadFile(String bucket_name, String key_name,
          String file_path, boolean pause)
    {
        System.out.println("Downloading to file: " + file_path +
              (pause ? " (pause)" : ""));

        File f = new File(file_path);
        TransferManager xfer_mgr = new TransferManager();
        try {
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            // loop with xfer.isDone() or block with xfer.waitForCompletion()
            XferMgrProgress.showTransferProgress(xfer);
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
            "    Download [--recursive] [--pause] <s3_path> <local_paths>\n\n" +
            "Where:\n" +
            "    --recursive - Only applied if local_path is a directory.\n" +
            "                  Copies the contents of the directory recursively.\n\n" +
            "    --pause     - Attempt to pause+resume the download. This may not work for\n" +
            "                  small files.\n\n" +
            "    s3_path     - The S3 (bucket/path) to download the file(s) from. This can be\n" +
            "                  a single object or a set of files that share a common prefix.\n\n" +
            "                  * If the path ends with a '/', it is assumed to be a *path prefix*,\n" +
            "                    and all objects that share the same prefix will be downloaded to\n" +
            "                    the directory given in local_path.\n" +
            "                  * Otherwise, the S3 path is assumed to refer to an object, which\n" +
            "                    will be downloaded to the file name given in local_path.\n\n" +
            "    local_path  - The local path to use to download the object(s) specified in\n" +
            "                  s3_path.\n" +
            "                  * If s3_path ends with a '/', then local_path *must* refer to a\n" +
            "                    local directory. It will be created if it doesn't already\n" +
            "                    exist.\n" +
            "                  * Otherwise, local_path is scanned to see if it's a directory or\n" +
            "                    file. If it's a file, the specified file name will be used for\n" +
            "                    the object in s3_path. If it's a directory, the file in s3_path\n" +
            "                    will be downloaded into that directory. If the path doesn't exist\n" +
            "                    or is empty, then a file will be created with the object key name\n" +
            "                    in s3_path.\n\n" +
            "Examples:\n" +
            "    XferMgrDownload public_photos/cat_happy.png\n" +
            "    XferMgrDownload public_photos/ my_photos\n\n";

        if (args.length < 1) {
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
        String bucket_name = s3_path[0];
        String key_name = s3_path[1];
        boolean s3_path_is_prefix = (key_name.lastIndexOf('/') == key_name.length()-1);
        cur_arg += 1;


        // The final argument is either a local directory or file to copy to.
        // If there is no final arg, use the key (object) name as the local file
        // name.
        String local_path = ((cur_arg < args.length) ? args[cur_arg] : key_name);
        File f = new File(local_path);
        if (f.isFile() && s3_path_is_prefix) {
           System.out.format(
                 "You can't copy an S3 prefix (%) into a single file!\n",
                 key_name);
           System.exit(1);
        }

        // If the path already exists, print a warning.
        if (f.exists()) {
           System.out.println("The local path already exists: " + local_path);
           String a = System.console().readLine("Do you want to overwrite it anyway? (yes/no): ");
           if (!a.toLowerCase().equals("yes")) {
              System.out.println("Aborting download!");
              System.exit(0);
           }
        } else if (s3_path_is_prefix) {
           try {
              f.mkdir();
           } catch (Exception e) {
              System.out.println("Couldn't create destination directory!");
              System.exit(1);
           }
        }

        // Assume that the path exists, do the download.
        if (s3_path_is_prefix) {
           downloadDir(bucket_name, key_name, local_path, false);
        } else {
           downloadFile(bucket_name, key_name, local_path, false);
        }
    }
}

