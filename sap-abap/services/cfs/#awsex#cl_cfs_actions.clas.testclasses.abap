" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_cfs_actions DEFINITION DEFERRED.
CLASS /awsex/cl_cfs_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_cfs_actions.

CLASS ltc_awsex_cl_cfs_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_rule_name_put TYPE /aws1/cfsconfigrulename.
    CLASS-DATA av_rule_name_describe TYPE /aws1/cfsconfigrulename.
    CLASS-DATA av_rule_name_delete TYPE /aws1/cfsconfigrulename.
    CLASS-DATA av_config_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_config_recorder_name TYPE /aws1/cfsrecordername.
    CLASS-DATA av_delivery_channel_name TYPE /aws1/cfschannelname.
    CLASS-DATA av_config_role_name TYPE /aws1/iamrolename.
    CLASS-DATA av_config_role_arn TYPE /aws1/cfsarn.

    CLASS-DATA ao_cfs TYPE REF TO /aws1/if_cfs.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_cfs_actions TYPE REF TO /awsex/cl_cfs_actions.

    METHODS: put_config_rule FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_config_rule FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_config_rule FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown.

    CLASS-METHODS setup_config_recorder RAISING /aws1/cx_rt_generic.
    CLASS-METHODS create_config_role RAISING /aws1/cx_rt_generic.
    CLASS-METHODS cleanup_config_recorder.

    METHODS wait_for_rule_creation
      IMPORTING
        iv_rule_name TYPE /aws1/cfsconfigrulename
      RAISING
        /aws1/cx_rt_generic.

    METHODS assert_rule_exists
      IMPORTING
        iv_rule_name TYPE /aws1/cfsconfigrulename
        iv_exp       TYPE abap_bool
        iv_msg       TYPE string
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_cfs_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_cfs = /aws1/cl_cfs_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_cfs_actions = NEW /awsex/cl_cfs_actions( ).

    " Setup AWS Config prerequisites
    DATA(lv_account_id) = ao_session->get_account_id( ).
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.

    " Create S3 bucket for Config
    av_config_bucket = |config-test-{ lv_account_id }|.
    CONDENSE av_config_bucket NO-GAPS.

    " Create IAM role for Config
    av_config_role_name = |ConfigRole-{ lv_uuid_string }|.
    CONDENSE av_config_role_name NO-GAPS.

    " Create recorder and delivery channel names
    av_config_recorder_name = |default|.
    av_delivery_channel_name = |default|.

    " Setup Config service (bucket, role, recorder, delivery channel)
    setup_config_recorder( ).

    " Create unique rule names for testing using utility function
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    av_rule_name_put = |sap-abap-cfs-rule-put-{ lv_uuid_string }|.

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    av_rule_name_describe = |sap-abap-cfs-rule-dsc-{ lv_uuid_string }|.

    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_uuid_string = lv_uuid.
    CONDENSE lv_uuid_string NO-GAPS.
    av_rule_name_delete = |sap-abap-cfs-rule-del-{ lv_uuid_string }|.

    " Create rule for describe test with convert_test tag
    ao_cfs->putconfigrule(
      io_configrule = NEW /aws1/cl_cfsconfigrule(
        iv_configrulename = av_rule_name_describe
        iv_description = |Test S3 Public Read Rule for Describe|
        io_scope = NEW /aws1/cl_cfsscope(
          it_complianceresourcetypes = VALUE /aws1/cl_cfscplncresrctypes_w=>tt_complianceresourcetypes(
            ( NEW /aws1/cl_cfscplncresrctypes_w( |AWS::S3::Bucket| ) )
          )
        )
        io_source = NEW /aws1/cl_cfssource(
          iv_owner = |AWS|
          iv_sourceidentifier = |S3_BUCKET_PUBLIC_READ_PROHIBITED|
        )
        iv_inputparameters = '{}'
        iv_configrulestate = |ACTIVE|
      )
      it_tags = VALUE /aws1/cl_cfstag=>tt_tagslist(
        ( NEW /aws1/cl_cfstag(
            iv_key = |convert_test|
            iv_value = |true|
          ) )
      )
    ).

    " Wait for describe rule to be available
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed_seconds TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          DATA(lo_check_result) = ao_cfs->describeconfigrules(
            it_configrulenames = VALUE /aws1/cl_cfsconfigrulenames_w=>tt_configrulenames(
              ( NEW /aws1/cl_cfsconfigrulenames_w( av_rule_name_describe ) )
            )
          ).
          IF lo_check_result IS BOUND AND lo_check_result->get_configrules( ) IS NOT INITIAL.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Rule not yet available
      ENDTRY.

      WAIT UP TO 2 SECONDS.

      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_seconds = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_seconds > 120.
        EXIT.
      ENDIF.
    ENDDO.

    " Create rule for delete test with convert_test tag
    ao_cfs->putconfigrule(
      io_configrule = NEW /aws1/cl_cfsconfigrule(
        iv_configrulename = av_rule_name_delete
        iv_description = |Test S3 Public Read Rule for Delete|
        io_scope = NEW /aws1/cl_cfsscope(
          it_complianceresourcetypes = VALUE /aws1/cl_cfscplncresrctypes_w=>tt_complianceresourcetypes(
            ( NEW /aws1/cl_cfscplncresrctypes_w( |AWS::S3::Bucket| ) )
          )
        )
        io_source = NEW /aws1/cl_cfssource(
          iv_owner = |AWS|
          iv_sourceidentifier = |S3_BUCKET_PUBLIC_READ_PROHIBITED|
        )
        iv_inputparameters = '{}'
        iv_configrulestate = |ACTIVE|
      )
      it_tags = VALUE /aws1/cl_cfstag=>tt_tagslist(
        ( NEW /aws1/cl_cfstag(
            iv_key = |convert_test|
            iv_value = |true|
          ) )
      )
    ).

    " Wait for delete rule to be available
    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          lo_check_result = ao_cfs->describeconfigrules(
            it_configrulenames = VALUE /aws1/cl_cfsconfigrulenames_w=>tt_configrulenames(
              ( NEW /aws1/cl_cfsconfigrulenames_w( av_rule_name_delete ) )
            )
          ).
          IF lo_check_result IS BOUND AND lo_check_result->get_configrules( ) IS NOT INITIAL.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Rule not yet available
      ENDTRY.

      WAIT UP TO 2 SECONDS.

      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_seconds = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_seconds > 120.
        EXIT.
      ENDIF.
    ENDDO.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up any remaining test rules
    TRY.
        ao_cfs->deleteconfigrule( av_rule_name_put ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors during cleanup
    ENDTRY.

    TRY.
        ao_cfs->deleteconfigrule( av_rule_name_describe ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors during cleanup
    ENDTRY.

    TRY.
        ao_cfs->deleteconfigrule( av_rule_name_delete ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors during cleanup
    ENDTRY.

    " Cleanup Config recorder (this will handle stopping and deleting)
    cleanup_config_recorder( ).

    " Cleanup IAM role
    TRY.
        ao_iam->deleterole( iv_rolename = av_config_role_name ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors during cleanup
    ENDTRY.

    " Note: S3 bucket is tagged with convert_test and will be manually cleaned up
    " to avoid issues with Config recorder dependencies

  ENDMETHOD.

  METHOD put_config_rule.
    TRY.
        ao_cfs_actions->put_config_rule( av_rule_name_put ).

        " Wait for rule creation to propagate
        wait_for_rule_creation( av_rule_name_put ).

        " Verify the rule was created
        assert_rule_exists(
          iv_rule_name = av_rule_name_put
          iv_exp = abap_true
          iv_msg = |Config rule { av_rule_name_put }  was not created| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Failed to create config rule: { lo_exception->get_text( ) } | ).
    ENDTRY.

  ENDMETHOD.

  METHOD describe_config_rule.
    TRY.
        DATA(lt_rules) = ao_cfs_actions->describe_config_rule( av_rule_name_describe ).

        " Verify we got at least one rule back
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_rules
          msg = |No config rules returned for { av_rule_name_describe } | ).

        " Verify the rule name matches
        DATA(lv_found) = abap_false.
        LOOP AT lt_rules INTO DATA(lo_rule).
          IF lo_rule->get_configrulename( ) = av_rule_name_describe.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Config rule { av_rule_name_describe }  not found in results| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Failed to describe config rule: { lo_exception->get_text( ) } | ).
    ENDTRY.

  ENDMETHOD.

  METHOD delete_config_rule.
    TRY.
        ao_cfs_actions->delete_config_rule( av_rule_name_delete ).

        " Wait for deletion to propagate
        WAIT UP TO 5 SECONDS.

        " Verify the rule was deleted
        assert_rule_exists(
          iv_rule_name = av_rule_name_delete
          iv_exp = abap_false
          iv_msg = |Config rule { av_rule_name_delete }  should have been deleted| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( |Failed to delete config rule: { lo_exception->get_text( ) } | ).
    ENDTRY.

  ENDMETHOD.

  METHOD wait_for_rule_creation.
    " Wait for up to 60 seconds for the rule to be available
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed_seconds TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          DATA(lo_result) = ao_cfs->describeconfigrules(
            it_configrulenames = VALUE /aws1/cl_cfsconfigrulenames_w=>tt_configrulenames(
              ( NEW /aws1/cl_cfsconfigrulenames_w( iv_rule_name ) )
            )
          ).
          IF lo_result IS BOUND AND lo_result->get_configrules( ) IS NOT INITIAL.
            " Rule is available
            RETURN.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Rule not yet available, continue waiting
      ENDTRY.

      WAIT UP TO 2 SECONDS.

      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_seconds = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_seconds > 60.
        " Timeout after 60 seconds
        EXIT.
      ENDIF.
    ENDDO.

  ENDMETHOD.

  METHOD assert_rule_exists.
    DATA lv_found TYPE abap_bool.
    lv_found = abap_false.

    TRY.
        DATA(lo_result) = ao_cfs->describeconfigrules(
          it_configrulenames = VALUE /aws1/cl_cfsconfigrulenames_w=>tt_configrulenames(
            ( NEW /aws1/cl_cfsconfigrulenames_w( iv_rule_name ) )
          )
        ).
        IF lo_result IS BOUND AND lo_result->get_configrules( ) IS NOT INITIAL.
          lv_found = abap_true.
        ENDIF.
      CATCH /aws1/cx_cfsnosuchconfigruleex.
        lv_found = abap_false.
      CATCH /aws1/cx_rt_generic.
        lv_found = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_equals(
      act = lv_found
      exp = iv_exp
      msg = iv_msg ).

  ENDMETHOD.

  METHOD create_config_role.
    " Create IAM role for AWS Config with proper trust policy
    DATA(lv_assume_role_policy) = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"config.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}]| &&
      |\}|.

    TRY.
        DATA(lo_role_result) = ao_iam->createrole(
          iv_rolename = av_config_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          iv_description = |IAM role for AWS Config test|
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag(
                iv_key = |convert_test|
                iv_value = |true|
              ) )
          )
        ).
        av_config_role_arn = lo_role_result->get_role( )->get_arn( ).
      CATCH /aws1/cx_iamentityalrdyexists.
        " Role already exists, get its ARN
        DATA(lo_get_role) = ao_iam->getrole( iv_rolename = av_config_role_name ).
        av_config_role_arn = lo_get_role->get_role( )->get_arn( ).
    ENDTRY.

    " Attach AWS managed policy for Config
    TRY.
        ao_iam->attachrolepolicy(
          iv_rolename = av_config_role_name
          iv_policyarn = |arn:aws:iam::aws:policy/service-role/ConfigRole|
        ).
      CATCH /aws1/cx_rt_generic.
        " Policy may already be attached
    ENDTRY.

    " Wait for role to propagate
    WAIT UP TO 10 SECONDS.

  ENDMETHOD.

  METHOD setup_config_recorder.
    " Create IAM role
    create_config_role( ).

    " Create S3 bucket for Config with tagging
    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_config_bucket
          io_s3 = ao_s3
          io_session = ao_session
        ).

        " Tag the bucket
        ao_s3->putbuckettagging(
          iv_bucket = av_config_bucket
          io_tagging = NEW /aws1/cl_s3_tagging(
            it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
              ( NEW /aws1/cl_s3_tag(
                  iv_key = |convert_test|
                  iv_value = |true|
                ) )
            )
          )
        ).
      CATCH /aws1/cx_s3_bucketalrdyexists /aws1/cx_s3_bucketalrdyownedbyu.
        " Bucket already exists
    ENDTRY.

    " Create configuration recorder
    TRY.
        ao_cfs->putconfigurationrecorder(
          io_configurationrecorder = NEW /aws1/cl_cfsconfrecorder(
            iv_name = av_config_recorder_name
            iv_rolearn = av_config_role_arn
            io_recordinggroup = NEW /aws1/cl_cfsrecordinggroup(
              iv_allsupported = abap_true
              iv_inclglobalresourcetypes = abap_true
            )
          )
        ).
      CATCH /aws1/cx_rt_generic.
        " Recorder may already exist
    ENDTRY.

    " Create delivery channel
    TRY.
        ao_cfs->putdeliverychannel(
          io_deliverychannel = NEW /aws1/cl_cfsdeliverychannel(
            iv_name = av_delivery_channel_name
            iv_s3bucketname = av_config_bucket
          )
        ).
      CATCH /aws1/cx_rt_generic.
        " Channel may already exist
    ENDTRY.

    " Start configuration recorder
    TRY.
        ao_cfs->startconfigurationrecorder(
          iv_configurationrecordername = av_config_recorder_name
        ).
      CATCH /aws1/cx_rt_generic.
        " Recorder may already be running
    ENDTRY.

    " Wait for recorder to start
    WAIT UP TO 10 SECONDS.

  ENDMETHOD.

  METHOD cleanup_config_recorder.
    " Stop configuration recorder
    TRY.
        ao_cfs->stopconfigurationrecorder(
          iv_configurationrecordername = av_config_recorder_name
        ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors
    ENDTRY.

    WAIT UP TO 5 SECONDS.

    " Delete delivery channel
    TRY.
        ao_cfs->deletedeliverychannel(
          iv_deliverychannel = av_delivery_channel_name
        ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors
    ENDTRY.

    " Delete configuration recorder
    TRY.
        ao_cfs->deleteconfigurationrecorder(
          iv_configurationrecordername = av_config_recorder_name
        ).
      CATCH /aws1/cx_rt_generic.
        " Ignore errors
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
