#!/bin/bash

aws cloudformation create-stack --stack-name CrossAccountDeploymentRoleStack --template-body file://cross-account-role-template.yaml --capabilities CAPABILITY_NAMED_IAM

