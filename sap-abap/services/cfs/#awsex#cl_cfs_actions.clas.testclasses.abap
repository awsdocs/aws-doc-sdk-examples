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
    CLASS-DATA av_config_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_config_role_arn TYPE /aws1/cfsstring.

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

    METHODS wait_for_rule_deletion
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

    METHODS create_and_wait_for_rule
      IMPORTING
        iv_rule_name    TYPE /aws1/cfsconfigrulename
        iv_description  TYPE /aws1/cfsstring
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
    create_and_wait_for_rule(
      iv_rule_name = av_rule_name_describe
      iv_description = |Test S3 Public Read Rule for Describe|
    ).

    " Create rule for delete test with convert_test tag
    create_and_wait_for_rule(
      iv_rule_name = av_rule_name_delete
      iv_description = |Test S3 Public Read Rule for Delete|
    ).

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

    " Detach AWS_ConfigRole managed policy before deleting role
    TRY.
        ao_iam->detachrolepolicy(
          iv_rolename = av_config_role_name
          iv_policyarn = |arn:aws:iam::aws:policy/service-role/AWS_ConfigRole|
        ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Policy not attached
      CATCH /aws1/cx_iaminvalidinputex
            /aws1/cx_iamsvccfailureex INTO DATA(lo_detach_ex).
        " Log but continue cleanup
        WRITE: / |Error detaching AWS_ConfigRole policy: { lo_detach_ex->get_text( ) }|.
    ENDTRY.

    " Delete inline ConfigS3Policy before deleting role
    TRY.
        ao_iam->deleterolepolicy(
          iv_rolename = av_config_role_name
          iv_policyname = |ConfigS3Policy|
        ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Policy doesn't exist
      CATCH /aws1/cx_iaminvalidinputex
            /aws1/cx_iamsvccfailureex INTO DATA(lo_inline_ex).
        " Log but continue cleanup
        WRITE: / |Error deleting inline policy: { lo_inline_ex->get_text( ) }|.
    ENDTRY.

    " Delete IAM role
    TRY.
        ao_iam->deleterole( iv_rolename = av_config_role_name ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Role doesn't exist
      CATCH /aws1/cx_iamdelconflictex
            /aws1/cx_iaminvalidinputex INTO DATA(lo_role_ex).
        " Log but continue cleanup
        WRITE: / |Error deleting IAM role: { lo_role_ex->get_text( ) }|.
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

        " Wait for deletion to propagate (Config rule deletion is asynchronous)
        DATA lv_start_time TYPE timestamp.
        DATA lv_current_time TYPE timestamp.
        DATA lv_elapsed_seconds TYPE i.
        DATA lv_deleted TYPE abap_bool.
        lv_deleted = abap_false.

        GET TIME STAMP FIELD lv_start_time.

        " Poll for up to 60 seconds to verify deletion
        DO.
          TRY.
              DATA(lo_result) = ao_cfs->describeconfigrules(
                it_configrulenames = VALUE /aws1/cl_cfsconfigrulenames_w=>tt_configrulenames(
                  ( NEW /aws1/cl_cfsconfigrulenames_w( av_rule_name_delete ) )
                )
              ).
              " If we get here without exception, rule still exists
              IF lo_result IS INITIAL OR lo_result->get_configrules( ) IS INITIAL.
                lv_deleted = abap_true.
                EXIT.
              ENDIF.
            CATCH /aws1/cx_cfsnosuchconfigruleex.
              " Rule has been deleted
              lv_deleted = abap_true.
              EXIT.
            CATCH /aws1/cx_rt_generic.
              " Rule may have been deleted
              lv_deleted = abap_true.
              EXIT.
          ENDTRY.

          WAIT UP TO 3 SECONDS.

          GET TIME STAMP FIELD lv_current_time.
          lv_elapsed_seconds = cl_abap_tstmp=>subtract(
            tstmp1 = lv_current_time
            tstmp2 = lv_start_time ).

          IF lv_elapsed_seconds > 60.
            " Timeout after 60 seconds
            EXIT.
          ENDIF.
        ENDDO.

        " Verify the rule was deleted
        cl_abap_unit_assert=>assert_true(
          act = lv_deleted
          msg = |Config rule { av_rule_name_delete } should have been deleted| ).

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
      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists, get its ARN
        DATA(lo_get_role) = ao_iam->getrole( iv_rolename = av_config_role_name ).
        av_config_role_arn = lo_get_role->get_role( )->get_arn( ).
    ENDTRY.

    " Attach AWS managed policy for Config (updated from deprecated ConfigRole to AWS_ConfigRole)
    TRY.
        ao_iam->attachrolepolicy(
          iv_rolename = av_config_role_name
          iv_policyarn = |arn:aws:iam::aws:policy/service-role/AWS_ConfigRole|
        ).
      CATCH /aws1/cx_iaminvalidinputex
            /aws1/cx_iamnosuchentityex INTO DATA(lo_attach_ex).
        " Policy attachment failed
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            previous = lo_attach_ex.
      CATCH /aws1/cx_iampolicynotattachex.
        " Policy may already be attached, continue
    ENDTRY.

    " Create inline policy for S3 bucket access and additional Config permissions
    DATA(lv_s3_policy) = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Action":[| &&
      |"s3:GetBucketVersioning",| &&
      |"s3:PutObject",| &&
      |"s3:GetObject",| &&
      |"s3:ListBucket"| &&
      |],| &&
      |"Resource":[| &&
      |"arn:aws:s3:::{ av_config_bucket }",| &&
      |"arn:aws:s3:::{ av_config_bucket }/*"| &&
      |]| &&
      |\},| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Action":[| &&
      |"config:ListDiscoveredResources"| &&
      |],| &&
      |"Resource":"*"| &&
      |\}]| &&
      |\}|.

    TRY.
        ao_iam->putrolepolicy(
          iv_rolename = av_config_role_name
          iv_policyname = |ConfigS3Policy|
          iv_policydocument = lv_s3_policy
        ).
      CATCH /aws1/cx_iaminvalidinputex
            /aws1/cx_iamnosuchentityex INTO DATA(lo_policy_ex).
        " Inline policy creation failed
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            previous = lo_policy_ex.
    ENDTRY.

    " Wait for role and policies to propagate
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
      CATCH /aws1/cx_s3_bucketalrdyexists /aws1/cx_s3_bktalrdyownedbyyou.
        " Bucket already exists, continue
      CATCH /aws1/cx_s3_bucketnotfound
            /aws1/cx_s3_nosuchbucket INTO DATA(lo_s3_ex).
        " S3 bucket creation failed
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            previous = lo_s3_ex.
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
      CATCH /aws1/cx_cfsmaxnumconfigrecrdsex.
        " Maximum number of recorders already exists, try to use existing
      CATCH /aws1/cx_cfsinvalidparamvalueex
            /aws1/cx_cfsinvalidroleex
            /aws1/cx_cfsinvldrecordnameex INTO DATA(lo_recorder_ex).
        " Configuration recorder creation failed with invalid parameters
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            previous = lo_recorder_ex.
    ENDTRY.

    " Create delivery channel
    TRY.
        ao_cfs->putdeliverychannel(
          io_deliverychannel = NEW /aws1/cl_cfsdeliverychannel(
            iv_name = av_delivery_channel_name
            iv_s3bucketname = av_config_bucket
          )
        ).
      CATCH /aws1/cx_cfsmaxnumdlvrychnlsex.
        " Maximum number of channels already exists, try to use existing
      CATCH /aws1/cx_cfsinsufficientdlvryplc
            /aws1/cx_cfsinvalidparamvalueex
            /aws1/cx_cfsinvalids3keyprefix
            /aws1/cx_cfsinvalidsnstopiarn
            /aws1/cx_cfsnosuchtopicex INTO DATA(lo_channel_ex).
        " Delivery channel creation failed
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            previous = lo_channel_ex.
    ENDTRY.

    " Start configuration recorder
    TRY.
        ao_cfs->startconfigurationrecorder(
          iv_configurationrecordername = av_config_recorder_name
        ).
      CATCH /aws1/cx_cfsnoavailbledlvrychnl.
        " No available delivery channel - this is a critical error
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            textid = /aws1/cx_rt_generic=>generic_error
            msgv1  = 'No available delivery channel to start recorder'.
      CATCH /aws1/cx_cfsnosuchconfigrecrdrex.
        " Configuration recorder doesn't exist - this is a critical error
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic
          EXPORTING
            textid = /aws1/cx_rt_generic=>generic_error
            msgv1  = 'Configuration recorder does not exist'.
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
          iv_deliverychannelname = av_delivery_channel_name
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

  METHOD create_and_wait_for_rule.
    " Create a config rule with standard settings and wait for it to be available
    ao_cfs->putconfigrule(
      io_configrule = NEW /aws1/cl_cfsconfigrule(
        iv_configrulename = iv_rule_name
        iv_description = iv_description
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

    " Wait for rule to be available
    wait_for_rule_creation( iv_rule_name ).

  ENDMETHOD.

ENDCLASS.
