import { describe, it, expect } from "vitest";

import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";

import { listServerCertificates } from "../actions/list-server-certificates.js";
import { uploadServerCertificate } from "../actions/upload-server-certificate.js";
import { updateServerCertificate } from "../actions/update-server-certificate.js";
import { deleteServerCertificate } from "../actions/delete-server-certificate.js";
import { getServerCertificate } from "../actions/get-server-certificate.js";

describe("Server certificates", () => {
  it("should create, read, update, and delete server certificates", async () => {
    // Create a server certificate.
    const certName = getUniqueName("test-cert");
    await uploadServerCertificate(certName);

    // List server certificates.
    let serverCertificate = await findServerCertificate(certName);
    expect(serverCertificate).toBeDefined();

    const { ServerCertificate } = await getServerCertificate(certName);
    expect(
      ServerCertificate?.ServerCertificateMetadata?.ServerCertificateName,
    ).toEqual(certName);

    // Update the server certificate.
    const newCertName = getUniqueName("test-cert");
    await updateServerCertificate(certName, newCertName);
    serverCertificate = await findServerCertificate(newCertName);
    expect(serverCertificate).toBeDefined();

    // Delete the server certificate.
    await deleteServerCertificate(newCertName);

    serverCertificate = await findServerCertificate(certName);
    expect(serverCertificate).toBeUndefined();
  });
});

/**
 *
 * @param {string} name
 */
const findServerCertificate = async (name) => {
  for await (const serverCertificate of listServerCertificates()) {
    if (serverCertificate.ServerCertificateName === name) {
      return serverCertificate;
    }
  }
};
