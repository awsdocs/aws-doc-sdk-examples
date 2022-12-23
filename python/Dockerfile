# syntax=docker/dockerfile:1
FROM python:3

# copy in files
RUN mkdir /src
COPY . /src/

# resolve dependencies
WORKDIR /src/python/
RUN pip install -r requirements.txt

CMD ["bash"]
