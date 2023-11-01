# syntax=docker/dockerfile:1
FROM node:20

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /home/automation
USER automation:automation

# Add config
RUN mkdir /home/automation/.aws && \
    echo "[default]\nregion = us-east-1" > /home/automation/.aws/config

# Copy source code
COPY --chown=automation:automation ./javascriptv3 /home/automation/javascriptv3

# Copy resources
COPY --chown=automation:automation ./resources /home/automation/resources
COPY --chown=automation:automation ./python/example_code/glue/flight_etl_job_script.py /home/automation/python/example_code/glue/flight_etl_job_script.py
COPY --chown=automation:automation ./workflows /home/automation/workflows

# Set default command
# `npm i` needs to be run in the container. Otherwise it causes a dependency issue: https://github.com/evanw/esbuild/issues/1646#issuecomment-1238080595
CMD npm i --prefix /home/automation/javascriptv3 && npm run --prefix /home/automation/javascriptv3 integration-test
