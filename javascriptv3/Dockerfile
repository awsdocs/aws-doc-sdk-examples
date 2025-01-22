# syntax=docker/dockerfile:1
FROM node:20

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY ./javascriptv3 /javascriptv3

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /javascriptv3
USER automation:automation

# Copy resources
COPY --chown=automation:automation ./resources /resources
COPY --chown=automation:automation ./python/example_code/glue/flight_etl_job_script.py /python/example_code/glue/flight_etl_job_script.py
COPY --chown=automation:automation ./scenarios/features /scenarios/features

# Set default command
# `npm i` needs to be run in the container. Otherwise it causes a dependency issue: https://github.com/evanw/esbuild/issues/1646#issuecomment-1238080595
# `aws-cdk` is required by some integration tests in order to deploy resources
CMD npm i aws-cdk && npm i --prefix /javascriptv3 && npm run --prefix /javascriptv3 integration-test
