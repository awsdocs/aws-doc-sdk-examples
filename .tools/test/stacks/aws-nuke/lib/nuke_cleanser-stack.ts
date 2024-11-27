import * as cdk from 'aws-cdk-lib';
import * as codebuild from 'aws-cdk-lib/aws-codebuild';
import * as events from 'aws-cdk-lib/aws-events';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as sns from 'aws-cdk-lib/aws-sns';
import * as stepfunctions from 'aws-cdk-lib/aws-stepfunctions';

export interface NukeCleanserStackProps extends cdk.StackProps {
  /**
   * The name of the bucket where the nuke binary and config files are stored
   * @default 'nuke-account-cleanser-config'
   */
  readonly bucketName?: string;
  /**
   * The name of the Nuke Role to be assumed within each account, providing permissions to cleanse the account(s)
   * @default 'nuke-auto-account-cleanser'
   */
  readonly nukeCleanserRoleName?: string;
  /**
   * IAM Path
   * @default '/'
   */
  readonly iamPath?: string;
  /**
   * The dry run flag to run for the aws-nuke. By default it is set to True which will not delete any resources.
   * @default 'true'
   */
  readonly awsNukeDryRunFlag?: string;
  /**
   * The aws-nuke latest version to be used from internal artifactory/S3. Make sure to check the latest releases and any resource additions added. As you update the version, you will have to handle filtering any new resources that gets updated with that new version.
   * @default '2.21.2'
   */
  readonly awsNukeVersion?: string;
  /**
   * The SNS Topic name to publish the nuke output logs email
   * @default 'nuke-cleanser-notify-topic'
   */
  readonly nukeTopicName?: string;
  /**
   * The Owner of the account to be used for tagging purpose
   * @default 'OpsAdmin'
   */
  readonly owner?: string;
}

/**
 * This CFN Template creates a StepFunction state machine definition , to invoke a CodeBuild Project and  associated resources for setting up a single-account script that cleanses the resources across supplied regions via AWS-Nuke. It also creates the role for the nuke cleanser which is used for configuring the credentials for aws-nuke binary to cleanse resources within that account/region.

 */
export class NukeCleanserStack extends cdk.Stack {
  /**
   * Arn of SNS Topic used for notifying nuke results in email
   */
  public readonly nukeTopicArn;
  /**
   * S3 bucket created with the random generated name
   */
  public readonly nukeS3BucketValue;

