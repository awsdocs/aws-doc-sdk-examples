
from csv_to_manifest import check_errors, create_manifest_file
from os.path import exists
from os import remove
import pytest

"""
Unit tests for csv_to_manifest.py.
"""

def clean_up(*argv):
    """
    Deletes supplied files, if they exists
    Ensures that no previous test run files are present.
    """
    for arg in argv:
        if exists(arg):
            remove(arg)



@pytest.mark.parametrize("csv_file, result",
    [
        ('test/test_csvs/test_s3_supplied.csv', False)
    ]
)
def test_check_no_errors(csv_file, result):
    """
    Confirms that valid CSV checks OK
    """

    deduplicated_file=f"{csv_file}_deduplicated.csv"
    errors_file=f"{csv_file}_errors.csv"
    manifest_file = f"{csv_file}_manifest"
    
    clean_up(deduplicated_file, errors_file,manifest_file)

    assert check_errors(csv_file) == result
    assert not exists(deduplicated_file)
    assert not exists(errors_file)


@pytest.mark.parametrize("csv_file,result",
    [
        ('test/test_csvs/test_dups_errors.csv', True)
    ]
)
def test_check_errors(csv_file, result):
    """
    Checks that a CSV file with duplications and classification
    errors creates the deduplication and errors CSV file.
    """
    
    deduplicated_file=f"{csv_file}_deduplicated.csv"
    errors_file=f"{csv_file}_errors.csv"
    manifest_file = f"{csv_file}_manifest"
    
    clean_up(deduplicated_file, errors_file,manifest_file)
    
    assert check_errors(csv_file) == result
    assert exists(deduplicated_file)
    assert exists(errors_file)
    assert not exists(manifest_file)

    clean_up(deduplicated_file, errors_file,manifest_file)
    

@pytest.mark.parametrize("csv_file,img_count,anom_count",
    [
        ("test/test_csvs/test_s3_supplied.csv", 9,5)
    ]
)
def test_create_manifest_s3_supplied(csv_file, img_count, anom_count):
    """
    Checks that a CSV file with images + S3 path creates
    a manifest file.
    """

    s3_path="s3://docexamplebucket1/circuitboard/train/"
    deduplicated_file=f"{csv_file}_deduplicated.csv"
    errors_file=f"{csv_file}_errors.csv"
    manifest_file = f"{csv_file}_manifest"
    
    clean_up(deduplicated_file, errors_file,manifest_file)
    
    image_count, anomalous_count = create_manifest_file(csv_file,
                manifest_file,
                s3_path)
    assert image_count == img_count
    assert anomalous_count == anom_count
    assert exists(manifest_file)
    assert not exists(deduplicated_file)
    assert not exists(errors_file)
    

@pytest.mark.parametrize("csv_file,img_count,anom_count",
    [
        ('test/test_csvs/test_no_s3.csv', 7,4)
    ]
)
def test_create_manifest_no_s3_supplied(csv_file,img_count, anom_count):
    """
    Checks that a CSV file with images without S3 path creates
    a manifest file.
    """

    s3_path=""
    deduplicated_file=f"{csv_file}_deduplicated.csv"
    errors_file=f"{csv_file}_errors.csv"
    manifest_file = f"{csv_file}_manifest"
    
    clean_up(deduplicated_file, errors_file,manifest_file)
    
    image_count, anomalous_count = create_manifest_file(csv_file,
                manifest_file,
                s3_path)
    assert image_count == img_count
    assert anomalous_count ==  anom_count
    assert exists(manifest_file)
    assert not exists(deduplicated_file)
    assert not exists(errors_file)
    

