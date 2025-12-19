CLASS ltc_awsex_cl_iam_actions DEFINITION DEFERRED.
CLASS /awsex/cl_iam_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_iam_actions.

CLASS ltc_awsex_cl_iam_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_iam_actions TYPE REF TO /awsex/cl_iam_actions.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.

    " Test resource names created in class_setup
    CLASS-DATA av_test_user_name TYPE /aws1/iamusernametype.
    CLASS-DATA av_test_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_test_policy_name TYPE /aws1/iampolicynametype.
    CLASS-DATA av_test_policy_arn TYPE /aws1/iamarntype.
    CLASS-DATA av_test_bucket_name TYPE /aws1/s3_bucketname.
    
    " Temporary resource names for individual tests
    CLASS-DATA av_user_name TYPE /aws1/iamusernametype.
    CLASS-DATA av_user_name_2 TYPE /aws1/iamusernametype.
    CLASS-DATA av_policy_name TYPE /aws1/iampolicynametype.
    CLASS-DATA av_policy_arn TYPE /aws1/iamarntype.
    CLASS-DATA av_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_role_name_2 TYPE /aws1/iamrolenametype.
    CLASS-DATA av_bucket_name TYPE /aws1/s3_bucketname.
    CLASS-DATA av_alias_name TYPE /aws1/iamaccountaliastype.

    METHODS: create_user FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_user FOR TESTING RAISING /aws1/cx_rt_generic,
      list_users FOR TESTING RAISING /aws1/cx_rt_generic,
      update_user FOR TESTING RAISING /aws1/cx_rt_generic,
      create_access_key FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_access_key FOR TESTING RAISING /aws1/cx_rt_generic,
      list_access_keys FOR TESTING RAISING /aws1/cx_rt_generic,
      update_access_key FOR TESTING RAISING /aws1/cx_rt_generic,
      get_access_key_last_used FOR TESTING RAISING /aws1/cx_rt_generic,
      create_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      list_policies FOR TESTING RAISING /aws1/cx_rt_generic,
      get_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      create_policy_version FOR TESTING RAISING /aws1/cx_rt_generic,
      attach_user_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      detach_user_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      create_role FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_role FOR TESTING RAISING /aws1/cx_rt_generic,
      get_role FOR TESTING RAISING /aws1/cx_rt_generic,
      list_roles FOR TESTING RAISING /aws1/cx_rt_generic,
      attach_role_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      detach_role_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      list_attached_role_policies FOR TESTING RAISING /aws1/cx_rt_generic,
      list_role_policies FOR TESTING RAISING /aws1/cx_rt_generic,
      list_groups FOR TESTING RAISING /aws1/cx_rt_generic,
      create_account_alias FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_account_alias FOR TESTING RAISING /aws1/cx_rt_generic,
      list_account_aliases FOR TESTING RAISING /aws1/cx_rt_generic,
      get_account_authorization_det FOR TESTING RAISING /aws1/cx_rt_generic,
      get_account_summary FOR TESTING RAISING /aws1/cx_rt_generic,
      generate_credential_report FOR TESTING RAISING /aws1/cx_rt_generic,
      get_credential_report FOR TESTING RAISING /aws1/cx_rt_generic,
      get_account_password_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      list_saml_providers FOR TESTING RAISING /aws1/cx_rt_generic,
      create_service_linked_role FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS setup.
ENDCLASS.

