from typing import Optional, Dict, Set, Tuple
from dataclasses import dataclass
import re

from .metadata_errors import ErrorsList, MetadataError


@dataclass
class EntityError(MetadataError):
    """
    Base error. Do not use directly.
    """

    entity: Optional[str] = None

    def message(self) -> str:
        return ""


@dataclass
class MissingEntityError(EntityError):
    def message(self):
        return f"{self.entity} not found."


class EntityErrors(ErrorsList[EntityError]):
    pass


def expand_all_entities(
    text: str, entity_map: Dict[str, str]
) -> Tuple[str, EntityErrors]:
    errors = EntityErrors()
    entities = find_all_entities(text)

    for entity in entities:
        expanded, error = expand_entity(entity, entity_map)
        if error:
            errors.append(error)
        else:
            text = text.replace(entity, expanded)

    return text, errors


def find_all_entities(text: str) -> Set[str]:
    return set(re.findall(r"&[\dA-Za-z-_]+;", text))


def expand_entity(
    entity: str, entity_map: Dict[str, str]
) -> Tuple[str, Optional[EntityError]]:
    expanded = entity_map.get(entity)
    if expanded is not None:
        return entity.replace(entity, expanded), None
    else:
        return entity, MissingEntityError(entity=entity)
