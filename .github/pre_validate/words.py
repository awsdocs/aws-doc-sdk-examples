from urllib.request import urlopen
import json
GOODWORDS={"throat", "dummy", "dp"}
DATA=urlopen("https://raw.githubusercontent.com/zacanger/profane-words/5ad6c62fa5228293bc610602eae475d50036dac2/words.json")
WORDS=set(json.load(DATA)).difference(GOODWORDS)