CLASS ltc_awsex_cl_iam_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid_string TYPE string.
    DATA lv_policy_doc TYPE string.
    DATA lv_trust_policy TYPE string.
    DATA lt_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.

    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).
    ao_iam_actions = NEW /awsex/cl_iam_actions( ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).

    " Generate unique resource names
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    av_test_user_name = |test-user-{ lv_uuid_string(10) }|.
    av_user_name = |demo-user-{ lv_uuid_string(10) }|.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    av_user_name_2 = |demo-user2-{ lv_uuid_string(10) }|.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    av_test_policy_name = |test-policy-{ lv_uuid_string(10) }|.
    av_policy_name = |demo-policy-{ lv_uuid_string(10) }|.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    av_test_role_name = |test-role-{ lv_uuid_string(10) }|.
    av_role_name = |demo-role-{ lv_uuid_string(10) }|.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    av_role_name_2 = |demo-role2-{ lv_uuid_string(10) }|.

    DATA(lv_acct) = ao_session->get_account_id( ).
    av_test_bucket_name = |test-iam-bucket-{ lv_acct }|.
    av_bucket_name = |demo-iam-bucket-{ lv_acct }|.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    av_alias_name = |demo-alias-{ lv_uuid_string(10) }|.

    " Create tag for test resources
    lt_tags = VALUE #( ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) ) ).

    " Create test user with tags
    TRY.
        ao_iam->createuser(
          iv_username = av_test_user_name
          it_tags = lt_tags ).
        MESSAGE |Created test user: { av_test_user_name }| TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE |Test user already exists: { av_test_user_name }| TYPE 'I'.
    ENDTRY.

    " Wait for user propagation
    WAIT UP TO 3 SECONDS.

    " Create test policy
    lv_policy_doc = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":"s3:ListBucket","Resource":"arn:aws:s3:::*"}]}'.
    TRY.
        DATA(lo_policy_result) = ao_iam->createpolicy(
          iv_policyname = av_test_policy_name
          iv_policydocument = lv_policy_doc
          iv_description = 'Test policy for IAM unit tests'
          it_tags = lt_tags ).
        av_test_policy_arn = lo_policy_result->get_policy( )->get_arn( ).
        MESSAGE |Created test policy: { av_test_policy_name }| TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        " Policy exists, get its ARN
        DATA(lo_list_policies) = ao_iam->listpolicies( iv_scope = 'Local' ).
        LOOP AT lo_list_policies->get_policies( ) INTO DATA(lo_policy).
          IF lo_policy->get_policyname( ) = av_test_policy_name.
            av_test_policy_arn = lo_policy->get_arn( ).
            EXIT.
          ENDIF.
        ENDLOOP.
        MESSAGE |Test policy already exists: { av_test_policy_name }| TYPE 'I'.
    ENDTRY.

    " Wait for policy propagation
    WAIT UP TO 3 SECONDS.

    " Create test role
    lv_trust_policy = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"lambda.amazonaws.com"},"Action":"sts:AssumeRole"}]}'.
    TRY.
        ao_iam->createrole(
          iv_rolename = av_test_role_name
          iv_assumerolepolicydocument = lv_trust_policy
          iv_description = 'Test role for IAM unit tests'
          it_tags = lt_tags ).
        MESSAGE |Created test role: { av_test_role_name }| TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE |Test role already exists: { av_test_role_name }| TYPE 'I'.
    ENDTRY.

    " Wait for role propagation
    WAIT UP TO 3 SECONDS.

    " Create test S3 bucket
    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_test_bucket_name
          io_s3 = ao_s3
          io_session = ao_session ).
        " Tag the bucket
        ao_s3->putbuckettagging(
          iv_bucket = av_test_bucket_name
          io_tagging = NEW /aws1/cl_s3_tagging(
            it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
              ( NEW /aws1/cl_s3_tag( iv_key = 'TestType' iv_value = 'convert_test' ) )
            )
          )
        ).
        MESSAGE |Created test bucket: { av_test_bucket_name }| TYPE 'I'.
      CATCH /aws1/cx_s3_bucketalrdyexists /aws1/cx_s3_bktalrdyownedbyyou.
        MESSAGE |Test bucket already exists: { av_test_bucket_name }| TYPE 'I'.
    ENDTRY.

    " Verify all shared resources were created
    IF av_test_user_name IS INITIAL OR av_test_policy_arn IS INITIAL OR
       av_test_role_name IS INITIAL OR av_test_bucket_name IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create shared test resources in class_setup' ).
    ENDIF.

  ENDMETHOD.

  METHOD class_teardown.
    " Note: IAM resources are tagged with 'convert_test' tag for manual cleanup if needed
    " Cleanup shared test resources
    
    " Delete test user (must delete access keys first if any)
    IF av_test_user_name IS NOT INITIAL.
      TRY.
          DATA(lo_keys_result) = ao_iam->listaccesskeys( iv_username = av_test_user_name ).
          LOOP AT lo_keys_result->get_accesskeymetadata( ) INTO DATA(lo_key).
            TRY.
                ao_iam->deleteaccesskey(
                  iv_username = av_test_user_name
                  iv_accesskeyid = lo_key->get_accesskeyid( ) ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      " Detach all policies from user
      TRY.
          DATA(lo_attached) = ao_iam->listattacheduserpolicies( iv_username = av_test_user_name ).
          LOOP AT lo_attached->get_attachedpolicies( ) INTO DATA(lo_attached_policy).
            TRY.
                ao_iam->detachuserpolicy(
                  iv_username = av_test_user_name
                  iv_policyarn = lo_attached_policy->get_arn( ) ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      TRY.
          ao_iam->deleteuser( iv_username = av_test_user_name ).
          MESSAGE |Deleted test user: { av_test_user_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          MESSAGE |Could not delete test user: { av_test_user_name }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    " Delete test policy (must detach from all entities first)
    IF av_test_policy_arn IS NOT INITIAL.
      TRY.
          " List and detach from all users
          DATA(lo_entities) = ao_iam->listentitiesforpolicy( iv_policyarn = av_test_policy_arn ).
          LOOP AT lo_entities->get_policyusers( ) INTO DATA(lo_policy_user).
            TRY.
                ao_iam->detachuserpolicy(
                  iv_username = lo_policy_user->get_username( )
                  iv_policyarn = av_test_policy_arn ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.

          " List and detach from all roles
          LOOP AT lo_entities->get_policyroles( ) INTO DATA(lo_policy_role).
            TRY.
                ao_iam->detachrolepolicy(
                  iv_rolename = lo_policy_role->get_rolename( )
                  iv_policyarn = av_test_policy_arn ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      " Delete all non-default policy versions
      TRY.
          DATA(lo_versions) = ao_iam->listpolicyversions( iv_policyarn = av_test_policy_arn ).
          LOOP AT lo_versions->get_versions( ) INTO DATA(lo_version).
            IF lo_version->get_isdefaultversion( ) = abap_false.
              TRY.
                  ao_iam->deletepolicyversion(
                    iv_policyarn = av_test_policy_arn
                    iv_versionid = lo_version->get_versionid( ) ).
                CATCH /aws1/cx_rt_generic.
              ENDTRY.
            ENDIF.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      TRY.
          ao_iam->deletepolicy( iv_policyarn = av_test_policy_arn ).
          MESSAGE |Deleted test policy: { av_test_policy_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          MESSAGE |Could not delete test policy: { av_test_policy_name }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    " Delete test role (must detach all policies first)
    IF av_test_role_name IS NOT INITIAL.
      TRY.
          DATA(lo_role_policies) = ao_iam->listattachedrolepolicies( iv_rolename = av_test_role_name ).
          LOOP AT lo_role_policies->get_attachedpolicies( ) INTO DATA(lo_role_policy).
            TRY.
                ao_iam->detachrolepolicy(
                  iv_rolename = av_test_role_name
                  iv_policyarn = lo_role_policy->get_arn( ) ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      " Delete inline policies
      TRY.
          DATA(lo_inline_policies) = ao_iam->listrolepolicies( iv_rolename = av_test_role_name ).
          LOOP AT lo_inline_policies->get_policynames( ) INTO DATA(lv_policy_name).
            TRY.
                ao_iam->deleterolepolicy(
                  iv_rolename = av_test_role_name
                  iv_policyname = lv_policy_name ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
        CATCH /aws1/cx_rt_generic.
      ENDTRY.

      TRY.
          ao_iam->deleterole( iv_rolename = av_test_role_name ).
          MESSAGE |Deleted test role: { av_test_role_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          MESSAGE |Could not delete test role: { av_test_role_name }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    " Cleanup test bucket using utility function
    IF av_test_bucket_name IS NOT INITIAL.
      TRY.
          /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_test_bucket_name ).
          MESSAGE |Deleted test bucket: { av_test_bucket_name }| TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          MESSAGE |Could not delete test bucket: { av_test_bucket_name }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    " Clean up any remaining temporary resources
    IF av_user_name IS NOT INITIAL.
      TRY.
          ao_iam->deleteuser( iv_username = av_user_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_user_name_2 IS NOT INITIAL.
      TRY.
          ao_iam->deleteuser( iv_username = av_user_name_2 ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_policy_arn IS NOT INITIAL.
      TRY.
          ao_iam->deletepolicy( iv_policyarn = av_policy_arn ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_role_name IS NOT INITIAL.
      TRY.
          ao_iam->deleterole( iv_rolename = av_role_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_role_name_2 IS NOT INITIAL.
      TRY.
          ao_iam->deleterole( iv_rolename = av_role_name_2 ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_bucket_name IS NOT INITIAL.
      TRY.
          /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_bucket_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_alias_name IS NOT INITIAL.
      TRY.
          ao_iam->deleteaccountalias( iv_accountalias = av_alias_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD setup.
    " Each test method gets a fresh start
  ENDMETHOD.

  METHOD create_user.
    DATA lo_result TYPE REF TO /aws1/cl_iamcreateuserresponse.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_user TYPE /aws1/iamusernametype.
    DATA lt_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.

    " Generate unique user name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_user = |create-usr-{ lv_uuid_string(10) }|.

    " Tag for cleanup
    lt_tags = VALUE #( ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) ) ).

    ao_iam_actions->create_user(
      EXPORTING
        iv_user_name = lv_temp_user
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |User creation result should not be initial| ).

    DATA(lo_user) = lo_result->get_user( ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_temp_user
      act = lo_user->get_username( )
      msg = |User name should match| ).

    " Clean up
    ao_iam->deleteuser( iv_username = lv_temp_user ).
  ENDMETHOD.

  METHOD delete_user.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_user TYPE /aws1/iamusernametype.
    DATA lt_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.

    " Generate unique user name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_user = |delete-usr-{ lv_uuid_string(10) }|.

    " Tag for cleanup
    lt_tags = VALUE #( ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) ) ).

    " Create user first
    ao_iam->createuser( iv_username = lv_temp_user it_tags = lt_tags ).

    " Wait for user to propagate
    WAIT UP TO 3 SECONDS.

    " Delete user
    ao_iam_actions->delete_user( iv_user_name = lv_temp_user ).

    " Verify deletion
    TRY.
        ao_iam->getuser( iv_username = lv_temp_user ).
        cl_abap_unit_assert=>fail( msg = |User should have been deleted| ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Expected
    ENDTRY.
  ENDMETHOD.

  METHOD list_users.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistusersresponse.

    " Use the test user created in class_setup
    ao_iam_actions->list_users(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List users result should not be initial| ).

    DATA(lt_users) = lo_result->get_users( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_users
      msg = |User list should not be empty| ).

    " Verify our test user is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_users INTO DATA(lo_user).
      IF lo_user->get_username( ) = av_test_user_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test user should be in the list| ).
  ENDMETHOD.

  METHOD update_user.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_user TYPE /aws1/iamusernametype.
    DATA lv_new_user_name TYPE /aws1/iamusernametype.
    DATA lt_tags TYPE /aws1/cl_iamtag=>tt_taglisttype.

    " Generate unique user names for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_user = |update-usr-{ lv_uuid_string(10) }|.

    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_new_user_name = |update-new-{ lv_uuid_string(10) }|.

    " Tag for cleanup
    lt_tags = VALUE #( ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) ) ).

    " Create user
    ao_iam->createuser( iv_username = lv_temp_user it_tags = lt_tags ).
    WAIT UP TO 3 SECONDS.

    " Update user name
    ao_iam_actions->update_user(
      iv_user_name = lv_temp_user
      iv_new_user_name = lv_new_user_name ).

    " Wait for update propagation
    WAIT UP TO 3 SECONDS.

    " Verify update
    DATA(lo_result) = ao_iam->getuser( iv_username = lv_new_user_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_new_user_name
      act = lo_result->get_user( )->get_username( )
      msg = |User name should be updated| ).

    " Clean up
    ao_iam->deleteuser( iv_username = lv_new_user_name ).
  ENDMETHOD.

  METHOD create_access_key.
    DATA lo_result TYPE REF TO /aws1/cl_iamcreateaccesskeyrsp.

    " Use the test user created in class_setup
    ao_iam_actions->create_access_key(
      EXPORTING
        iv_user_name = av_test_user_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Access key creation result should not be initial| ).

    DATA(lo_key) = lo_result->get_accesskey( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_key->get_accesskeyid( )
      msg = |Access key ID should not be initial| ).

    " Clean up - delete the access key we just created
    ao_iam->deleteaccesskey(
      iv_username = av_test_user_name
      iv_accesskeyid = lo_key->get_accesskeyid( ) ).
  ENDMETHOD.

  METHOD delete_access_key.
    " Use the test user created in class_setup
    " Create an access key for testing deletion
    DATA(lo_create_result) = ao_iam->createaccesskey( iv_username = av_test_user_name ).
    DATA(lv_key_id) = lo_create_result->get_accesskey( )->get_accesskeyid( ).
    WAIT UP TO 3 SECONDS.

    ao_iam_actions->delete_access_key(
      iv_user_name = av_test_user_name
      iv_access_key_id = lv_key_id ).

    " Verify deletion by listing keys
    DATA(lo_list_result) = ao_iam->listaccesskeys( iv_username = av_test_user_name ).
    DATA(lt_keys) = lo_list_result->get_accesskeymetadata( ).
    
    " Ensure the deleted key is not in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_keys INTO DATA(lo_key_meta).
      IF lo_key_meta->get_accesskeyid( ) = lv_key_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Access key should have been deleted| ).
  ENDMETHOD.

  METHOD list_access_keys.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistaccesskeysrsp.

    " Use the test user created in class_setup
    " Create an access key for testing
    DATA(lo_create_result) = ao_iam->createaccesskey( iv_username = av_test_user_name ).
    DATA(lv_key_id) = lo_create_result->get_accesskey( )->get_accesskeyid( ).
    WAIT UP TO 3 SECONDS.

    ao_iam_actions->list_access_keys(
      EXPORTING
        iv_user_name = av_test_user_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List access keys result should not be initial| ).

    DATA(lt_keys) = lo_result->get_accesskeymetadata( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_keys
      msg = |Access keys list should not be empty| ).

    " Verify our key is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_keys INTO DATA(lo_key).
      IF lo_key->get_accesskeyid( ) = lv_key_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Created access key should be in the list| ).

    " Clean up
    ao_iam->deleteaccesskey(
      iv_username = av_test_user_name
      iv_accesskeyid = lv_key_id ).
  ENDMETHOD.

  METHOD update_access_key.
    " Use the test user created in class_setup
    " Create an access key for testing
    DATA(lo_create_result) = ao_iam->createaccesskey( iv_username = av_test_user_name ).
    DATA(lv_key_id) = lo_create_result->get_accesskey( )->get_accesskeyid( ).
    WAIT UP TO 3 SECONDS.

    " Deactivate key
    ao_iam_actions->update_access_key(
      iv_user_name = av_test_user_name
      iv_access_key_id = lv_key_id
      iv_status = 'Inactive' ).

    " Wait for status update propagation
    WAIT UP TO 3 SECONDS.

    " Verify status
    DATA(lo_list_result) = ao_iam->listaccesskeys( iv_username = av_test_user_name ).
    DATA(lt_keys) = lo_list_result->get_accesskeymetadata( ).
    
    DATA lv_status TYPE /aws1/iamstatustype.
    LOOP AT lt_keys INTO DATA(lo_key).
      IF lo_key->get_accesskeyid( ) = lv_key_id.
        lv_status = lo_key->get_status( ).
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_equals(
      exp = 'Inactive'
      act = lv_status
      msg = |Access key should be inactive| ).

    " Clean up
    ao_iam->deleteaccesskey(
      iv_username = av_test_user_name
      iv_accesskeyid = lv_key_id ).
  ENDMETHOD.

  METHOD get_access_key_last_used.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetacckeylastuse01.

    " Use the test user created in class_setup
    " Create an access key for testing
    DATA(lo_create_result) = ao_iam->createaccesskey( iv_username = av_test_user_name ).
    DATA(lv_key_id) = lo_create_result->get_accesskey( )->get_accesskeyid( ).
    WAIT UP TO 3 SECONDS.

    ao_iam_actions->get_access_key_last_used(
      EXPORTING
        iv_access_key_id = lv_key_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get access key last used result should not be initial| ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_username( )
      msg = |User name should not be initial| ).

    " Clean up
    ao_iam->deleteaccesskey(
      iv_username = av_test_user_name
      iv_accesskeyid = lv_key_id ).
  ENDMETHOD.

  METHOD create_policy.
    DATA lo_result TYPE REF TO /aws1/cl_iamcreatepolicyrsp.
    DATA lv_policy_doc TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_policy TYPE /aws1/iampolicynametype.
    DATA lv_temp_arn TYPE /aws1/iamarntype.

    " Generate unique policy name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_policy = |cre-pol-{ lv_uuid_string(10) }|.

    " Create policy document
    lv_policy_doc = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":"s3:ListBucket","Resource":"arn:aws:s3:::*"}]}'.

    ao_iam_actions->create_policy(
      EXPORTING
        iv_policy_name = lv_temp_policy
        iv_policy_document = lv_policy_doc
        iv_description = 'Test policy for demo'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Policy creation result should not be initial| ).

    DATA(lo_policy) = lo_result->get_policy( ).
    lv_temp_arn = lo_policy->get_arn( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_temp_arn
      msg = |Policy ARN should not be initial| ).

    " Tag the policy
    TRY.
        ao_iam->tagpolicy(
          iv_policyarn = lv_temp_arn
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) )
          )
        ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    " Clean up
    ao_iam->deletepolicy( iv_policyarn = lv_temp_arn ).
  ENDMETHOD.

  METHOD delete_policy.
    DATA lv_policy_doc TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_policy TYPE /aws1/iampolicynametype.
    DATA lv_temp_arn TYPE /aws1/iamarntype.

    " Generate unique policy name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_policy = |del-pol-{ lv_uuid_string(10) }|.

    " Create policy first
    lv_policy_doc = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":"s3:ListBucket","Resource":"arn:aws:s3:::*"}]}'.
    DATA(lo_create_result) = ao_iam->createpolicy(
      iv_policyname = lv_temp_policy
      iv_policydocument = lv_policy_doc
      it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
        ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) )
      )
    ).
    lv_temp_arn = lo_create_result->get_policy( )->get_arn( ).
    WAIT UP TO 3 SECONDS.

    ao_iam_actions->delete_policy( iv_policy_arn = lv_temp_arn ).

    " Verify deletion
    TRY.
        ao_iam->getpolicy( iv_policyarn = lv_temp_arn ).
        cl_abap_unit_assert=>fail( msg = |Policy should have been deleted| ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Expected
    ENDTRY.
  ENDMETHOD.

  METHOD list_policies.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistpolresponse.

    " Use test policy created in class_setup
    ao_iam_actions->list_policies(
      EXPORTING
        iv_scope = 'Local'
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List policies result should not be initial| ).

    DATA(lt_policies) = lo_result->get_policies( ).
    cl_abap_unit_assert=>assert_bound(
      act = lt_policies
      msg = |Policies list should be bound| ).

    " Verify our test policy is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_policies INTO DATA(lo_policy).
      IF lo_policy->get_policyname( ) = av_test_policy_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test policy should be in the list| ).
  ENDMETHOD.

  METHOD get_policy.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetpolicyresponse.

    " Use test policy created in class_setup
    ao_iam_actions->get_policy(
      EXPORTING
        iv_policy_arn = av_test_policy_arn
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get policy result should not be initial| ).

    DATA(lo_policy) = lo_result->get_policy( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_test_policy_arn
      act = lo_policy->get_arn( )
      msg = |Policy ARN should match| ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_test_policy_name
      act = lo_policy->get_policyname( )
      msg = |Policy name should match| ).
  ENDMETHOD.

  METHOD create_policy_version.
    DATA lo_result TYPE REF TO /aws1/cl_iamcreatepolicyvrsrsp.
    DATA lv_new_policy_doc TYPE string.

    " Use test policy created in class_setup
    " Create new version with different permissions
    lv_new_policy_doc = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":"s3:GetObject","Resource":"arn:aws:s3:::*/*"}]}'.
    ao_iam_actions->create_policy_version(
      EXPORTING
        iv_policy_arn = av_test_policy_arn
        iv_policy_document = lv_new_policy_doc
        iv_set_as_default = abap_true
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Create policy version result should not be initial| ).

    DATA(lo_version) = lo_result->get_policyversion( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_version->get_versionid( )
      msg = |Version ID should not be initial| ).

    cl_abap_unit_assert=>assert_true(
      act = lo_version->get_isdefaultversion( )
      msg = |Version should be set as default| ).

    " Note: We don't clean up the policy version here, class_teardown will handle it
  ENDMETHOD.

  METHOD attach_user_policy.
    " Use test user and test policy created in class_setup
    ao_iam_actions->attach_user_policy(
      iv_user_name = av_test_user_name
      iv_policy_arn = av_test_policy_arn ).

    " Wait for attachment propagation
    WAIT UP TO 3 SECONDS.

    " Verify attachment
    DATA(lo_list_result) = ao_iam->listattacheduserpolicies( iv_username = av_test_user_name ).
    DATA(lt_policies) = lo_list_result->get_attachedpolicies( ).
    
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_policies INTO DATA(lo_policy).
      IF lo_policy->get_arn( ) = av_test_policy_arn.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Policy should be attached to user| ).

    " Clean up - detach the policy
    ao_iam->detachuserpolicy(
      iv_username = av_test_user_name
      iv_policyarn = av_test_policy_arn ).
  ENDMETHOD.

  METHOD detach_user_policy.
    " Use test user and test policy created in class_setup
    " First attach the policy
    ao_iam->attachuserpolicy(
      iv_username = av_test_user_name
      iv_policyarn = av_test_policy_arn ).
    WAIT UP TO 3 SECONDS.

    " Now detach it
    ao_iam_actions->detach_user_policy(
      iv_user_name = av_test_user_name
      iv_policy_arn = av_test_policy_arn ).

    " Wait for detachment propagation
    WAIT UP TO 3 SECONDS.

    " Verify detachment
    DATA(lo_list_result) = ao_iam->listattacheduserpolicies( iv_username = av_test_user_name ).
    DATA(lt_policies) = lo_list_result->get_attachedpolicies( ).
    
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_policies INTO DATA(lo_policy).
      IF lo_policy->get_arn( ) = av_test_policy_arn.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Policy should not be attached to user| ).
  ENDMETHOD.

  METHOD create_role.
    DATA lo_result TYPE REF TO /aws1/cl_iamcreateroleresponse.
    DATA lv_trust_policy TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_role TYPE /aws1/iamrolenametype.

    " Generate unique role name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_role = |cre-role-{ lv_uuid_string(10) }|.

    " Create trust policy
    lv_trust_policy = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"lambda.amazonaws.com"},"Action":"sts:AssumeRole"}]}'.

    ao_iam_actions->create_role(
      EXPORTING
        iv_role_name = lv_temp_role
        iv_assume_role_policy_document = lv_trust_policy
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Role creation result should not be initial| ).

    DATA(lo_role) = lo_result->get_role( ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_temp_role
      act = lo_role->get_rolename( )
      msg = |Role name should match| ).

    " Tag the role
    TRY.
        ao_iam->tagrole(
          iv_rolename = lv_temp_role
          it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) )
          )
        ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    " Clean up
    ao_iam->deleterole( iv_rolename = lv_temp_role ).
  ENDMETHOD.

  METHOD delete_role.
    DATA lv_trust_policy TYPE string.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_role TYPE /aws1/iamrolenametype.

    " Generate unique role name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_role = |del-role-{ lv_uuid_string(10) }|.

    " Create role first
    lv_trust_policy = '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"lambda.amazonaws.com"},"Action":"sts:AssumeRole"}]}'.
    ao_iam->createrole(
      iv_rolename = lv_temp_role
      iv_assumerolepolicydocument = lv_trust_policy
      it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
        ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) )
      )
    ).
    WAIT UP TO 3 SECONDS.

    ao_iam_actions->delete_role( iv_role_name = lv_temp_role ).

    " Verify deletion
    TRY.
        ao_iam->getrole( iv_rolename = lv_temp_role ).
        cl_abap_unit_assert=>fail( msg = |Role should have been deleted| ).
      CATCH /aws1/cx_iamnosuchentityex.
        " Expected
    ENDTRY.
  ENDMETHOD.

  METHOD get_role.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetroleresponse.

    " Use test role created in class_setup
    ao_iam_actions->get_role(
      EXPORTING
        iv_role_name = av_test_role_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get role result should not be initial| ).

    DATA(lo_role) = lo_result->get_role( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_test_role_name
      act = lo_role->get_rolename( )
      msg = |Role name should match| ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_role->get_arn( )
      msg = |Role ARN should not be initial| ).
  ENDMETHOD.

  METHOD list_roles.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistrolesresponse.

    " Use test role created in class_setup
    ao_iam_actions->list_roles(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List roles result should not be initial| ).

    DATA(lt_roles) = lo_result->get_roles( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_roles
      msg = |Roles list should not be empty| ).

    " Verify our test role is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_roles INTO DATA(lo_role).
      IF lo_role->get_rolename( ) = av_test_role_name.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test role should be in the list| ).
  ENDMETHOD.

  METHOD attach_role_policy.
    " Use test role and test policy created in class_setup
    ao_iam_actions->attach_role_policy(
      iv_role_name = av_test_role_name
      iv_policy_arn = av_test_policy_arn ).

    " Wait for attachment propagation
    WAIT UP TO 3 SECONDS.

    " Verify attachment
    DATA(lo_list_result) = ao_iam->listattachedrolepolicies( iv_rolename = av_test_role_name ).
    DATA(lt_policies) = lo_list_result->get_attachedpolicies( ).
    
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_policies INTO DATA(lo_policy).
      IF lo_policy->get_arn( ) = av_test_policy_arn.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Policy should be attached to role| ).

    " Clean up - detach the policy
    ao_iam->detachrolepolicy(
      iv_rolename = av_test_role_name
      iv_policyarn = av_test_policy_arn ).
  ENDMETHOD.

  METHOD detach_role_policy.
    " Use test role and test policy created in class_setup
    " First attach the policy
    ao_iam->attachrolepolicy(
      iv_rolename = av_test_role_name
      iv_policyarn = av_test_policy_arn ).
    WAIT UP TO 3 SECONDS.

    " Now detach it
    ao_iam_actions->detach_role_policy(
      iv_role_name = av_test_role_name
      iv_policy_arn = av_test_policy_arn ).

    " Wait for detachment propagation
    WAIT UP TO 3 SECONDS.

    " Verify detachment
    DATA(lo_list_result) = ao_iam->listattachedrolepolicies( iv_rolename = av_test_role_name ).
    DATA(lt_policies) = lo_list_result->get_attachedpolicies( ).
    
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_policies INTO DATA(lo_policy).
      IF lo_policy->get_arn( ) = av_test_policy_arn.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Policy should not be attached to role| ).
  ENDMETHOD.

  METHOD list_attached_role_policies.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistattrolepolrsp.

    " Use test role created in class_setup
    ao_iam_actions->list_attached_role_policies(
      EXPORTING
        iv_role_name = av_test_role_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List attached role policies result should not be initial| ).

    " Result should contain attached policies list (may be empty)
    DATA(lt_policies) = lo_result->get_attachedpolicies( ).
    cl_abap_unit_assert=>assert_bound(
      act = lt_policies
      msg = |Attached policies list should be bound| ).
  ENDMETHOD.

  METHOD list_role_policies.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistrolepolrsp.

    " Use test role created in class_setup
    ao_iam_actions->list_role_policies(
      EXPORTING
        iv_role_name = av_test_role_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List role policies result should not be initial| ).

    " Result should contain inline policy names list (may be empty)
    DATA(lt_policy_names) = lo_result->get_policynames( ).
    cl_abap_unit_assert=>assert_bound(
      act = lt_policy_names
      msg = |Policy names list should be bound| ).
  ENDMETHOD.

  METHOD list_groups.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistgroupsresponse.

    ao_iam_actions->list_groups(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List groups result should not be initial| ).
  ENDMETHOD.

  METHOD create_account_alias.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_alias TYPE /aws1/iamaccountaliastype.

    " Generate unique alias name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_alias = |cre-alias-{ lv_uuid_string(8) }|.

    ao_iam_actions->create_account_alias( iv_account_alias = lv_temp_alias ).

    " Wait for alias creation propagation
    WAIT UP TO 3 SECONDS.

    " Verify creation
    DATA(lo_result) = ao_iam->listaccountaliases( ).
    DATA(lt_aliases) = lo_result->get_accountaliases( ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_aliases INTO DATA(lv_alias).
      IF lv_alias = lv_temp_alias.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Account alias should be in the list| ).

    " Clean up
    ao_iam->deleteaccountalias( iv_accountalias = lv_temp_alias ).
  ENDMETHOD.

  METHOD delete_account_alias.
    DATA lv_uuid_string TYPE string.
    DATA lv_temp_alias TYPE /aws1/iamaccountaliastype.

    " Generate unique alias name for this test
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    lv_temp_alias = |del-alias-{ lv_uuid_string(8) }|.

    " Create alias first
    TRY.
        ao_iam->createaccountalias( iv_accountalias = lv_temp_alias ).
      CATCH /aws1/cx_iamentityalrdyexex.
        " Alias may already exist from previous failed test, that's ok
    ENDTRY.
    WAIT UP TO 3 SECONDS.

    ao_iam_actions->delete_account_alias( iv_account_alias = lv_temp_alias ).

    " Wait for alias deletion propagation
    WAIT UP TO 3 SECONDS.

    " Verify deletion
    DATA(lo_result) = ao_iam->listaccountaliases( ).
    DATA(lt_aliases) = lo_result->get_accountaliases( ).
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_aliases INTO DATA(lv_alias).
      IF lv_alias = lv_temp_alias.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Account alias should not be in the list| ).
  ENDMETHOD.

  METHOD list_account_aliases.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistacctaliasesrsp.

    ao_iam_actions->list_account_aliases(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List account aliases result should not be initial| ).
  ENDMETHOD.

  METHOD get_account_authorization_det.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetacctauthdetsrsp.

    ao_iam_actions->get_account_authorization_det(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get account authorization details result should not be initial| ).
  ENDMETHOD.

  METHOD get_account_summary.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetacctsummaryrsp.

    ao_iam_actions->get_account_summary(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get account summary result should not be initial| ).

    DATA(lt_summary) = lo_result->get_summarymap( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_summary
      msg = |Summary map should not be empty| ).
  ENDMETHOD.

  METHOD generate_credential_report.
    DATA lo_result TYPE REF TO /aws1/cl_iamgeneratecredrptrsp.
    DATA lv_max_attempts TYPE i VALUE 30.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_state TYPE /aws1/iamreportstatetype.

    " Generate credential report and poll for completion
    DO lv_max_attempts TIMES.
      lv_attempt = lv_attempt + 1.
      
      ao_iam_actions->generate_credential_report(
        IMPORTING
          oo_result = lo_result ).

      cl_abap_unit_assert=>assert_bound(
        act = lo_result
        msg = |Generate credential report result should not be initial| ).

      lv_state = lo_result->get_state( ).
      cl_abap_unit_assert=>assert_not_initial(
        act = lv_state
        msg = |Report state should not be initial| ).

      IF lv_state = 'COMPLETE'.
        EXIT.
      ENDIF.

      " Wait before next attempt
      WAIT UP TO 2 SECONDS.
    ENDDO.

    cl_abap_unit_assert=>assert_equals(
      exp = 'COMPLETE'
      act = lv_state
      msg = |Credential report should be complete after polling| ).
  ENDMETHOD.

  METHOD get_credential_report.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetcredreportrsp.
    DATA lv_max_attempts TYPE i VALUE 30.
    DATA lv_attempt TYPE i VALUE 0.

    " First ensure report is generated and ready
    DO lv_max_attempts TIMES.
      lv_attempt = lv_attempt + 1.
      
      TRY.
          DATA(lo_gen_result) = ao_iam->generatecredentialreport( ).
          IF lo_gen_result->get_state( ) = 'COMPLETE'.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue trying
      ENDTRY.

      WAIT UP TO 2 SECONDS.
    ENDDO.

    " Now get the report
    ao_iam_actions->get_credential_report(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |Get credential report result should not be initial| ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_content( )
      msg = |Report content should not be initial| ).
  ENDMETHOD.

  METHOD get_account_password_policy.
    DATA lo_result TYPE REF TO /aws1/cl_iamgetacpasswordply00.

    TRY.
        ao_iam_actions->get_account_password_policy(
          IMPORTING
            oo_result = lo_result ).
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |Get account password policy result should not be initial| ).
      CATCH /aws1/cx_iamnosuchentityex.
        " No password policy exists which is acceptable for this test
    ENDTRY.
  ENDMETHOD.

  METHOD list_saml_providers.
    DATA lo_result TYPE REF TO /aws1/cl_iamlistsamlpvdrsrsp.

    ao_iam_actions->list_saml_providers(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = |List SAML providers result should not be initial| ).
  ENDMETHOD.

  METHOD create_service_linked_role.
    DATA lo_result TYPE REF TO /aws1/cl_iamcresvclnkrolersp.
    DATA lv_service_name TYPE string VALUE 'elasticbeanstalk.amazonaws.com'.
    DATA lv_role_name TYPE /aws1/iamrolenametype.

    TRY.
        ao_iam_actions->create_service_linked_role(
          EXPORTING
            iv_aws_service_name = lv_service_name
            iv_description = 'Demo service-linked role'
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = |Create service-linked role result should not be initial| ).

        DATA(lo_role) = lo_result->get_role( ).
        lv_role_name = lo_role->get_rolename( ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_role_name
          msg = |Service-linked role name should not be initial| ).

        " Tag the role (best effort)
        TRY.
            ao_iam->tagrole(
              iv_rolename = lv_role_name
              it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
                ( NEW /aws1/cl_iamtag( iv_key = 'TestType' iv_value = 'convert_test' ) )
              )
            ).
          CATCH /aws1/cx_rt_generic.
            " Tagging may not be supported for service-linked roles
        ENDTRY.

        " Clean up - Note: Service-linked role deletion is complex
        " We'll attempt to delete but won't fail the test if it doesn't work
        TRY.
            DATA(lo_delete_result) = ao_iam->deleteservicelinkedrole( iv_rolename = lv_role_name ).
            DATA(lv_deletion_task_id) = lo_delete_result->get_deletiontaskid( ).
            
            " Poll for deletion status
            DATA lv_max_attempts TYPE i VALUE 10.
            DATA lv_deletion_complete TYPE abap_bool VALUE abap_false.
            DO lv_max_attempts TIMES.
              TRY.
                  DATA(lo_status_result) = ao_iam->getservicelinkedroledeletionstatus(
                    iv_deletiontaskid = lv_deletion_task_id ).
                  DATA(lv_status) = lo_status_result->get_status( ).
                  
                  IF lv_status = 'SUCCEEDED'.
                    lv_deletion_complete = abap_true.
                    EXIT.
                  ELSEIF lv_status = 'FAILED'.
                    " Deletion failed, that's ok - role is tagged for manual cleanup
                    EXIT.
                  ENDIF.
                CATCH /aws1/cx_rt_generic.
                  EXIT.
              ENDTRY.
              
              WAIT UP TO 2 SECONDS.
            ENDDO.
            
            MESSAGE |Service-linked role deletion initiated: { lv_role_name }| TYPE 'I'.
          CATCH /aws1/cx_rt_generic.
            " Deletion may fail if service still needs the role
            " Role is tagged for manual cleanup
            MESSAGE |Service-linked role tagged for manual cleanup: { lv_role_name }| TYPE 'I'.
        ENDTRY.

      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists from previous test, which is acceptable
        MESSAGE 'Service-linked role already exists' TYPE 'I'.
      CATCH /aws1/cx_iamservicenotsuppedex.
        " Service doesn't support service-linked roles, test passed
        MESSAGE 'Service does not support service-linked roles' TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
