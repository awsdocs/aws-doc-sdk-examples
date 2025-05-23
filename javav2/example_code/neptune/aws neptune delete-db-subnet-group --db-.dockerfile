aws neptune delete-db-subnet-group --db-subnet-group-name my-neptune-subnet-group

aws neptune delete-db-cluster --db-cluster-identifier my-neptune-cluster --skip-final-snapshot

aws neptune describe-db-instances --query "DBInstances[?Engine=='neptune']"

aws neptune delete-db-instance --db-instance-identifier my-neptune-db --skip-final-snapshot


aws iam create-role --role-name NeptuneLoadFromS3Role --assume-role-policy-document file://trust-policy.json


