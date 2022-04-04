from custom_labels_csv_to_manifest import check_duplicates, create_manifest_file
from os.path import exists
from os import remove
from os import path
import pytest

"""
Unit tests for custom_labels_csv_to_manifest.py.
"""

def clean_up(*argv):
    """
    Deletes supplied files, if they exist.
    Removes any previous test run files.
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
    Checks that the CSV file is valid.
    """

    file_name = path.splitext(csv_file)[0]
    manifest_file = f'{file_name}.manifest'
    duplicates_file = f'{file_name}-duplicates.csv'
    deduplicated_file = f'{file_name}-deduplicated.csv'
    
    clean_up(deduplicated_file, duplicates_file, manifest_file)

    assert check_duplicates(csv_file,deduplicated_file,duplicates_file) == result
    assert not exists(deduplicated_file)
    assert not exists(duplicates_file)
    
    clean_up(deduplicated_file, duplicates_file, manifest_file)



@pytest.mark.parametrize("csv_file,result",
    [
        ('test/test_csvs/test_dups_errors.csv', True)
    ]
)
def test_duplicates_errors(csv_file, result):
    """
    Checks that a CSV file with duplicate entries
    creates the deduplication and errors CSV file.
    """

    file_name = path.splitext(csv_file)[0]
    manifest_file = f'{file_name}.manifest'
    duplicates_file = f'{file_name}-duplicates.csv'
    deduplicated_file = f'{file_name}-deduplicated.csv'
  
    
    
    
    clean_up(deduplicated_file, duplicates_file,manifest_file)
    
    assert check_duplicates(csv_file,deduplicated_file,duplicates_file) == result
    assert exists(deduplicated_file)
    assert exists(duplicates_file)

    clean_up(deduplicated_file, duplicates_file,manifest_file)
    

@pytest.mark.parametrize("csv_file,img_count,anom_count",
    [
        ("test/test_csvs/test_no_s3.csv", 15,33)
    ]
)
def test_create_manifest_no_s3_supplied(csv_file, img_count, anom_count):
    """
    Checks that a CSV file with images and no Amazon S3 path creates
    a manifest file.
    """

    s3_path="s3://docexamplebucket1/circuitboard/train/"
    errors_file=f"{path.splitext(csv_file)[0]}_errors.csv"
    deduplicated_file = f"{path.splitext(csv_file)[0]}_deduplicated.csv"
    manifest_file = f"{path.splitext(csv_file)[0]}.manifest"
    
    clean_up(deduplicated_file, errors_file,manifest_file)
    
    image_count, anomalous_count = create_manifest_file(csv_file,
                manifest_file,
                s3_path)
    assert image_count == img_count
    assert anomalous_count == anom_count
    assert exists(manifest_file)
    assert not exists(deduplicated_file)
    assert not exists(errors_file)
    
    clean_up(deduplicated_file, errors_file,manifest_file)
    

@pytest.mark.parametrize("csv_file,img_count,lbl_count",
    [
        ('test/test_csvs/test_s3_supplied.csv', 15,33)
    ]
)
def test_create_manifest_s3_supplied(csv_file,img_count, lbl_count):
    """
    Checks that a CSV file with images and an Amazon S3 path creates
    a manifest file.
    """

    s3_path=""
    errors_file=f"{path.splitext(csv_file)[0]}_errors.csv"
    deduplicated_file = f"{path.splitext(csv_file)[0]}_deduplicated.csv"
    manifest_file = f"{path.splitext(csv_file)[0]}.manifest"
    
    clean_up(deduplicated_file, errors_file, manifest_file)
    
    image_count, label_count = create_manifest_file(csv_file,
                manifest_file,
                s3_path)
    assert image_count == img_count
    assert label_count ==  lbl_count
    assert exists(manifest_file)
    assert not exists(deduplicated_file)
    assert not exists(errors_file)
    
    clean_up(deduplicated_file, errors_file,manifest_file)
    