# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
# snippet-start:[deeplens.python.deeplens_frameworks_mxnet.export_model_gluon_api]

import mxnet as mx
from mxnet.gluon.model_zoo import vision
squeezenet = vision.squeezenet_v1(pretrained=True, ctx=mx.cpu())

# To export, you need to hybridize your gluon model,
squeezenet.hybridize()

# SqueezeNet’s input pattern is 224 pixel X 224 pixel images. Prepare a fake image,
fake_image = mx.nd.random.uniform(shape=(1,3,224,224), ctx=mx.cpu())

# Run the model once.
result = squeezenet(fake_image)

# Now you can export the model. You can use a path if you want ‘models/squeezenet’.
squeezenet.export(‘squeezenet')

# snippet-end:[deeplens.python.deeplens_frameworks_mxnet.export_model_gluon_api]


# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[deeplens_frameworks_mxnet.py demonstrates how to create an inference Lambda function on an AWS DeepLens model.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS Lambda]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS DeepLens]
# snippet-service:[deeplens]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-07]
# snippet-sourceauthor:[AWS]
