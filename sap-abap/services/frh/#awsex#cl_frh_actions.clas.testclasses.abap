" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_frh_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_frh TYPE REF TO /aws1/if_frh.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_frh_actions TYPE REF TO /awsex/cl_frh_actions.
    CLASS-DATA av_delivery_stream TYPE /aws1/frhdeliverystreamname.
    CLASS-DATA av_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_role_arn TYPE /aws1/frhrolearn.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_lmd_uuid TYPE sysuuid_x16.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic cx_uuid_error.
    CLASS-METHODS class_teardown.

    METHODS put_record FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_record_batch FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_stream_active
      IMPORTING
        iv_stream_name       TYPE /aws1/frhdeliverystreamname
      RETURNING
        VALUE(rv_is_active)  TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_frh_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_account_id TYPE string.
    DATA lv_region TYPE /aws1/rt_region_id.

    " Create session and service clients
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_frh = /aws1/cl_frh_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_frh_actions = NEW /awsex/cl_frh_actions( ).

    " Generate unique names using UUID
    av_lmd_uuid = cl_system_uuid=>create_uuid_x16_static( ).
    lv_account_id = ao_session->get_account_id( ).
    lv_region = ao_session->get_region( ).

    " Create unique resource names
    av_bucket = |sap-abap-frh-test-{ lv_account_id }|.
    TRANSLATE av_bucket TO LOWER CASE.

    av_delivery_stream = 'test-stream-' && av_lmd_uuid.
    TRANSLATE av_delivery_stream TO LOWER CASE.

    av_role_name = 'frh-test-role-' && av_lmd_uuid.
    TRANSLATE av_role_name TO LOWER CASE.

    " Step 1: Create S3 bucket for Firehose destination
    TRY.
        " Use utility function to create bucket
        /awsex/cl_utils=>create_bucket(
          iv_bucket   = av_bucket
          io_s3       = ao_s3
          io_session  = ao_session ).

      CATCH /aws1/cx_s3_bktalrdyownedbyyou /aws1/cx_s3_bucketalrdyexists.
        " Bucket already exists, continue - we'll use it
    ENDTRY.

    " Tag the S3 bucket for cleanup
    TRY.
        DATA(lt_s3_tags) = VALUE /aws1/cl_s3_tag=>tt_tagset(
          ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) ) ).

        ao_s3->putbuckettagging(
          iv_bucket = av_bucket
          io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_s3_tags ) ).
      CATCH /aws1/cx_s3_nosuchbucket.
        " Bucket creation failed
        cl_abap_unit_assert=>fail( msg = |Failed to create S3 bucket { av_bucket }| ).
    ENDTRY.

    " Step 2: Create IAM role for Firehose
    DATA(lv_trust_policy) = `{` &&
      `"Version":"2012-10-17",` &&
      `"Statement":[{` &&
        `"Effect":"Allow",` &&
        `"Principal":{"Service":"firehose.amazonaws.com"},` &&
        `"Action":"sts:AssumeRole"` &&
      `}]` &&
    `}`.

    TRY.
        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_trust_policy ).

        av_role_arn = lo_role_result->get_role( )->get_arn( ).

      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists, get its ARN
        TRY.
            DATA(lo_existing_role) = ao_iam->getrole( iv_rolename = av_role_name ).
            av_role_arn = lo_existing_role->get_role( )->get_arn( ).
          CATCH /aws1/cx_iamnosuchentityex.
            " Role doesn't exist despite exception
            cl_abap_unit_assert=>fail( msg = |Failed to create or retrieve IAM role { av_role_name }| ).
        ENDTRY.
    ENDTRY.

    " Tag the IAM role for cleanup
    TRY.
        DATA(lt_iam_tags) = VALUE /aws1/cl_iamtag=>tt_taglisttype(
          ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) ) ).

        ao_iam->tagrole(
          iv_rolename = av_role_name
          it_tags = lt_iam_tags ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Role doesn't exist
        cl_abap_unit_assert=>fail( msg = |Failed to tag IAM role { av_role_name }| ).
    ENDTRY.

    " Step 3: Attach inline policy to IAM role for S3 access
    DATA(lv_policy) = `{` &&
      `"Version":"2012-10-17",` &&
      `"Statement":[{` &&
        `"Effect":"Allow",` &&
        `"Action":[` &&
          `"s3:AbortMultipartUpload",` &&
          `"s3:GetBucketLocation",` &&
          `"s3:GetObject",` &&
          `"s3:ListBucket",` &&
          `"s3:ListBucketMultipartUploads",` &&
          `"s3:PutObject"` &&
        `],` &&
        `"Resource":[` &&
          `"arn:aws:s3:::` && av_bucket && `",` &&
          `"arn:aws:s3:::` && av_bucket && `/*"` &&
        `]` &&
      `}]` &&
    `}`.

    TRY.
        ao_iam->putrolepolicy(
          iv_rolename = av_role_name
          iv_policyname = 'FirehoseS3Policy'
          iv_policydocument = lv_policy ).
      CATCH /aws1/cx_iamnosuchentityex.
        cl_abap_unit_assert=>fail( msg = |Failed to attach policy to IAM role { av_role_name }| ).
    ENDTRY.

    " Wait for IAM role and policy to propagate
    WAIT UP TO 15 SECONDS.

    " Step 4: Create Firehose delivery stream with S3 destination
    TRY.
        DATA(lo_s3_dest_config) = NEW /aws1/cl_frhs3destinationconf(
          iv_rolearn   = av_role_arn
          iv_bucketarn = |arn:aws:s3:::{ av_bucket }|
          io_bufferinghints = NEW /aws1/cl_frhbufferinghints(
            iv_sizeinmbs = 1
            iv_intervalinseconds = 60 )
          io_cloudwatchloggingoptions = NEW /aws1/cl_frhcloudwatchlogopts(
            iv_enabled = abap_false ) ).

        DATA(lo_create_result) = ao_frh->createdeliverystream(
          iv_deliverystreamname = av_delivery_stream
          io_s3destinationconf  = lo_s3_dest_config ).

      CATCH /aws1/cx_frhresourceinuseex.
        " Stream already exists, continue
      CATCH /aws1/cx_frhinvalidargumentex INTO DATA(lo_invalid_arg).
        cl_abap_unit_assert=>fail( msg = |Failed to create delivery stream: { lo_invalid_arg->get_text( ) }| ).
      CATCH /aws1/cx_frhlimitexceededex.
        cl_abap_unit_assert=>fail( msg = |Limit exceeded creating delivery stream { av_delivery_stream }| ).
    ENDTRY.

    " Tag the delivery stream for cleanup
    TRY.
        DATA(lt_stream_tags) = VALUE /aws1/cl_frhtag=>tt_tagdeliverystrminputtaglist(
          ( NEW /aws1/cl_frhtag( iv_key = 'convert_test' iv_value = 'true' ) ) ).

        ao_frh->tagdeliverystream(
          iv_deliverystreamname = av_delivery_stream
          it_tags = lt_stream_tags ).
      CATCH /aws1/cx_frhresourcenotfoundex.
        " Stream might not be ready yet, that's ok
    ENDTRY.

    " Step 5: Wait for the delivery stream to become active
    DATA(lv_is_active) = wait_for_stream_active( av_delivery_stream ).

    IF lv_is_active = abap_false.
      cl_abap_unit_assert=>fail( msg = |Delivery stream { av_delivery_stream } did not become active| ).
    ENDIF.

  ENDMETHOD.

  METHOD class_teardown.
    " Note: We do NOT delete the S3 bucket here because the delivery stream
    " may still be writing to it after deletion. The bucket is tagged with
    " 'convert_test' for manual cleanup later.

    " Step 1: Delete delivery stream (with force delete)
    TRY.
        IF av_delivery_stream IS NOT INITIAL.
          ao_frh->deletedeliverystream(
            iv_deliverystreamname = av_delivery_stream
            iv_allowforcedelete = abap_true ).
        ENDIF.
      CATCH /aws1/cx_frhresourcenotfoundex.
        " Already deleted, that's fine
    ENDTRY.

    " Wait for stream deletion to begin
    WAIT UP TO 5 SECONDS.

    " Step 2: Delete IAM role policy
    TRY.
        IF av_role_name IS NOT INITIAL.
          ao_iam->deleterolepolicy(
            iv_rolename = av_role_name
            iv_policyname = 'FirehoseS3Policy' ).
        ENDIF.
      CATCH /aws1/cx_iamnosuchentityex.
        " Already deleted, that's fine
    ENDTRY.

    " Step 3: Delete IAM role
    TRY.
        IF av_role_name IS NOT INITIAL.
          ao_iam->deleterole( iv_rolename = av_role_name ).
        ENDIF.
      CATCH /aws1/cx_iamnosuchentityex.
        " Already deleted, that's fine
    ENDTRY.

  ENDMETHOD.

  METHOD wait_for_stream_active.
    DATA lo_describe_result TYPE REF TO /aws1/cl_frhdscdeliverystrmout.
    DATA lo_stream_desc TYPE REF TO /aws1/cl_frhdeliverystreamdesc.
    DATA lv_status TYPE /aws1/frhdeliverystreamstatus.

    rv_is_active = abap_false.

    " Maximum wait time: 10 minutes (60 iterations * 10 seconds)
    DO 60 TIMES.
      TRY.
          lo_describe_result = ao_frh->describedeliverystream(
            iv_deliverystreamname = iv_stream_name ).

          lo_stream_desc = lo_describe_result->get_deliverystreamdesc( ).
          lv_status = lo_stream_desc->get_deliverystreamstatus( ).

          IF lv_status = 'ACTIVE'.
            rv_is_active = abap_true.
            RETURN.
          ELSEIF lv_status = 'CREATING'.
            " Still creating, continue waiting
            WAIT UP TO 10 SECONDS.
          ELSEIF lv_status = 'DELETING'.
            " Stream is being deleted, cannot use it
            RETURN.
          ELSE.
            " Unexpected status (CREATING_FAILED, DELETING_FAILED, etc.)
            RETURN.
          ENDIF.

        CATCH /aws1/cx_frhresourcenotfoundex.
          " Stream not found yet, wait and retry
          WAIT UP TO 10 SECONDS.
      ENDTRY.
    ENDDO.

  ENDMETHOD.

  METHOD put_record.
    " Create test data in JSON format
    DATA(lv_json_data) = `{"test":"put_record","timestamp":"` &&
                         sy-datum && `","time":"` && sy-uzeit && `"}`.
    DATA(lv_data) = /aws1/cl_rt_util=>string_to_xstring( lv_json_data ).

    " Test put_record method
    TRY.
        ao_frh_actions->put_record(
          iv_deliv_stream_name = av_delivery_stream
          iv_data = lv_data ).

        " Validation: If no exception was raised, the test passed
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |put_record executed successfully| ).

      CATCH /aws1/cx_frhresourcenotfoundex INTO DATA(lo_not_found).
        cl_abap_unit_assert=>fail(
          msg = |Delivery stream not found: { lo_not_found->get_text( ) }| ).
      CATCH /aws1/cx_frhinvalidargumentex INTO DATA(lo_invalid_arg).
        cl_abap_unit_assert=>fail(
          msg = |Invalid argument: { lo_invalid_arg->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD put_record_batch.
    " Create multiple test records for batch operation
    DATA lt_records TYPE /aws1/cl_frhrecord=>tt_putrecordbatchreqentrylist.

    " Create 5 test records
    DO 5 TIMES.
      DATA(lv_json_data) = `{"test":"put_record_batch","index":` &&
                           sy-index && `,"timestamp":"` && sy-datum &&
                           `","time":"` && sy-uzeit && `"}`.
      DATA(lv_data) = /aws1/cl_rt_util=>string_to_xstring( lv_json_data ).

      APPEND NEW /aws1/cl_frhrecord( iv_data = lv_data ) TO lt_records.
    ENDDO.

    " Test put_record_batch method
    TRY.
        ao_frh_actions->put_record_batch(
          iv_deliv_stream_name = av_delivery_stream
          it_records = lt_records ).

        " Validation: If no exception was raised, the test passed
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |put_record_batch executed successfully| ).

      CATCH /aws1/cx_frhresourcenotfoundex INTO DATA(lo_not_found).
        cl_abap_unit_assert=>fail(
          msg = |Delivery stream not found: { lo_not_found->get_text( ) }| ).
      CATCH /aws1/cx_frhinvalidargumentex INTO DATA(lo_invalid_arg).
        cl_abap_unit_assert=>fail(
          msg = |Invalid argument: { lo_invalid_arg->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
