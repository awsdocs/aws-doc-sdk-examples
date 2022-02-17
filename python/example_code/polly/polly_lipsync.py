# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Polly and Tkinter to
create a lip-sync application that displays an animated face speaking along with the
speech synthesized by Amazon Polly. Lip-sync is accomplished by requesting a list
of visemes from Amazon Polly that match up with the synthesized speech.
"""

import json
import logging
import os
from tempfile import TemporaryDirectory
import time
import tkinter
import tkinter.simpledialog
import boto3
from botocore.exceptions import ClientError
from playsound import playsound
import requests
from polly_wrapper import PollyWrapper

logger = logging.getLogger(__name__)


# Media is loaded from this URL if it is not found in the local .media folder.
GITHUB_URL = 'https://raw.githubusercontent.com/awsdocs/aws-doc-sdk-examples/' \
             'awsdocs/polly-examples/python/example_code/polly/'


class PollyMouth:
    """
    A Tkinter application that lets a user enter text, select an Amazon Polly voice,
    and hear the text spoken by the selected voice while an animated face lip-syncs
    along with it.
    """

    # A dictionary of visemes mapped to image file names.
    lips = {
        'p': {'name': '.media/lips_m.png'},
        't': {'name': '.media/lips_c.png'},
        'S': {'name': '.media/lips_ch.png'},
        'T': {'name': '.media/lips_th.png'},
        'f': {'name': '.media/lips_f.png'},
        'k': {'name': '.media/lips_c.png'},
        'i': {'name': '.media/lips_e.png'},
        'r': {'name': '.media/lips_r.png'},
        's': {'name': '.media/lips_c.png'},
        'u': {'name': '.media/lips_w.png'},
        '@': {'name': '.media/lips_u.png'},
        'a': {'name': '.media/lips_a.png'},
        'e': {'name': '.media/lips_a.png'},
        'E': {'name': '.media/lips_u.png'},
        'o': {'name': '.media/lips_o.png'},
        'O': {'name': '.media/lips_u.png'},
        'sil': {'name': '.media/lips_sil.png'}
    }

    def __init__(self, polly_wrapper):
        """
        Initializes the main Tkinter window and adds all of the widgets needed for
        the application.

        :param polly_wrapper: An object that can call Amazon Polly API functions.
        """
        self.polly_wrapper = polly_wrapper
        self.app = tkinter.Tk()
        self.app.title("Amazon Polly Lip Sync")
        self.app.resizable(False, False)

        self.load_lips()

        choices_frame = tkinter.Frame(self.app)

        self.sayit_label = tkinter.Label(
            self.app, wraplength=410,
            text="Write some text in the box below, then click 'Say it!' "
                 "to hear and see your text.")
        self.sayit_txt = tkinter.Text(self.app, width=50, height=16)

        self.engine_label = tkinter.Label(choices_frame, text='Engine:')
        self.engine_var = tkinter.StringVar(choices_frame, 'neural')
        self.engine_options = tkinter.OptionMenu(
            choices_frame, self.engine_var, *sorted(polly_wrapper.get_voice_engines()),
            command=self.change_engine)

        self.language_label = tkinter.Label(choices_frame, text='Language:')
        self.language_var = tkinter.StringVar(choices_frame, 'US English')
        self.language_choices = polly_wrapper.get_languages(self.engine_var.get())
        self.language_options = tkinter.OptionMenu(
            choices_frame, self.language_var, *sorted(self.language_choices),
            command=self.change_language)

        self.voice_label = tkinter.Label(choices_frame, text='Voice:')
        self.voice_var = tkinter.StringVar(choices_frame, 'Joanna')
        self.voice_choices = polly_wrapper.get_voices(
            self.engine_var.get(), self.language_choices[self.language_var.get()])
        self.voice_options = tkinter.OptionMenu(
            choices_frame, self.voice_var, *sorted(self.voice_choices))

        self.face_canvas = tkinter.Canvas(
            choices_frame, height=100, width=200, bg='white')
        self.sayit_button = tkinter.Button(
            self.app, text="Say it!", command=self.say_it)

        self.loading_text = tkinter.Label(self.app, bg='white')

        self.app.geometry("635x320")

        self.sayit_label.grid(row=0)
        self.sayit_txt.grid(row=1, column=0)
        self.sayit_txt.focus_set()
        self.sayit_button.grid(row=2, pady=10, columnspan=2)
        self.sayit_button.configure(width=85, padx=10)

        choices_frame.grid(row=1, column=1, sticky=tkinter.N)
        self.engine_label.grid(row=0, column=0, sticky=tkinter.N, pady=10)
        self.engine_options.grid(row=0, column=1, sticky=tkinter.NW, padx=5, pady=10)
        self.engine_options.configure(width=18)
        self.language_label.grid(row=1, column=0, sticky=tkinter.N, pady=10)
        self.language_options.grid(row=1, column=1, sticky=tkinter.NW, padx=5, pady=10)
        self.language_options.configure(width=18)
        self.voice_label.grid(row=2, column=0, sticky=tkinter.N, pady=10)
        self.voice_options.grid(row=2, column=1, sticky=tkinter.NW, padx=5, pady=10)
        self.voice_options.configure(width=18)
        self.face_canvas.grid(row=3, columnspan=2, padx=10)

        self.face_canvas.create_image(100, 60, image=self.lips['sil']['image'])

        self.app.mainloop()

    def load_lips(self):
        """
        Loads lip-sync images either from a local '.media' folder or from GitHub
        and saves image data in a dictionary of visemes.
        """
        if os.path.isdir('.media'):
            logger.info("Found .media folder. Loading images from the local folder.")
            for viseme in self.lips:
                self.lips[viseme]['image'] = tkinter.PhotoImage(
                    file=self.lips[viseme]['name'])
        else:
            logger.info("No local .media folder. Trying to load images from GitHub.")
            for viseme in self.lips:
                url = GITHUB_URL + self.lips[viseme]['name']
                resp = requests.get(url)
                img = resp.content if resp.status_code == 200 else b''
                if resp.status_code != 200:
                    logger.warning("Couldn't load image from %s.", url)
                self.lips[viseme]['image'] = tkinter.PhotoImage(data=img)

    def change_engine(self, engine):
        """
        Handles the event that is fired when the selected engine type is changed in
        the UI. Updates the lists of available languages and voices that are supported
        for the selected engine type.

        :param engine: The newly selected engine type.
        """
        self.language_choices = self.polly_wrapper.get_languages(engine)
        lang_menu = self.language_options['menu']
        lang_menu.delete(0, 'end')
        sorted_choices = sorted(self.language_choices)
        for lang in sorted_choices:
            lang_menu.add_command(
                label=lang, command=lambda l=lang: self.change_language(l))
        self.change_language(sorted_choices[0])

    def change_language(self, language):
        """
        Handles the event that is fired when the selected language is changed in the
        UI. Updates the list of available voices that are available for the selected
        language and engine type.

        :param language: The newly selected language.
        """
        self.language_var.set(language)
        self.voice_choices = self.polly_wrapper.get_voices(
            self.engine_var.get(), self.language_choices[language])
        voice_menu = self.voice_options['menu']
        voice_menu.delete(0, 'end')
        sorted_choices = sorted(self.voice_choices)
        for voice in sorted_choices:
            voice_menu.add_command(
                label=voice, command=lambda v=voice: self.voice_var.set(v))
        self.voice_var.set(sorted_choices[0])

    def animate_lips(self, start_time, viseme, viseme_iter):
        """
        Animates the face that lip-syncs along with the synthesized speech. This
        uses the list of visemes and their associated timings that is returned from
        Amazon Polly. The image associated with a viseme is displayed and the next
        viseme is scheduled to display at the time indicated in the list of visemes.

        :param start_time: The time the animation is started. This is used to
                           calculate the time to wait until the next viseme image
                           is displayed.
        :param viseme: The current viseme to display.
        :param viseme_iter: An iterator that yields visemes from the list returned
                            from Amazon Polly.
        """
        try:
            mouth = self.lips.get(viseme['value'], self.lips['sil'])
            self.face_canvas.create_image(
                100, 60, image=mouth['image'])
            self.app.update()
            next_viseme = next(viseme_iter)
            next_time = start_time + next_viseme['time']
            cur_time = time.time_ns() // 1000000  # milliseconds
            wait_time = max(0, next_time - cur_time)
            logger.info("Vis: %s, cur_time %s, wait_time %s", mouth,
                        cur_time - start_time, wait_time)
            self.app.after(
                wait_time, self.animate_lips, start_time, next_viseme, viseme_iter)
        except StopIteration:
            pass

    def long_text_wait_callback(self, task_type, task_status):
        """
        A callback function that displays status while waiting for an asynchronous
        long text speech synthesis task to complete.

        :param task_type: The type of synthesis task (either 'speech' or 'viseme').
        :param task_status: The status of the task.
        """
        self.loading_text.grid(row=0, rowspan=4, columnspan=2, sticky=tkinter.NSEW)
        self.loading_text.configure(
            text=f"Waiting for {task_type}. Current status: {task_status}.")
        self.app.update()
        if task_status in ('completed', 'failed'):
            self.app.after(1000)
            self.loading_text.grid_forget()

    def say_it(self):
        """
        Gets synthesized speech and visemes from Amazon Polly, stores the audio in
        a temporary file, and plays the sound and lip-sync animation.

        When the text is too long for synchronous synthesis, this function displays a
        dialog that asks the user for an Amazon Simple Storage Service (Amazon S3)
        bucket to use for output storage, starts an asynchronous synthesis task, and
        waits for the task to complete.
        """
        audio_stream = None
        visemes = []
        try:
            audio_stream, visemes = self.polly_wrapper.synthesize(
                self.sayit_txt.get(1.0, tkinter.END),
                self.engine_var.get(),
                self.voice_choices[self.voice_var.get()],
                'mp3',
                self.language_choices[self.language_var.get()],
                True)
        except ClientError as error:
            if error.response['Error']['Code'] == 'TextLengthExceededException':
                bucket_name = tkinter.simpledialog.askstring(
                    "Text too long",
                    "The text is too long for synchronous synthesis. To start an\n"
                    "asynchronous job, enter the name of an existing Amazon S3\n"
                    "bucket to use for speech synthesis output and click OK.",
                    parent=self.app)
                if bucket_name:
                    audio_stream, visemes = self.polly_wrapper.do_synthesis_task(
                        self.sayit_txt.get(1.0, tkinter.END),
                        self.engine_var.get(),
                        self.voice_choices[self.voice_var.get()],
                        'mp3',
                        bucket_name,
                        self.language_choices[self.language_var.get()],
                        True,
                        self.long_text_wait_callback)

        logger.debug("Visemes: %s.", json.dumps(visemes))

        if audio_stream is not None:
            with TemporaryDirectory() as tempdir:
                speech_file_name = tempdir + '/speech.mp3'
                with open(speech_file_name, 'wb') as speech_file:
                    speech_file.write(audio_stream.read())
                silence = '.media/silence.mp3'
                if not os.path.isdir('.media'):
                    silence = GITHUB_URL + silence
                # Play a short silent audio file to ensure playsound is loaded and
                # ready. Without this, the audio tends to lag behind viseme playback.
                playsound(silence)
                playsound(speech_file_name, block=False)
                start_time = time.time_ns() // 1000000
                self.app.after(
                    0, self.animate_lips, start_time, {'value': 'sil'}, iter(visemes))


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    PollyMouth(PollyWrapper(boto3.client('polly'), boto3.resource('s3')))
