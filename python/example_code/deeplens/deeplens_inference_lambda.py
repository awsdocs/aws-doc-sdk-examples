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
# snippet-start:[deeplens.python.deeplens_inference_lambda.import]

from threading import Thread, Event
import os
import json
import numpy as np
import awscam
import cv2
import greengrasssdk

# snippet-end:[deeplens.python.deeplens_inference_lambda.import]
# snippet-start:[deeplens.python.deeplens_inference_lambda.class_LocalDisplay]

class LocalDisplay(Thread):
    """ Class for facilitating the local display of inference results
        (as images). The class is designed to run on its own thread. In
        particular the class dumps the inference results into a FIFO
        located in the tmp directory (which lambda has access to). The
        results can be rendered using mplayer by typing:
        mplayer -demuxer lavf -lavfdopts format=mjpeg:probesize=32 /tmp/results.mjpeg
    """
    def __init__(self, resolution):
        """ resolution - Desired resolution of the project stream """
        # Initialize the base class, so that the object can run on its own
        # thread.
        super(LocalDisplay, self).__init__()
        # List of valid resolutions
        RESOLUTION = {'1080p' : (1920, 1080), '720p' : (1280, 720), '480p' : (858, 480)}
        if resolution not in RESOLUTION:
            raise Exception("Invalid resolution")
        self.resolution = RESOLUTION[resolution]
        # Initialize the default image to be a white canvas. Clients
        # will update the image when ready.
        self.frame = cv2.imencode('.jpg', 255*np.ones([640, 480, 3]))[1]
        self.stop_request = Event()

    def run(self):
        """ Overridden method that continually dumps images to the desired
            FIFO file.
        """
        # Path to the FIFO file. The lambda only has permissions to the tmp
        # directory. Pointing to a FIFO file in another directory
        # will cause the lambda to crash.
        result_path = '/tmp/results.mjpeg'
        # Create the FIFO file if it doesn't exist.
        if not os.path.exists(result_path):
            os.mkfifo(result_path)
        # This call will block until a consumer is available
        with open(result_path, 'w') as fifo_file:
            while not self.stop_request.isSet():
                try:
                    # Write the data to the FIFO file. This call will block
                    # meaning the code will come to a halt here until a consumer
                    # is available.
                    fifo_file.write(self.frame.tobytes())
                except IOError:
                    continue

    def set_frame_data(self, frame):
        """ Method updates the image data. This currently encodes the
            numpy array to jpg but can be modified to support other encodings.
            frame - Numpy array containing the image data of the next frame
                    in the project stream.
        """
        ret, jpeg = cv2.imencode('.jpg', cv2.resize(frame, self.resolution))
        if not ret:
            raise Exception('Failed to set frame data')
        self.frame = jpeg

    def join(self):
        self.stop_request.set()
# snippet-end:[deeplens.python.deeplens_inference_lambda.class_LocalDisplay]

# snippet-start:[deeplens.python.deeplens_inference_lambda.inference_loop]
def infinite_infer_run():
    """ Entry point of the lambda function"""
    try:
        # This cat-dog model is implemented as binary classifier, since the number
        # of labels is small, create a dictionary that converts the machine
        # labels to human readable labels.
        model_type = 'classification'
        output_map = {0: 'dog', 1: 'cat'}

        # Create an IoT client for sending to messages to the cloud.
        client = greengrasssdk.client('iot-data')
        iot_topic = '$aws/things/{}/infer'.format(os.environ['AWS_IOT_THING_NAME'])

        # Create a local display instance that will dump the image bytes to a FIFO
        # file that the image can be rendered locally.
        local_display = LocalDisplay('480p')
        local_display.start()

        # The sample projects come with optimized artifacts, hence only the artifact
        # path is required.
        model_path = '/opt/awscam/artifacts/mxnet_resnet18-catsvsdogs_FP32_FUSED.xml'

        # Load the model onto the GPU.
        client.publish(topic=iot_topic, payload='Loading action cat-dog model')
        model = awscam.Model(model_path, {'GPU': 1})
        client.publish(topic=iot_topic, payload='Cat-Dog model loaded')

        # Since this is a binary classifier only retrieve 2 classes.
        num_top_k = 2

        # The height and width of the training set images
        input_height = 224
        input_width = 224

        # Do inference until the lambda is killed.
        while True:
            # inference loop to add. See the next step 
            ...


    except Exception as ex:
        client.publish(topic=iot_topic, payload='Error in cat-dog lambda: {}'.format(ex))
