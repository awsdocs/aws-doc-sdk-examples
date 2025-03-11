" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_zcl_aws1_s3_actions DEFINITION DEFERRED.
CLASS zcl_aws1_s3_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_s3_actions.

CLASS ltc_zcl_aws1_s3_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.


    CLASS-DATA av_bucket         TYPE /aws1/s3_bucketname.
    CLASS-DATA av_bucket_create      TYPE /aws1/s3_bucketname.
    CLASS-DATA av_bucket_delete      TYPE /aws1/s3_bucketname.
    CLASS-DATA av_src_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_dest_bucket TYPE /aws1/s3_bucketname.

    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_s3_actions TYPE REF TO zcl_aws1_s3_actions.

    METHODS: create_bucket FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object FOR TESTING RAISING /aws1/cx_rt_generic,
      get_object FOR TESTING RAISING /aws1/cx_rt_generic,
      copy_object FOR TESTING RAISING /aws1/cx_rt_generic,
      list_objects FOR TESTING RAISING /aws1/cx_rt_generic,
      list_objects_v2 FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_object FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_bucket FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic zcx_aws1_ex_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic zcx_aws1_ex_generic.

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

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_s3_actions = NEW zcl_aws1_s3_actions( ).

    DATA(lv_acct) = ao_session->get_account_id( ).
    av_bucket = |sap-abap-s3-demo-bucket-{ lv_acct }|.
    av_src_bucket = |sap-abap-s3-demo-copy-object-src-bucket-{ lv_acct }|.
    av_dest_bucket = |sap-abap-s3-demo-copy-object-dst-bucket-{ lv_acct }|.
    av_bucket_delete = |sap-abap-s3-demo-bucket-delete-{ lv_acct }|.
    av_bucket_create = |sap-abap-s3-demo-bucket-create-{ lv_acct }|.
    ao_s3_actions->create_bucket( av_bucket ).
    ao_s3_actions->create_bucket( av_src_bucket ).
    ao_s3_actions->create_bucket( av_dest_bucket ).
    ao_s3_actions->create_bucket( av_bucket_delete ).


  ENDMETHOD.
  METHOD class_teardown.
    zcl_aws1_ex_utils=>cleanup_bucket( io_s3 = ao_s3
                                       iv_bucket = av_bucket ).
    zcl_aws1_ex_utils=>cleanup_bucket( io_s3 = ao_s3
                                       iv_bucket = av_bucket_create ).
    zcl_aws1_ex_utils=>cleanup_bucket( io_s3 = ao_s3
                                       iv_bucket = av_bucket_delete ).
    zcl_aws1_ex_utils=>cleanup_bucket( io_s3 = ao_s3
                                       iv_bucket = av_src_bucket ).
    zcl_aws1_ex_utils=>cleanup_bucket( io_s3 = ao_s3
                                       iv_bucket = av_dest_bucket ).
  ENDMETHOD.

  METHOD create_bucket.
    ao_s3_actions->create_bucket( av_bucket_create ).

    assert_bucket_exists(
      iv_bucket = av_bucket
      iv_exp = abap_true
      iv_msg = |Bucket { av_bucket_create } was not created| ).

  ENDMETHOD.
  METHOD put_object.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'put_object_ex_file'.
    create_file( cv_file ).

    ao_s3_actions->put_object(
        iv_bucket_name = av_bucket
        iv_file_name = cv_file ).

    cl_abap_unit_assert=>assert_equals(
        exp = get_file_data( iv_file = cv_file )
        act = ao_s3->getobject( iv_bucket = av_bucket iv_key = cv_file )->get_body( )
        msg = |Object { cv_file } did not match expected value| ).

    ao_s3->deleteobject( iv_bucket = av_bucket
                         iv_key = cv_file ).
    delete_file( cv_file ).

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
    /aws1/cl_rt_assert_abap=>assert_subrc( iv_exp = 0
                                           iv_msg = |Could not create { iv_file }| ).
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
      msg = |Could not delete { iv_file }| ).
  ENDMETHOD.
  METHOD get_object.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'get_object_ex_file'.
    DATA lo_result TYPE REF TO /aws1/cl_s3_getobjectoutput.
    create_file( cv_file ).

    put_file_in_bucket( iv_bucket = av_bucket
                        iv_file = cv_file ).


    ao_s3_actions->get_object(
      EXPORTING
        iv_bucket_name = av_bucket
        iv_object_key = cv_file
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_equals(
      exp = get_file_data( iv_file = cv_file )
      act = lo_result->get_body( )
      msg = |Object { cv_file } did not match expected value| ).

    ao_s3->deleteobject( iv_bucket = av_bucket
                         iv_key = cv_file ).
    delete_file( cv_file ).

  ENDMETHOD.
  METHOD put_file_in_bucket.
    ao_s3->putobject(
          iv_bucket = iv_bucket
          iv_key = iv_file
          iv_body = get_file_data( iv_file = iv_file ) ).
  ENDMETHOD.
  METHOD copy_object.
    CONSTANTS cv_src_file TYPE /aws1/s3_objectkey VALUE 'copy_object_ex_file'.
    CONSTANTS cv_dest_file TYPE /aws1/s3_objectkey VALUE 'copied_object_ex_file'.


    create_file( cv_src_file ).
    put_file_in_bucket( iv_bucket = av_src_bucket
                        iv_file = cv_src_file ).

    ao_s3_actions->copy_object(
      iv_dest_bucket = av_dest_bucket
        iv_dest_object = cv_dest_file
        iv_src_bucket  = av_src_bucket
        iv_src_object  = cv_src_file ).

    cl_abap_unit_assert=>assert_equals(
      exp = get_file_data( iv_file = cv_src_file )
      act = ao_s3->getobject( iv_bucket = av_dest_bucket iv_key = cv_dest_file )->get_body( )
      msg = |Object { cv_dest_file } did not match expected value| ).

    ao_s3->deleteobject( iv_bucket = av_src_bucket
                         iv_key = cv_src_file ).
    ao_s3->deletebucket( iv_bucket = av_src_bucket ).
    delete_file( cv_src_file ).

    ao_s3->deleteobject( iv_bucket = av_dest_bucket
                         iv_key = cv_dest_file ).
    ao_s3->deletebucket( iv_bucket = av_dest_bucket ).
    delete_file( cv_dest_file ).

  ENDMETHOD.
  METHOD list_objects.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'list_objects_ex_file1'.
    create_file( cv_file ).

    put_file_in_bucket( iv_bucket = av_bucket
                        iv_file = cv_file ).

    DATA lo_list TYPE REF TO /aws1/cl_s3_listobjectsoutput.
    ao_s3_actions->list_objects(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_list ).


    LOOP AT lo_list->get_contents( ) INTO DATA(lo_object).
      IF lo_object->get_key( ) = cv_file.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Could not find object { cv_file } in the list| ).

    ao_s3->deleteobject( iv_bucket = av_bucket
                         iv_key = cv_file ).
    delete_file( cv_file ).

  ENDMETHOD.

  METHOD list_objects_v2.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'list_objects_ex_file1'.
    create_file( cv_file ).

    put_file_in_bucket( iv_bucket = av_bucket
                        iv_file = cv_file ).

    DATA lo_list TYPE REF TO /aws1/cl_s3_listobjsv2output.
    ao_s3_actions->list_objects_v2(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_list ).


    LOOP AT lo_list->get_contents( ) INTO DATA(lo_object).
      IF lo_object->get_key( ) = cv_file.
        DATA(lv_found) = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Could not find object { cv_file } in the list| ).

    ao_s3->deleteobject( iv_bucket = av_bucket
                         iv_key = cv_file ).
    delete_file( cv_file ).

  ENDMETHOD.
  METHOD delete_object.
    CONSTANTS cv_file1 TYPE /aws1/s3_objectkey VALUE 'delete_object_ex_file1'.
    CONSTANTS cv_file2 TYPE /aws1/s3_objectkey VALUE 'delete_object_ex_file2'.
    create_file( cv_file1 ).
    create_file( cv_file2 ).

    put_file_in_bucket( iv_bucket = av_bucket
                        iv_file = cv_file1 ).
    put_file_in_bucket( iv_bucket = av_bucket
                        iv_file = cv_file2 ).

    ao_s3_actions->delete_object( iv_bucket_name = av_bucket
                                  iv_object_key = cv_file1 ).
    ao_s3_actions->delete_object( iv_bucket_name = av_bucket
                                  iv_object_key = cv_file2 ).

    DATA(lo_list) = ao_s3->listobjects( iv_bucket = av_bucket ).
    cl_abap_unit_assert=>assert_equals(
      exp = lines( lo_list->get_contents( ) )
      act = 0
      msg = |Could not delete all objects in bucket { av_bucket }| ).

    delete_file( cv_file1 ).
    delete_file( cv_file2 ).

  ENDMETHOD.
  METHOD delete_bucket.
    ao_s3_actions->delete_bucket( av_bucket_delete ).
    assert_bucket_exists(
      iv_exp = abap_false
      iv_bucket = av_bucket_delete
      iv_msg = |Bucket { av_bucket_delete } should have been deleted| ).


  ENDMETHOD.
  METHOD assert_bucket_exists.
    DATA(lv_found) = abap_true.
    TRY.
        ao_s3->headbucket( iv_bucket = av_bucket_delete ).
      CATCH /aws1/cx_s3_nosuchbucket INTO DATA(lo_ex).
        lv_found = abap_false.
      CATCH /aws1/cx_s3_clientexc INTO DATA(lo_ex2).
        IF lo_ex2->av_http_code = 404.
          lv_found = abap_false.
        ELSE.
          RAISE EXCEPTION lo_ex2.
        ENDIF.
    ENDTRY.
    cl_abap_unit_assert=>assert_equals(
      act = lv_found
      exp = iv_exp
      msg = iv_msg ).

  ENDMETHOD.

ENDCLASS.
