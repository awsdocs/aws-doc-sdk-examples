#!/usr/bin/env node
import { Construct, Stack, StackProps } from "@aws-cdk/core";
import * as ecr from "@aws-cdk/aws-ecr";

export class ContainerImage extends Stack {
  constructor(scope: Construct, id: string, props: StackProps = {}) {
    super(scope, id, props);

    new ecr.CfnPublicRepository(this, "sap-abap", {
      repositoryName: "sap-abap",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for SAP ABAP environment and is recommended for local testing of SDK for SAP ABAP example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/sap-abap/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for SAP ABAP environment and is recommended for local testing of SDK for SAP ABAP example code."
      },
    });
    new ecr.CfnPublicRepository(this, "cpp", {
      repositoryName: "cpp",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for C++ environment and is recommended for local testing of SDK for C++ example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/cpp/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for C++ environment and is recommended for local testing of SDK for C++ example code."
      },
    });
    new ecr.CfnPublicRepository(this, "gov2", {
      repositoryName: "gov2",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Go environment and is recommended for local testing of SDK for Go example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/gov2/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Go environment and is recommended for local testing of SDK for Go example code."
      },
    });
    new ecr.CfnPublicRepository(this, "javav2", {
      repositoryName: "javav2",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Java environment and is recommended for local testing of SDK for Java example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Java environment and is recommended for local testing of SDK for Java example code."
      },
    });
    new ecr.CfnPublicRepository(this, "javascriptv3", {
      repositoryName: "javascriptv3",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for JavaScript environment and is recommended for local testing of SDK for JavaScript example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javascriptv3/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for JavaScript environment and is recommended for local testing of SDK for JavaScript example code."
      },
    });
    new ecr.CfnPublicRepository(this, "kotlin", {
      repositoryName: "kotlin",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Kotlin environment and is recommended for local testing of SDK for Kotlin example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Kotlin environment and is recommended for local testing of SDK for Kotlin example code."
      },
    });
    new ecr.CfnPublicRepository(this, "dotnetv3", {
      repositoryName: "dotnetv3",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for .NET environment and is recommended for local testing of SDK for .NET example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/dotnetv3/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for .NET environment and is recommended for local testing of SDK for .NET example code."
      },
    });
    new ecr.CfnPublicRepository(this, "php", {
      repositoryName: "php",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for PHP environment and is recommended for local testing of SDK for PHP example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/php/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for PHP environment and is recommended for local testing of SDK for PHP example code."
      },
    });
    new ecr.CfnPublicRepository(this, "python", {
      repositoryName: "python",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Python environment and is recommended for local testing of SDK for Python example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Python environment and is recommended for local testing of SDK for Python example code."
      },
    });
    new ecr.CfnPublicRepository(this, "ruby", {
      repositoryName: "ruby",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Ruby environment and is recommended for local testing of SDK for Ruby example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Ruby environment and is recommended for local testing of SDK for Ruby example code."
      },
    });
    new ecr.CfnPublicRepository(this, "rust_dev_preview", {
      repositoryName: "rust_dev_preview",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Rust environment and is recommended for local testing of SDK for Rust example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/rust_dev_preview/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Rust environment and is recommended for local testing of SDK for Rust example code."
      },
    });
    new ecr.CfnPublicRepository(this, "swift", {
      repositoryName: "swift",
      repositoryCatalogData: {
        UsageText:  "This image provides a pre-built for SDK for Swift environment and is recommended for local testing of SDK for Swift example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, please see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/swift/README.md#docker-image-beta.",
        OperatingSystems: ["Linux"],
        Architectures: ["x86", "ARM"],
        RepositoryDescription: "This image provides a pre-built for SDK for Swift environment and is recommended for local testing of SDK for Swift example code."
      },
    });
  }
}
