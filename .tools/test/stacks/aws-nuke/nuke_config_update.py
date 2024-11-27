"""
    Python class responsible for updating the nuke generic config , based on exceptions to be filtered
    and also updates dynamically the region attribute passed in from the StepFunctions invocation. This should be modified to suit your needs.
"""
import boto3
import yaml
import argparse
import copy

GLOBAL_RESOURCE_EXCEPTIONS = [
    {"property": "tag:DoNotNuke", "value": "True"},
    {"property": "tag:Permanent", "value": "True"},
    {"type": "regex", "value": ".*auto-account-cleanser*.*"},
    {"type": "regex", "value": ".*nuke-account-cleanser*.*"},
    {"type": "regex", "value": ".*securityhub*.*"},
    {"type": "regex", "value": ".*aws-prod*.*"},
]


class StackInfo:

    def __init__(self, account, target_regions):
        self.session = boto3.Session(profile_name="nuke")
        # Regions to be targeted set from the Stepfunctions/CodeBuild workflow
        self.regions = target_regions
        self.resources = {}
        self.config = {}
        self.account = account

    def Populate(self):
        self.UpdateCFNStackList()
        self.OverrideDefaultConfig()

    def UpdateCFNStackList(self):
        try:
            for region in self.regions:
                cfn_client = self.session.client("cloudformation", region_name=region)
                stack_paginator = cfn_client.get_paginator("list_stacks")
                responses = stack_paginator.paginate(
                    StackStatusFilter=[
                        "CREATE_COMPLETE",
                        "UPDATE_COMPLETE",
                        "UPDATE_ROLLBACK_COMPLETE",
                        "IMPORT_COMPLETE",
                        "IMPORT_ROLLBACK_COMPLETE",
                    ]
                )
                for page in responses:
                    for stack in page.get("StackSummaries"):
                        self.GetCFNResources(stack, cfn_client)
                self.BuildIamExclusionList(region)
        except Exception as e:
            print("Error in calling UpdateCFNStackList:\n {}".format(e))

    def GetCFNResources(self, stack, cfn_client):
        try:
            stack_name = stack.get("StackName")

            if stack_name is None:
                stack_name = stack.get("PhysicalResourceId")

            stack_description = cfn_client.describe_stacks(StackName=stack_name)
            print("Stack Description: ", stack_description)
            tags = stack_description.get("Stacks")[0].get("Tags")
            for tag in tags:
                key = tag.get("Key")
                value = tag.get("Value")
                if key == "AWS_Solutions" and value == "LandingZoneStackSet":
                    if "CloudFormationStack" in self.resources:
                        self.resources["CloudFormationStack"].append(
                            stack.get("StackName")
                        )
                    else:
                        self.resources["CloudFormationStack"] = [stack.get("StackName")]
                    stack_resources = cfn_client.list_stack_resources(
                        StackName=stack_name
                    )
                    for resource in stack_resources.get("StackResourceSummaries"):
                        if resource.get("ResourceType") == "AWS::CloudFormation::Stack":
                            self.GetCFNResources(resource, cfn_client)
                        else:
                            nuke_type = self.UpdateResourceName(resource["ResourceType"])
                            if nuke_type in self.resources:
                                self.resources[nuke_type].append(
                                    {
                                        "type": "regex",
                                        "value": resource["PhysicalResourceId"],
                                    }
                                )
                            else:
                                self.resources[nuke_type] = [
                                    {
                                        "type": "regex",
                                        "value": resource["PhysicalResourceId"],
                                    }
                                ]
        except Exception as e:
            print("Error calling GetCFNResources:\n {}".format(e))

    def UpdateResourceName(self, resource):
        nuke_type = str.replace(resource, "AWS::", "")
        nuke_type = str.replace(nuke_type, "::", "")
        nuke_type = str.replace(nuke_type, "Config", "ConfigService", 1)
        return nuke_type

    def BuildIamExclusionList(self, region):
        # This excludes and appends to the config IAMRole resources , the roles that are federated principals
        # You can add any other custom filterting logic based on regions for IAM/Global roles that should be excluded
        iam_client = self.session.client("iam", region_name=region)
        iam_paginator = iam_client.get_paginator("list_roles")
        responses = iam_paginator.paginate()
        for page in responses:
            for role in page["Roles"]:
                apd = role.get("AssumeRolePolicyDocument")
                if apd is not None:
                    for item in apd.get("Statement"):
                        if item is not None:
                            for principal in item.get("Principal"):
                                if principal == "Federated":
                                    if "IAMRole" in self.resources:
                                        self.resources["IAMRole"].append(
                                            role.get("RoleName")
                                        )
                                    else:
                                        self.resources["IAMRole"] = [
                                            role.get("RoleName")
                                        ]

    def OverrideDefaultConfig(self):
        # Open the nuke_generic_config.yaml and merge the captured resources/exclusions with it
        try:
            with open(r"nuke_generic_config.yaml") as config_file:
                self.config = yaml.load(config_file)
                # Not all resources handled by the tool, but we will add them to the exclusion anyhow.
                for resource in self.resources:
                    if resource in self.config["accounts"]["ACCOUNT"]["filters"]:
                        self.config["accounts"]["ACCOUNT"]["filters"][resource].extend(
                            self.resources[resource]
                        )
                    else:
                        self.config["accounts"]["ACCOUNT"]["filters"][
                            resource
                        ] = self.resources[resource]
                self.config["accounts"][self.account] = copy.deepcopy(
                    self.config["accounts"]["ACCOUNT"]
                )
                if "ACCOUNT" in self.config["accounts"]:
                    self.config["accounts"].pop("ACCOUNT", None)
            # Global exclusions apply to every type of resource
            for resource in self.config["accounts"][self.account]["filters"]:
                for exception in GLOBAL_RESOURCE_EXCEPTIONS:
                    self.config["accounts"][self.account]["filters"][resource].append(
                        exception.copy()
                    )
            config_file.close()
        except Exception as e:
            print("Failed merging nuke-config-test.yaml with error {}".format(e))
            exit(1)

    def WriteConfig(self):
        # CodeBuild script updates the target region in the generic config and is validated here.
        try:
            for region in self.config["regions"]:
                local_config = stackInfo.config.copy()
                local_config["regions"] = [region]
                filename = "nuke_config_{}.yaml".format(region)
                with open(filename, "w") as output_file:
                    output = yaml.dump(local_config, output_file)
                output_file.close()
        except Exception as e:
            print("Failed opening nuke_config.yaml for writing with error {}".format(e))


try:
    parser = argparse.ArgumentParser()
    parser.add_argument("--account", dest="account", help="Account to nuke") # Account and Region from StepFunctions - CodeBuild overridden params
    parser.add_argument("--region", dest="region", help="Region to target for nuke")
    args = parser.parse_args()
    if not args.account or not args.region:
        parser.print_help()
        exit(1)
except Exception as e:
    print(e)
    exit(1)

if __name__ == "__main__":
    print("Incoming Args: ", args)
    stackInfo = StackInfo(args.account, [args.region])
    stackInfo.Populate()
    stackInfo.WriteConfig()