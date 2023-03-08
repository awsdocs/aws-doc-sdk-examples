from aws_cdk import App
from rekognition_photo_analyzer.rekognition_photo_analyzer import (
    JavaRekognitionPhotoAnalyzerStack,
)


if __name__ == "__main__":
    app = App()
    JavaRekognitionPhotoAnalyzerStack(app, name="debug", email="debug@amazon.com")
    app.synth()
