" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_kms_actions DEFINITION DEFERRED.
CLASS /awsex/cl_kms_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_kms_actions.

CLASS ltc_awsex_cl_kms_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_kms TYPE REF TO /aws1/if_kms.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_kms_actions TYPE REF TO /awsex/cl_kms_actions.
    CLASS-DATA av_key_id TYPE /aws1/kmskeyidtype.
    CLASS-DATA av_key_id_2 TYPE /aws1/kmskeyidtype.
    CLASS-DATA av_asym_key_id TYPE /aws1/kmskeyidtype.
    CLASS-DATA av_alias_name TYPE /aws1/kmsaliasnametype.
    CLASS-DATA av_alias_name_2 TYPE /aws1/kmsaliasnametype.
    CLASS-DATA av_role_arn TYPE /aws1/iamarntype.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_account_id TYPE string.

    METHODS:
      create_key FOR TESTING RAISING /aws1/cx_rt_generic,
      create_asymmetric_key FOR TESTING RAISING /aws1/cx_rt_generic,
      list_keys FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_key FOR TESTING RAISING /aws1/cx_rt_generic,
      generate_data_key FOR TESTING RAISING /aws1/cx_rt_generic,
      enable_key FOR TESTING RAISING /aws1/cx_rt_generic,
      disable_key FOR TESTING RAISING /aws1/cx_rt_generic,
      schedule_key_deletion FOR TESTING RAISING /aws1/cx_rt_generic,
      enable_key_rotation FOR TESTING RAISING /aws1/cx_rt_generic,
      tag_resource FOR TESTING RAISING /aws1/cx_rt_generic,
      create_alias FOR TESTING RAISING /aws1/cx_rt_generic,
      list_aliases FOR TESTING RAISING /aws1/cx_rt_generic,
      update_alias FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_alias FOR TESTING RAISING /aws1/cx_rt_generic,
      create_grant FOR TESTING RAISING /aws1/cx_rt_generic,
      list_grants FOR TESTING RAISING /aws1/cx_rt_generic,
      retire_grant FOR TESTING RAISING /aws1/cx_rt_generic,
      revoke_grant FOR TESTING RAISING /aws1/cx_rt_generic,
      get_key_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      put_key_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      list_key_policies FOR TESTING RAISING /aws1/cx_rt_generic,
      encrypt FOR TESTING RAISING /aws1/cx_rt_generic,
      decrypt FOR TESTING RAISING /aws1/cx_rt_generic,
      re_encrypt FOR TESTING RAISING /aws1/cx_rt_generic,
      sign FOR TESTING RAISING /aws1/cx_rt_generic,
      verify FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS:
      class_setup RAISING /aws1/cx_rt_generic,
      class_teardown RAISING /aws1/cx_rt_generic.

    METHODS:
      wait_for_key_state
        IMPORTING
          iv_key_id       TYPE /aws1/kmskeyidtype
          iv_target_state TYPE /aws1/kmskeystate
          iv_max_wait     TYPE i DEFAULT 300
        RAISING
          /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_kms_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_kms = /aws1/cl_kms_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_kms_actions = NEW /awsex/cl_kms_actions( ).
    av_account_id = ao_session->get_account_id( ).

    " Create shared resources for tests using utility function
    DATA(lv_random_suffix) = /awsex/cl_utils=>get_random_string( ).

    " Create primary test key
    TRY.
        DATA(lv_key_desc) = |{ /awsex/cl_utils=>cv_asset_prefix }-kms-key-{ lv_random_suffix }|.
        DATA(lo_key_result) = ao_kms->createkey(
          iv_description = lv_key_desc
          it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
            ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
          )
        ).
        av_key_id = lo_key_result->get_keymetadata( )->get_keyid( ).

        " Wait for key to be enabled
        DATA(lv_wait_count) = 0.
        DATA(lv_key_enabled) = abap_false.
        WHILE lv_wait_count < 60 AND lv_key_enabled = abap_false.
          WAIT UP TO 2 SECONDS.
          lv_wait_count = lv_wait_count + 1.
          TRY.
              DATA(lo_describe) = ao_kms->describekey( iv_keyid = av_key_id ).
              IF lo_describe->get_keymetadata( )->get_enabled( ) = abap_true.
                lv_key_enabled = abap_true.
              ENDIF.
            CATCH /aws1/cx_rt_generic.
              " Continue waiting
          ENDTRY.
        ENDWHILE.

        IF lv_key_enabled = abap_false.
          cl_abap_unit_assert=>fail( msg = |Primary key { av_key_id } did not become enabled in time| ).
        ENDIF.

      CATCH /aws1/cx_kmskmsinternalex INTO DATA(lo_error).
        cl_abap_unit_assert=>fail( msg = |Failed to create primary test key: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Create second key for re-encryption test
    TRY.
        lv_random_suffix = /awsex/cl_utils=>get_random_string( ).
        lv_key_desc = |{ /awsex/cl_utils=>cv_asset_prefix }-kms-key2-{ lv_random_suffix }|.
        lo_key_result = ao_kms->createkey(
          iv_description = lv_key_desc
          it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
            ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
          )
        ).
        av_key_id_2 = lo_key_result->get_keymetadata( )->get_keyid( ).

        " Wait for key to be enabled
        lv_wait_count = 0.
        lv_key_enabled = abap_false.
        WHILE lv_wait_count < 60 AND lv_key_enabled = abap_false.
          WAIT UP TO 2 SECONDS.
          lv_wait_count = lv_wait_count + 1.
          TRY.
              lo_describe = ao_kms->describekey( iv_keyid = av_key_id_2 ).
              IF lo_describe->get_keymetadata( )->get_enabled( ) = abap_true.
                lv_key_enabled = abap_true.
              ENDIF.
            CATCH /aws1/cx_rt_generic.
              " Continue waiting
          ENDTRY.
        ENDWHILE.

        IF lv_key_enabled = abap_false.
          cl_abap_unit_assert=>fail( msg = |Second key { av_key_id_2 } did not become enabled in time| ).
        ENDIF.

      CATCH /aws1/cx_kmskmsinternalex INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to create second test key: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Create asymmetric key for signing tests
    TRY.
        lv_random_suffix = /awsex/cl_utils=>get_random_string( ).
        lv_key_desc = |{ /awsex/cl_utils=>cv_asset_prefix }-kms-asym-{ lv_random_suffix }|.
        lo_key_result = ao_kms->createkey(
          iv_keyspec = 'RSA_2048'
          iv_keyusage = 'SIGN_VERIFY'
          iv_origin = 'AWS_KMS'
          iv_description = lv_key_desc
          it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
            ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
          )
        ).
        av_asym_key_id = lo_key_result->get_keymetadata( )->get_keyid( ).

        " Wait for key to be enabled
        lv_wait_count = 0.
        lv_key_enabled = abap_false.
        WHILE lv_wait_count < 60 AND lv_key_enabled = abap_false.
          WAIT UP TO 2 SECONDS.
          lv_wait_count = lv_wait_count + 1.
          TRY.
              lo_describe = ao_kms->describekey( iv_keyid = av_asym_key_id ).
              IF lo_describe->get_keymetadata( )->get_enabled( ) = abap_true.
                lv_key_enabled = abap_true.
              ENDIF.
            CATCH /aws1/cx_rt_generic.
              " Continue waiting
          ENDTRY.
        ENDWHILE.

        IF lv_key_enabled = abap_false.
          cl_abap_unit_assert=>fail( msg = |Asymmetric key { av_asym_key_id } did not become enabled in time| ).
        ENDIF.

      CATCH /aws1/cx_kmskmsinternalex INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to create asymmetric test key: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Create alias names using utility function
    lv_random_suffix = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random_suffix TO LOWER CASE.
    av_alias_name = |alias/{ /awsex/cl_utils=>cv_asset_prefix }-alias-{ lv_random_suffix }|.

    lv_random_suffix = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random_suffix TO LOWER CASE.
    av_alias_name_2 = |alias/{ /awsex/cl_utils=>cv_asset_prefix }-alias2-{ lv_random_suffix }|.

    " Create IAM role for grant tests
    TRY.
        lv_random_suffix = /awsex/cl_utils=>get_random_string( ).
        av_role_name = |{ /awsex/cl_utils=>cv_asset_prefix }KMSRole{ lv_random_suffix(8) }|.

        DATA(lv_trust_policy) = |\{| &&
          |"Version": "2012-10-17",| &&
          |"Statement": [| &&
            |\{| &&
              |"Effect": "Allow",| &&
              |"Principal": \{"Service": "lambda.amazonaws.com"\},| &&
              |"Action": "sts:AssumeRole"| &&
            |\}| &&
          |]| &&
        |\}|.

        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = av_role_name
          iv_assumerolepolicydocument = lv_trust_policy
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).
        av_role_arn = lo_role_result->get_role( )->get_arn( ).

        " Attach KMS policy to role
        DATA(lv_policy_doc) = |\{| &&
          |"Version": "2012-10-17",| &&
          |"Statement": [| &&
            |\{| &&
              |"Effect": "Allow",| &&
              |"Action": ["kms:Encrypt","kms:Decrypt","kms:GenerateDataKey"],| &&
              |"Resource": "*"| &&
            |\}| &&
          |]| &&
        |\}|.

        ao_iam->putrolepolicy(
          iv_rolename = av_role_name
          iv_policyname = 'KMSTestPolicy'
          iv_policydocument = lv_policy_doc
        ).

        " Wait for role to propagate
        WAIT UP TO 10 SECONDS.

      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists - get the ARN
        TRY.
            DATA(lo_get_role) = ao_iam->getrole( iv_rolename = av_role_name ).
            av_role_arn = lo_get_role->get_role( )->get_arn( ).
          CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_error).
            cl_abap_unit_assert=>fail( msg = |Failed to get existing IAM role: { lo_generic_error->get_text( ) }| ).
        ENDTRY.
      CATCH /aws1/cx_rt_generic INTO lo_generic_error.
        cl_abap_unit_assert=>fail( msg = |Failed to create IAM role: { lo_generic_error->get_text( ) }| ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up keys - schedule for deletion
    " KMS keys take time to delete (7-30 days), so we tag them and schedule for deletion
    IF av_key_id IS NOT INITIAL.
      TRY.
          ao_kms->schedulekeydeletion(
            iv_keyid = av_key_id
            iv_pendingwindowindays = 7
          ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup - key might already be scheduled
      ENDTRY.
    ENDIF.

    IF av_key_id_2 IS NOT INITIAL.
      TRY.
          ao_kms->schedulekeydeletion(
            iv_keyid = av_key_id_2
            iv_pendingwindowindays = 7
          ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    IF av_asym_key_id IS NOT INITIAL.
      TRY.
          ao_kms->schedulekeydeletion(
            iv_keyid = av_asym_key_id
            iv_pendingwindowindays = 7
          ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Delete aliases (immediate)
    IF av_alias_name IS NOT INITIAL.
      TRY.
          ao_kms->deletealias( iv_aliasname = av_alias_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    IF av_alias_name_2 IS NOT INITIAL.
      TRY.
          ao_kms->deletealias( iv_aliasname = av_alias_name_2 ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up IAM role (immediate)
    IF av_role_name IS NOT INITIAL.
      TRY.
          ao_iam->deleterolepolicy(
            iv_rolename = av_role_name
            iv_policyname = 'KMSTestPolicy'
          ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.

      TRY.
          ao_iam->deleterole( iv_rolename = av_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD wait_for_key_state.
    DATA(lv_wait_count) = 0.
    DATA(lv_max_iterations) = iv_max_wait / 2.

    WHILE lv_wait_count < lv_max_iterations.
      WAIT UP TO 2 SECONDS.
      lv_wait_count = lv_wait_count + 1.

      TRY.
          DATA(lo_describe) = ao_kms->describekey( iv_keyid = iv_key_id ).
          DATA(lv_current_state) = lo_describe->get_keymetadata( )->get_keystate( ).

          IF lv_current_state = iv_target_state.
            RETURN.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue waiting
      ENDTRY.
    ENDWHILE.

    cl_abap_unit_assert=>fail( msg = |Key did not reach state { iv_target_state } within { iv_max_wait } seconds| ).
  ENDMETHOD.

  METHOD create_key.
    DATA lo_result TYPE REF TO /aws1/cl_kmscreatekeyresponse.
    DATA(lv_random_suffix) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_description) = |{ /awsex/cl_utils=>cv_asset_prefix }-test-key-{ lv_random_suffix }|.

    ao_kms_actions->create_key(
      EXPORTING
        iv_description = lv_description
      IMPORTING
        oo_result = lo_result
    ).

    DATA(lv_new_key_id) = lo_result->get_keymetadata( )->get_keyid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_new_key_id
      msg = 'Key ID should not be empty'
    ).

    " Tag for cleanup
    ao_kms->tagresource(
      iv_keyid = lv_new_key_id
      it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
        ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
      )
    ).

    " Schedule for deletion
    ao_kms->schedulekeydeletion(
      iv_keyid = lv_new_key_id
      iv_pendingwindowindays = 7
    ).

  ENDMETHOD.

  METHOD create_asymmetric_key.
    DATA lo_result TYPE REF TO /aws1/cl_kmscreatekeyresponse.

    ao_kms_actions->create_asymmetric_key(
      IMPORTING
        oo_result = lo_result
    ).

    DATA(lv_key_id) = lo_result->get_keymetadata( )->get_keyid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_key_id
      msg = 'Asymmetric key ID should not be empty'
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'RSA_2048'
      act = lo_result->get_keymetadata( )->get_keyspec( )
      msg = 'Key spec should be RSA_2048'
    ).

    " Tag for cleanup
    ao_kms->tagresource(
      iv_keyid = lv_key_id
      it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
        ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
      )
    ).

    " Schedule for deletion
    ao_kms->schedulekeydeletion(
      iv_keyid = lv_key_id
      iv_pendingwindowindays = 7
    ).

  ENDMETHOD.

  METHOD list_keys.
    DATA lo_result TYPE REF TO /aws1/cl_kmslistkeysresponse.

    ao_kms_actions->list_keys(
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List keys result should be bound'
    ).

    DATA(lt_keys) = lo_result->get_keys( ).
    cl_abap_unit_assert=>assert_true(
      act = xsdbool( lines( lt_keys ) > 0 )
      msg = 'Should return at least one key'
    ).

  ENDMETHOD.

  METHOD describe_key.
    DATA lo_result TYPE REF TO /aws1/cl_kmsdescrkeyresponse.

    " Verify av_key_id exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    ao_kms_actions->describe_key(
      EXPORTING
        iv_key_id = av_key_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_key_id
      act = lo_result->get_keymetadata( )->get_keyid( )
      msg = 'Returned key ID should match requested key ID'
    ).

  ENDMETHOD.

  METHOD generate_data_key.
    DATA lo_result TYPE REF TO /aws1/cl_kmsgeneratedatakeyrsp.

    " Verify av_key_id exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    ao_kms_actions->generate_data_key(
      EXPORTING
        iv_key_id = av_key_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_ciphertextblob( )
      msg = 'Encrypted data key should not be empty'
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_plaintext( )
      msg = 'Plaintext data key should not be empty'
    ).

  ENDMETHOD.

  METHOD enable_key.
    " Verify av_key_id exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    " First disable the key
    ao_kms->disablekey( iv_keyid = av_key_id ).
    wait_for_key_state(
      iv_key_id = av_key_id
      iv_target_state = 'Disabled'
    ).

    " Now test enabling
    ao_kms_actions->enable_key( iv_key_id = av_key_id ).

    wait_for_key_state(
      iv_key_id = av_key_id
      iv_target_state = 'Enabled'
    ).

    DATA(lo_describe) = ao_kms->describekey( iv_keyid = av_key_id ).
    cl_abap_unit_assert=>assert_true(
      act = lo_describe->get_keymetadata( )->get_enabled( )
      msg = 'Key should be enabled'
    ).

  ENDMETHOD.

  METHOD disable_key.
    " Verify av_key_id exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    " Ensure key is enabled first
    ao_kms->enablekey( iv_keyid = av_key_id ).
    wait_for_key_state(
      iv_key_id = av_key_id
      iv_target_state = 'Enabled'
    ).

    " Test disabling
    ao_kms_actions->disable_key( iv_key_id = av_key_id ).

    wait_for_key_state(
      iv_key_id = av_key_id
      iv_target_state = 'Disabled'
    ).

    DATA(lo_describe) = ao_kms->describekey( iv_keyid = av_key_id ).
    cl_abap_unit_assert=>assert_false(
      act = lo_describe->get_keymetadata( )->get_enabled( )
      msg = 'Key should be disabled'
    ).

    " Re-enable for other tests
    ao_kms->enablekey( iv_keyid = av_key_id ).
    wait_for_key_state(
      iv_key_id = av_key_id
      iv_target_state = 'Enabled'
    ).

  ENDMETHOD.

  METHOD schedule_key_deletion.
    DATA lo_result TYPE REF TO /aws1/cl_kmsschdkeydeletionrsp.
    DATA(lv_random_suffix) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_description) = |{ /awsex/cl_utils=>cv_asset_prefix }-del-key-{ lv_random_suffix }|.

    " Create a new key for deletion test
    DATA(lo_create_result) = ao_kms->createkey(
      iv_description = lv_description
      it_tags = VALUE /aws1/cl_kmstag=>tt_taglist(
        ( NEW /aws1/cl_kmstag( iv_tagkey = 'convert_test' iv_tagvalue = 'true' ) )
      )
    ).
    DATA(lv_delete_key_id) = lo_create_result->get_keymetadata( )->get_keyid( ).

    " Wait for key to be ready
    DATA(lv_wait_count) = 0.
    DATA(lv_key_ready) = abap_false.
    WHILE lv_wait_count < 60 AND lv_key_ready = abap_false.
      WAIT UP TO 2 SECONDS.
      lv_wait_count = lv_wait_count + 1.
      TRY.
          DATA(lo_describe) = ao_kms->describekey( iv_keyid = lv_delete_key_id ).
          IF lo_describe->get_keymetadata( )->get_enabled( ) = abap_true.
            lv_key_ready = abap_true.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue waiting
      ENDTRY.
    ENDWHILE.

    IF lv_key_ready = abap_false.
      cl_abap_unit_assert=>fail( msg = |Key for deletion test did not become ready| ).
    ENDIF.

    " Test scheduling deletion
    ao_kms_actions->schedule_key_deletion(
      EXPORTING
        iv_key_id = lv_delete_key_id
        iv_pending_window_days = 7
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_deletiondate( )
      msg = 'Deletion date should be set'
    ).

  ENDMETHOD.

  METHOD enable_key_rotation.
    " Verify av_key_id exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    " Ensure key is enabled
    ao_kms->enablekey( iv_keyid = av_key_id ).
    wait_for_key_state(
      iv_key_id = av_key_id
      iv_target_state = 'Enabled'
    ).

    ao_kms_actions->enable_key_rotation( iv_key_id = av_key_id ).

    DATA(lo_rotation_status) = ao_kms->getkeyrotationstatus( iv_keyid = av_key_id ).
    cl_abap_unit_assert=>assert_true(
      act = lo_rotation_status->get_keyrotationenabled( )
      msg = 'Key rotation should be enabled'
    ).

  ENDMETHOD.

  METHOD tag_resource.
    " Verify av_key_id exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    ao_kms_actions->tag_resource(
      iv_key_id = av_key_id
      iv_tag_key = 'TestTag'
      iv_tag_value = 'TestValue'
    ).

    DATA(lo_tags) = ao_kms->listresourcetags( iv_keyid = av_key_id ).
    DATA(lv_found) = abap_false.

    LOOP AT lo_tags->get_tags( ) INTO DATA(lo_tag).
      IF lo_tag->get_tagkey( ) = 'TestTag' AND lo_tag->get_tagvalue( ) = 'TestValue'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Tag should be found on the key'
    ).

  ENDMETHOD.

  METHOD create_alias.
    " Verify av_key_id and av_alias_name exist
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.
    IF av_alias_name IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Alias name not created in class_setup' ).
    ENDIF.

    ao_kms_actions->create_alias(
      iv_key_id = av_key_id
      iv_alias_name = av_alias_name
    ).

    DATA(lo_aliases) = ao_kms->listaliases( ).
    DATA(lv_found) = abap_false.

    LOOP AT lo_aliases->get_aliases( ) INTO DATA(lo_alias).
      IF lo_alias->get_aliasname( ) = av_alias_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Alias should be created'
    ).

  ENDMETHOD.

  METHOD list_aliases.
    DATA lo_result TYPE REF TO /aws1/cl_kmslistaliasesrsp.

    ao_kms_actions->list_aliases(
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List aliases result should be bound'
    ).

  ENDMETHOD.

  METHOD update_alias.
    " Verify keys and alias name exist
    IF av_key_id IS INITIAL OR av_key_id_2 IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test keys not created in class_setup' ).
    ENDIF.
    IF av_alias_name IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Alias name not created in class_setup' ).
    ENDIF.

    " First create an alias if not exists
    TRY.
        ao_kms->createalias(
          iv_aliasname = av_alias_name
          iv_targetkeyid = av_key_id
        ).
      CATCH /aws1/cx_kmsalreadyexistsex.
        " Alias already exists, this is fine
    ENDTRY.

    " Update the alias to point to second key
    ao_kms_actions->update_alias(
      iv_alias_name = av_alias_name
      iv_target_key_id = av_key_id_2
    ).

    " Verify alias now points to second key
    DATA(lo_aliases) = ao_kms->listaliases( ).
    DATA(lv_found) = abap_false.

    LOOP AT lo_aliases->get_aliases( ) INTO DATA(lo_alias).
      IF lo_alias->get_aliasname( ) = av_alias_name.
        IF lo_alias->get_targetkeyid( ) = av_key_id_2.
          lv_found = abap_true.
        ENDIF.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Alias should point to second key'
    ).

  ENDMETHOD.

  METHOD delete_alias.
    " Verify key and alias name exist
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.
    IF av_alias_name_2 IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Second alias name not created in class_setup' ).
    ENDIF.

    " First create an alias
    TRY.
        ao_kms->createalias(
          iv_aliasname = av_alias_name_2
          iv_targetkeyid = av_key_id
        ).
      CATCH /aws1/cx_kmsalreadyexistsex.
        " Alias already exists, this is fine
    ENDTRY.

    " Test deleting the alias
    ao_kms_actions->delete_alias( iv_alias_name = av_alias_name_2 ).

    " Verify alias is deleted
    DATA(lo_aliases) = ao_kms->listaliases( ).
    DATA(lv_found) = abap_false.

    LOOP AT lo_aliases->get_aliases( ) INTO DATA(lo_alias).
      IF lo_alias->get_aliasname( ) = av_alias_name_2.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Alias should be deleted'
    ).

  ENDMETHOD.

  METHOD create_grant.
    DATA lo_result TYPE REF TO /aws1/cl_kmscreategrantrsp.
    DATA lt_operations TYPE /aws1/cl_kmsgrantoplist_w=>tt_grantoperationlist.

    " Verify key and role exist
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.
    IF av_role_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'IAM role not created in class_setup' ).
    ENDIF.

    APPEND 'Encrypt' TO lt_operations.
    APPEND 'Decrypt' TO lt_operations.
    APPEND 'GenerateDataKey' TO lt_operations.

    ao_kms_actions->create_grant(
      EXPORTING
        iv_key_id = av_key_id
        iv_grantee_principal = av_role_arn
        it_operations = lt_operations
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_grantid( )
      msg = 'Grant ID should not be empty'
    ).

    " Clean up grant
    ao_kms->revokegrant(
      iv_keyid = av_key_id
      iv_grantid = lo_result->get_grantid( )
    ).

  ENDMETHOD.

  METHOD list_grants.
    DATA lo_result TYPE REF TO /aws1/cl_kmslistgrantsresponse.
    DATA lt_operations TYPE /aws1/cl_kmsgrantoplist_w=>tt_grantoperationlist.

    " Verify key and role exist
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.
    IF av_role_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'IAM role not created in class_setup' ).
    ENDIF.

    " Create a grant first
    APPEND 'Encrypt' TO lt_operations.
    DATA(lo_grant) = ao_kms->creategrant(
      iv_keyid = av_key_id
      iv_granteeprincipal = av_role_arn
      it_operations = lt_operations
    ).

    " Test listing grants
    ao_kms_actions->list_grants(
      EXPORTING
        iv_key_id = av_key_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'List grants result should be bound'
    ).

    " Clean up grant
    ao_kms->revokegrant(
      iv_keyid = av_key_id
      iv_grantid = lo_grant->get_grantid( )
    ).

  ENDMETHOD.

  METHOD retire_grant.
    DATA lt_operations TYPE /aws1/cl_kmsgrantoplist_w=>tt_grantoperationlist.

    " Verify key and role exist
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.
    IF av_role_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'IAM role not created in class_setup' ).
    ENDIF.

    " Create a grant first
    APPEND 'Encrypt' TO lt_operations.
    DATA(lo_grant) = ao_kms->creategrant(
      iv_keyid = av_key_id
      iv_granteeprincipal = av_role_arn
      it_operations = lt_operations
    ).

    " Test retiring the grant
    ao_kms_actions->retire_grant( iv_grant_token = lo_grant->get_granttoken( ) ).

    " Verify grant is retired - should not be found in list
    DATA(lo_grants) = ao_kms->listgrants( iv_keyid = av_key_id ).
    DATA(lv_found) = abap_false.

    LOOP AT lo_grants->get_grants( ) INTO DATA(lo_grant_entry).
      IF lo_grant_entry->get_grantid( ) = lo_grant->get_grantid( ).
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Retired grant should not be in the list'
    ).

  ENDMETHOD.

  METHOD revoke_grant.
    DATA lt_operations TYPE /aws1/cl_kmsgrantoplist_w=>tt_grantoperationlist.

    " Verify key and role exist
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.
    IF av_role_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'IAM role not created in class_setup' ).
    ENDIF.

    " Create a grant first
    APPEND 'Encrypt' TO lt_operations.
    DATA(lo_grant) = ao_kms->creategrant(
      iv_keyid = av_key_id
      iv_granteeprincipal = av_role_arn
      it_operations = lt_operations
    ).

    " Test revoking the grant
    ao_kms_actions->revoke_grant(
      iv_key_id = av_key_id
      iv_grant_id = lo_grant->get_grantid( )
    ).

    " Verify grant is revoked
    DATA(lo_grants) = ao_kms->listgrants( iv_keyid = av_key_id ).
    DATA(lv_found) = abap_false.

    LOOP AT lo_grants->get_grants( ) INTO DATA(lo_grant_entry).
      IF lo_grant_entry->get_grantid( ) = lo_grant->get_grantid( ).
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Revoked grant should not be in the list'
    ).

  ENDMETHOD.

  METHOD get_key_policy.
    DATA lo_result TYPE REF TO /aws1/cl_kmsgetkeypolicyrsp.

    " Verify key exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    ao_kms_actions->get_key_policy(
      EXPORTING
        iv_key_id = av_key_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_policy( )
      msg = 'Key policy should not be empty'
    ).

  ENDMETHOD.

  METHOD put_key_policy.
    " Verify key exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    " Get current policy
    DATA(lo_current_policy) = ao_kms->getkeypolicy(
      iv_keyid = av_key_id
      iv_policyname = 'default'
    ).

    " Test putting the same policy back
    ao_kms_actions->put_key_policy(
      iv_key_id = av_key_id
      iv_policy = lo_current_policy->get_policy( )
    ).

    " Verify policy is still there
    DATA(lo_new_policy) = ao_kms->getkeypolicy(
      iv_keyid = av_key_id
      iv_policyname = 'default'
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_new_policy->get_policy( )
      msg = 'Key policy should still exist'
    ).

  ENDMETHOD.

  METHOD list_key_policies.
    DATA lo_result TYPE REF TO /aws1/cl_kmslistkeypolresponse.

    " Verify key exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    ao_kms_actions->list_key_policies(
      EXPORTING
        iv_key_id = av_key_id
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_true(
      act = xsdbool( lines( lo_result->get_policynames( ) ) > 0 )
      msg = 'Should return at least one policy name'
    ).

  ENDMETHOD.

  METHOD encrypt.
    DATA lo_result TYPE REF TO /aws1/cl_kmsencryptresponse.
    DATA lv_plaintext TYPE /aws1/kmsplaintexttype.

    " Verify key exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    lv_plaintext = cl_abap_codepage=>convert_to( source = 'Test message for encryption' ).

    ao_kms_actions->encrypt(
      EXPORTING
        iv_key_id = av_key_id
        iv_plaintext = lv_plaintext
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_ciphertextblob( )
      msg = 'Ciphertext should not be empty'
    ).

  ENDMETHOD.

  METHOD decrypt.
    DATA lo_result TYPE REF TO /aws1/cl_kmsdecryptresponse.
    DATA lv_plaintext TYPE /aws1/kmsplaintexttype.

    " Verify key exists
    IF av_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test key not created in class_setup' ).
    ENDIF.

    " First encrypt some data
    lv_plaintext = cl_abap_codepage=>convert_to( source = 'Test message for decryption' ).
    DATA(lo_encrypt_result) = ao_kms->encrypt(
      iv_keyid = av_key_id
      iv_plaintext = lv_plaintext
    ).

    " Now test decryption
    ao_kms_actions->decrypt(
      EXPORTING
        iv_key_id = av_key_id
        iv_ciphertext_blob = lo_encrypt_result->get_ciphertextblob( )
      IMPORTING
        oo_result = lo_result
    ).

    DATA(lv_decrypted) = cl_abap_codepage=>convert_from( source = lo_result->get_plaintext( ) ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'Test message for decryption'
      act = lv_decrypted
      msg = 'Decrypted text should match original'
    ).

  ENDMETHOD.

  METHOD re_encrypt.
    DATA lo_result TYPE REF TO /aws1/cl_kmsreencryptresponse.
    DATA lv_plaintext TYPE /aws1/kmsplaintexttype.

    " Verify keys exist
    IF av_key_id IS INITIAL OR av_key_id_2 IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Test keys not created in class_setup' ).
    ENDIF.

    " First encrypt with first key
    lv_plaintext = cl_abap_codepage=>convert_to( source = 'Test message for re-encryption' ).
    DATA(lo_encrypt_result) = ao_kms->encrypt(
      iv_keyid = av_key_id
      iv_plaintext = lv_plaintext
    ).

    " Now test re-encryption to second key
    ao_kms_actions->re_encrypt(
      EXPORTING
        iv_source_key_id = av_key_id
        iv_destination_key_id = av_key_id_2
        iv_ciphertext_blob = lo_encrypt_result->get_ciphertextblob( )
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_ciphertextblob( )
      msg = 'Re-encrypted ciphertext should not be empty'
    ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_key_id_2
      act = lo_result->get_keyid( )
      msg = 'Re-encrypted data should be encrypted with second key'
    ).

  ENDMETHOD.

  METHOD sign.
    DATA lo_result TYPE REF TO /aws1/cl_kmssignresponse.
    DATA lv_message TYPE /aws1/kmsplaintexttype.

    " Verify asymmetric key exists
    IF av_asym_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Asymmetric test key not created in class_setup' ).
    ENDIF.

    lv_message = cl_abap_codepage=>convert_to( source = 'Message to sign' ).

    ao_kms_actions->sign(
      EXPORTING
        iv_key_id = av_asym_key_id
        iv_message = lv_message
        iv_signing_algorithm = 'RSASSA_PSS_SHA_256'
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_signature( )
      msg = 'Signature should not be empty'
    ).

  ENDMETHOD.

  METHOD verify.
    DATA lo_result TYPE REF TO /aws1/cl_kmsverifyresponse.
    DATA lv_message TYPE /aws1/kmsplaintexttype.

    " Verify asymmetric key exists
    IF av_asym_key_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Asymmetric test key not created in class_setup' ).
    ENDIF.

    " First sign a message
    lv_message = cl_abap_codepage=>convert_to( source = 'Message to verify' ).
    DATA(lo_sign_result) = ao_kms->sign(
      iv_keyid = av_asym_key_id
      iv_message = lv_message
      iv_signingalgorithm = 'RSASSA_PSS_SHA_256'
    ).

    " Now test verification
    ao_kms_actions->verify(
      EXPORTING
        iv_key_id = av_asym_key_id
        iv_message = lv_message
        iv_signature = lo_sign_result->get_signature( )
        iv_signing_algorithm = 'RSASSA_PSS_SHA_256'
      IMPORTING
        oo_result = lo_result
    ).

    cl_abap_unit_assert=>assert_true(
      act = lo_result->get_signaturevalid( )
      msg = 'Signature should be valid'
    ).

  ENDMETHOD.

ENDCLASS.
