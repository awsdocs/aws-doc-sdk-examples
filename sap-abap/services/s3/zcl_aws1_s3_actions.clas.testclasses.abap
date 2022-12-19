" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_s3_actions DEFINITION DEFERRED.
CLASS zcl_aws1_s3_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_s3_actions.

CLASS ltc_zcl_aws1_s3_actions DEFINITION FOR TESTING  DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_s3_actions TYPE REF TO zcl_aws1_s3_actions.

    METHODS: create_bucket FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object FOR TESTING RAISING /aws1/cx_rt_generic,
      get_object FOR TESTING RAISING /aws1/cx_rt_generic,
      copy_object FOR TESTING RAISING /aws1/cx_rt_generic,
      list_objects FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_object FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_bucket FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.

    METHODS assert_bucket_exists
      IMPORTING
                iv_bucket TYPE /aws1/s3_bucketname
                iv_exp    TYPE abap_bool
                iv_msg    TYPE string
      RAISING   /aws1/cx_rt_generic.
    METHODS create_file IMPORTING iv_file TYPE /aws1/s3_objectkey.
    METHODS get_file_data
      IMPORTING
        iv_file             TYPE /aws1/s3_objectkey
      RETURNING
        VALUE(ov_file_data) TYPE /aws1/s3_streamingblob.
    METHODS delete_file IMPORTING iv_file TYPE /aws1/s3_objectkey.
    METHODS put_file_in_bucket
      IMPORTING
        iv_bucket TYPE /aws1/s3_bucketname
        iv_file   TYPE /aws1/s3_objectkey.

ENDCLASS.

