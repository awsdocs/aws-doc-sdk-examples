#!/usr/bin/env python3

from aws_cdk import App
import os

from rekognition_photo_analyzer.rekognition_photo_analyzer import PythonRekognitionPhotoAnalyzerStack, JavaRekognitionPhotoAnalyzerStack

PAM_URL = "https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/cdk/rekognition-photo-analyzer"


app = App()
PythonRekognitionPhotoAnalyzerStack(
    app, "Python-RekognitionPhotoAnalyzer", name=os.environ['PAM_NAME'], email=os.environ["PAM_EMAIL"])
JavaRekognitionPhotoAnalyzerStack(
    app, "Java-RekognitionPhotoAnalyzer", name=os.environ['PAM_NAME'], email=os.environ["PAM_EMAIL"])

app.synth()
