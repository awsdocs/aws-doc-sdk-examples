# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Wraps several Amazon Rekognition elements in Python classes. Provides functions
to draw bounding boxes and polygons on an image and display it with the default
viewer.
"""

# snippet-start:[python.example_code.rekognition.helper.imports]
import io
import logging
from PIL import Image, ImageDraw

logger = logging.getLogger(__name__)

# snippet-end:[python.example_code.rekognition.helper.imports]


# snippet-start:[python.example_code.rekognition.helper.show_bounding_boxes]
def show_bounding_boxes(image_bytes, box_sets, colors):
    """
    Draws bounding boxes on an image and shows it with the default image viewer.

    :param image_bytes: The image to draw, as bytes.
    :param box_sets: A list of lists of bounding boxes to draw on the image.
    :param colors: A list of colors to use to draw the bounding boxes.
    """
    image = Image.open(io.BytesIO(image_bytes))
    draw = ImageDraw.Draw(image)
    for boxes, color in zip(box_sets, colors):
        for box in boxes:
            left = image.width * box['Left']
            top = image.height * box['Top']
            right = (image.width * box['Width']) + left
            bottom = (image.height * box['Height']) + top
            draw.rectangle([left, top, right, bottom], outline=color, width=3)
    image.show()
# snippet-end:[python.example_code.rekognition.helper.show_bounding_boxes]


# snippet-start:[python.example_code.rekognition.helper.show_polygons]
def show_polygons(image_bytes, polygons, color):
    """
    Draws polygons on an image and shows it with the default image viewer.

    :param image_bytes: The image to draw, as bytes.
    :param polygons: The list of polygons to draw on the image.
    :param color: The color to use to draw the polygons.
    """
    image = Image.open(io.BytesIO(image_bytes))
    draw = ImageDraw.Draw(image)
    for polygon in polygons:
        draw.polygon([
            (image.width * point['X'], image.height * point['Y']) for point in polygon],
            outline=color)
    image.show()
# snippet-end:[python.example_code.rekognition.helper.show_polygons]


# snippet-start:[python.example_code.rekognition.helper.RekognitionFace]
class RekognitionFace:
    """Encapsulates an Amazon Rekognition face."""
    def __init__(self, face, timestamp=None):
        """
        Initializes the face object.

        :param face: Face data, in the format returned by Amazon Rekognition
                     functions.
        :param timestamp: The time when the face was detected, if the face was
                          detected in a video.
        """
        self.bounding_box = face.get('BoundingBox')
        self.confidence = face.get('Confidence')
        self.landmarks = face.get('Landmarks')
        self.pose = face.get('Pose')
        self.quality = face.get('Quality')
        age_range = face.get('AgeRange')
        if age_range is not None:
            self.age_range = (age_range.get('Low'), age_range.get('High'))
        else:
            self.age_range = None
        self.smile = face.get('Smile', {}).get('Value')
        self.eyeglasses = face.get('Eyeglasses', {}).get('Value')
        self.sunglasses = face.get('Sunglasses', {}).get('Value')
        self.gender = face.get('Gender', {}).get('Value', None)
        self.beard = face.get('Beard', {}).get('Value')
        self.mustache = face.get('Mustache', {}).get('Value')
        self.eyes_open = face.get('EyesOpen', {}).get('Value')
        self.mouth_open = face.get('MouthOpen', {}).get('Value')
        self.emotions = [emo.get('Type') for emo in face.get('Emotions', [])
                         if emo.get('Confidence', 0) > 50]
        self.face_id = face.get('FaceId')
        self.image_id = face.get('ImageId')
        self.timestamp = timestamp

    def to_dict(self):
        """
        Renders some of the face data to a dict.

        :return: A dict that contains the face data.
        """
        rendering = {}
        if self.bounding_box is not None:
            rendering['bounding_box'] = self.bounding_box
        if self.age_range is not None:
            rendering['age'] = f'{self.age_range[0]} - {self.age_range[1]}'
        if self.gender is not None:
            rendering['gender'] = self.gender
        if self.emotions:
            rendering['emotions'] = self.emotions
        if self.face_id is not None:
            rendering['face_id'] = self.face_id
        if self.image_id is not None:
            rendering['image_id'] = self.image_id
        if self.timestamp is not None:
            rendering['timestamp'] = self.timestamp
        has = []
        if self.smile:
            has.append('smile')
        if self.eyeglasses:
            has.append('eyeglasses')
        if self.sunglasses:
            has.append('sunglasses')
        if self.beard:
            has.append('beard')
        if self.mustache:
            has.append('mustache')
        if self.eyes_open:
            has.append('open eyes')
        if self.mouth_open:
            has.append('open mouth')
        if has:
            rendering['has'] = has
        return rendering
# snippet-end:[python.example_code.rekognition.helper.RekognitionFace]


# snippet-start:[python.example_code.rekognition.helper.RekognitionCelebrity]
class RekognitionCelebrity:
    """Encapsulates an Amazon Rekognition celebrity."""
    def __init__(self, celebrity, timestamp=None):
        """
        Initializes the celebrity object.

        :param celebrity: Celebrity data, in the format returned by Amazon Rekognition
                          functions.
        :param timestamp: The time when the celebrity was detected, if the celebrity
                          was detected in a video.
        """
        self.info_urls = celebrity.get('Urls')
        self.name = celebrity.get('Name')
        self.id = celebrity.get('Id')
        self.face = RekognitionFace(celebrity.get('Face'))
        self.confidence = celebrity.get('MatchConfidence')
        self.bounding_box = celebrity.get('BoundingBox')
        self.timestamp = timestamp

    def to_dict(self):
        """
        Renders some of the celebrity data to a dict.

        :return: A dict that contains the celebrity data.
        """
        rendering = self.face.to_dict()
        if self.name is not None:
            rendering['name'] = self.name
        if self.info_urls:
            rendering['info URLs'] = self.info_urls
        if self.timestamp is not None:
            rendering['timestamp'] = self.timestamp
        return rendering
# snippet-end:[python.example_code.rekognition.helper.RekognitionCelebrity]


# snippet-start:[python.example_code.rekognition.helper.RekognitionPerson]
class RekognitionPerson:
    """Encapsulates an Amazon Rekognition person."""
    def __init__(self, person, timestamp=None):
        """
        Initializes the person object.

        :param person: Person data, in the format returned by Amazon Rekognition
                       functions.
        :param timestamp: The time when the person was detected, if the person
                          was detected in a video.
        """
        self.index = person.get('Index')
        self.bounding_box = person.get('BoundingBox')
        face = person.get('Face')
        self.face = RekognitionFace(face) if face is not None else None
        self.timestamp = timestamp

    def to_dict(self):
        """
        Renders some of the person data to a dict.

        :return: A dict that contains the person data.
        """
        rendering = self.face.to_dict() if self.face is not None else {}
        if self.index is not None:
            rendering['index'] = self.index
        if self.bounding_box is not None:
            rendering['bounding_box'] = self.bounding_box
        if self.timestamp is not None:
            rendering['timestamp'] = self.timestamp
        return rendering
# snippet-end:[python.example_code.rekognition.helper.RekognitionPerson]


# snippet-start:[python.example_code.rekognition.helper.RekognitionLabel]
class RekognitionLabel:
    """Encapsulates an Amazon Rekognition label."""
    def __init__(self, label, timestamp=None):
        """
        Initializes the label object.

        :param label: Label data, in the format returned by Amazon Rekognition
                      functions.
        :param timestamp: The time when the label was detected, if the label
                          was detected in a video.
        """
        self.name = label.get('Name')
        self.confidence = label.get('Confidence')
        self.instances = label.get('Instances')
        self.parents = label.get('Parents')
        self.timestamp = timestamp

    def to_dict(self):
        """
        Renders some of the label data to a dict.

        :return: A dict that contains the label data.
        """
        rendering = {}
        if self.name is not None:
            rendering['name'] = self.name
        if self.timestamp is not None:
            rendering['timestamp'] = self.timestamp
        return rendering
# snippet-end:[python.example_code.rekognition.helper.RekognitionLabel]


# snippet-start:[python.example_code.rekognition.helper.RekognitionModerationLabel]
class RekognitionModerationLabel:
    """Encapsulates an Amazon Rekognition moderation label."""
    def __init__(self, label, timestamp=None):
        """
        Initializes the moderation label object.

        :param label: Label data, in the format returned by Amazon Rekognition
                      functions.
        :param timestamp: The time when the moderation label was detected, if the
                          label was detected in a video.
        """
        self.name = label.get('Name')
        self.confidence = label.get('Confidence')
        self.parent_name = label.get('ParentName')
        self.timestamp = timestamp

    def to_dict(self):
        """
        Renders some of the moderation label data to a dict.

        :return: A dict that contains the moderation label data.
        """
        rendering = {}
        if self.name is not None:
            rendering['name'] = self.name
        if self.parent_name is not None:
            rendering['parent_name'] = self.parent_name
        if self.timestamp is not None:
            rendering['timestamp'] = self.timestamp
        return rendering
# snippet-end:[python.example_code.rekognition.helper.RekognitionModerationLabel]


# snippet-start:[python.example_code.rekognition.helper.RekognitionText]
class RekognitionText:
    """Encapsulates an Amazon Rekognition text element."""
    def __init__(self, text_data):
        """
        Initializes the text object.

        :param text_data: Text data, in the format returned by Amazon Rekognition
                          functions.
        """
        self.text = text_data.get('DetectedText')
        self.kind = text_data.get('Type')
        self.id = text_data.get('Id')
        self.parent_id = text_data.get('ParentId')
        self.confidence = text_data.get('Confidence')
        self.geometry = text_data.get('Geometry')

    def to_dict(self):
        """
        Renders some of the text data to a dict.

        :return: A dict that contains the text data.
        """
        rendering = {}
        if self.text is not None:
            rendering['text'] = self.text
        if self.kind is not None:
            rendering['kind'] = self.kind
        if self.geometry is not None:
            rendering['polygon'] = self.geometry.get('Polygon')
        return rendering
# snippet-end:[python.example_code.rekognition.helper.RekognitionText]
