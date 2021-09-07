from testbook import testbook

@testbook('TextractAndComprehendNotebook.ipynb', execute=True)
def test_text_detection(tb):
    detect_func = tb.ref("process_text_detection")
    # sample images
    bucket = 'bucket-for-testing-textract'
    document = 'WholeFoodsReceipt.jpeg'
    res = detect_func(bucket, document)
    # Check if list returned
    print(res)
    assert len(res)

@testbook('TextractAndComprehendNotebook.ipynb', execute=True)
def test_entity_detection(tb):
    entity_func = tb.ref("entity_detection")
    # Samples list of entities
    entity_list = ["San Antonio", "Dallas is in the state of Texas", "Boston is in Massachusetts",
                   "the Metropolitan Museum of Art of is New York City ",
                   "the NASA headquarters is in our nation's capital"]
    res = entity_func(entity_list)
    print(res)
    # check if list returned
    assert len(res)

if __name__ == "__main__":
    test_text_detection()
    test_entity_detection()
