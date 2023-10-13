/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[javascript.v3.iam.actions.UploadServerCertificate]
import { UploadServerCertificateCommand, IAMClient } from "@aws-sdk/client-iam";
import { readFileSync } from "fs";
import { dirnameFromMetaUrl } from "@aws-sdk-examples/libs/utils/util-fs.js";
import * as path from "path";

const client = new IAMClient({});

/**
 * The certificate body and private key were generated with the
 * following command.
 *
 * ```
 * openssl req -x509 -newkey rsa:4096 -sha256 -days 3650 -nodes \
 * -keyout example.key -out example.crt -subj "/CN=example.com" \
 * -addext "subjectAltName=DNS:example.com,DNS:www.example.net,IP:10.0.0.1"
 * ```
 */

const certBody = readFileSync(
  path.join(
    dirnameFromMetaUrl(import.meta.url),
    "../../../../resources/sample_files/sample_cert.pem",
  ),
);

const privateKey = readFileSync(
  path.join(
    dirnameFromMetaUrl(import.meta.url),
    "../../../../resources/sample_files/sample_private_key.pem",
  ),
);

/**
 *
 * @param {string} certificateName
 */
export const uploadServerCertificate = (certificateName) => {
  const command = new UploadServerCertificateCommand({
    ServerCertificateName: certificateName,
    CertificateBody: certBody.toString(),
    PrivateKey: privateKey.toString(),
  });

  return client.send(command);
};
// snippet-end:[javascript.v3.iam.actions.UploadServerCertificate]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  uploadServerCertificate("CERTIFICATE_NAME");
}
