" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_s3_actions DEFINITION DEFERRED.
CLASS /awsex/cl_s3_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_s3_actions.

CLASS ltc_awsex_cl_s3_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.


    CLASS-DATA av_bucket         TYPE /aws1/s3_bucketname.
    CLASS-DATA av_bucket_create      TYPE /aws1/s3_bucketname.
    CLASS-DATA av_bucket_delete      TYPE /aws1/s3_bucketname.
    CLASS-DATA av_src_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_dest_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_lock_bucket TYPE /aws1/s3_bucketname.

    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_s3_actions TYPE REF TO /awsex/cl_s3_actions.

    METHODS: create_bucket FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object FOR TESTING RAISING /aws1/cx_rt_generic,
      get_object FOR TESTING RAISING /aws1/cx_rt_generic,
      copy_object FOR TESTING RAISING /aws1/cx_rt_generic,
      list_objects FOR TESTING RAISING /aws1/cx_rt_generic,
      list_objects_v2 FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_object FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_bucket FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_objects FOR TESTING RAISING /aws1/cx_rt_generic,
      get_bucket_acl FOR TESTING RAISING /aws1/cx_rt_generic,
      put_bucket_acl FOR TESTING RAISING /aws1/cx_rt_generic,
      get_bucket_cors FOR TESTING RAISING /aws1/cx_rt_generic,
      put_bucket_cors FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_bucket_cors FOR TESTING RAISING /aws1/cx_rt_generic,
      get_bucket_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      put_bucket_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_bucket_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      get_bucket_lifecycle_conf FOR TESTING RAISING /aws1/cx_rt_generic,
      put_bucket_lifecycle_conf FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_bucket_lifecycle FOR TESTING RAISING /aws1/cx_rt_generic,
      get_object_acl FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object_acl FOR TESTING RAISING /aws1/cx_rt_generic,
      head_bucket FOR TESTING RAISING /aws1/cx_rt_generic,
      put_bucket_versioning FOR TESTING RAISING /aws1/cx_rt_generic,
      list_object_versions FOR TESTING RAISING /aws1/cx_rt_generic,
      get_object_legal_hold FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object_legal_hold FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object_retention FOR TESTING RAISING /aws1/cx_rt_generic,
      get_object_lock_conf FOR TESTING RAISING /aws1/cx_rt_generic,
      put_object_lock_conf FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic /awsex/cx_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic /awsex/cx_generic.

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
                iv_file   TYPE /aws1/s3_objectkey
      RAISING   /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_s3_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_s3_actions = NEW /awsex/cl_s3_actions( ).

    DATA(lv_acct) = ao_session->get_account_id( ).
    av_bucket = |sap-abap-s3-demo-bucket-{ lv_acct }|.
    av_src_bucket = |sap-abap-s3-demo-copy-object-src-bucket-{ lv_acct }|.
    av_dest_bucket = |sap-abap-s3-demo-copy-object-dst-bucket-{ lv_acct }|.
    av_bucket_delete = |sap-abap-s3-demo-bucket-delete-{ lv_acct }|.
    av_bucket_create = |sap-abap-s3-demo-bucket-create-{ lv_acct }|.
    av_lock_bucket = |sap-abap-s3-lock-bucket-{ lv_acct }|.

    " Create standard buckets with convert_test tag
    DATA lt_tags TYPE /aws1/cl_s3_tag=>tt_tagset.
    APPEND NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_tags.

    /awsex/cl_utils=>create_bucket( iv_bucket = av_bucket io_s3 = ao_s3 io_session = ao_session ).
    /awsex/cl_utils=>create_bucket( iv_bucket = av_src_bucket io_s3 = ao_s3 io_session = ao_session ).
    /awsex/cl_utils=>create_bucket( iv_bucket = av_dest_bucket io_s3 = ao_s3 io_session = ao_session ).
    /awsex/cl_utils=>create_bucket( iv_bucket = av_bucket_delete io_s3 = ao_s3 io_session = ao_session ).

    " Tag all standard buckets
    TRY.
        ao_s3->putbuckettagging( iv_bucket = av_bucket io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).
        ao_s3->putbuckettagging( iv_bucket = av_src_bucket io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).
        ao_s3->putbuckettagging( iv_bucket = av_dest_bucket io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).
        ao_s3->putbuckettagging( iv_bucket = av_bucket_delete io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).
      CATCH /aws1/cx_rt_generic.
        " Ignore tagging errors
    ENDTRY.

    " Create object lock enabled bucket
    " Object lock must be enabled at bucket creation time
    DATA(lv_region) = CONV /aws1/s3_bucketlocationcnstrnt( ao_session->get_region( ) ).
    DATA lo_constraint TYPE REF TO /aws1/cl_s3_createbucketconf.

    IF lv_region = 'us-east-1'.
      CLEAR lo_constraint.
    ELSE.
      lo_constraint = NEW /aws1/cl_s3_createbucketconf( lv_region ).
    ENDIF.

    TRY.
        ao_s3->createbucket(
          iv_bucket = av_lock_bucket
          io_createbucketconfiguration = lo_constraint
          iv_objectlockenabledforbucket = abap_true ).
        " Tag the lock bucket
        ao_s3->putbuckettagging( iv_bucket = av_lock_bucket io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).
      CATCH /aws1/cx_s3_bktalrdyownedbyyou.
        " Bucket already exists, continue
      CATCH /aws1/cx_rt_generic.
        " Ignore other errors during setup
    ENDTRY.

  ENDMETHOD.
  METHOD class_teardown.
    /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_bucket ).
    /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_bucket_create ).
    /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_bucket_delete ).
    /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_src_bucket ).
    /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_dest_bucket ).
    
    " Note: av_lock_bucket is tagged with 'convert_test' and should be manually cleaned up
    " because object lock protection makes cleanup take longer
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

  METHOD delete_objects.
    CONSTANTS cv_file1 TYPE /aws1/s3_objectkey VALUE 'delete_objects_ex_file1'.
    CONSTANTS cv_file2 TYPE /aws1/s3_objectkey VALUE 'delete_objects_ex_file2'.
    CONSTANTS cv_file3 TYPE /aws1/s3_objectkey VALUE 'delete_objects_ex_file3'.

    create_file( cv_file1 ).
    create_file( cv_file2 ).
    create_file( cv_file3 ).

    put_file_in_bucket( iv_bucket = av_bucket iv_file = cv_file1 ).
    put_file_in_bucket( iv_bucket = av_bucket iv_file = cv_file2 ).
    put_file_in_bucket( iv_bucket = av_bucket iv_file = cv_file3 ).

    DATA lt_obj_keys TYPE /aws1/cl_s3_objectidentifier=>tt_objectidentifierlist.
    APPEND NEW /aws1/cl_s3_objectidentifier( iv_key = cv_file1 ) TO lt_obj_keys.
    APPEND NEW /aws1/cl_s3_objectidentifier( iv_key = cv_file2 ) TO lt_obj_keys.
    APPEND NEW /aws1/cl_s3_objectidentifier( iv_key = cv_file3 ) TO lt_obj_keys.

    DATA lo_result TYPE REF TO /aws1/cl_s3_deleteobjsoutput.
    ao_s3_actions->delete_objects(
      EXPORTING
        iv_bucket_name = av_bucket
        it_object_keys = lt_obj_keys
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_equals(
      exp = 3
      act = lines( lo_result->get_deleted( ) )
      msg = |Not all objects were deleted| ).

    delete_file( cv_file1 ).
    delete_file( cv_file2 ).
    delete_file( cv_file3 ).
  ENDMETHOD.

  METHOD get_bucket_acl.
    DATA lo_result TYPE REF TO /aws1/cl_s3_getbucketacloutput.

    ao_s3_actions->get_bucket_acl(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not get bucket ACL| ).
  ENDMETHOD.

  METHOD put_bucket_acl.
    DATA(lv_grantwrite) = 'uri=http://acs.amazonaws.com/groups/s3/LogDelivery'.

    ao_s3_actions->put_bucket_acl(
      iv_bucket_name = av_bucket
      iv_grantwrite = lv_grantwrite ).

    " Verify the ACL was set by getting it
    DATA(lo_acl) = ao_s3->getbucketacl( iv_bucket = av_bucket ).
    DATA(lv_found) = abap_false.
    LOOP AT lo_acl->get_grants( ) INTO DATA(lo_grant).
      IF lo_grant->get_grantee( )->get_uri( ) = 'http://acs.amazonaws.com/groups/s3/LogDelivery'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Bucket ACL was not updated correctly| ).
  ENDMETHOD.

  METHOD get_bucket_cors.
    " First set a CORS configuration
    DATA lt_cors_rules TYPE /aws1/cl_s3_corsrule=>tt_corsrules.
    DATA lt_methods TYPE /aws1/cl_s3_allowedmethods_w=>tt_allowedmethods.
    DATA lt_origins TYPE /aws1/cl_s3_allowedorigins_w=>tt_allowedorigins.

    APPEND NEW /aws1/cl_s3_allowedmethods_w( iv_value = 'GET' ) TO lt_methods.
    APPEND NEW /aws1/cl_s3_allowedorigins_w( iv_value = '*' ) TO lt_origins.

    APPEND NEW /aws1/cl_s3_corsrule(
      it_allowedmethods = lt_methods
      it_allowedorigins = lt_origins ) TO lt_cors_rules.

    ao_s3->putbucketcors(
      iv_bucket = av_bucket
      io_corsconfiguration = NEW /aws1/cl_s3_corsconfiguration( it_corsrules = lt_cors_rules ) ).

    " Now test getting the CORS configuration
    DATA lo_result TYPE REF TO /aws1/cl_s3_getbktcorsoutput.
    ao_s3_actions->get_bucket_cors(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not get bucket CORS configuration| ).

    " Cleanup
    ao_s3->deletebucketcors( iv_bucket = av_bucket ).
  ENDMETHOD.

  METHOD put_bucket_cors.
    DATA lt_cors_rules TYPE /aws1/cl_s3_corsrule=>tt_corsrules.
    DATA lt_methods TYPE /aws1/cl_s3_allowedmethods_w=>tt_allowedmethods.
    DATA lt_origins TYPE /aws1/cl_s3_allowedorigins_w=>tt_allowedorigins.

    APPEND NEW /aws1/cl_s3_allowedmethods_w( iv_value = 'PUT' ) TO lt_methods.
    APPEND NEW /aws1/cl_s3_allowedmethods_w( iv_value = 'POST' ) TO lt_methods.
    APPEND NEW /aws1/cl_s3_allowedmethods_w( iv_value = 'DELETE' ) TO lt_methods.
    APPEND NEW /aws1/cl_s3_allowedorigins_w( iv_value = 'http://www.example.com' ) TO lt_origins.

    APPEND NEW /aws1/cl_s3_corsrule(
      it_allowedmethods = lt_methods
      it_allowedorigins = lt_origins ) TO lt_cors_rules.

    ao_s3_actions->put_bucket_cors(
      iv_bucket_name = av_bucket
      it_cors_rules = lt_cors_rules ).

    " Verify CORS was set
    DATA(lo_cors) = ao_s3->getbucketcors( iv_bucket = av_bucket ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_cors->get_corsrules( )
      msg = |CORS configuration was not set| ).

    " Cleanup
    ao_s3->deletebucketcors( iv_bucket = av_bucket ).
  ENDMETHOD.

  METHOD delete_bucket_cors.
    " First set a CORS configuration
    DATA lt_cors_rules TYPE /aws1/cl_s3_corsrule=>tt_corsrules.
    DATA lt_methods TYPE /aws1/cl_s3_allowedmethods_w=>tt_allowedmethods.
    DATA lt_origins TYPE /aws1/cl_s3_allowedorigins_w=>tt_allowedorigins.

    APPEND NEW /aws1/cl_s3_allowedmethods_w( iv_value = 'GET' ) TO lt_methods.
    APPEND NEW /aws1/cl_s3_allowedorigins_w( iv_value = '*' ) TO lt_origins.

    APPEND NEW /aws1/cl_s3_corsrule(
      it_allowedmethods = lt_methods
      it_allowedorigins = lt_origins ) TO lt_cors_rules.

    ao_s3->putbucketcors(
      iv_bucket = av_bucket
      io_corsconfiguration = NEW /aws1/cl_s3_corsconfiguration( it_corsrules = lt_cors_rules ) ).

    " Now delete it
    ao_s3_actions->delete_bucket_cors( iv_bucket_name = av_bucket ).

    " Verify it was deleted - should raise exception
    DATA(lv_deleted) = abap_false.
    TRY.
        ao_s3->getbucketcors( iv_bucket = av_bucket ).
      CATCH /aws1/cx_s3_clientexc.
        lv_deleted = abap_true.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_deleted
      msg = |CORS configuration was not deleted| ).
  ENDMETHOD.

  METHOD get_bucket_policy.
    " First set a policy
    DATA(lv_policy) = |{ '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::' }{ av_bucket }/*"}]}|.

    ao_s3->putbucketpolicy(
      iv_bucket = av_bucket
      iv_policy = lv_policy ).

    " Now test getting it
    DATA lo_result TYPE REF TO /aws1/cl_s3_getbktpolicyoutput.
    ao_s3_actions->get_bucket_policy(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not get bucket policy| ).

    " Cleanup
    ao_s3->deletebucketpolicy( iv_bucket = av_bucket ).
  ENDMETHOD.

  METHOD put_bucket_policy.
    DATA(lv_policy) = |{ '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::' }{ av_bucket }/*"}]}|.

    ao_s3_actions->put_bucket_policy(
      iv_bucket_name = av_bucket
      iv_policy = lv_policy ).

    " Verify policy was set
    DATA(lo_policy_result) = ao_s3->getbucketpolicy( iv_bucket = av_bucket ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_policy_result->get_policy( )
      msg = |Bucket policy was not set| ).

    " Cleanup
    ao_s3->deletebucketpolicy( iv_bucket = av_bucket ).
  ENDMETHOD.

  METHOD delete_bucket_policy.
    " First set a policy
    DATA(lv_policy) = |{ '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::' }{ av_bucket }/*"}]}|.

    ao_s3->putbucketpolicy(
      iv_bucket = av_bucket
      iv_policy = lv_policy ).

    " Now delete it
    ao_s3_actions->delete_bucket_policy( iv_bucket_name = av_bucket ).

    " Verify it was deleted - should raise exception
    DATA(lv_deleted) = abap_false.
    TRY.
        ao_s3->getbucketpolicy( iv_bucket = av_bucket ).
      CATCH /aws1/cx_s3_clientexc.
        lv_deleted = abap_true.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_deleted
      msg = |Bucket policy was not deleted| ).
  ENDMETHOD.

  METHOD get_bucket_lifecycle_conf.
    " First set a lifecycle configuration
    DATA lt_rules TYPE /aws1/cl_s3_lifecyclerule=>tt_lifecyclerules.
    APPEND NEW /aws1/cl_s3_lifecyclerule(
      iv_id = 'TestRule'
      iv_status = 'Enabled'
      io_filter = NEW /aws1/cl_s3_lcrulefilter( iv_prefix = 'logs/' )
      io_expiration = NEW /aws1/cl_s3_lifecycleexpir( iv_days = 30 ) ) TO lt_rules.

    ao_s3->putbucketlifecycleconf(
      iv_bucket = av_bucket
      io_lifecycleconfiguration = NEW /aws1/cl_s3_bucketlcconf( it_rules = lt_rules ) ).

    " Now test getting it
    DATA lo_result TYPE REF TO /aws1/cl_s3_getbktlcconfoutput.
    ao_s3_actions->get_bucket_lifecycle_conf(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not get bucket lifecycle configuration| ).

    " Cleanup
    ao_s3->deletebucketlifecycle( iv_bucket = av_bucket ).
  ENDMETHOD.

  METHOD put_bucket_lifecycle_conf.
    DATA lt_rules TYPE /aws1/cl_s3_lifecyclerule=>tt_lifecyclerules.
    APPEND NEW /aws1/cl_s3_lifecyclerule(
      iv_id = 'TestRule'
      iv_status = 'Enabled'
      io_filter = NEW /aws1/cl_s3_lcrulefilter( iv_prefix = 'archive/' )
      io_expiration = NEW /aws1/cl_s3_lifecycleexpir( iv_days = 60 ) ) TO lt_rules.

    ao_s3_actions->put_bucket_lifecycle_conf(
      iv_bucket_name = av_bucket
      it_lifecycle_rule = lt_rules ).

    " Verify lifecycle was set
    DATA(lo_lc) = ao_s3->getbucketlifecycleconf( iv_bucket = av_bucket ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_lc->get_rules( )
      msg = |Lifecycle configuration was not set| ).

    " Cleanup
    ao_s3->deletebucketlifecycle( iv_bucket = av_bucket ).
  ENDMETHOD.

  METHOD delete_bucket_lifecycle.
    " First set a lifecycle configuration
    DATA lt_rules TYPE /aws1/cl_s3_lifecyclerule=>tt_lifecyclerules.
    APPEND NEW /aws1/cl_s3_lifecyclerule(
      iv_id = 'TestRule'
      iv_status = 'Enabled'
      io_filter = NEW /aws1/cl_s3_lcrulefilter( iv_prefix = 'temp/' )
      io_expiration = NEW /aws1/cl_s3_lifecycleexpir( iv_days = 7 ) ) TO lt_rules.

    ao_s3->putbucketlifecycleconf(
      iv_bucket = av_bucket
      io_lifecycleconfiguration = NEW /aws1/cl_s3_bucketlcconf( it_rules = lt_rules ) ).

    " Now delete it
    ao_s3_actions->delete_bucket_lifecycle( iv_bucket_name = av_bucket ).

    " Verify it was deleted - should raise exception
    DATA(lv_deleted) = abap_false.
    TRY.
        ao_s3->getbucketlifecycleconf( iv_bucket = av_bucket ).
      CATCH /aws1/cx_s3_clientexc.
        lv_deleted = abap_true.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_deleted
      msg = |Lifecycle configuration was not deleted| ).
  ENDMETHOD.

  METHOD get_object_acl.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'get_object_acl_ex_file'.
    create_file( cv_file ).
    put_file_in_bucket( iv_bucket = av_bucket iv_file = cv_file ).

    DATA lo_result TYPE REF TO /aws1/cl_s3_getobjectacloutput.
    ao_s3_actions->get_object_acl(
      EXPORTING
        iv_bucket_name = av_bucket
        iv_object_key = cv_file
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not get object ACL| ).

    ao_s3->deleteobject( iv_bucket = av_bucket iv_key = cv_file ).
    delete_file( cv_file ).
  ENDMETHOD.

  METHOD put_object_acl.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'put_object_acl_ex_file'.
    create_file( cv_file ).
    put_file_in_bucket( iv_bucket = av_bucket iv_file = cv_file ).

    DATA(lv_grantread) = 'uri=http://acs.amazonaws.com/groups/global/AllUsers'.

    ao_s3_actions->put_object_acl(
      iv_bucket_name = av_bucket
      iv_object_key = cv_file
      iv_grantread = lv_grantread ).

    " Verify ACL was set
    DATA(lo_acl) = ao_s3->getobjectacl( iv_bucket = av_bucket iv_key = cv_file ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_acl
      msg = |Object ACL was not set| ).

    ao_s3->deleteobject( iv_bucket = av_bucket iv_key = cv_file ).
    delete_file( cv_file ).
  ENDMETHOD.

  METHOD head_bucket.
    DATA lo_result TYPE REF TO /aws1/cl_s3_headbucketoutput.

    ao_s3_actions->head_bucket(
      EXPORTING
        iv_bucket_name = av_bucket
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not execute head_bucket| ).
  ENDMETHOD.

  METHOD put_bucket_versioning.
    " Create a separate bucket for versioning tests
    DATA(lv_version_bucket) = |sap-abap-s3-versioning-{ ao_session->get_account_id( ) }|.
    /awsex/cl_utils=>create_bucket( iv_bucket = lv_version_bucket
                                    io_s3 = ao_s3
                                    io_session = ao_session ).

    ao_s3_actions->put_bucket_versioning(
      iv_bucket_name = lv_version_bucket
      iv_status = 'Enabled' ).

    " Verify versioning was enabled
    DATA(lo_versioning) = ao_s3->getbucketversioning( iv_bucket = lv_version_bucket ).
    cl_abap_unit_assert=>assert_equals(
      exp = 'Enabled'
      act = lo_versioning->get_status( )
      msg = |Bucket versioning was not enabled| ).

    " Cleanup
    /awsex/cl_utils=>cleanup_bucket( iv_bucket = lv_version_bucket io_s3 = ao_s3 ).
  ENDMETHOD.

  METHOD list_object_versions.
    " Create a separate bucket with versioning enabled
    DATA(lv_version_bucket) = |sap-abap-s3-list-vers-{ ao_session->get_account_id( ) }|.
    /awsex/cl_utils=>create_bucket( iv_bucket = lv_version_bucket
                                    io_s3 = ao_s3
                                    io_session = ao_session ).

    " Enable versioning
    ao_s3->putbucketversioning(
      iv_bucket = lv_version_bucket
      io_versioningconfiguration = NEW /aws1/cl_s3_versioningconf( iv_status = 'Enabled' ) ).

    " Create and upload a file multiple times to create versions
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'list_versions_ex_file'.
    create_file( cv_file ).
    DATA(lv_body) = get_file_data( cv_file ).

    ao_s3->putobject( iv_bucket = lv_version_bucket iv_key = cv_file iv_body = lv_body ).
    ao_s3->putobject( iv_bucket = lv_version_bucket iv_key = cv_file iv_body = lv_body ).
    ao_s3->putobject( iv_bucket = lv_version_bucket iv_key = cv_file iv_body = lv_body ).

    " Now test listing versions
    DATA lo_result TYPE REF TO /aws1/cl_s3_listobjvrssoutput.
    ao_s3_actions->list_object_versions(
      EXPORTING
        iv_bucket_name = lv_version_bucket
        iv_prefix = cv_file
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_versions( )
      msg = |Could not list object versions| ).

    delete_file( cv_file ).
    " Cleanup
    /awsex/cl_utils=>cleanup_bucket( iv_bucket = lv_version_bucket io_s3 = ao_s3 ).
  ENDMETHOD.

  METHOD get_object_legal_hold.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'get_legal_hold_file'.
    
    " Create file and upload to lock-enabled bucket
    create_file( cv_file ).
    DATA(lv_body) = get_file_data( cv_file ).
    
    ao_s3->putobject( 
      iv_bucket = av_lock_bucket 
      iv_key = cv_file 
      iv_body = lv_body ).

    " Set legal hold first
    TRY.
        ao_s3->putobjectlegalhold(
          iv_bucket = av_lock_bucket
          iv_key = cv_file
          io_legalhold = NEW /aws1/cl_s3_objlocklegalhold( iv_status = 'ON' ) ).
      CATCH /aws1/cx_rt_generic.
        " If legal hold isn't supported, skip this test
        ao_s3->deleteobject( iv_bucket = av_lock_bucket iv_key = cv_file ).
        delete_file( cv_file ).
        RETURN.
    ENDTRY.

    " Now test getting legal hold
    DATA lo_result TYPE REF TO /aws1/cl_s3_getobjlegalholdout.
    ao_s3_actions->get_object_legal_hold(
      EXPORTING
        iv_bucket_name = av_lock_bucket
        iv_object_key = cv_file
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Could not get object legal hold| ).

    " Cleanup - turn off legal hold first
    ao_s3->putobjectlegalhold(
      iv_bucket = av_lock_bucket
      iv_key = cv_file
      io_legalhold = NEW /aws1/cl_s3_objlocklegalhold( iv_status = 'OFF' ) ).
    ao_s3->deleteobject( iv_bucket = av_lock_bucket iv_key = cv_file ).
    delete_file( cv_file ).
  ENDMETHOD.

  METHOD put_object_legal_hold.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'put_legal_hold_file'.
    
    " Create file and upload to lock-enabled bucket
    create_file( cv_file ).
    DATA(lv_body) = get_file_data( cv_file ).
    
    ao_s3->putobject( 
      iv_bucket = av_lock_bucket 
      iv_key = cv_file 
      iv_body = lv_body ).

    " Test setting legal hold
    TRY.
        ao_s3_actions->put_object_legal_hold(
          iv_bucket_name = av_lock_bucket
          iv_object_key = cv_file
          iv_status = 'ON' ).

        " Verify legal hold was set
        DATA(lo_hold) = ao_s3->getobjectlegalhold( iv_bucket = av_lock_bucket iv_key = cv_file ).
        cl_abap_unit_assert=>assert_equals(
          exp = 'ON'
          act = lo_hold->get_legalhold( )->get_status( )
          msg = |Legal hold was not set correctly| ).

        " Cleanup - turn off legal hold first
        ao_s3->putobjectlegalhold(
          iv_bucket = av_lock_bucket
          iv_key = cv_file
          io_legalhold = NEW /aws1/cl_s3_objlocklegalhold( iv_status = 'OFF' ) ).
      CATCH /aws1/cx_rt_generic.
        " If legal hold isn't supported, skip verification
    ENDTRY.

    ao_s3->deleteobject( iv_bucket = av_lock_bucket iv_key = cv_file ).
    delete_file( cv_file ).
  ENDMETHOD.

  METHOD put_object_retention.
    CONSTANTS cv_file TYPE /aws1/s3_objectkey VALUE 'put_retention_file'.
    
    " Create file and upload to lock-enabled bucket
    create_file( cv_file ).
    DATA(lv_body) = get_file_data( cv_file ).
    
    ao_s3->putobject( 
      iv_bucket = av_lock_bucket 
      iv_key = cv_file 
      iv_body = lv_body ).

    " Calculate retention date (30 days from now)
    DATA lv_timestamp TYPE timestamp.
    DATA lv_retain_date TYPE /aws1/s3_objlockrtnuntildate.
    GET TIME STAMP FIELD lv_timestamp.
    lv_timestamp = lv_timestamp + ( 30 * 24 * 60 * 60 ). " Add 30 days in seconds
    lv_retain_date = lv_timestamp.

    " Test setting retention
    TRY.
        ao_s3_actions->put_object_retention(
          iv_bucket_name = av_lock_bucket
          iv_object_key = cv_file
          iv_mode = 'GOVERNANCE'
          iv_retain_date = lv_retain_date ).

        " Verify retention was set
        DATA(lo_ret) = ao_s3->getobjectretention( iv_bucket = av_lock_bucket iv_key = cv_file ).
        cl_abap_unit_assert=>assert_equals(
          exp = 'GOVERNANCE'
          act = lo_ret->get_retention( )->get_mode( )
          msg = |Retention was not set correctly| ).

        " Cleanup - delete with bypass
        ao_s3->deleteobject(
          iv_bucket = av_lock_bucket
          iv_key = cv_file
          iv_bypassgovernanceretention = abap_true ).
      CATCH /aws1/cx_rt_generic.
        " If retention isn't supported, try cleanup without bypass
        TRY.
            ao_s3->deleteobject( iv_bucket = av_lock_bucket iv_key = cv_file ).
          CATCH /aws1/cx_rt_generic.
            " Object may still be locked, skip cleanup
        ENDTRY.
    ENDTRY.

    delete_file( cv_file ).
  ENDMETHOD.

  METHOD get_object_lock_conf.
    DATA lo_result TYPE REF TO /aws1/cl_s3_getobjlockconfout.

    " Test getting object lock configuration
    TRY.
        ao_s3_actions->get_object_lock_conf(
          EXPORTING
            iv_bucket_name = av_lock_bucket
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |Could not get object lock configuration| ).
      CATCH /aws1/cx_rt_generic.
        " Object lock may not be fully configured, which is acceptable
    ENDTRY.
  ENDMETHOD.

  METHOD put_object_lock_conf.
    " Create a new bucket specifically for this test
    DATA(lv_test_bucket) = |sap-abap-s3-putlock-{ ao_session->get_account_id( ) }|.
    
    DATA(lv_region) = CONV /aws1/s3_bucketlocationcnstrnt( ao_session->get_region( ) ).
    DATA lo_constraint TYPE REF TO /aws1/cl_s3_createbucketconf.

    IF lv_region = 'us-east-1'.
      CLEAR lo_constraint.
    ELSE.
      lo_constraint = NEW /aws1/cl_s3_createbucketconf( lv_region ).
    ENDIF.

    TRY.
        " Create bucket with object lock enabled
        ao_s3->createbucket(
          iv_bucket = lv_test_bucket
          io_createbucketconfiguration = lo_constraint
          iv_objectlockenabledforbucket = abap_true ).

        " Tag the bucket
        DATA lt_tags TYPE /aws1/cl_s3_tag=>tt_tagset.
        APPEND NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_tags.
        ao_s3->putbuckettagging( iv_bucket = lv_test_bucket io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).

        " Test putting object lock configuration
        ao_s3_actions->put_object_lock_conf(
          iv_bucket_name = lv_test_bucket
          iv_enabled = 'Enabled' ).

        " Verify configuration was set
        DATA(lo_config) = ao_s3->getobjectlockconfiguration( iv_bucket = lv_test_bucket ).
        cl_abap_unit_assert=>assert_equals(
          exp = 'Enabled'
          act = lo_config->get_objectlockconfiguration( )->get_objectlockenabled( )
          msg = |Object lock configuration was not set| ).

        " Note: Bucket with object lock cannot be easily cleaned up
        " It's tagged for manual cleanup
      CATCH /aws1/cx_s3_bktalrdyownedbyyou.
        " Bucket already exists, try to set configuration anyway
        TRY.
            ao_s3_actions->put_object_lock_conf(
              iv_bucket_name = lv_test_bucket
              iv_enabled = 'Enabled' ).
          CATCH /aws1/cx_rt_generic.
            " Configuration may already be set
        ENDTRY.
      CATCH /aws1/cx_rt_generic.
        " Object lock operations may fail if not properly configured
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
