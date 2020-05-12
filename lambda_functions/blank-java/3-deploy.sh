#!/bin/bash
set -eo pipefail
ARTIFACT_BUCKET=$(cat bucket-name.txt)
TEMPLATE=template.yml
if [ $1 ]
then
  if [ $1 = mvn ]
  then
    TEMPLATE=template-mvn.yml
    mvn package
  fi
else
  gradle build -i
fi
aws cloudformation package --template-file $TEMPLATE --s3-bucket $ARTIFACT_BUCKET --output-template-file out.yml
aws cloudformation deploy --template-file out.yml --stack-name blank-java --capabilities CAPABILITY_NAMED_IAM