  public constructor(scope: cdk.App, id: string, props: NukeCleanserStackProps = {}) {
    super(scope, id, props);

    // Applying default props
    props = {
      ...props,
      bucketName: props.bucketName ?? 'nuke-account-cleanser-config',
      nukeCleanserRoleName: props.nukeCleanserRoleName ?? 'nuke-auto-account-cleanser',
      iamPath: props.iamPath ?? '/',
      awsNukeDryRunFlag: props.awsNukeDryRunFlag ?? 'true',
      awsNukeVersion: props.awsNukeVersion ?? '2.21.2',
      nukeTopicName: props.nukeTopicName ?? 'nuke-cleanser-notify-topic',
      owner: props.owner ?? 'OpsAdmin',
    };

    // Resources
    const nukeAccountCleanserPolicy = new iam.CfnManagedPolicy(this, 'NukeAccountCleanserPolicy', {
      managedPolicyName: 'NukeAccountCleanser',
      policyDocument: {
        Statement: [
          {
            Action: [
              'access-analyzer:*',
              'autoscaling:*',
              'aws-portal:*',
              'budgets:*',
              'cloudtrail:*',
              'cloudwatch:*',
              'config:*',
              'ec2:*',
              'ec2messages:*',
              'elasticloadbalancing:*',
              'eks:*',
              'elasticache:*',
              'events:*',
              'firehose:*',
              'guardduty:*',
              'iam:*',
              'inspector:*',
              'kinesis:*',
              'kms:*',
              'lambda:*',
              'logs:*',
              'organizations:*',
              'pricing:*',
              's3:*',
              'secretsmanager:*',
              'securityhub:*',
              'sns:*',
              'sqs:*',
              'ssm:*',
              'ssmmessages:*',
              'sts:*',
              'support:*',
              'tag:*',
              'trustedadvisor:*',
              'waf-regional:*',
              'wafv2:*',
              'cloudformation:*',
            ],
            Effect: 'Allow',
            Resource: '*',
            Sid: 'WhitelistedServices',
          },
        ],
        Version: '2012-10-17',
      },
      description: 'Managed policy for nuke account cleansing',
      path: props.iamPath!,
    });

    const nukeEmailTopic = new sns.CfnTopic(this, 'NukeEmailTopic', {
      displayName: 'NukeTopic',
      fifoTopic: false,
      kmsMasterKeyId: 'alias/aws/sns',
      subscription: [
        {
          endpoint: 'test@test.com',
          protocol: 'email',
        },
      ],
      topicName: props.nukeTopicName!,
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
    });

    const nukeS3Bucket = new s3.CfnBucket(this, 'NukeS3Bucket', {
      bucketName: [
        props.bucketName!,
        this.account,
        this.region,
        cdk.Fn.select(0, cdk.Fn.split('-', cdk.Fn.select(2, cdk.Fn.split('/', this.stackId)))),
      ].join('-'),
      publicAccessBlockConfiguration: {
        blockPublicAcls: true,
        ignorePublicAcls: true,
        blockPublicPolicy: true,
        restrictPublicBuckets: true,
      },
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
    });
    nukeS3Bucket.cfnOptions.deletionPolicy = cdk.CfnDeletionPolicy.RETAIN;

    const nukeCodeBuildProjectRole = new iam.CfnRole(this, 'NukeCodeBuildProjectRole', {
      roleName: `NukeCodeBuildProject-${this.stackName}`,
      assumeRolePolicyDocument: {
        Version: '2012-10-17',
        Statement: [
          {
            Effect: 'Allow',
            Principal: {
              Service: 'codebuild.amazonaws.com',
            },
            Action: 'sts:AssumeRole',
          },
        ],
      },
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
      policies: [
        {
          policyName: 'NukeCodeBuildLogsPolicy',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: [
                  'logs:CreateLogGroup',
                  'logs:CreateLogStream',
                  'logs:PutLogEvents',
                  'logs:DescribeLogStreams',
                  'logs:FilterLogEvents',
                ],
                Resource: [
                  `arn:aws:logs:${this.region}:${this.account}:log-group:AccountNuker-${this.stackName}`,
                  `arn:aws:logs:${this.region}:${this.account}:log-group:AccountNuker-${this.stackName}:*`,
                ],
              },
            ],
          },
        },
        {
          policyName: 'AssumeNukePolicy',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: 'sts:AssumeRole',
                Resource: `arn:aws:iam::*:role/${props.nukeCleanserRoleName!}`,
              },
            ],
          },
        },
        {
          policyName: 'NukeListOUAccounts',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: 'organizations:ListAccountsForParent',
                Resource: '*',
              },
            ],
          },
        },
        {
          policyName: 'S3BucketReadOnly',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: [
                  's3:Get*',
                  's3:List*',
                ],
                Resource: [
                  `arn:aws:s3:::${nukeS3Bucket.ref}`,
                  `arn:aws:s3:::${nukeS3Bucket.ref}/*`,
                ],
              },
            ],
          },
        },
        {
          policyName: 'SNSPublishPolicy',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: [
                  'sns:ListTagsForResource',
                  'sns:ListSubscriptionsByTopic',
                  'sns:GetTopicAttributes',
                  'sns:Publish',
                ],
                Resource: [
                  nukeEmailTopic.ref,
                ],
              },
            ],
          },
        },
      ],
    });

    const nukeS3BucketPolicy = new s3.CfnBucketPolicy(this, 'NukeS3BucketPolicy', {
      bucket: nukeS3Bucket.ref,
      policyDocument: {
        Version: '2012-10-17',
        Statement: [
          {
            Sid: 'ForceSSLOnlyAccess',
            Effect: 'Deny',
            Principal: '*',
            Action: 's3:*',
            Resource: [
              `arn:aws:s3:::${nukeS3Bucket.ref}`,
              `arn:aws:s3:::${nukeS3Bucket.ref}/*`,
            ],
            Condition: {
              Bool: {
                'aws:SecureTransport': 'false',
              },
            },
          },
        ],
      },
    });

    const nukeAccountCleanserRole = new iam.CfnRole(this, 'NukeAccountCleanserRole', {
      roleName: props.nukeCleanserRoleName!,
      description: 'Nuke Auto account cleanser role for Dev/Sandbox accounts',
      maxSessionDuration: 7200,
      tags: [
        {
          key: 'privileged',
          value: 'true',
        },
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'description',
          value: 'PrivilegedReadWrite:auto-account-cleanser-role',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
      assumeRolePolicyDocument: {
        Statement: [
          {
            Action: 'sts:AssumeRole',
            Effect: 'Allow',
            Principal: {
              AWS: [
                nukeCodeBuildProjectRole.attrArn,
              ],
            },
          },
        ],
        Version: '2012-10-17',
      },
      managedPolicyArns: [
        nukeAccountCleanserPolicy.ref,
      ],
      path: props.iamPath!,
    });

    const nukeCodeBuildProject = new codebuild.CfnProject(this, 'NukeCodeBuildProject', {
      artifacts: {
        type: 'NO_ARTIFACTS',
      },
      badgeEnabled: false,
      description: 'Builds a container to run AWS-Nuke for all accounts within the specified account/regions',
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
      environment: {
        computeType: 'BUILD_GENERAL1_SMALL',
        image: 'aws/codebuild/docker:18.09.0',
        imagePullCredentialsType: 'CODEBUILD',
        privilegedMode: true,
        type: 'LINUX_CONTAINER',
        environmentVariables: [
          {
            name: 'AWS_NukeDryRun',
            type: 'PLAINTEXT',
            value: props.awsNukeDryRunFlag!,
          },
          {
            name: 'AWS_NukeVersion',
            type: 'PLAINTEXT',
            value: props.awsNukeVersion!,
          },
          {
            name: 'Publish_TopicArn',
            type: 'PLAINTEXT',
            value: nukeEmailTopic.ref,
          },
          {
            name: 'NukeS3Bucket',
            type: 'PLAINTEXT',
            value: nukeS3Bucket.ref,
          },
          {
            name: 'NukeAssumeRoleArn',
            type: 'PLAINTEXT',
            value: nukeAccountCleanserRole.attrArn,
          },
          {
            name: 'NukeCodeBuildProjectName',
            type: 'PLAINTEXT',
            value: `AccountNuker-${this.stackName}`,
          },
        ],
      },
      logsConfig: {
        cloudWatchLogs: {
          groupName: `AccountNuker-${this.stackName}`,
          status: 'ENABLED',
        },
      },
      name: `AccountNuker-${this.stackName}`,
      serviceRole: nukeCodeBuildProjectRole.attrArn,
      timeoutInMinutes: 120,
      source: {
        buildSpec: 'version: 0.2\nphases:\n  install:\n    on-failure: ABORT\n    commands:\n      - export AWS_NUKE_VERSION=$AWS_NukeVersion\n      - apt-get install -y wget\n      - apt-get install jq\n      - wget https://github.com/rebuy-de/aws-nuke/releases/download/v$AWS_NUKE_VERSION/aws-nuke-v$AWS_NUKE_VERSION-linux-amd64.tar.gz --no-check-certificate\n      - tar xvf aws-nuke-v$AWS_NUKE_VERSION-linux-amd64.tar.gz\n      - chmod +x aws-nuke-v$AWS_NUKE_VERSION-linux-amd64\n      - mv aws-nuke-v$AWS_NUKE_VERSION-linux-amd64 /usr/local/bin/aws-nuke\n      - aws-nuke version\n      - echo \"Setting aws cli profile with config file for role assumption using metadata\"\n      - aws configure set profile.nuke.role_arn ${NukeAssumeRoleArn}\n      - aws configure set profile.nuke.credential_source \"EcsContainer\"\n      - export AWS_PROFILE=nuke\n      - export AWS_DEFAULT_PROFILE=nuke\n      - export AWS_SDK_LOAD_CONFIG=1\n      - echo \"Getting 12-digit ID of this account\"\n      - account_id=$(aws sts get-caller-identity |jq -r \".Account\");\n  build:\n    on-failure: CONTINUE\n    commands:\n      - echo \" ------------------------------------------------ \" >> error_log.txt\n      - echo \"Getting nuke generic config file from S3\";\n      - aws s3 cp s3://$NukeS3Bucket/nuke_generic_config.yaml .\n      - echo \"Updating the TARGET_REGION in the generic config from the parameter\"\n      - sed -i \"s/TARGET_REGION/$NukeTargetRegion/g\" nuke_generic_config.yaml\n      - echo \"Getting filter/exclusion python script from S3\";\n      - aws s3 cp s3://$NukeS3Bucket/nuke_config_update.py .\n      - echo \"Getting 12-digit ID of this account\"\n      - account_id=$(aws sts get-caller-identity |jq -r \".Account\");\n      - echo \"Running Config filter/update script\";\n      - python3 nuke_config_update.py --account $account_id --region \"$NukeTargetRegion\";\n      - echo \"Configured nuke_config.yaml\";\n      - echo \"Running Nuke on Account\";\n      - |\n        if [ \"$AWS_NukeDryRun\" = \"true\" ]; then\n          for file in $(ls nuke_config_$NukeTargetRegion*) ; do aws-nuke -c $file --force --max-wait-retries 10 --profile nuke 2>&1 |tee -a aws-nuke.log; done\n        elif [ \"$AWS_NukeDryRun\" = \"false\" ]; then\n          for file in $(ls nuke_config_$NukeTargetRegion*) ; do aws-nuke -c $file --force --max-wait-retries 10 --no-dry-run --profile nuke 2>&1 |tee -a aws-nuke.log; done\n        else\n          echo \"Couldn\'t determine Dryrun flag...exiting\"\n          exit 1\n        fi\n      - nuke_pid=$!;\n      - wait $nuke_pid;\n      - echo \"Checking if Nuke Process completed for account\"\n      - |\n        if cat aws-nuke.log | grep -F \"Error: The specified account doesn\"; then\n          echo \"Nuke errored due to no AWS account alias set up - exiting\"\n          cat aws-nuke.log >> error_log.txt\n          exit 1\n        else\n          echo \"Nuke completed Successfully - Continuing\"\n        fi\n\n  post_build:\n    commands:\n      - echo $CODEBUILD_BUILD_SUCCEEDING\n      - echo \"Get current timestamp for naming reports\"\n      - BLD_START_TIME=$(date -d @$(($CODEBUILD_START_TIME/1000)))\n      - CURR_TIME_UTC=$(date -u)\n      - |\n        {\n                echo \"  Account Cleansing Process Failed;\"\n                echo    \"\"\n                \n                echo \"  ----------------------------------------------------------------\"\n                echo \"  Summary of the process:\"\n                echo \"  ----------------------------------------------------------------\"\n                echo \"  DryRunMode                   : $AWS_NukeDryRun\"\n                echo \"  Account ID                   : $account_id\"\n                echo \"  Target Region                : $NukeTargetRegion\"\n                echo \"  Build State                  : $([ \"${CODEBUILD_BUILD_SUCCEEDING}\" = \"1\" ] && echo \"JOB SUCCEEDED\" || echo \"JOB FAILED\")\"\n                echo \"  Build ID                     : ${CODEBUILD_BUILD_ID}\"\n                echo \"  CodeBuild Project Name       : $NukeCodeBuildProjectName\"\n                echo \"  Process Start Time           : ${BLD_START_TIME}\"\n                echo \"  Process End Time             : ${CURR_TIME_UTC}\"\n                echo \"  Log Stream Path              : $NukeCodeBuildProjectName/${CODEBUILD_LOG_PATH}\"\n                echo \"  ----------------------------------------------------------------\"\n                echo \"  ################# Failed Nuke Process - Exiting ###################\"\n                echo    \"\"\n        } >> fail_email_template.txt\n      - | \n        if [ \"$CODEBUILD_BUILD_SUCCEEDING\" = \"0\" ]; then \n          echo \" Couldn\'t process Nuke Cleanser - Exiting \" >> fail_email_template.txt\n          cat error_log.txt >> fail_email_template.txt\n          aws sns publish --topic-arn $Publish_TopicArn --message file://fail_email_template.txt --subject \"Nuke Account Cleanser Failed in account $account_id and region $NukeTargetRegion\"\n          exit 1;\n        fi\n      - sleep 120\n      - LOG_STREAM_NAME=$CODEBUILD_LOG_PATH;\n      - CURR_TIME_UTC=$(date -u)\n      - | \n        if [ -z \"${LOG_STREAM_NAME}\" ]; then\n          echo \"Couldn\'t find the log stream for log events\";\n          exit 0;\n        else\n          aws logs filter-log-events --log-group-name $NukeCodeBuildProjectName --log-stream-names $LOG_STREAM_NAME --filter-pattern \"removed\" --no-interleaved | jq -r .events[].message > log_output.txt;\n          awk \'/There are resources in failed state/,/Error: failed/\' aws-nuke.log > failure_email_output.txt\n          awk \'/Error: failed/,/\\n/\' failure_email_output.txt > failed_log_output.txt\n        fi\n      - |\n        if [ -r log_output.txt ]; then\n          content=$(cat log_output.txt)\n          echo $content\n        elif [ -f \"log_output.txt\" ]; then\n          echo \"The file log_output.txt exists but is not readable to the script.\"\n        else\n          echo \"The file log_output.txt does not exist.\"\n        fi\n      - echo \"Publishing Log Ouput to SNS:\"\n      - sub=\"Nuke Account Cleanser Succeeded in account \"$account_id\" and region \"$NukeTargetRegion\"\"\n      - |\n        {\n                echo \"  Account Cleansing Process Completed;\"\n                echo    \"\"\n                \n                echo \"  ------------------------------------------------------------------\"\n                echo \"  Summary of the process:\"\n                echo \"  ------------------------------------------------------------------\"\n                echo \"  DryRunMode                   : $AWS_NukeDryRun\"\n                echo \"  Account ID                   : $account_id\"\n                echo \"  Target Region                : $NukeTargetRegion\"\n                echo \"  Build State                  : $([ \"${CODEBUILD_BUILD_SUCCEEDING}\" = \"1\" ] && echo \"JOB SUCCEEDED\" || echo \"JOB FAILED\")\"\n                echo \"  Build ID                     : ${CODEBUILD_BUILD_ID}\"\n                echo \"  CodeBuild Project Name       : $NukeCodeBuildProjectName\"\n                echo \"  Process Start Time           : ${BLD_START_TIME}\"\n                echo \"  Process End Time             : ${CURR_TIME_UTC}\"\n                echo \"  Log Stream Path              : $NukeCodeBuildProjectName/${CODEBUILD_LOG_PATH}\"\n                echo \"  ------------------------------------------------------------------\"\n                echo \"  ################### Nuke Cleanser Logs ####################\"\n                echo    \"\"\n        } >> email_template.txt\n\n      - cat aws-nuke.log | grep -F \"Scan complete:\" || echo \"No Resources scanned and nukeable yet\"\n      - echo \"Number of Resources that is filtered by config:\" >> email_template.txt\n      - cat aws-nuke.log | grep -c \" - filtered by config\" || echo 0 >> email_template.txt\n      - echo \" ------------------------------------------ \" >> email_template.txt\n      - |\n        if [ \"$AWS_NukeDryRun\" = \"true\" ]; then\n          echo \"RESOURCES THAT WOULD BE REMOVED:\" >> email_template.txt\n          echo \" ----------------------------------------- \" >> email_template.txt\n          cat aws-nuke.log | grep -c \" - would remove\" || echo 0 >> email_template.txt\n          cat aws-nuke.log | grep -F \" - would remove\" >> email_template.txt || echo \"No resources to be removed\" >> email_template.txt\n        else\n          echo \" FAILED RESOURCES \" >> email_template.txt\n          echo \" ------------------------------- \" >> email_template.txt\n          cat failed_log_output.txt >> email_template.txt\n          echo \" SUCCESSFULLY NUKED RESOURCES \" >> email_template.txt\n          echo \" ------------------------------- \" >> email_template.txt\n          cat log_output.txt >> email_template.txt\n        fi\n      - aws sns publish --topic-arn $Publish_TopicArn --message file://email_template.txt --subject \"$sub\"\n      - echo \"Resources Nukeable:\"\n      - cat aws-nuke.log | grep -F \"Scan complete:\" || echo \"Nothing Nukeable yet\"\n      - echo \"Total number of Resources that would be removed:\"\n      - cat aws-nuke.log | grep -c \" - would remove\" || echo \"Nothing would be removed yet\"\n      - echo \"Total number of Resources Deleted:\"\n      - cat aws-nuke.log | grep -c \" - removed\" || echo \"Nothing deleted yet\"\n      - echo \"List of Resources Deleted today:\"\n      - cat aws-nuke.log | grep -F \" - removed\" || echo \"Nothing deleted yet\"\n',
        type: 'NO_SOURCE',
      },
    });

    const nukeStepFunctionRole = new iam.CfnRole(this, 'NukeStepFunctionRole', {
      roleName: 'nuke-account-cleanser-codebuild-state-machine-role',
      assumeRolePolicyDocument: {
        Version: '2012-10-17',
        Statement: [
          {
            Effect: 'Allow',
            Principal: {
              Service: [
                `states.${this.region}.amazonaws.com`,
              ],
            },
            Action: [
              'sts:AssumeRole',
            ],
          },
        ],
      },
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
      path: '/',
      policies: [
        {
          policyName: 'nuke-account-cleanser-codebuild-state-machine-policy',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: [
                  'codebuild:StartBuild',
                  'codebuild:StartBuild',
                  'codebuild:StopBuild',
                  'codebuild:StartBuildBatch',
                  'codebuild:StopBuildBatch',
                  'codebuild:RetryBuild',
                  'codebuild:RetryBuildBatch',
                  'codebuild:BatchGet*',
                  'codebuild:GetResourcePolicy',
                  'codebuild:DescribeTestCases',
                  'codebuild:DescribeCodeCoverages',
                  'codebuild:List*',
                ],
                Resource: [
                  nukeCodeBuildProject.attrArn,
                ],
              },
              {
                Effect: 'Allow',
                Action: [
                  'events:PutTargets',
                  'events:PutRule',
                  'events:DescribeRule',
                ],
                Resource: `arn:aws:events:${this.region}:${this.account}:rule/StepFunctionsGetEventForCodeBuildStartBuildRule`,
              },
              {
                Effect: 'Allow',
                Action: [
                  'sns:Publish',
                ],
                Resource: [
                  nukeEmailTopic.ref,
                ],
              },
              {
                Effect: 'Allow',
                Action: [
                  'states:DescribeStateMachine',
                  'states:ListExecutions',
                  'states:StartExecution',
                  'states:StopExecution',
                  'states:DescribeExecution',
                ],
                Resource: [
                  `arn:aws:states:${this.region}:${this.account}:stateMachine:nuke-account-cleanser-codebuild-state-machine`,
                ],
              },
            ],
          },
        },
      ],
    });

    const nukeStepFunction = new stepfunctions.CfnStateMachine(this, 'NukeStepFunction', {
      stateMachineName: 'nuke-account-cleanser-codebuild-state-machine',
      roleArn: nukeStepFunctionRole.attrArn,
      definitionString: `{
        "Comment": "AWS Nuke Account Cleanser for multi-region single account clean up using SFN Map state parallel invocation of CodeBuild project.",
        "StartAt": "StartNukeCodeBuildForEachRegion",
        "States": {
          "StartNukeCodeBuildForEachRegion": {
            "Type": "Map",
            "ItemsPath": "$.InputPayLoad.region_list",
            "Parameters": {
              "region_id.$": "$$.Map.Item.Value",
              "nuke_dry_run.$": "$.InputPayLoad.nuke_dry_run",
              "nuke_version.$": "$.InputPayLoad.nuke_version"
            },
            "Next": "Clean Output and Notify",
            "MaxConcurrency": 0,
            "Iterator": {
              "StartAt": "Trigger Nuke CodeBuild Job",
              "States": {
                "Trigger Nuke CodeBuild Job": {
                  "Type": "Task",
                  "Resource": "arn:aws:states:::codebuild:startBuild.sync",
                  "Parameters": {
                    "ProjectName": "${nukeCodeBuildProject.attrArn}",
                    "EnvironmentVariablesOverride": [
                      {
                        "Name": "NukeTargetRegion",
                        "Type": "PLAINTEXT",
                        "Value.$": "$.region_id"
                      },
                      {
                        "Name": "AWS_NukeDryRun",
                        "Type": "PLAINTEXT",
                        "Value.$": "$.nuke_dry_run"
                      },
                      {
                        "Name": "AWS_NukeVersion",
                        "Type": "PLAINTEXT",
                        "Value.$": "$.nuke_version"
                      }
                    ]
                  },
                  "Next": "Check Nuke CodeBuild Job Status",
                  "ResultSelector": {
                    "NukeBuildOutput.$": "$.Build"
                  },
                  "ResultPath": "$.AccountCleanserRegionOutput",
                  "Retry": [
                    {
                      "ErrorEquals": [
                        "States.TaskFailed"
                      ],
                      "BackoffRate": 1,
                      "IntervalSeconds": 1,
                      "MaxAttempts": 1
                    }
                  ],
                  "Catch": [
                    {
                      "ErrorEquals": [
                        "States.ALL"
                      ],
                      "Next": "Nuke Failed",
                      "ResultPath": "$.AccountCleanserRegionOutput"
                    }
                  ]
                },
                "Check Nuke CodeBuild Job Status": {
                  "Type": "Choice",
                  "Choices": [
                    {
                      "Variable": "$.AccountCleanserRegionOutput.NukeBuildOutput.BuildStatus",
                      "StringEquals": "SUCCEEDED",
                      "Next": "Nuke Success"
                    },
                    {
                      "Variable": "$.AccountCleanserRegionOutput.NukeBuildOutput.BuildStatus",
                      "StringEquals": "FAILED",
                      "Next": "Nuke Failed"
                    }
                  ],
                  "Default": "Nuke Success"
                },
                "Nuke Success": {
                  "Type": "Pass",
                  "Parameters": {
                    "Status": "Succeeded",
                    "Region.$": "$.region_id",
                    "CodeBuild Status.$": "$.AccountCleanserRegionOutput.NukeBuildOutput.BuildStatus"
                  },
                  "ResultPath": "$.result",
                  "End": true
                },
                "Nuke Failed": {
                  "Type": "Pass",
                  "Parameters": {
                    "Status": "Failed",
                    "Region.$": "$.region_id",
                    "CodeBuild Status.$": "States.Format('Nuke Account Cleanser failed with error {}. Check CodeBuild execution for input region {} to investigate', $.AccountCleanserRegionOutput.Error, $.region_id)"
                  },
                  "ResultPath": "$.result",
                  "End": true
                }
              }
            },
            "ResultSelector": {
              "filteredResult.$": "$..result"
            },
            "ResultPath": "$.NukeFinalMapAllRegionsOutput"
          },
          "Clean Output and Notify": {
            "Type": "Task",
            "Resource": "arn:aws:states:::sns:publish",
            "Parameters": {
              "Subject": "State Machine for Nuke Account Cleanser completed",
              "Message.$": "States.Format('Nuke Account Cleanser completed for input payload: {}. ----------------------------------------- Check the summmary of execution below: {}', $.InputPayLoad, $.NukeFinalMapAllRegionsOutput.filteredResult)",
              "TopicArn": "${nukeEmailTopic.ref}"
            },
            "End": true
          }
        }
      }`,
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
    });

    const eventBridgeNukeScheduleRole = new iam.CfnRole(this, 'EventBridgeNukeScheduleRole', {
      roleName: `EventBridgeNukeSchedule-${this.stackName}`,
      assumeRolePolicyDocument: {
        Version: '2012-10-17',
        Statement: [
          {
            Effect: 'Allow',
            Principal: {
              Service: 'events.amazonaws.com',
            },
            Action: 'sts:AssumeRole',
          },
        ],
      },
      tags: [
        {
          key: 'DoNotNuke',
          value: 'True',
        },
        {
          key: 'owner',
          value: props.owner!,
        },
      ],
      policies: [
        {
          policyName: 'EventBridgeNukeStateMachineExecutionPolicy',
          policyDocument: {
            Version: '2012-10-17',
            Statement: [
              {
                Effect: 'Allow',
                Action: 'states:StartExecution',
                Resource: nukeStepFunction.ref,
              },
            ],
          },
        },
      ],
    });

    const eventBridgeNukeSchedule = new events.CfnRule(this, 'EventBridgeNukeSchedule', {
      name: `EventBridgeNukeSchedule-${this.stackName}`,
      description: 'Scheduled Event for running AWS Nuke on the target accounts within the specified regions',
      scheduleExpression: 'cron(0 7 ? * * *)',
      state: 'ENABLED',
      roleArn: eventBridgeNukeScheduleRole.attrArn,
      targets: [
        {
          arn: nukeStepFunction.ref,
          roleArn: eventBridgeNukeScheduleRole.attrArn,
          id: nukeStepFunction.attrName,
          input: `{
            "InputPayLoad": {
              "nuke_dry_run": "${props.awsNukeDryRunFlag!}",
              "nuke_version": "${props.awsNukeVersion!}",
              "region_list": [
                "us-west-1",
                "us-east-1"
              ]
            }
          }`,
        },
      ],
    });

    // Outputs
    this.nukeTopicArn = nukeEmailTopic.ref;
    new cdk.CfnOutput(this, 'CfnOutputNukeTopicArn', {
      key: 'NukeTopicArn',
      description: 'Arn of SNS Topic used for notifying nuke results in email',
      value: this.nukeTopicArn!.toString(),
    });
    this.nukeS3BucketValue = nukeS3Bucket.ref;
    new cdk.CfnOutput(this, 'CfnOutputNukeS3BucketValue', {
      key: 'NukeS3BucketValue',
      description: 'S3 bucket created with the random generated name',
      value: this.nukeS3BucketValue!.toString(),
    });
  }
}
