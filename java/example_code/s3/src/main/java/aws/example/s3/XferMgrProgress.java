//snippet-sourceauthor: [soo-aws]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class XferMgrProgress
{
    // waits for the transfer to complete, catching any exceptions that occur.
    public static void waitForCompletion(Transfer xfer)
    {
        try {
            xfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            System.err.println("Amazon service error: " + e.getMessage());
            System.exit(1);
        } catch (AmazonClientException e) {
            System.err.println("Amazon client error: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Transfer interrupted: " + e.getMessage());
            System.exit(1);
        }
    }

    // Prints progress while waiting for the transfer to finish.
    public static void showTransferProgress(Transfer xfer)
    {
        // print the transfer's human-readable description
        System.out.println(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
            long so_far = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            eraseProgressBar();
            printProgressBar(pct);
        } while (xfer.isDone() == false);
        // print the final state of the transfer.
        TransferState xfer_state = xfer.getState();
        System.out.println(": " + xfer_state);
    }

    // Prints progress of a multiple file upload while waiting for it to finish.
    public static void showMultiUploadProgress(MultipleFileUpload multi_upload)
    {
        // print the upload's human-readable description
        System.out.println(multi_upload.getDescription());

        Collection<? extends Upload> sub_xfers = new ArrayList<Upload>();
        sub_xfers = multi_upload.getSubTransfers();

        do {
            System.out.println("\nSubtransfer progress:\n");
            for (Upload u : sub_xfers) {
                System.out.println("  " + u.getDescription());
                if (u.isDone()) {
                    TransferState xfer_state = u.getState();
                    System.out.println("  " + xfer_state);
                } else {
                    TransferProgress progress = u.getProgress();
                    double pct = progress.getPercentTransferred();
                    printProgressBar(pct);
                    System.out.println();
                }
            }

            // wait a bit before the next update.
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
               return;
            }
        } while (multi_upload.isDone() == false);
        // print the final state of the transfer.
        TransferState xfer_state = multi_upload.getState();
        System.out.println("\nMultipleFileUpload " + xfer_state);
    }

    // prints a simple text progressbar: [#####     ]
    public static void printProgressBar(double pct)
    {
        // if bar_size changes, then change erase_bar (in eraseProgressBar) to
        // match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int)(bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
              empty_bar.substring(0, bar_size - amt_full));
    }

    // erases the progress bar.
    public static void eraseProgressBar()
    {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }

    public static void uploadFileWithListener(String file_path,
          String bucket_name, String key_prefix, boolean pause)
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
            Upload u = xfer_mgr.upload(bucket_name, key_name, f);
            // print an empty progress bar...
            printProgressBar(0.0);
            u.addProgressListener(new ProgressListener() {
                public void progressChanged(ProgressEvent e) {
                    double pct = e.getBytesTransferred() * 100.0 / e.getBytes();
                    eraseProgressBar();
                    printProgressBar(pct);
                }
            });
            // block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(u);
            // print the final state of the transfer.
            TransferState xfer_state = u.getState();
            System.out.println(": " + xfer_state);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void uploadDirWithSubprogress(String dir_path,
            String bucket_name, String key_prefix, boolean recursive,
            boolean pause)
    {
        System.out.println("directory: " + dir_path + (recursive ?
                    " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager();
        try {
            MultipleFileUpload multi_upload = xfer_mgr.uploadDirectory(
                    bucket_name, key_prefix, new File(dir_path), recursive);
            // loop with Transfer.isDone()
            XferMgrProgress.showMultiUploadProgress(multi_upload);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(multi_upload);
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
            "    XferMgrProgress [--recursive] [--pause] <s3_path> <local_path>\n\n" +
            "Where:\n" +
            "    --recursive - Only applied if local_path is a directory.\n" +
            "                  Copies the contents of the directory recursively.\n\n" +
            "    --pause     - Attempt to pause+resume the upload. This may not work for\n" +
            "                  small files.\n\n" +
            "    s3_path     - The S3 destination (bucket/path) to upload the file(s) to.\n\n" +
            "    local_path  - The path to a local file or directory path to upload to S3.\n\n" +
            "Examples:\n" +
            "    XferMgrProgress public_photos/cat_happy.png my_photos/funny_cat.png\n" +
            "    XferMgrProgress public_photos my_photos/cat_sad.png\n" +
            "    XferMgrProgress public_photos my_photos\n\n";

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

        String bucket_name = s3_path[0];
        String key_prefix = null;
        if (s3_path.length > 1) {
            key_prefix = s3_path[1];
        }

        String local_path = args[cur_arg];

        // check to see if local path is a directory or file...
        File f = new File(args[cur_arg]);
        if (f.exists() == false) {
            System.out.println("Input path doesn't exist: " + args[cur_arg]);
            System.exit(1);
        }
        else if (f.isDirectory()) {
            uploadDirWithSubprogress(local_path, bucket_name, key_prefix,
                  recursive, pause);
        } else {
            uploadFileWithListener(local_path, bucket_name, key_prefix, pause);
        }
    }
}

