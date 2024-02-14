require "rspec"
require_relative("../list_saml_providers")

describe SamlProviderManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:manager) { SamlProviderManager.new(iam_client) }

  describe "#list_saml_providers" do
    it "logs the ARNs of up to the specified number of SAML providers" do
      expect { manager.list_saml_providers(10) }.not_to raise_error
    end
  end
end