CLASS ltc_zcl_aws1_s3_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_s3_actions = NEW zcl_aws1_s3_actions( ).
  ENDMETHOD.
  METHOD create_bucket.
    CONSTANTS cv_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-create-bucket'.
    ao_s3_actions->create_bucket( iv_bucket_name = cv_bucket ).

    assert_bucket_exists(
      iv_bucket = cv_bucket
      iv_exp = abap_true
      iv_msg = |Bucket { cv_bucket } was not created|
    ).

    ao_s3->deletebucket( iv_bucket = cv_bucket ).

  ENDMETHOD.
  METHOD put_object.

    CONSTANTS cv_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-put-object'.
    ao_s3->createbucket( iv_bucket = cv_bucket ).

    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'put_object_ex_file'.
    create_file( iv_file = cv_file ).

    ao_s3_actions->put_object(
        iv_bucket_name = cv_bucket
        iv_file_name = cv_file
     ).

    cl_abap_unit_assert=>assert_equals(
        exp = get_file_data( iv_file = cv_file )
        act = ao_s3->getobject( iv_bucket = cv_bucket iv_key = cv_file )->get_body( )
        msg = |Object { cv_file } did not match expected value|
    ).

    ao_s3->deleteobject( iv_bucket = cv_bucket iv_key = cv_file ).
    ao_s3->deletebucket( iv_bucket = cv_bucket ).
    delete_file( iv_file = cv_file ).

  ENDMETHOD.
  METHOD create_file.
    DATA lv_param TYPE btcxpgpar.
    lv_param = |if=/dev/random of={ iv_file } bs=1M count=1 iflag=fullblock|.
    CALL FUNCTION 'SXPG_COMMAND_EXECUTE'
      EXPORTING
        commandname           = 'DB24DD'
        additional_parameters = lv_param
        operatingsystem       = 'ANYOS'
      EXCEPTIONS
        OTHERS                = 15.
    /aws1/cl_rt_assert_abap=>assert_subrc( iv_exp = 0 iv_msg = |Could not create { iv_file }| ).
  ENDMETHOD.
  METHOD get_file_data.
    "Get file content.
    OPEN DATASET iv_file FOR INPUT IN BINARY MODE.
    READ DATASET iv_file INTO ov_file_data.
    CLOSE DATASET iv_file.
  ENDMETHOD.
  METHOD delete_file.
    DELETE DATASET iv_file.
    cl_abap_unit_assert=>assert_equals(
      exp = sy-subrc
      act = 0
      msg = |Could not delete { iv_file }|
     ).
  ENDMETHOD.
  METHOD get_object.

    CONSTANTS cv_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-get-object'.
    ao_s3->createbucket( iv_bucket = cv_bucket ).

    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'get_object_ex_file'.
    create_file( iv_file = cv_file ).

    put_file_in_bucket( iv_bucket = cv_bucket iv_file = cv_file ).

    DATA lo_result TYPE REF TO /aws1/cl_s3_getobjectoutput.
    ao_s3_actions->get_object(
      EXPORTING
        iv_bucket_name = cv_bucket
        iv_object_key = cv_file
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = get_file_data( iv_file = cv_file )
      act = lo_result->get_body( )
      msg = |Object { cv_file } did not match expected value|
    ).

    ao_s3->deleteobject( iv_bucket = cv_bucket iv_key = cv_file ).
    ao_s3->deletebucket( iv_bucket = cv_bucket ).
    delete_file( iv_file = cv_file ).

  ENDMETHOD.
  METHOD put_file_in_bucket.
    ao_s3->putobject(
          iv_bucket = iv_bucket
          iv_key = iv_file
          iv_body = get_file_data( iv_file = iv_file )
        ).
  ENDMETHOD.
  METHOD copy_object.
    CONSTANTS cv_src_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-copy-object-src-bucket'.
    ao_s3->createbucket( iv_bucket = cv_src_bucket ).
    CONSTANTS cv_dest_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-copy-object-dest-bucket'.
    ao_s3->createbucket( iv_bucket = cv_dest_bucket ).

    CONSTANTS cv_src_file TYPE /aws1/s3_objectkey VALUE 'copy_object_ex_file'.
    CONSTANTS cv_dest_file TYPE /aws1/s3_objectkey VALUE 'copied_object_ex_file'.
    create_file( iv_file = cv_src_file ).
    put_file_in_bucket( iv_bucket = cv_src_bucket iv_file = cv_src_file ).

    ao_s3_actions->copy_object(
      EXPORTING
        iv_dest_bucket = cv_dest_bucket
        iv_dest_object = cv_dest_file
        iv_src_bucket  = cv_src_bucket
        iv_src_object  = cv_src_file
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = get_file_data( iv_file = cv_src_file )
      act = ao_s3->getobject( iv_bucket = cv_dest_bucket iv_key = cv_dest_file )->get_body( )
      msg = |Object { cv_dest_file } did not match expected value|
    ).

    ao_s3->deleteobject( iv_bucket = cv_src_bucket iv_key = cv_src_file ).
    ao_s3->deletebucket( iv_bucket = cv_src_bucket ).
    delete_file( iv_file = cv_src_file ).

    ao_s3->deleteobject( iv_bucket = cv_dest_bucket iv_key = cv_dest_file ).
    ao_s3->deletebucket( iv_bucket = cv_dest_bucket ).
    delete_file( iv_file = cv_dest_file ).

  ENDMETHOD.
  METHOD list_objects.

    CONSTANTS cv_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-list-objects'.
    ao_s3->createbucket( iv_bucket = cv_bucket ).

    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'list_objects_ex_file1'.
    create_file( iv_file = cv_file ).

    put_file_in_bucket( iv_bucket = cv_bucket iv_file = cv_file ).

    DATA lo_list TYPE REF TO /aws1/cl_s3_listobjectsoutput.
    ao_s3_actions->list_objects(
      EXPORTING
        iv_bucket_name = cv_bucket
      IMPORTING
        oo_result = lo_list
    ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_list->get_contents( ) INTO DATA(lo_object).
      IF lo_object->get_key( ) = cv_file.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Could not find object { cv_file } in the list|
    ).

    ao_s3->deleteobject( iv_bucket = cv_bucket iv_key = cv_file ).
    ao_s3->deletebucket( iv_bucket = cv_bucket ).
    delete_file( iv_file = cv_file ).

  ENDMETHOD.
  METHOD delete_object.
    CONSTANTS cv_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-delete-object'.
    ao_s3->createbucket( iv_bucket = cv_bucket ).

    CONSTANTS cv_file1 TYPE /aws1/s3_objectkey VALUE 'delete_object_ex_file1'.
    CONSTANTS cv_file2 TYPE /aws1/s3_objectkey VALUE 'delete_object_ex_file2'.
    create_file( iv_file = cv_file1 ).
    create_file( iv_file = cv_file2 ).

    put_file_in_bucket( iv_bucket = cv_bucket iv_file = cv_file1 ).
    put_file_in_bucket( iv_bucket = cv_bucket iv_file = cv_file2 ).

    ao_s3_actions->delete_object( iv_bucket_name =  cv_bucket iv_object_key = cv_file1 ).
    ao_s3_actions->delete_object( iv_bucket_name =  cv_bucket iv_object_key = cv_file2 ).

    DATA(lo_list) =  ao_s3->listobjects( iv_bucket = cv_bucket ).
    cl_abap_unit_assert=>assert_equals(
      exp = lines( lo_list->get_contents( ) )
      act = 0
      msg = |Could not delete all objects in bucket { cv_bucket }|
     ).

    ao_s3->deletebucket( iv_bucket = cv_bucket ).
    delete_file( iv_file = cv_file1 ).
    delete_file( iv_file = cv_file2 ).

  ENDMETHOD.
  METHOD delete_bucket.
    CONSTANTS cv_bucket TYPE /aws1/s3_bucketname VALUE 'code-example-delete-bucket'.
    ao_s3->createbucket( iv_bucket = cv_bucket ).
    ao_s3_actions->delete_bucket( iv_bucket_name =  cv_bucket ).

    assert_bucket_exists(
      iv_bucket = cv_bucket
      iv_exp = abap_false
      iv_msg = |Bucket { cv_bucket } should have been deleted|
    ).

  ENDMETHOD.
  METHOD assert_bucket_exists.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_s3->listbuckets( )->get_buckets( ) INTO DATA(lo_bucket).
      IF lo_bucket->get_name( ) = iv_bucket.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_found
      msg = iv_msg
    ).
  ENDMETHOD.
ENDCLASS.
