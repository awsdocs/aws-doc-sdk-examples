#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Prereq: Install and run Ailly
# npm install -g @ailly/cli

# Choose your engine, probably bedrock
export AILLY_ENGINE=bedrock

# Define languages and create directory structures
languages=("$1")
for lang in "${languages[@]}"; do
  mkdir -p "$lang"
  cp SPECIFICATION.md $lang/02_SPECIFICATION.md
  pushd "$lang"

  # Add .aillyrc file
  cat << EOF > .aillyrc
---
parent: always
combined: true
---
EOF

  # Add 03_README.md file
  cat << EOF > 03_README.md
---
parent: always
prompt: |
  Write me a README.md (in GitHub markdown) for a new Workflow Example.

  This document will be informed by the Workflow Details and this template:

  # Title and Overview:
  Start with a concise title that captures the essence of the use case.
  Provide an overview section that briefly describes the use case, its relevance, and how it integrates with AWS services. Mention any specific AWS limitations or challenges this example addresses.

  # Supporting Infrastructure & Data:
  Detail the AWS infrastructure components involved in the example (e.g., specific AWS services and resources).
  Include a link to a AWS CloudFormation template in the `workflows/kinesis-firehose/resources` directory for setting up necessary resources, and mention its location.
  Provide step-by-step commands for deploying the infrastructure using the CloudFormation.
  Additionally, mention the need for users to create a sample_records.json file using the mock_data.py script located in the `workflows/kinesis-firehose/resources` directory.

  # Deployment Instructions:
  Provide step-by-step commands for running the script outlined in the SPECIFICATION.md file.

  # Resource Generation and Cleanup:
  Include instructions for cleaning up or deleting the resources created during the example to avoid unnecessary charges. This will be a simple CFN command.

  # Example Implementation:
  Link to implementation code samples in one or more programming languages relevant to the use case.
  Provide brief descriptions of what each implementation does and how it contributes to solving the use case.

  # Additional Resources and Reading:
  Recommend further reading or documentation that can help users understand the concepts or AWS services used in the example.

  # Copyright and Licensing:
  Include a copyright notice and licensing information, specifying how users are permitted to use, modify, or distribute the example.'
---
EOF

  # Create 03_FILES.md
  cat << EOF > 03_FILES.md
---
prompt: |
    Persona: You are a guru-level solutions engineer with expertise in $lang and AWS architecture.
    Task: Sketch out a workflow using the AWS SDK for $lang to interface with the AWS services.
    Requirements:
      Summary of Application Needs:
         - Key components for a basic terminal-based application in $lang.
         - Requirements for the interpreter or compiler.
         - External dependencies and operating environment specifics.
         - Configuration and error handling protocols.
         - Development Environment:
              - Ensure compatibility with MacOS; include Windows-specific instructions as necessary.
      Code and Configuration Files:
         - List all necessary files, including source code in $lang. Do not provide infra setup files, such as CDK or CFN.
         - Also include test files in $lang
      Best Practices:
         - Ensure all $lang code adheres to best practices.
    Output:
       - Provide a detailed list of files required to build, test, and run a fully functional example on a user’s laptop.
       - Be as descriptive as possible regarding what these files should look like, without giving me the source code.
       - For example, inputs, outputs, approach. Another LLM should be able to easily take the descriptions in your response and use it to create the actual files
---
EOF

# Create 03_FILES.md
  cat << EOF > 04_PROCESS.md
---
prompt: |
    Persona: You are a guru-level solutions engineer with expertise in $lang and AWS.
    Task: Write code for a workflow using the AWS SDK for $lang to interface with the AWS services.
    Output:
       - Give me a perfectly-functional file based on the list of files you created in the previous step (03_FILES.md)
       - Each file should be wrapped in <file></file> parent tags that will allow an automated process to interpret them
       - Additionally, within these <file> tags, include a <name> tag identifying the file name and a <contents> tag with the raw file contents.
       - Do not include ANY formatting. For example: ticks or any other markdown language formatting.
       - Do not explain anything. Just provide the file contents in the format requested.
---
EOF

  cat << EOF > generate_files.py
import os
import re

def create_files_from_md(file_path, target_dir):
    # Create the target directory if it doesn't exist
    os.makedirs(target_dir, exist_ok=True)

    # Read the content of the markdown file
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            content = file.read()
    except FileNotFoundError:
        print(f"File {file_path} not found.")
        return

    # Regex to find <file>...</file> blocks
    file_blocks = re.findall(r'<file>(.*?)</file>', content, re.DOTALL)

    for block in file_blocks:
        # Extract the name and contents of the file
        name = re.search(r'<name>(.*?)</name>', block, re.DOTALL)
        contents = re.search(r'<contents>(.*?)</contents>', block, re.DOTALL)

        if name and contents:
            name = name.group(1).strip()
            contents = contents.group(1).strip()
            file_path = os.path.join(target_dir, name)

            # Write the filtered contents to the respective file
            try:
                with open(file_path, 'w', encoding='utf-8') as new_file:
                    new_file.write(contents)
                    print(f"File created: {file_path}")
            except IOError as e:
                print(f"Failed to create file {name}. Error: {e}")
        else:
            print("Name or contents missing in one of the <file> tags.")

if __name__ == '__main__':
    create_files_from_md('04_PROCESS.md', 'app')

EOF

  cat << EOF > run.sh
#!/bin/bash
ailly 03_README.md
ailly 03_FILES.md
ailly 04_PROCESS.md
python3 generate_files.py
EOF
  chmod +x run.sh
  popd
done

# Add .aillyrc file
cat << EOF > .aillyrc
---
isolated: false
parent: always
---

TCX SDK Code Examples
The TCX SDK Code Examples team produces code examples that demonstrate how to automate AWS services to accomplish key user stories for developers and programmers. These code examples are quick and easy to find and use, are continually tested, and demonstrate AWS and community coding best practices.

Mission

We provide code examples for builders integrating AWS services into their applications and business workflows using the AWS Software Development Kits (SDKs). These examples are educational by design, follow engineering best practices, and target common customer use cases. Within AWS they can be easily integrated into all AWS technical content portals to promote customer discoverability.

Vision

We envision a best-in-class library of code examples for every AWS service and in every actively maintained SDK language. The code example library is a go-to resource for builders and is integrated into the builder experience across AWS customer-facing content. Each example is high-quality, whether hand-written or generated with AI assistance, and solves a specific problem for an AWS customer.

Tenets

These are our tenets, unless you know better ones:

We are educators. Comprehension and learnability always take precedence.
We are engineers. Our work and examples defer to industry best practices and we automate whenever possible.
Our examples address common user challenges. They do not deliberately mirror AWS service silos.
Our examples are discoverable. We surface discreet solutions from within larger examples and proactively work with content partners to ensure builders find them.
We are subject matter experts. We are the primary reference for code example standards in TCX.

A Workflow Example, as defined by the TCX Code Examples team, is an example scenario that is targeted to a particular real-world user story, use case, problem, or other common service integration. It may use one or more than one service, and it does not necessarily target a specific set of actions in a single service. Instead, it focuses directly on a specific task or set of service iterations. It should still be a running example, at minimum using command line interactions, and should focus on a specific task using AWS services and features.
EOF

# Add .aillyrc file
cat << EOF > .aillyrc
---
parent: always
combined: true
---
EOF

root=$PWD

for lang in "${languages[@]}"; do
  cd "$root"/"$lang"
  ./run.sh
done