# snippet-end:[deeplens.python.deeplens_inference_lambda.inference_loop]

# snippet-start:[deeplens.python.deeplens_inference_lambda.inference_step]
            # Get a frame from the video stream
            ret, frame = awscam.getLastFrame()
            if not ret:
                raise Exception('Failed to get frame from the stream')
            # Resize frame to the same size as the training set.
            frame_resize = cv2.resize(frame, (input_height, input_width))
            # Run the images through the inference engine and parse the results using
            # the parser API, note it is possible to get the output of doInference
            # and do the parsing manually, but since it is a classification model,
            # a simple API is provided.
            parsed_inference_results = model.parseResult(model_type,
                                                         model.doInference(frame_resize))
            # Get top k results with highest probabilities
            top_k = parsed_inference_results[model_type][0:num_top_k]
            # Add the label of the top result to the frame used by local display.
            # See https://docs.opencv.org/3.4.1/d6/d6e/group__imgproc__draw.html
            # for more information about the cv2.putText method.
            # Method signature: image, text, origin, font face, font scale, color, and tickness
            cv2.putText(frame, output_map[top_k[0]['label']], (10, 70),
                        cv2.FONT_HERSHEY_SIMPLEX, 3, (255, 165, 20), 8)
            # Set the next frame in the local display stream.
            local_display.set_frame_data(frame)
            # Send the top k results to the IoT console via MQTT
            cloud_output = {}
            for obj in top_k:
                cloud_output[output_map[obj['label']]] = obj['prob']
            client.publish(topic=iot_topic, payload=json.dumps(cloud_output))
# snippet-end:[deeplens.python.deeplens_inference_lambda.inference_step]

# snippet-start:[deeplens.python.deeplens_inference_lambda.complete]
#*****************************************************
#                                                    *
# Copyright 2018 Amazon.com, Inc. or its affiliates. *
# All Rights Reserved.                               *
#                                                    *
#*****************************************************
""" A sample lambda for cat-dog detection"""
from threading import Thread, Event
import os
import json
import numpy as np
import awscam
import cv2
import greengrasssdk

class LocalDisplay(Thread):
    """ Class for facilitating the local display of inference results
        (as images). The class is designed to run on its own thread. In
        particular the class dumps the inference results into a FIFO
        located in the tmp directory (which lambda has access to). The
        results can be rendered using mplayer by typing:
        mplayer -demuxer lavf -lavfdopts format=mjpeg:probesize=32 /tmp/results.mjpeg
    """
    def __init__(self, resolution):
        """ resolution - Desired resolution of the project stream """
        # Initialize the base class, so that the object can run on its own
        # thread.
        super(LocalDisplay, self).__init__()
        # List of valid resolutions
        RESOLUTION = {'1080p' : (1920, 1080), '720p' : (1280, 720), '480p' : (858, 480)}
        if resolution not in RESOLUTION:
            raise Exception("Invalid resolution")
        self.resolution = RESOLUTION[resolution]
        # Initialize the default image to be a white canvas. Clients
        # will update the image when ready.
        self.frame = cv2.imencode('.jpg', 255*np.ones([640, 480, 3]))[1]
        self.stop_request = Event()

    def run(self):
        """ Overridden method that continually dumps images to the desired
            FIFO file.
        """
        # Path to the FIFO file. The lambda only has permissions to the tmp
        # directory. Pointing to a FIFO file in another directory
        # will cause the lambda to crash.
        result_path = '/tmp/results.mjpeg'
        # Create the FIFO file if it doesn't exist.
        if not os.path.exists(result_path):
            os.mkfifo(result_path)
        # This call will block until a consumer is available
        with open(result_path, 'w') as fifo_file:
            while not self.stop_request.isSet():
                try:
                    # Write the data to the FIFO file. This call will block
                    # meaning the code will come to a halt here until a consumer
                    # is available.
                    fifo_file.write(self.frame.tobytes())
                except IOError:
                    continue

    def set_frame_data(self, frame):
        """ Method updates the image data. This currently encodes the
            numpy array to jpg but can be modified to support other encodings.
            frame - Numpy array containing the image data of the next frame
                    in the project stream.
        """
        ret, jpeg = cv2.imencode('.jpg', cv2.resize(frame, self.resolution))
        if not ret:
            raise Exception('Failed to set frame data')
        self.frame = jpeg

    def join(self):
        self.stop_request.set()

