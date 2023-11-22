from dataclasses import dataclass


@dataclass
class Snippet:
    id: str
    file: str
    line_start: int
    line_end: int
