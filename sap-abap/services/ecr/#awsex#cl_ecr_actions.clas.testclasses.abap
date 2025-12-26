" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_ecr_actions DEFINITION DEFERRED.
CLASS /awsex/cl_ecr_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_ecr_actions.

CLASS ltc_awsex_cl_ecr_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ecr TYPE REF TO /aws1/if_ecr.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ecr_actions TYPE REF TO /awsex/cl_ecr_actions.
    CLASS-DATA av_base_repo_name TYPE /aws1/ecrrepositoryname.
    CLASS-DATA av_policy_repo_name TYPE /aws1/ecrrepositoryname.
    CLASS-DATA av_lifecycle_repo_name TYPE /aws1/ecrrepositoryname.
    CLASS-DATA av_images_repo_name TYPE /aws1/ecrrepositoryname.
    CLASS-DATA av_create_repo_name TYPE /aws1/ecrrepositoryname.
    CLASS-DATA av_delete_repo_name TYPE /aws1/ecrrepositoryname.

    METHODS: create_repository FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_repositories FOR TESTING RAISING /aws1/cx_rt_generic,
      get_authorization_token FOR TESTING RAISING /aws1/cx_rt_generic,
      set_repository_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      get_repository_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      put_lifecycle_policy FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_images FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_repository FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
    CLASS-METHODS create_test_repository
      IMPORTING
        iv_repo_name TYPE /aws1/ecrrepositoryname
      RAISING
        /aws1/cx_rt_generic.
    CLASS-METHODS delete_test_repository
      IMPORTING
        iv_repo_name TYPE /aws1/ecrrepositoryname
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_ecr_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ecr = /aws1/cl_ecr_factory=>create( ao_session ).
    ao_ecr_actions = NEW /awsex/cl_ecr_actions( ).

    " Generate unique repository names using util function
    DATA(lv_random) = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    
    av_base_repo_name = |ecr-test-base-{ lv_random }|.
    
    lv_random = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    av_policy_repo_name = |ecr-test-policy-{ lv_random }|.
    
    lv_random = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    av_lifecycle_repo_name = |ecr-test-lc-{ lv_random }|.
    
    lv_random = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    av_images_repo_name = |ecr-test-images-{ lv_random }|.
    
    lv_random = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    av_create_repo_name = |ecr-test-create-{ lv_random }|.
    
    lv_random = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    av_delete_repo_name = |ecr-test-delete-{ lv_random }|.

    " Create test repositories
    create_test_repository( av_base_repo_name ).
    create_test_repository( av_policy_repo_name ).
    create_test_repository( av_lifecycle_repo_name ).
    create_test_repository( av_images_repo_name ).

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up all test repositories
    delete_test_repository( av_base_repo_name ).
    delete_test_repository( av_policy_repo_name ).
    delete_test_repository( av_lifecycle_repo_name ).
    delete_test_repository( av_images_repo_name ).
    delete_test_repository( av_create_repo_name ).
    delete_test_repository( av_delete_repo_name ).
  ENDMETHOD.

  METHOD create_test_repository.
    TRY.
        ao_ecr->createrepository(
          iv_repositoryname = iv_repo_name
          it_tags = VALUE #(
            ( NEW /aws1/cl_ecrtag( iv_key = 'convert_test' iv_value = 'true' ) )
          ) ).
      CATCH /aws1/cx_ecrrepositoryalrexex.
        " Repository already exists, which is fine for idempotent setup
    ENDTRY.
  ENDMETHOD.

  METHOD delete_test_repository.
    TRY.
        ao_ecr->deleterepository(
          iv_repositoryname = iv_repo_name
          iv_force = abap_true ).
      CATCH /aws1/cx_ecrrepositorynotfndex.
        " Repository doesn't exist, which is fine
    ENDTRY.
  ENDMETHOD.

  METHOD create_repository.
    DATA lo_result TYPE REF TO /aws1/cl_ecrcrerepositoryrsp.

    " Test creating a NEW repository
    ao_ecr_actions->create_repository(
      EXPORTING
        iv_repository_name = av_create_repo_name
      IMPORTING
        oo_result = lo_result ).

    " Verify the result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be returned' ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result->get_repository( )
      msg = 'Repository object should be returned' ).

    DATA(lv_repo_name) = lo_result->get_repository( )->get_repositoryname( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_create_repo_name
      act = lv_repo_name
      msg = |Repository name should be { av_create_repo_name }| ).

    " Verify repository URI is not empty
    DATA(lv_repo_uri) = lo_result->get_repository( )->get_repositoryuri( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_repo_uri
      msg = 'Repository URI should not be empty' ).

  ENDMETHOD.

  METHOD describe_repositories.
    DATA lo_result TYPE REF TO /aws1/cl_ecrdscrepositoriesrsp.
    DATA lt_repo_names TYPE /aws1/cl_ecrrepositorynamels00=>tt_repositorynamelist.

    " Test describing existing repository
    lt_repo_names = VALUE #( ( CONV /aws1/ecrrepositoryname( av_base_repo_name ) ) ).

    ao_ecr_actions->describe_repositories(
      EXPORTING
        it_repository_names = lt_repo_names
      IMPORTING
        oo_result = lo_result ).

    " Verify the result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be returned' ).

    DATA(lt_repositories) = lo_result->get_repositories( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_repositories
      msg = 'Repositories list should not be empty' ).

    cl_abap_unit_assert=>assert_equals(
      exp = 1
      act = lines( lt_repositories )
      msg = 'Should return exactly one repository' ).

    " Verify the repository name matches
    READ TABLE lt_repositories INDEX 1 INTO DATA(lo_repository).
    cl_abap_unit_assert=>assert_equals(
      exp = av_base_repo_name
      act = lo_repository->get_repositoryname( )
      msg = 'Repository name should match' ).

  ENDMETHOD.

  METHOD get_authorization_token.
    DATA lo_result TYPE REF TO /aws1/cl_ecrgetauthtokenrsp.

    " Test getting authorization token
    ao_ecr_actions->get_authorization_token(
      IMPORTING
        oo_result = lo_result ).

    " Verify the result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be returned' ).

    DATA(lt_auth_data) = lo_result->get_authorizationdata( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_auth_data
      msg = 'Authorization data should not be empty' ).

    cl_abap_unit_assert=>assert_differs(
      act = lines( lt_auth_data )
      exp = 0
      msg = 'Should have at least one authorization entry' ).

    " Verify token and endpoint are present
    READ TABLE lt_auth_data INDEX 1 INTO DATA(lo_auth_data).
    cl_abap_unit_assert=>assert_not_initial(
      act = lo_auth_data->get_authorizationtoken( )
      msg = 'Authorization token should not be empty' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_auth_data->get_proxyendpoint( )
      msg = 'Proxy endpoint should not be empty' ).

  ENDMETHOD.

  METHOD set_repository_policy.
    " Test setting a repository policy
    DATA(lv_account_id) = ao_session->get_account_id( ).
    
    " Create a simple policy allowing specific actions
    DATA(lv_policy) = |{'{'}|  &&
                      |"Version":"2012-10-17",|  &&
                      |"Statement":[{'{'}|  &&
                      |"Sid":"AllowPull",|  &&
                      |"Effect":"Allow",|  &&
                      |"Principal":{'{'}|  &&
                      |"AWS":"arn:aws:iam::{ lv_account_id }:root"|  &&
                      |{'}'},|  &&
                      |"Action":["ecr:BatchGetImage","ecr:GetDownloadUrlForLayer"]|  &&
                      |{'}'}]|  &&
                      |{'}'}|.

    " Set the policy
    ao_ecr_actions->set_repository_policy(
      iv_repository_name = av_policy_repo_name
      iv_policy_text = CONV #( lv_policy ) ).

    " Verify policy was set by retrieving it
    DATA(lo_get_result) = ao_ecr->getrepositorypolicy(
      iv_repositoryname = av_policy_repo_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_get_result->get_policytext( )
      msg = 'Policy text should not be empty after setting' ).

    " Verify the policy contains our Sid
    DATA(lv_retrieved_policy) = lo_get_result->get_policytext( ).
    cl_abap_unit_assert=>assert_char_cp(
      act = lv_retrieved_policy
      exp = '*AllowPull*'
      msg = 'Policy should contain AllowPull Sid' ).

  ENDMETHOD.

  METHOD get_repository_policy.
    DATA lo_result TYPE REF TO /aws1/cl_ecrgetrepositoryply01.
    DATA(lv_account_id) = ao_session->get_account_id( ).

    " First, ensure a policy is set on the repository
    DATA(lv_policy) = |{'{'}|  &&
                      |"Version":"2012-10-17",|  &&
                      |"Statement":[{'{'}|  &&
                      |"Sid":"TestGetPolicy",|  &&
                      |"Effect":"Allow",|  &&
                      |"Principal":{'{'}|  &&
                      |"AWS":"arn:aws:iam::{ lv_account_id }:root"|  &&
                      |{'}'},|  &&
                      |"Action":["ecr:BatchGetImage"]|  &&
                      |{'}'}]|  &&
                      |{'}'}|.

    ao_ecr->setrepositorypolicy(
      iv_repositoryname = av_policy_repo_name
      iv_policytext = CONV #( lv_policy ) ).

    " Now test getting the policy
    ao_ecr_actions->get_repository_policy(
      EXPORTING
        iv_repository_name = av_policy_repo_name
      IMPORTING
        oo_result = lo_result ).

    " Verify the result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be returned' ).

    DATA(lv_policy_text) = lo_result->get_policytext( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_policy_text
      msg = 'Policy text should not be empty' ).

    " Verify policy contains expected Sid
    cl_abap_unit_assert=>assert_char_cp(
      act = lv_policy_text
      exp = '*TestGetPolicy*'
      msg = 'Policy should contain TestGetPolicy Sid' ).

    " Verify repository name is returned
    DATA(lv_repo_name) = lo_result->get_repositoryname( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_policy_repo_name
      act = lv_repo_name
      msg = 'Repository name should match' ).

  ENDMETHOD.

  METHOD put_lifecycle_policy.
    " Create a lifecycle policy to expire old images
    DATA(lv_lc_policy) = |{'{'}|  &&
                         |"rules":[{'{'}|  &&
                         |"rulePriority":1,|  &&
                         |"description":"Expire images older than 14 days",|  &&
                         |"selection":{'{'}|  &&
                         |"tagStatus":"any",|  &&
                         |"countType":"sinceImagePushed",|  &&
                         |"countUnit":"days",|  &&
                         |"countNumber":14|  &&
                         |{'}'},|  &&
                         |"action":{'{'}|  &&
                         |"type":"expire"|  &&
                         |{'}'}|  &&
                         |{'}'}]|  &&
                         |{'}'}|.

    " Test putting the lifecycle policy
    ao_ecr_actions->put_lifecycle_policy(
      iv_repository_name = av_lifecycle_repo_name
      iv_lifecycle_policy_text = CONV #( lv_lc_policy ) ).

    " Verify by getting the lifecycle policy
    DATA(lo_get_result) = ao_ecr->getlifecyclepolicy(
      iv_repositoryname = av_lifecycle_repo_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_get_result->get_lifecyclepolicytext( )
      msg = 'Lifecycle policy should be set' ).

    " Verify the policy text contains expected elements
    DATA(lv_retrieved_policy) = lo_get_result->get_lifecyclepolicytext( ).
    cl_abap_unit_assert=>assert_char_cp(
      act = lv_retrieved_policy
      exp = '*rulePriority*'
      msg = 'Lifecycle policy should contain rulePriority' ).

    cl_abap_unit_assert=>assert_char_cp(
      act = lv_retrieved_policy
      exp = '*expire*'
      msg = 'Lifecycle policy should contain expire action' ).

    " Verify repository name is returned
    DATA(lv_repo_name) = lo_get_result->get_repositoryname( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_lifecycle_repo_name
      act = lv_repo_name
      msg = 'Repository name should match' ).

  ENDMETHOD.

  METHOD describe_images.
    DATA lo_result TYPE REF TO /aws1/cl_ecrdescrimagesrsp.

    " Test describing images (will be empty as no images are pushed)
    ao_ecr_actions->describe_images(
      EXPORTING
        iv_repository_name = av_images_repo_name
      IMPORTING
        oo_result = lo_result ).

    " Verify the result object is returned
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should be returned' ).

    " The image details is a table type, not an object reference
    DATA(lt_image_details) = lo_result->get_imagedetails( ).

    " Since no images are pushed, the list should be empty
    cl_abap_unit_assert=>assert_equals(
      exp = 0
      act = lines( lt_image_details )
      msg = 'Image details list should be empty for repository without images' ).

  ENDMETHOD.

  METHOD delete_repository.
    " Create a temporary repository for deletion test
    DATA(lv_random) = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_random TO LOWER CASE.
    av_delete_repo_name = |ecr-test-del-{ lv_random }|.

    " Create the repository with proper tag
    ao_ecr->createrepository(
      iv_repositoryname = av_delete_repo_name
      it_tags = VALUE #(
        ( NEW /aws1/cl_ecrtag( iv_key = 'convert_test' iv_value = 'true' ) )
      ) ).

    " Verify repository exists before deletion
    DATA(lo_desc_before) = ao_ecr->describerepositories(
      it_repositorynames = VALUE #( ( CONV /aws1/ecrrepositoryname( av_delete_repo_name ) ) ) ).

    cl_abap_unit_assert=>assert_equals(
      exp = 1
      act = lines( lo_desc_before->get_repositories( ) )
      msg = 'Repository should exist before deletion' ).

    " Test deleting the repository
    ao_ecr_actions->delete_repository(
      iv_repository_name = av_delete_repo_name ).

    " Verify deletion by attempting to describe it
    TRY.
        ao_ecr->describerepositories(
          it_repositorynames = VALUE #( ( CONV /aws1/ecrrepositoryname( av_delete_repo_name ) ) ) ).
        cl_abap_unit_assert=>fail( msg = 'Repository should have been deleted' ).
      CATCH /aws1/cx_ecrrepositorynotfndex.
        " Expected - repository was successfully deleted
    ENDTRY.

  ENDMETHOD.

ENDCLASS.
