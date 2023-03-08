import setuptools

with open("README.md") as fp:
    long_description = fp.read()

setuptools.setup(
    name="rekognition_photo_analyzer",
    version="0.0.1",
    description="A CDK Python app for classifying images",
    long_description="Image classification based on S3 trigger",
    long_description_content_type="text/markdown",
    author="Ford Prior",
    package_dir={"": "rekognition_photo_analyzer"},
    packages=setuptools.find_packages(where="rekognition_photo_analyzer"),
    python_requires=">=3.7",
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: Apache Software License",
        "Programming Language :: JavaScript",
        "Programming Language :: Python :: 3 :: Only",
        "Programming Language :: Python :: 3.6",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Topic :: Software Development :: Code Generators",
        "Topic :: Utilities",
        "Typing :: Typed",
    ],
)
