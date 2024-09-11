// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0package aws.example.s3;
// snippet-start:[s3.java1.s3_xfer_mgr_copy.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
// snippet-end:[s3.java1.s3_xfer_mgr_copy.import]

// snippet-start:[s3.java1.s3_xfer_mgr_copy.complete]

/**
 * Copy an object from one Amazon S3 bucket to another using S3 TransferManager.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class XferMgrCopy {
    public static void copyObjectSimple(String from_bucket, String from_key,
            String to_bucket, String to_key) {
        // snippet-start:[s3.java1.s3_xfer_mgr_copy.copy_object]
        System.out.println("Copying s3 object: " + from_key);
        System.out.println("      from bucket: " + from_bucket);
        System.out.println("     to s3 object: " + to_key);
        System.out.println("        in bucket: " + to_bucket);

        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            Copy xfer = xfer_mgr.copy(from_bucket, from_key, to_bucket, to_key);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        // snippet-end:[s3.java1.s3_xfer_mgr_copy.copy_object]
    }

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    Copy <s3_src> <s3_dest>\n\n" +
                "Where:\n" +
                "    s3_src  - the source (bucket/key) of the object to copy.\n\n" +
                "    s3_dest - the destination of the object. A key name is optional.\n" +
                "              If a destination key name is not given, the object\n" +
                "              will be copied with the same name.\n\n" +
                "Examples:\n" +
                "    Copy my_photos/cat_happy.png public_photos/funny_cat.png\n" +
                "    Copy my_photos/cat_sad.png public_photos\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // only the first '/' character is of interest to get the bucket name.
        // Subsequent ones are part of the key name.
        String[] src = args[0].split("/", 2);
        String[] dst = args[1].split("/", 2);

        if (src.length < 2) {
            System.out.println("I need both a bucket and key name to copy!");
            System.out.println(USAGE);
            System.exit(1);
        }

        if (dst.length < 2) {
            copyObjectSimple(src[0], src[1], dst[0], src[1]);
        } else {
            copyObjectSimple(src[0], src[1], dst[0], dst[1]);
        }
    }
}
// snippet-end:[s3.java1.s3_xfer_mgr_copy.complete]