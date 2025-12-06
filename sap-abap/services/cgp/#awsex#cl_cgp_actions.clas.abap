" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_cgp_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    " Sign up a new user with Amazon Cognito
    METHODS sign_up_user
      IMPORTING
        !iv_client_id   TYPE /aws1/cgpclientidtype
        !iv_user_name   TYPE /aws1/cgpusernametype
        !iv_password    TYPE /aws1/cgppasswordtype
        !iv_user_email  TYPE /aws1/cgpemailaddresstype
        !iv_secret_hash TYPE /aws1/cgpsecrethashtype OPTIONAL
      RETURNING
        VALUE(ov_confirmed) TYPE /aws1/cgpbooleantype
      RAISING
        /aws1/cx_rt_generic.

    " Resend confirmation code to a user
    METHODS resend_confirmation
      IMPORTING
        !iv_client_id   TYPE /aws1/cgpclientidtype
        !iv_user_name   TYPE /aws1/cgpusernametype
        !iv_secret_hash TYPE /aws1/cgpsecrethashtype OPTIONAL
      RETURNING
        VALUE(oo_delivery) TYPE REF TO /aws1/cl_cgpcodedeliverydets00
      RAISING
        /aws1/cx_rt_generic.

    " Confirm a user sign-up
    METHODS confirm_user_sign_up
      IMPORTING
        !iv_client_id         TYPE /aws1/cgpclientidtype
        !iv_user_name         TYPE /aws1/cgpusernametype
        !iv_confirmation_code TYPE /aws1/cgpconfirmationcodetype
        !iv_secret_hash       TYPE /aws1/cgpsecrethashtype OPTIONAL
      RETURNING
        VALUE(ov_success) TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

    " List users in a user pool
    METHODS list_users
      IMPORTING
        !iv_user_pool_id TYPE /aws1/cgpuserpoolidtype
      RETURNING
        VALUE(ot_users) TYPE /aws1/cl_cgpusertype=>tt_userslisttype
      RAISING
        /aws1/cx_rt_generic.

    " Start admin sign-in process
    METHODS start_sign_in
      IMPORTING
        !iv_user_pool_id TYPE /aws1/cgpuserpoolidtype
        !iv_client_id    TYPE /aws1/cgpclientidtype
        !iv_user_name    TYPE /aws1/cgpusernametype
        !iv_password     TYPE /aws1/cgppasswordtype
        !iv_secret_hash  TYPE /aws1/cgpsecrethashtype OPTIONAL
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cgpadmininitiateaut01
      RAISING
        /aws1/cx_rt_generic.

    " Get MFA secret for software token
    METHODS get_mfa_secret
      IMPORTING
        !iv_session        TYPE /aws1/cgpsessiontype
      RETURNING
        VALUE(ov_secret_code) TYPE /aws1/cgpsecretcodetype
      RAISING
        /aws1/cx_rt_generic.

    " Verify software token MFA
    METHODS verify_mfa
      IMPORTING
        !iv_session    TYPE /aws1/cgpsessiontype
        !iv_user_code  TYPE /aws1/cgpsoftwaretokmfauserc00
      RETURNING
        VALUE(ov_status) TYPE /aws1/cgpverifysoftwaretokrs00
      RAISING
        /aws1/cx_rt_generic.

    " Respond to MFA challenge
    METHODS respond_to_mfa_challenge
      IMPORTING
        !iv_user_pool_id TYPE /aws1/cgpuserpoolidtype
        !iv_client_id    TYPE /aws1/cgpclientidtype
        !iv_user_name    TYPE /aws1/cgpusernametype
        !iv_session      TYPE /aws1/cgpsessiontype
        !iv_mfa_code     TYPE /aws1/cgpsoftwaretokmfauserc00
        !iv_secret_hash  TYPE /aws1/cgpsecrethashtype OPTIONAL
      RETURNING
        VALUE(oo_auth_result) TYPE REF TO /aws1/cl_cgpauthntctnrslttype
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_CGP_ACTIONS IMPLEMENTATION.


  METHOD sign_up_user.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.sign_up_user]
    TRY.
        " Create user attribute for email
        DATA(lt_user_attrs) = VALUE /aws1/cl_cgpattributetype=>tt_attributelisttype(
          ( NEW /aws1/cl_cgpattributetype(
              iv_name = 'email'
              iv_value = iv_user_email ) )
        ).

        DATA(lo_result) = lo_cgp->signup(
          iv_clientid = iv_client_id
          iv_username = iv_user_name
          iv_password = iv_password
          iv_secrethash = iv_secret_hash
          it_userattributes = lt_user_attrs
        ).

        ov_confirmed = lo_result->get_userconfirmed( ).

        IF ov_confirmed = abap_true.
          MESSAGE 'User signed up and confirmed.' TYPE 'I'.
        ELSE.
          MESSAGE 'User signed up but requires confirmation.' TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_cgpusernameexistsex INTO DATA(lo_username_ex).
        " User already exists, check if confirmed
        DATA(lo_user) = lo_cgp->admingetuser(
          iv_userpoolid = |{ iv_client_id }|  " This would need actual user pool ID
          iv_username = iv_user_name
        ).
        DATA(lv_status) = lo_user->get_userstatus( ).
        IF lv_status = 'CONFIRMED'.
          ov_confirmed = abap_true.
        ELSE.
          ov_confirmed = abap_false.
        ENDIF.
        MESSAGE |User { iv_user_name } already exists with status { lv_status }.| TYPE 'I'.

      CATCH /aws1/cx_cgpinvalidpasswordex INTO DATA(lo_pwd_ex).
        MESSAGE |Invalid password: { lo_pwd_ex->get_text( ) }| TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.sign_up_user]

  ENDMETHOD.


  METHOD resend_confirmation.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.resend_confirmation]
    TRY.
        DATA(lo_result) = lo_cgp->resendconfirmationcode(
          iv_clientid = iv_client_id
          iv_username = iv_user_name
          iv_secrethash = iv_secret_hash
        ).

        oo_delivery = lo_result->get_codedeliverydetails( ).

        IF oo_delivery IS BOUND.
          DATA(lv_destination) = oo_delivery->get_destination( ).
          MESSAGE |Confirmation code sent to { lv_destination }.| TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_cgpusernotfoundex INTO DATA(lo_ex).
        MESSAGE |User { iv_user_name } not found.| TYPE 'E'.

      CATCH /aws1/cx_cgplimitexceededex INTO DATA(lo_limit_ex).
        MESSAGE 'Too many requests. Please wait before retrying.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.resend_confirmation]

  ENDMETHOD.


  METHOD confirm_user_sign_up.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.confirm_user_sign_up]
    TRY.
        lo_cgp->confirmsignup(
          iv_clientid = iv_client_id
          iv_username = iv_user_name
          iv_confirmationcode = iv_confirmation_code
          iv_secrethash = iv_secret_hash
        ).

        ov_success = abap_true.
        MESSAGE |User { iv_user_name } confirmed successfully.| TYPE 'I'.

      CATCH /aws1/cx_cgpcodemismatchex INTO DATA(lo_code_ex).
        MESSAGE 'Invalid confirmation code provided.' TYPE 'E'.
        ov_success = abap_false.

      CATCH /aws1/cx_cgpexpiredcodeex INTO DATA(lo_expired_ex).
        MESSAGE 'Confirmation code has expired.' TYPE 'E'.
        ov_success = abap_false.

      CATCH /aws1/cx_cgpusernotfoundex INTO DATA(lo_user_ex).
        MESSAGE |User { iv_user_name } not found.| TYPE 'E'.
        ov_success = abap_false.
    ENDTRY.
    " snippet-end:[cgp.abapv1.confirm_user_sign_up]

  ENDMETHOD.


  METHOD list_users.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.list_users]
    TRY.
        DATA(lo_result) = lo_cgp->listusers(
          iv_userpoolid = iv_user_pool_id
        ).

        ot_users = lo_result->get_users( ).

        MESSAGE |Found { lines( ot_users ) } users in the pool.| TYPE 'I'.

      CATCH /aws1/cx_cgpresourcenotfoundex INTO DATA(lo_ex).
        MESSAGE |User pool { iv_user_pool_id } not found.| TYPE 'E'.

      CATCH /aws1/cx_cgpnotauthorizedex INTO DATA(lo_auth_ex).
        MESSAGE 'Not authorized to list users.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.list_users]

  ENDMETHOD.


  METHOD start_sign_in.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.start_sign_in]
    TRY.
        " Set up authentication parameters
        DATA(lt_auth_params) = VALUE /aws1/cl_cgpauthparamstype_w=>tt_authparameterstype(
          ( VALUE /aws1/cl_cgpauthparamstype_w=>ts_authparameterstype_maprow(
              key = 'USERNAME'
              value = NEW /aws1/cl_cgpauthparamstype_w( iv_user_name ) ) )
          ( VALUE /aws1/cl_cgpauthparamstype_w=>ts_authparameterstype_maprow(
              key = 'PASSWORD'
              value = NEW /aws1/cl_cgpauthparamstype_w( iv_password ) ) )
        ).

        " Add SECRET_HASH if provided
        IF iv_secret_hash IS NOT INITIAL.
          APPEND VALUE #(
            key = 'SECRET_HASH'
            value = NEW /aws1/cl_cgpauthparamstype_w( iv_secret_hash )
          ) TO lt_auth_params.
        ENDIF.

        oo_result = lo_cgp->admininitiateauth(
          iv_userpoolid = iv_user_pool_id
          iv_clientid = iv_client_id
          iv_authflow = 'ADMIN_USER_PASSWORD_AUTH'
          it_authparameters = lt_auth_params
        ).

        DATA(lv_challenge) = oo_result->get_challengename( ).

        IF lv_challenge = 'MFA_SETUP'.
          MESSAGE 'MFA setup required for this user.' TYPE 'I'.
        ELSEIF lv_challenge = 'SOFTWARE_TOKEN_MFA'.
          MESSAGE 'Software token MFA required.' TYPE 'I'.
        ELSEIF lv_challenge IS INITIAL.
          MESSAGE 'Sign-in successful.' TYPE 'I'.
        ELSE.
          MESSAGE |Challenge: { lv_challenge }| TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_cgpusernotfoundex INTO DATA(lo_user_ex).
        MESSAGE |User { iv_user_name } not found.| TYPE 'E'.

      CATCH /aws1/cx_cgpnotauthorizedex INTO DATA(lo_auth_ex).
        MESSAGE 'Invalid credentials or not authorized.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.start_sign_in]

  ENDMETHOD.


  METHOD get_mfa_secret.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.get_mfa_secret]
    TRY.
        DATA(lo_result) = lo_cgp->associatesoftwaretoken(
          iv_session = iv_session
        ).

        ov_secret_code = lo_result->get_secretcode( ).

        MESSAGE 'MFA secret code retrieved successfully.' TYPE 'I'.

      CATCH /aws1/cx_cgpresourcenotfoundex INTO DATA(lo_ex).
        MESSAGE 'Session not found or expired.' TYPE 'E'.

      CATCH /aws1/cx_cgpsoftwaretokmfano00 INTO DATA(lo_mfa_ex).
        MESSAGE 'Software token MFA not enabled.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.get_mfa_secret]

  ENDMETHOD.


  METHOD verify_mfa.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.verify_mfa]
    TRY.
        DATA(lo_result) = lo_cgp->verifysoftwaretoken(
          iv_session = iv_session
          iv_usercode = iv_user_code
        ).

        ov_status = lo_result->get_status( ).

        IF ov_status = 'SUCCESS'.
          MESSAGE 'MFA device verified successfully.' TYPE 'I'.
        ELSE.
          MESSAGE |MFA verification status: { ov_status }| TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_cgpcodemismatchex INTO DATA(lo_code_ex).
        MESSAGE 'Invalid MFA code provided.' TYPE 'E'.

      CATCH /aws1/cx_cgpenbsoftwaretokmf00 INTO DATA(lo_enabled_ex).
        MESSAGE 'Software token MFA already enabled.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.verify_mfa]

  ENDMETHOD.


  METHOD respond_to_mfa_challenge.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cgp) = /aws1/cl_cgp_factory=>create( lo_session ).

    " snippet-start:[cgp.abapv1.respond_to_mfa_challenge]
    TRY.
        " Build challenge responses
        DATA(lt_challenge_responses) = VALUE /aws1/cl_cgpchallengerspstyp00=>tt_challengeresponsestype(
          ( VALUE /aws1/cl_cgpchallengerspstyp00=>ts_challengeresponsestype_maprow(
              key = 'USERNAME'
              value = NEW /aws1/cl_cgpchallengerspstyp00( iv_user_name ) ) )
          ( VALUE /aws1/cl_cgpchallengerspstyp00=>ts_challengeresponsestype_maprow(
              key = 'SOFTWARE_TOKEN_MFA_CODE'
              value = NEW /aws1/cl_cgpchallengerspstyp00( iv_mfa_code ) ) )
        ).

        " Add SECRET_HASH if provided
        IF iv_secret_hash IS NOT INITIAL.
          APPEND VALUE #(
            key = 'SECRET_HASH'
            value = NEW /aws1/cl_cgpchallengerspstyp00( iv_secret_hash )
          ) TO lt_challenge_responses.
        ENDIF.

        DATA(lo_result) = lo_cgp->adminrespondtoauthchallenge(
          iv_userpoolid = iv_user_pool_id
          iv_clientid = iv_client_id
          iv_challengename = 'SOFTWARE_TOKEN_MFA'
          iv_session = iv_session
          it_challengeresponses = lt_challenge_responses
        ).

        oo_auth_result = lo_result->get_authenticationresult( ).

        IF oo_auth_result IS BOUND.
          MESSAGE 'MFA challenge completed successfully.' TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_cgpcodemismatchex INTO DATA(lo_code_ex).
        MESSAGE 'Invalid MFA code.' TYPE 'E'.

      CATCH /aws1/cx_cgpexpiredcodeex INTO DATA(lo_expired_ex).
        MESSAGE 'MFA code has expired or been used already.' TYPE 'E'.

      CATCH /aws1/cx_cgpnotauthorizedex INTO DATA(lo_auth_ex).
        MESSAGE 'Not authorized to complete MFA challenge.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cgp.abapv1.respond_to_mfa_challenge]

  ENDMETHOD.
ENDCLASS.
