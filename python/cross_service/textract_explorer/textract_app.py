# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to create a Tkinter application that displays input images and
Amazon Textract output. Lets you explore how the detection output relates to the input
image.
"""

from io import BytesIO
import logging
import tkinter
import threading
from PIL import Image, ImageTk
from textract_wrapper import TextractWrapper

logger = logging.getLogger(__name__)

NO_DATA_MESSAGE = 'Click one of the extract buttons to extract data.'
EXTRACTING_MESSAGE = "Extracting data..."

COLOR_MAP = {
    'PAGE': 'purple4',
    'LINE': 'lime green',
    'WORD': 'burlywood4',
    'TABLE': 'turquoise3',
    'CELL': 'salmon3',
    'KEY_VALUE_SET': 'cornflower blue',
    'SELECTION_ELEMENT': 'orange2'
}

FILTER_MAP = {
    'TEXT': ['PAGE', 'LINE', 'WORD'],
    'TABLES': ['PAGE', 'TABLE', 'CELL', 'WORD'],
    'FORMS': ['PAGE', 'KEY_VALUE_SET', 'WORD', 'SELECTION_ELEMENT']
}


class TextractExplorer:
    """
    A Tkinter application that displays input images, runs Amazon Textract detection
    jobs, and shows a hierarchy of detected elements. Click elements in the output
    hierarchy to explore the output format and see bounding polygons drawn on the input
    image.
    """
    def __init__(
            self, textract_wrapper, stack_outputs, default_image_name,
            default_image_bytes):
        """
        Initializes the main Tkinter window and adds all of the widgets needed for
        the application.

        :param textract_wrapper: An object that wraps Amazon Textract API functions.
        :param stack_outputs: Outputs from the setup CloudFormation stack. These
                              include an Amazon Simple Storage Service (Amazon S3)
                              bucket and an Amazon Simple Notification Service
                              (Amazon SNS) topic.
        """
        self.textract_wrapper = textract_wrapper
        self.stack_outputs = stack_outputs

        self.app = tkinter.Tk()
        self.app.title("Amazon Textract Explorer")

        self.container = tkinter.Frame(self.app)
        self.container.pack(fill=tkinter.BOTH, expand=True)
        self.container.rowconfigure(0, weight=1)
        self.container.columnconfigure(1, weight=1)

        self.input_frame = tkinter.Frame(self.container)
        self.explorer_frame = tkinter.Frame(self.container)

        self.image_offset = (5, 5)
        self.file_label = tkinter.Label(
            self.input_frame, wraplength=410,
            text="Enter the name of an image file to load, then click one of "
                 "the extract buttons to extract data from the image.")
        self.load_frame = tkinter.Frame(self.input_frame)
        self.file_text = tkinter.Entry(self.load_frame, width=50)
        self.load_button = tkinter.Button(
            self.load_frame, text='Load', command=self.load_document)
        self.input_canvas = tkinter.Canvas(
            self.input_frame,
            height=300 + self.image_offset[0],
            width=500 + self.image_offset[1],
            bg='white')
        self.button_frame = tkinter.Frame(self.input_frame)
        self.extract_sync = tkinter.BooleanVar(self.button_frame, True)
        self.sync_mode_button = tkinter.Radiobutton(
            self.button_frame, text='Synchronous', variable=self.extract_sync,
            value=True)
        self.async_mode_button = tkinter.Radiobutton(
            self.button_frame, text='Asynchronous', variable=self.extract_sync,
            value=False)
        self.extract_text_button = tkinter.Button(
            self.button_frame, text="Extract text",
            command=lambda: self.extract(self.extract_text_button))
        self.extract_table_button = tkinter.Button(
            self.button_frame, text="Extract table",
            command=lambda: self.extract(self.extract_table_button))
        self.extract_form_button = tkinter.Button(
            self.button_frame, text="Extract form",
            command=lambda: self.extract(self.extract_form_button))

        self.explorer_label = tkinter.Label(
            self.explorer_frame, text=NO_DATA_MESSAGE)
        self.explorer_label.grid(row=0, column=0, sticky=tkinter.NW, pady=5)
        self.tree_frame = tkinter.Frame(self.explorer_frame)
        self.tree_frame.grid(row=1, column=0, sticky=tkinter.NSEW)
        self.tree_frame.grid_columnconfigure(0, weight=1)
        self.tree_frame.grid_rowconfigure(0, weight=1)
        self.doc_canvas = None
        self.doc_frame = None

        self.app.geometry("900x410")

        self.input_frame.grid(row=0, column=0, sticky=tkinter.N)
        self.file_label.grid(row=0)
        self.load_frame.grid(row=1, column=0)
        self.file_text.grid(row=0, column=0)
        self.file_text.focus_set()
        self.load_button.grid(row=0, column=1)
        self.input_canvas.grid(row=2, column=0)
        self.button_frame.grid(row=3, column=0, pady=10)

        self.sync_mode_button.grid(row=0, column=0, padx=5)
        self.async_mode_button.grid(row=0, column=1, padx=5)
        self.extract_text_button.grid(row=0, column=2, padx=5)
        self.extract_table_button.grid(row=0, column=3, padx=5)
        self.extract_form_button.grid(row=0, column=4, padx=5)

        self.explorer_frame.grid(row=0, column=1, sticky=tkinter.NSEW)
        self.explorer_frame.grid_columnconfigure(0, weight=1)
        self.explorer_frame.grid_rowconfigure(1, weight=1)

        self.image = None
        self.tk_image = None
        self.current_file = default_image_name
        self.file_text.insert(0, self.current_file)
        self.load_document(default_image_bytes)

        self.block_filter = 'TEXT'
        self.textract_data = None

        self.app.mainloop()

    def load_document(self, image_bytes=None):
        """
        Loads a document image from the local file system and displays it in the
        input canvas.
        """
        if image_bytes is None:
            file_name = self.file_text.get()
            self.image = Image.open(file_name)
        else:
            self.image = Image.open(image_bytes)
        self.image.thumbnail((500, 300), Image.ANTIALIAS)
        self.tk_image = ImageTk.PhotoImage(self.image)
        self.input_canvas.create_image(
            *self.image_offset, anchor=tkinter.NW,
            image=self.tk_image)
        self.clear_nodes()

    def clear_nodes(self):
        """
        Clears output hierarchy nodes from the output canvas.
        """
        if self.doc_canvas is not None:
            self.doc_canvas.destroy()
            self.doc_canvas = None
        self.explorer_label['text'] = NO_DATA_MESSAGE

    def render_document(self, document):
        """
        Renders output hierarchy for a document in the output canvas.

        :param document: The hierarchy of document nodes to render.
        """
        self.doc_canvas = tkinter.Canvas(self.tree_frame)
        self.doc_canvas.grid(row=0, column=0, sticky=tkinter.NSEW)

        doc_scroll = tkinter.Scrollbar(
            self.tree_frame, orient=tkinter.VERTICAL, command=self.doc_canvas.yview)
        doc_scroll.grid(row=0, column=1, sticky=tkinter.NS)
        self.doc_canvas.configure(yscrollcommand=doc_scroll.set)
        self.doc_frame = tkinter.Frame(self.doc_canvas)

        doc_node = {'frame': self.doc_frame, 'data': document}
        self.expand_node(doc_node, [doc_node])

        self.doc_canvas.create_window((4, 4), window=self.doc_frame, anchor=tkinter.NW)

    @staticmethod
    def render_block(block):
        """
        Renders an individual block from the output hierarchy.

        :param block: The block to render.
        :return: The string representation of the block.
        """
        text = block.get('Text', '')

        block_type = block['BlockType']
        if block_type == 'CELL':
            text = f"({block['RowIndex']}, {block['ColumnIndex']})"
        elif block_type == 'KEY_VALUE_SET':
            text = f"({block['EntityTypes'][0]})"
        elif block_type == 'SELECTION_ELEMENT':
            text = f"({block['SelectionStatus']})"

        return f"{block_type} {text}"

    def expand_node(self, sel_node, node_list):
        """
        Expands a node in the output hierarchy. Child node UI elements are lazily
        added from the hierarchy when a parent node is expanded. This function is
        called when a node is clicked.

        :param sel_node: The selected node to expand.
        :param node_list: The list of nodes that are siblings to the selected node.
        """
        for node in node_list:
            node['frame'].grid_remove()

        for kid_widget in sel_node['frame'].children.values():
            if kid_widget.widgetName == 'frame':
                kid_widget.grid_remove()

        if not sel_node['frame'].children:
            child_list = []
            child_var = tkinter.IntVar()
            indent = 10 * max(0, str(sel_node['frame']).count('frame') - 2)
            for index, child in enumerate([
                    n for n in
                    sel_node['data'].get('Children', [])
                    if n['BlockType'] in FILTER_MAP[self.block_filter]]):
                child_frame = tkinter.Frame(sel_node['frame'])
                child_radio = tkinter.Radiobutton(
                    sel_node['frame'],
                    fg=COLOR_MAP.get(child['BlockType'], 'red'),
                    activeforeground='gray',
                    text=self.render_block(child),
                    variable=child_var,
                    value=index,
                    command=lambda: self.select_document_node(child_list, child_var))
                child_list.append({'frame': child_frame, 'data': child})
                row = index * 2
                child_radio.grid(
                    row=row, column=0, sticky=tkinter.NW, pady=2, padx=indent)
                child_frame.grid(row=row + 1, column=0, sticky=tkinter.NW, padx=indent)

        sel_node['frame'].grid()

        self.doc_frame.update_idletasks()
        bbox = self.doc_canvas.bbox(tkinter.ALL)
        self.doc_canvas.configure(scrollregion=bbox)

    def draw_polygon(self, polygon, color):
        """
        Draws a polygon on the input image. A polygon is the boundary of an element
        detected by Textract.

        :param polygon: The polygon in Textract output format.
        :param color: The color of the polygon.
        """
        img_width = self.tk_image.width()
        img_height = self.tk_image.height()
        points = [((p['X'] * img_width) + self.image_offset[0],
                   (p['Y'] * img_height) + self.image_offset[1]) for p in polygon]
        points.append(points[0])
        self.input_canvas.delete('polygon')
        self.input_canvas.create_line(points, fill=color, width=2, tag='polygon')

    def select_document_node(self, node_list, node_var):
        """
        Handles the event that is fired when a node is selected in the hierarchy.
        When a node is selected, it is expanded and its polygon is drawn on the
        input image.

        :param node_list: The list of nodes that are siblings to the selected node.
        :param node_var: The selected node in a Tkinter variable.
        """
        sel_node = node_list[node_var.get()]
        color = COLOR_MAP.get(sel_node['data']['BlockType'], 'red')
        self.expand_node(sel_node, node_list)
        self.draw_polygon(sel_node['data']['Geometry']['Polygon'], color)

    def do_sync_extract(self, doc_bytes):
        """
        Calls synchronous Textract APIs to detect document elements.

        :param doc_bytes: The image as a stream of bytes.
        """
        if self.block_filter == 'TEXT':
            self.textract_data = self.textract_wrapper.detect_file_text(
                document_bytes=doc_bytes.getvalue())
        else:
            self.textract_data = self.textract_wrapper.analyze_file(
                [self.block_filter], document_bytes=doc_bytes.getvalue())

    def do_async_extract(self, bucket_name, obj_name, sns_topic_arn, sns_role_arn):
        """
        Calls asynchronous Textract APIs to start jobs to detect document elements.

        :param bucket_name: The name of an Amazon S3 bucket that contains the input
                            image.
        :param obj_name: The name of the file in the Amazon S3 bucket.
        :param sns_topic_arn: The Amazon Resource Name (ARN) of an Amazon SNS topic
                              to which notifications are published.
        :param sns_role_arn: The ARN of an Amazon Identity and Access Management (IAM)
                             role that grants permission to publish to the topic.
        :return: The ID of the detection job.
        """
        if self.block_filter == 'TEXT':
            job_id = self.textract_wrapper.start_detection_job(
                bucket_name, obj_name, sns_topic_arn, sns_role_arn)
        else:
            job_id = self.textract_wrapper.start_analysis_job(
                bucket_name, obj_name, [self.block_filter], sns_topic_arn, sns_role_arn)
        return job_id

    def render_data_when_thread_ready(self, thread, button, text):
        """
        Polls a worker thread for completion and renders the output data when the
        thread completes. This is used when synchronous Textract APIs are used, so
        that the application does not freeze while the image is being processed.

        :param thread: The worker thread that is waiting for Textract processing to
                       complete.
        :param button: The button that was clicked to start processing.
        :param text: The original text of the button.
        """
        if thread.is_alive():
            self.app.after(
                100, lambda: self.render_data_when_thread_ready(thread, button, text))
        else:
            self.render_data(button, text)

    def render_data_when_job_ready(self, queue_url, job_id, button, text):
        """
        Polls an Amazon SQS queue for a job completion message and renders the output
        data when the message is received. This is used when asynchronous Textract APIs
        are used, to check for job status and to get the output data from Textract.

        :param queue_url: The URL of the Amazon SQS queue that receives notifications
                          from Textract.
        :param job_id: The ID of the last Textract job that was started.
        :param button: The button that was clicked to start processing.
        :param text: The original text of the button.
        """
        status = self.textract_wrapper.check_job_queue(queue_url, job_id)
        if status == 'SUCCEEDED':
            if self.block_filter == 'TEXT':
                self.textract_data = self.textract_wrapper.get_detection_job(job_id)
            else:
                self.textract_data = self.textract_wrapper.get_analysis_job(job_id)
            self.render_data(button, text)
        else:
            self.app.after(
                1000, lambda: self.render_data_when_job_ready(
                    queue_url, job_id, button, text))

    def render_data(self, button, text):
        """
        Gets a hierarchy of detected elements when Textract detection completes
        and renders them into the output canvas. Resets the clicked button to
        its original text and active state.

        :param button: The button that was clicked to start Textract processing.
        :param text: The original text of the button.
        """
        doc_hierarchy = self.textract_wrapper.make_page_hierarchy(
            self.textract_data['Blocks'])
        self.render_document(doc_hierarchy)
        self.explorer_label['text'] = (
            f"Extracted {self.block_filter} data from {self.current_file}.\n"
            f"Click an element to expand it and see its bounding polygon.")

        button.update()
        button['text'] = text
        button['state'] = tkinter.ACTIVE

    def extract(self, button):
        """
        Starts a Textract detection process in response to a button click.
        The selected button is deactivated during processing so that it cannot be
        clicked a second time.
        When Synchronous is selected, synchronous Textract APIs are called in a worker
        thread. When Asynchronous is selected, an asynchronous Textract job is started
        and an Amazon SQS queue is polled for a completion message.

        :param button: The clicked button.
        """
        if button is self.extract_text_button:
            self.block_filter = 'TEXT'
        elif button is self.extract_table_button:
            self.block_filter = 'TABLES'
        elif button is self.extract_form_button:
            self.block_filter = 'FORMS'

        self.clear_nodes()
        self.explorer_label['text'] = EXTRACTING_MESSAGE
        original_text = button['text']
        button['text'] = 'Extracting...'
        button['state'] = tkinter.DISABLED
        button.update()

        doc_bytes = BytesIO()
        self.image.save(doc_bytes, format='PNG')
        doc_bytes.seek(0)

        if self.extract_sync.get():
            extract_thread = threading.Thread(
                target=self.do_sync_extract, args=(doc_bytes,))
            extract_thread.start()
            self.app.after(
                5,
                lambda: self.render_data_when_thread_ready(
                    extract_thread, button, original_text))
        else:
            self.textract_wrapper.prepare_job(
                self.stack_outputs['BucketName'], self.current_file, doc_bytes)
            job_id = self.do_async_extract(
                self.stack_outputs['BucketName'], self.current_file,
                self.stack_outputs['TopicArn'], self.stack_outputs['RoleArn'])
            self.app.after(
                1000, lambda: self.render_data_when_job_ready(
                    self.stack_outputs['QueueUrl'], job_id, button, original_text))
