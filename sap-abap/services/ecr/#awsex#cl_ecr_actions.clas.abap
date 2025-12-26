" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_ecr_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_repository
      IMPORTING
        !iv_repository_name TYPE /aws1/ecrrepositoryname
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_ecrcrerepositoryrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_repository
      IMPORTING
        !iv_repository_name TYPE /aws1/ecrrepositoryname
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_repositories
      IMPORTING
        !it_repository_names TYPE /aws1/cl_ecrrepositorynamels00=>tt_repositorynamelist
      EXPORTING
        !oo_result           TYPE REF TO /aws1/cl_ecrdscrepositoriesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_authorization_token
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_ecrgetauthtokenrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_repository_policy
      IMPORTING
        !iv_repository_name TYPE /aws1/ecrrepositoryname
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_ecrgetrepositoryply01
      RAISING
        /aws1/cx_rt_generic.

    METHODS set_repository_policy
      IMPORTING
        !iv_repository_name TYPE /aws1/ecrrepositoryname
        !iv_policy_text     TYPE /aws1/ecrrepositorypolicytext
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_lifecycle_policy
      IMPORTING
        !iv_repository_name        TYPE /aws1/ecrrepositoryname
        !iv_lifecycle_policy_text  TYPE /aws1/ecrlifecyclepolicytext
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_images
      IMPORTING
        !iv_repository_name TYPE /aws1/ecrrepositoryname
        !it_image_ids       TYPE /aws1/cl_ecrimageidentifier=>tt_imageidentifierlist OPTIONAL
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_ecrdescrimagesrsp
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_ecr_actions IMPLEMENTATION.


  METHOD create_repository.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.create_repository]
    TRY.
        " iv_repository_name = 'my-repository'
        oo_result = lo_ecr->createrepository(
          iv_repositoryname = iv_repository_name ).
        DATA(lv_repository_uri) = oo_result->get_repository( )->get_repositoryuri( ).
        MESSAGE |Repository created with URI: { lv_repository_uri }| TYPE 'I'.
      CATCH /aws1/cx_ecrrepositoryalrexex INTO DATA(lo_already_exists).
        " If repository already exists, retrieve it
        DATA lt_repo_names TYPE /aws1/cl_ecrrepositorynamels00=>tt_repositorynamelist.
        APPEND NEW /aws1/cl_ecrrepositorynamels00( iv_value = iv_repository_name ) TO lt_repo_names.
        DATA(lo_describe_result) = lo_ecr->describerepositories( it_repositorynames = lt_repo_names ).
        DATA(lt_repos) = lo_describe_result->get_repositories( ).
        IF lines( lt_repos ) > 0.
          READ TABLE lt_repos INDEX 1 INTO DATA(lo_repo).
          oo_result = NEW /aws1/cl_ecrcrerepositoryrsp( ).
          oo_result->set_repository( lo_repo ).
          MESSAGE |Repository { iv_repository_name } already exists.| TYPE 'I'.
        ENDIF.
    ENDTRY.
    " snippet-end:[ecr.abapv1.create_repository]
  ENDMETHOD.


  METHOD delete_repository.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.delete_repository]
    TRY.
        " iv_repository_name = 'my-repository'
        lo_ecr->deleterepository(
          iv_repositoryname = iv_repository_name
          iv_force = abap_true ).
        MESSAGE |Repository { iv_repository_name } deleted.| TYPE 'I'.
      CATCH /aws1/cx_ecrrepositorynotfndex.
        MESSAGE 'Repository not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.delete_repository]
  ENDMETHOD.


  METHOD describe_repositories.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.describe_repositories]
    TRY.
        " it_repository_names = VALUE #( ( NEW /aws1/cl_ecrrepositorynamels00( iv_value = 'my-repository' ) ) )
        oo_result = lo_ecr->describerepositories(
          it_repositorynames = it_repository_names ).
        DATA(lt_repositories) = oo_result->get_repositories( ).
        MESSAGE |Found { lines( lt_repositories ) } repositories.| TYPE 'I'.
      CATCH /aws1/cx_ecrrepositorynotfndex.
        MESSAGE 'Repository not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.describe_repositories]
  ENDMETHOD.


  METHOD get_authorization_token.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.get_authorization_token]
    TRY.
        oo_result = lo_ecr->getauthorizationtoken( ).
        DATA(lt_auth_data) = oo_result->get_authorizationdata( ).
        IF lines( lt_auth_data ) > 0.
          READ TABLE lt_auth_data INDEX 1 INTO DATA(lo_auth_data).
          DATA(lv_token) = lo_auth_data->get_authorizationtoken( ).
          MESSAGE 'Authorization token retrieved.' TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_ecrserverexception.
        MESSAGE 'Server exception occurred.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.get_authorization_token]
  ENDMETHOD.


  METHOD get_repository_policy.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.get_repository_policy]
    TRY.
        " iv_repository_name = 'my-repository'
        oo_result = lo_ecr->getrepositorypolicy(
          iv_repositoryname = iv_repository_name ).
        DATA(lv_policy_text) = oo_result->get_policytext( ).
        MESSAGE 'Repository policy retrieved.' TYPE 'I'.
      CATCH /aws1/cx_ecrrepositorynotfndex.
        MESSAGE 'Repository not found.' TYPE 'I'.
      CATCH /aws1/cx_ecrrepositoryplynot00.
        MESSAGE 'Repository policy not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.get_repository_policy]
  ENDMETHOD.


  METHOD set_repository_policy.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.set_repository_policy]
    TRY.
        " iv_repository_name = 'my-repository'
        " iv_policy_text = '{"Version":"2012-10-17","Statement":[...]}'
        lo_ecr->setrepositorypolicy(
          iv_repositoryname = iv_repository_name
          iv_policytext = iv_policy_text ).
        MESSAGE |Policy set for repository { iv_repository_name }.| TYPE 'I'.
      CATCH /aws1/cx_ecrrepositorynotfndex.
        MESSAGE 'Repository not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.set_repository_policy]
  ENDMETHOD.


  METHOD put_lifecycle_policy.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.put_lifecycle_policy]
    TRY.
        " iv_repository_name = 'my-repository'
        " iv_lifecycle_policy_text = '{"rules":[{"rulePriority":1,"description":"Expire images older than 14 days",...}]}'
        lo_ecr->putlifecyclepolicy(
          iv_repositoryname = iv_repository_name
          iv_lifecyclepolicytext = iv_lifecycle_policy_text ).
        MESSAGE |Lifecycle policy set for repository { iv_repository_name }.| TYPE 'I'.
      CATCH /aws1/cx_ecrrepositorynotfndex.
        MESSAGE 'Repository not found.' TYPE 'I'.
      CATCH /aws1/cx_ecrvalidationex.
        MESSAGE 'Invalid lifecycle policy format.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.put_lifecycle_policy]
  ENDMETHOD.


  METHOD describe_images.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_ecr) = /aws1/cl_ecr_factory=>create( lo_session ).

    " snippet-start:[ecr.abapv1.describe_images]
    TRY.
        " iv_repository_name = 'my-repository'
        " it_image_ids = VALUE #( ( NEW /aws1/cl_ecrimageidentifier( iv_imagetag = 'latest' ) ) )
        oo_result = lo_ecr->describeimages(
          iv_repositoryname = iv_repository_name
          it_imageids = it_image_ids ).
        DATA(lt_image_details) = oo_result->get_imagedetails( ).
        MESSAGE |Found { lines( lt_image_details ) } images in repository.| TYPE 'I'.
      CATCH /aws1/cx_ecrrepositorynotfndex.
        MESSAGE 'Repository not found.' TYPE 'I'.
      CATCH /aws1/cx_ecrimagenotfoundex.
        MESSAGE 'Image not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[ecr.abapv1.describe_images]
  ENDMETHOD.

ENDCLASS.
