#!/usr/bin/env python3

from aws_cdk import App

from rekognition_photo_analyzer.rekognition_photo_analyzer import RekognitionPhotoAnalyzerStack

app = App()
RekognitionPhotoAnalyzerStack(app, "RekognitionPhotoAnalyzerStack")

app.synth()
