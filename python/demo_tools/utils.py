from alive_progress import alive_bar
import time


def run_progress_bar(title: str, expected_duration: int):
    """
    Runs a progress bar with a given title and expected duration.

    Args:
        title (str): The title to display on the progress bar.
        expected_duration (int): The expected duration (in seconds) for the task.
    """
    with alive_bar(1, title=title, manual=True) as bar:
        for i in range(1, expected_duration + 1):  # Iterate over the expected duration in seconds
            time.sleep(1)  # Simulate work by sleeping for 1 second
            bar(i / expected_duration)  # Update the progress bar with the current percentage
