" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS zcl_aws1_s3_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_bucket
      IMPORTING
        !iv_bucket_name TYPE /aws1/s3_bucketname .
    METHODS put_object
      IMPORTING
        !iv_bucket_name TYPE /aws1/s3_bucketname
        !iv_file_name   TYPE /aws1/s3_objectkey .
    METHODS get_object
      IMPORTING
        !iv_bucket_name TYPE /aws1/s3_bucketname
        !iv_object_key  TYPE /aws1/s3_objectkey
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_s3_getobjectoutput .
    METHODS copy_object
      IMPORTING
        !iv_dest_bucket TYPE /aws1/s3_bucketname
        !iv_dest_object TYPE /aws1/s3_objectkey
        !iv_src_bucket  TYPE /aws1/s3_bucketname
        !iv_src_object  TYPE /aws1/s3_objectkey .
    METHODS list_objects
      IMPORTING
        !iv_bucket_name TYPE /aws1/s3_bucketname
      EXPORTING
        !oo_result      TYPE REF TO /aws1/cl_s3_listobjectsoutput .
    METHODS delete_object
      IMPORTING
        !iv_bucket_name TYPE /aws1/s3_bucketname
        !iv_object_key  TYPE /aws1/s3_objectkey .
    METHODS delete_bucket
      IMPORTING
        !iv_bucket_name TYPE /aws1/s3_bucketname .
protected section.
private section.
ENDCLASS.



CLASS ZCL_AWS1_S3_ACTIONS IMPLEMENTATION.


  METHOD copy_object.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.copy_object]
    TRY.
        lo_s3->copyobject(
          iv_bucket = iv_dest_bucket
          iv_key = iv_dest_object
          iv_copysource = |{ iv_src_bucket }/{ iv_src_object }|
        ).
        MESSAGE 'Object copied to another bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.copy_object]
  ENDMETHOD.


  METHOD create_bucket.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    " snippet-start:[s3.abapv1.create_bucket]
    TRY.
        lo_s3->createbucket(
            iv_bucket = iv_bucket_name
        ).
        MESSAGE 'S3 bucket created.' TYPE 'I'.
      CATCH /aws1/cx_s3_bucketalrdyexists.
        MESSAGE 'Bucket name already exists.' TYPE 'E'.
      CATCH /aws1/cx_s3_bktalrdyownedbyyou.
        MESSAGE 'Bucket already exists and is owned by you.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[s3.abapv1.create_bucket]
  ENDMETHOD.


  METHOD delete_bucket.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.delete_bucket]
    TRY.

        lo_s3->deletebucket(
            iv_bucket = iv_bucket_name
        ).
        MESSAGE 'Deleted S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.delete_bucket]

  ENDMETHOD.


  METHOD delete_object.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.delete_object]
    TRY.
        lo_s3->deleteobject(
            iv_bucket = iv_bucket_name
            iv_key = iv_object_key
        ).
        MESSAGE 'Object deleted from S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.delete_object]
  ENDMETHOD.


  METHOD get_object.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.get_object]
    TRY.
        oo_result = lo_s3->getobject(           " oo_result is returned for testing purposes. "
                  iv_bucket = iv_bucket_name
                  iv_key = iv_object_key
               ).
        DATA(lv_object_data) = oo_result->get_body( ).
        MESSAGE 'Object retrieved from S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
      CATCH /aws1/cx_s3_nosuchkey.
        MESSAGE 'Object key does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.get_object]

  ENDMETHOD.


  METHOD list_objects.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.list_objects]
    TRY.
        oo_result = lo_s3->listobjects(         " oo_result is returned for testing purposes. "
          iv_bucket = iv_bucket_name
        ).
        MESSAGE 'Retrieved list of objects in S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.
    "snippet-end:[s3.abapv1.list_objects]
  ENDMETHOD.


  METHOD put_object.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( lo_session ).

    "snippet-start:[s3.abapv1.put_object]

    "Get contents of file from application server."
    DATA lv_body TYPE xstring.
    OPEN DATASET iv_file_name FOR INPUT IN BINARY MODE.
    READ DATASET iv_file_name INTO lv_body.
    CLOSE DATASET iv_file_name.

    "Upload/put an object to an S3 bucket."
    TRY.
        lo_s3->putobject(
            iv_bucket = iv_bucket_name
            iv_key = iv_file_name
            iv_body = lv_body
        ).
        MESSAGE 'Object uploaded to S3 bucket.' TYPE 'I'.
      CATCH /aws1/cx_s3_nosuchbucket.
        MESSAGE 'Bucket does not exist.' TYPE 'E'.
    ENDTRY.

    "snippet-end:[s3.abapv1.put_object]
  ENDMETHOD.
ENDCLASS.
