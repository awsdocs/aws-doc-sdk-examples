# Amazon RDS Blue/Green encryption migration KMS keys

This AWS CDK app defines the `RdsBlueGreenKmsKeysStack` AWS CloudFormation stack used by the
[Python Amazon RDS Blue/Green encryption migration scenario](../../../python/example_code/rds/blue_green_encryption_migration_scenario).

The stack creates:

* `OriginalKmsKey`: an AWS KMS customer managed key that encrypts the source (blue) DB instance.
* `TargetKmsKey`: an AWS KMS customer managed key that encrypts the green DB instance.

Both keys have automatic key rotation turned on and use the default key policy, which delegates
access to IAM. When the stack is deleted, the keys are scheduled for deletion after a 7-day
pending window.

The stack has two outputs, `OriginalKmsKeyArn` and `TargetKmsKeyArn`.

The Python scenario deploys the synthesized template itself, so you don't need the CDK to run
the scenario.

## Regenerate the template

After you change `setup.ts`, run the following commands from this folder to update the template
that the scenario uses. The stack turns off the bootstrap version rule, and the flags below
remove CDK metadata, so the template can be deployed with a plain `CreateStack` call.

```
npm install
npx cdk synth --path-metadata false --version-reporting false > ../../../python/example_code/rds/blue_green_encryption_migration_scenario/rds_blue_green_kms_keys_template.yaml
```

On Windows PowerShell, run the `cdk synth` command through `cmd /c "..."` so the output file is
written as UTF-8. Then add back the two-line comment header at the top of the template.