def infinite_infer_run():
    """ Entry point of the lambda function"""
    try:
        # This cat-dog model is implemented as binary classifier, since the number
        # of labels is small, create a dictionary that converts the machine
        # labels to human readable labels.
        model_type = 'classification'
        output_map = {0: 'dog', 1: 'cat'}
        # Create an IoT client for sending to messages to the cloud.
        client = greengrasssdk.client('iot-data')
        iot_topic = '$aws/things/{}/infer'.format(os.environ['AWS_IOT_THING_NAME'])
        # Create a local display instance that will dump the image bytes to a FIFO
        # file that the image can be rendered locally.
        local_display = LocalDisplay('480p')
        local_display.start()
        # The sample projects come with optimized artifacts, hence only the artifact
        # path is required.
        model_path = '/opt/awscam/artifacts/mxnet_resnet18-catsvsdogs_FP32_FUSED.xml'
        # Load the model onto the GPU.
        client.publish(topic=iot_topic, payload='Loading action cat-dog model')
        model = awscam.Model(model_path, {'GPU': 1})
        client.publish(topic=iot_topic, payload='Cat-Dog model loaded')
        # Since this is a binary classifier only retrieve 2 classes.
        num_top_k = 2
        # The height and width of the training set images
        input_height = 224
        input_width = 224
        # Do inference until the lambda is killed.
        while True:
            # Get a frame from the video stream
            ret, frame = awscam.getLastFrame()
            if not ret:
                raise Exception('Failed to get frame from the stream')
            # Resize frame to the same size as the training set.
            frame_resize = cv2.resize(frame, (input_height, input_width))
            # Run the images through the inference engine and parse the results using
            # the parser API, note it is possible to get the output of doInference
            # and do the parsing manually, but since it is a classification model,
            # a simple API is provided.
            parsed_inference_results = model.parseResult(model_type,
                                                         model.doInference(frame_resize))
            # Get top k results with highest probabilities
            top_k = parsed_inference_results[model_type][0:num_top_k]
            # Add the label of the top result to the frame used by local display.
            # See https://docs.opencv.org/3.4.1/d6/d6e/group__imgproc__draw.html
            # for more information about the cv2.putText method.
            # Method signature: image, text, origin, font face, font scale, color, and tickness
            cv2.putText(frame, output_map[top_k[0]['label']], (10, 70),
                        cv2.FONT_HERSHEY_SIMPLEX, 3, (255, 165, 20), 8)
            # Set the next frame in the local display stream.
            local_display.set_frame_data(frame)
            # Send the top k results to the IoT console via MQTT
            cloud_output = {}
            for obj in top_k:
                cloud_output[output_map[obj['label']]] = obj['prob']
            client.publish(topic=iot_topic, payload=json.dumps(cloud_output))
    except Exception as ex:
        client.publish(topic=iot_topic, payload='Error in cat-dog lambda: {}'.format(ex))

infinite_infer_run()
# snippet-end:[deeplens.python.deeplens_inference_lambda.complete]

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[deeplens_inference_lambda.py demonstrates how to create an inference Lambda function on an AWS DeepLens model.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS Greengrass SDK]
# snippet-keyword:[AWS Lambda]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS DeepLens]
# snippet-service:[deeplens]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-07]
# snippet-sourceauthor:[AWS]
