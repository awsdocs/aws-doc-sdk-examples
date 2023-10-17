import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";
import { describe, it, expect } from "vitest";
import { createSAMLProvider } from "../actions/create-saml-provider.js";
import { listSamlProviders } from "../actions/list-saml-providers.js";
import { deleteSAMLProvider } from "../actions/delete-saml-provider.js";

describe("SAML provider", () => {
  it("should create, list, and delete a SAML provider", async () => {
    // Create SAML provider.
    const providerName = getUniqueName("saml-provider");
    const { SAMLProviderArn } = await createSAMLProvider(providerName);
    if (!SAMLProviderArn) {
      throw new Error("SAMLProviderArn is undefined");
    }

    // List SAML provider.
    let provider = await findSAMLProvider(SAMLProviderArn);
    expect(provider?.Arn).toEqual(SAMLProviderArn);

    // Delete SAML provider.
    await deleteSAMLProvider(SAMLProviderArn);

    provider = await findSAMLProvider(SAMLProviderArn);
    expect(provider).toBeUndefined();
  });
});

/**
 *
 * @param {string} providerArn
 */
const findSAMLProvider = async (providerArn) => {
  const { SAMLProviderList } = await listSamlProviders();
  if (!SAMLProviderList) {
    throw new Error("SAMLProviderList is undefined");
  }

  return SAMLProviderList.find(
    (samlProvider) => samlProvider.Arn === providerArn,
  );
};
