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
import aws.example.s3.Common;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.Transfer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Download objects to an Amazon S3 bucket using S3 TransferManager.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class XferMgrDownload
{
    public static void downloadDir(String dir_path, String bucket_name,
            String key_prefix, boolean recursive, boolean pause)
    {
        System.out.println("  directory: " + dir_path + (recursive ?
                    " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager();
        try {
            MultipleFileDownload downloads = xfer_mgr.downloadDirectory(
                    bucket_name, key_prefix, new File(dir_path));
            printDownloadProgress(downloads);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        System.out.println("");
    }

    public static void downloadFile(String file_path, String bucket_name,
            String key_prefix, boolean pause)
    {
        System.out.println("  file: " + file_path +
                (pause ? " (pause)" : ""));

        String key_name = null;
        if (key_prefix != null) {
            key_name = key_prefix + '/' + file_path;
        } else {
            key_name = file_path;
        }

        File f = new File(file_path);
        TransferManager xfer_mgr = new TransferManager();
        try {
            Download download = xfer_mgr.download(bucket_name, key_name, f);
            printDownloadProgress(download);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        System.out.println("");
    }

    // waits for the download to finish, and prints progress.
    public static void printDownloadProgress(Transfer download)
    {
        Common.printProgressBar(0.0, false);
        while (download.isDone() == false) {
            TransferProgress progress = download.getProgress();
            Common.printProgressBar(progress.getPercentTransferred(), true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // nothing.
            }
        }
        Common.printProgressBar(100.0, true);
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
            "    s3_path     - The S3 destination (bucket/path) to download the file(s) to.\n\n" +
            "    local_paths - One or more local paths to download to S3. These can be files\n" +
            "                  or directories. Globs are permitted (*.xml, etc.)\n\n" +
            "Examples:\n" +
            "    Copy my_photos/cat_happy.png public_photos/funny_cat.png\n\n" +
            "    Copy my_photos/cat_sad.png public_photos\n\n";

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
           }
           else {
              files_to_copy.add(args[cur_arg]);
           }
           cur_arg += 1;
        }

        String bucket_name = s3_path[0];
        String key_prefix = null;
        if (s3_path.length > 1) {
            key_prefix = s3_path[1];
        }

        System.out.println("Downloading to S3 bucket " + bucket_name +
                ((key_prefix != null) ? "using prefix \"" + key_prefix + "\"" : ""));

        // Download any directories in the list.
        for (String dir_path : dirs_to_copy) {
            downloadDir(dir_path, bucket_name, key_prefix, recursive, pause);
        }

        for (String key_path : files_to_copy) {
            downloadFile(key_path, bucket_name, key_prefix, pause);
        }

        System.out.println("Done!");
    }
}

