# Running EMR using AWS Java v2 SDK
## Setting up default roles
```sh
aws emr create-default-roles
```

## Build the docker image
```sh
docker build -t emr-spot-example .
```

## Run the application
```sh
docker build -t emr-spot-example .
docker run --rm -it \
       -e AWS_ACCESS_KEY_ID="" \
       -e AWS_SECRET_ACCESS_KEY="" \
       -e AWS_REGION="" \
       -e EC2_KEY_NAME="" \
       -e EC2_SUBNETS_IDs="" \ # comma-separated values
       -v $(pwd)/.m2:/root/.m2 \
       -v $(pwd):/app \
       emr-spot-example bash
```

Once inside the container, run:
```sh
./run_example.sh
```
