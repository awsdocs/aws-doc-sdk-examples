CLASS /awsex/cl_iam_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_user
      IMPORTING
        !iv_user_name TYPE /aws1/iamusernametype
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_iamcreateuserresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_user
      IMPORTING
        !iv_user_name TYPE /aws1/iamexistingusernametype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_users
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamlistusersresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_user
      IMPORTING
        !iv_user_name     TYPE /aws1/iamexistingusernametype
        !iv_new_user_name TYPE /aws1/iamusernametype OPTIONAL
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_access_key
      IMPORTING
        !iv_user_name TYPE /aws1/iamexistingusernametype
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_iamcreateaccesskeyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_access_key
      IMPORTING
        !iv_user_name   TYPE /aws1/iamexistingusernametype
        !iv_access_key_id TYPE /aws1/iamaccesskeyidtype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_access_keys
      IMPORTING
        !iv_user_name TYPE /aws1/iamexistingusernametype OPTIONAL
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_iamlistaccesskeysrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_access_key
      IMPORTING
        !iv_user_name     TYPE /aws1/iamexistingusernametype
        !iv_access_key_id TYPE /aws1/iamaccesskeyidtype
        !iv_status        TYPE /aws1/iamstatustype
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_access_key_last_used
      IMPORTING
        !iv_access_key_id TYPE /aws1/iamaccesskeyidtype
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_iamgetacckeylastuse01
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_policy
      IMPORTING
        !iv_policy_name     TYPE /aws1/iampolicynametype
        !iv_policy_document TYPE /aws1/iampolicydocumenttype
        !iv_description     TYPE /aws1/iampolicydescriptiontype OPTIONAL
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_iamcreatepolicyrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_policy
      IMPORTING
        !iv_policy_arn TYPE /aws1/iamarntype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_policies
      IMPORTING
        !iv_scope  TYPE /aws1/iampolicyscopetype DEFAULT 'All'
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamlistpolresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_policy
      IMPORTING
        !iv_policy_arn TYPE /aws1/iamarntype
      EXPORTING
        !oo_result     TYPE REF TO /aws1/cl_iamgetpolicyresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_policy_version
      IMPORTING
        !iv_policy_arn      TYPE /aws1/iamarntype
        !iv_policy_document TYPE /aws1/iampolicydocumenttype
        !iv_set_as_default  TYPE /aws1/iambooleantype DEFAULT abap_true
      EXPORTING
        !oo_result          TYPE REF TO /aws1/cl_iamcreatepolicyvrsrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS attach_user_policy
      IMPORTING
        !iv_user_name  TYPE /aws1/iamusernametype
        !iv_policy_arn TYPE /aws1/iamarntype
      RAISING
        /aws1/cx_rt_generic.

    METHODS detach_user_policy
      IMPORTING
        !iv_user_name  TYPE /aws1/iamusernametype
        !iv_policy_arn TYPE /aws1/iamarntype
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_role
      IMPORTING
        !iv_role_name                   TYPE /aws1/iamrolenametype
        !iv_assume_role_policy_document TYPE /aws1/iampolicydocumenttype
      EXPORTING
        !oo_result                      TYPE REF TO /aws1/cl_iamcreateroleresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_role
      IMPORTING
        !iv_role_name TYPE /aws1/iamrolenametype
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_role
      IMPORTING
        !iv_role_name TYPE /aws1/iamrolenametype
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_iamgetroleresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_roles
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamlistrolesresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS attach_role_policy
      IMPORTING
        !iv_role_name  TYPE /aws1/iamrolenametype
        !iv_policy_arn TYPE /aws1/iamarntype
      RAISING
        /aws1/cx_rt_generic.

    METHODS detach_role_policy
      IMPORTING
        !iv_role_name  TYPE /aws1/iamrolenametype
        !iv_policy_arn TYPE /aws1/iamarntype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_attached_role_policies
      IMPORTING
        !iv_role_name TYPE /aws1/iamrolenametype
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_iamlistattrolepolrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_role_policies
      IMPORTING
        !iv_role_name TYPE /aws1/iamrolenametype
      EXPORTING
        !oo_result    TYPE REF TO /aws1/cl_iamlistrolepolrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_groups
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamlistgroupsresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_account_alias
      IMPORTING
        !iv_account_alias TYPE /aws1/iamaccountaliastype
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_account_alias
      IMPORTING
        !iv_account_alias TYPE /aws1/iamaccountaliastype
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_account_aliases
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamlistacctaliasesrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_account_authorization_det
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamgetacctauthdetsrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_account_summary
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamgetacctsummaryrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS generate_credential_report
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamgeneratecredrptrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_credential_report
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamgetcredreportrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_account_password_policy
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamgetacpasswordply00
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_saml_providers
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_iamlistsamlpvdrsrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_service_linked_role
      IMPORTING
        !iv_aws_service_name TYPE string
        !iv_description      TYPE /aws1/iamroledescriptiontype OPTIONAL
      EXPORTING
        !oo_result           TYPE REF TO /aws1/cl_iamcresvclnkrolersp
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_IAM_ACTIONS IMPLEMENTATION.


  METHOD create_user.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_user]
    TRY.
        oo_result = lo_iam->createuser(
          iv_username = iv_user_name ).
        MESSAGE 'User created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE 'User already exists.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Limit exceeded for IAM users.' TYPE 'E'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Entity does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_user]
  ENDMETHOD.


  METHOD delete_user.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.delete_user]
    TRY.
        lo_iam->deleteuser( iv_username = iv_user_name ).
        MESSAGE 'User deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'User does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamdeleteconflictex.
        MESSAGE 'User cannot be deleted due to attached resources.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.delete_user]
  ENDMETHOD.


  METHOD list_users.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_users]
    TRY.
        oo_result = lo_iam->listusers( ).
        MESSAGE 'Retrieved user list.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when listing users.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_users]
  ENDMETHOD.


  METHOD update_user.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.update_user]
    TRY.
        lo_iam->updateuser(
          iv_username = iv_user_name
          iv_newusername = iv_new_user_name ).
        MESSAGE 'User updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'User does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE 'New user name already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.update_user]
  ENDMETHOD.


  METHOD create_access_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_access_key]
    TRY.
        oo_result = lo_iam->createaccesskey(
          iv_username = iv_user_name ).
        MESSAGE 'Access key created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'User does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Maximum number of access keys reached.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_access_key]
  ENDMETHOD.


  METHOD delete_access_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.delete_access_key]
    TRY.
        lo_iam->deleteaccesskey(
          iv_accesskeyid = iv_access_key_id
          iv_username = iv_user_name ).
        MESSAGE 'Access key deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Access key or user does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.delete_access_key]
  ENDMETHOD.


  METHOD list_access_keys.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_access_keys]
    TRY.
        oo_result = lo_iam->listaccesskeys(
          iv_username = iv_user_name ).
        MESSAGE 'Retrieved access key list.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'User does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_access_keys]
  ENDMETHOD.


  METHOD update_access_key.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.update_access_key]
    TRY.
        lo_iam->updateaccesskey(
          iv_accesskeyid = iv_access_key_id
          iv_status = iv_status
          iv_username = iv_user_name ).
        MESSAGE 'Access key updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Access key or user does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.update_access_key]
  ENDMETHOD.


  METHOD get_access_key_last_used.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_access_key_last_used]
    TRY.
        oo_result = lo_iam->getaccesskeylastused(
          iv_accesskeyid = iv_access_key_id ).
        MESSAGE 'Retrieved access key last used information.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Access key does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_access_key_last_used]
  ENDMETHOD.


  METHOD create_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_policy]
    TRY.
        oo_result = lo_iam->createpolicy(
          iv_policyname = iv_policy_name
          iv_policydocument = iv_policy_document
          iv_description = iv_description ).
        MESSAGE 'Policy created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE 'Policy already exists.' TYPE 'E'.
      CATCH /aws1/cx_iammalformedplydocex.
        MESSAGE 'Policy document is malformed.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Policy limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_policy]
  ENDMETHOD.


  METHOD delete_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.delete_policy]
    TRY.
        lo_iam->deletepolicy( iv_policyarn = iv_policy_arn ).
        MESSAGE 'Policy deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamdeleteconflictex.
        MESSAGE 'Policy cannot be deleted due to attachments.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.delete_policy]
  ENDMETHOD.


  METHOD list_policies.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_policies]
    TRY.
        oo_result = lo_iam->listpolicies( iv_scope = iv_scope ).
        MESSAGE 'Retrieved policy list.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when listing policies.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_policies]
  ENDMETHOD.


  METHOD get_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_policy]
    TRY.
        oo_result = lo_iam->getpolicy( iv_policyarn = iv_policy_arn ).
        MESSAGE 'Retrieved policy information.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Policy does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_policy]
  ENDMETHOD.


  METHOD create_policy_version.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_policy_version]
    TRY.
        oo_result = lo_iam->createpolicyversion(
          iv_policyarn = iv_policy_arn
          iv_policydocument = iv_policy_document
          iv_setasdefault = iv_set_as_default ).
        MESSAGE 'Policy version created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iammalformedplydocex.
        MESSAGE 'Policy document is malformed.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Policy version limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_policy_version]
  ENDMETHOD.


  METHOD attach_user_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.attach_user_policy]
    TRY.
        lo_iam->attachuserpolicy(
          iv_username = iv_user_name
          iv_policyarn = iv_policy_arn ).
        MESSAGE 'Policy attached to user successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'User or policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Policy attachment limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.attach_user_policy]
  ENDMETHOD.


  METHOD detach_user_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.detach_user_policy]
    TRY.
        lo_iam->detachuserpolicy(
          iv_username = iv_user_name
          iv_policyarn = iv_policy_arn ).
        MESSAGE 'Policy detached from user successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'User or policy does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.detach_user_policy]
  ENDMETHOD.


  METHOD create_role.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_role]
    TRY.
        oo_result = lo_iam->createrole(
          iv_rolename = iv_role_name
          iv_assumerolepolicydocument = iv_assume_role_policy_document ).
        MESSAGE 'Role created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE 'Role already exists.' TYPE 'E'.
      CATCH /aws1/cx_iammalformedplydocex.
        MESSAGE 'Assume role policy document is malformed.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Role limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_role]
  ENDMETHOD.


  METHOD delete_role.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.delete_role]
    TRY.
        lo_iam->deleterole( iv_rolename = iv_role_name ).
        MESSAGE 'Role deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Role does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamdeleteconflictex.
        MESSAGE 'Role cannot be deleted due to attached resources.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.delete_role]
  ENDMETHOD.


  METHOD get_role.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_role]
    TRY.
        oo_result = lo_iam->getrole( iv_rolename = iv_role_name ).
        MESSAGE 'Retrieved role information.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Role does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_role]
  ENDMETHOD.


  METHOD list_roles.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_roles]
    TRY.
        oo_result = lo_iam->listroles( ).
        MESSAGE 'Retrieved role list.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when listing roles.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_roles]
  ENDMETHOD.


  METHOD attach_role_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.attach_role_policy]
    TRY.
        lo_iam->attachrolepolicy(
          iv_rolename = iv_role_name
          iv_policyarn = iv_policy_arn ).
        MESSAGE 'Policy attached to role successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Role or policy does not exist.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Policy attachment limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.attach_role_policy]
  ENDMETHOD.


  METHOD detach_role_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.detach_role_policy]
    TRY.
        lo_iam->detachrolepolicy(
          iv_rolename = iv_role_name
          iv_policyarn = iv_policy_arn ).
        MESSAGE 'Policy detached from role successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Role or policy does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.detach_role_policy]
  ENDMETHOD.


  METHOD list_attached_role_policies.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_attached_role_policies]
    TRY.
        oo_result = lo_iam->listattachedrolepolicies(
          iv_rolename = iv_role_name ).
        MESSAGE 'Retrieved attached policy list for role.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Role does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_attached_role_policies]
  ENDMETHOD.


  METHOD list_role_policies.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_role_policies]
    TRY.
        oo_result = lo_iam->listrolepolicies(
          iv_rolename = iv_role_name ).
        MESSAGE 'Retrieved inline policy list for role.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Role does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_role_policies]
  ENDMETHOD.


  METHOD list_groups.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_groups]
    TRY.
        oo_result = lo_iam->listgroups( ).
        MESSAGE 'Retrieved group list.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when listing groups.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_groups]
  ENDMETHOD.


  METHOD create_account_alias.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_account_alias]
    TRY.
        lo_iam->createaccountalias(
          iv_accountalias = iv_account_alias ).
        MESSAGE 'Account alias created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE 'Account alias already exists.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Account alias limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_account_alias]
  ENDMETHOD.


  METHOD delete_account_alias.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.delete_account_alias]
    TRY.
        lo_iam->deleteaccountalias(
          iv_accountalias = iv_account_alias ).
        MESSAGE 'Account alias deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'Account alias does not exist.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.delete_account_alias]
  ENDMETHOD.


  METHOD list_account_aliases.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_account_aliases]
    TRY.
        oo_result = lo_iam->listaccountaliases( ).
        MESSAGE 'Retrieved account alias list.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when listing account aliases.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_account_aliases]
  ENDMETHOD.


  METHOD get_account_authorization_det.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_account_authorization_details]
    TRY.
        oo_result = lo_iam->getaccountauthdetails( ).
        MESSAGE 'Retrieved account authorization details.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when getting account authorization details.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_account_authorization_details]
  ENDMETHOD.


  METHOD get_account_summary.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_account_summary]
    TRY.
        oo_result = lo_iam->getaccountsummary( ).
        MESSAGE 'Retrieved account summary.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when getting account summary.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_account_summary]
  ENDMETHOD.


  METHOD generate_credential_report.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.generate_credential_report]
    TRY.
        oo_result = lo_iam->generatecredentialreport( ).
        MESSAGE 'Credential report generation started.' TYPE 'I'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Report generation limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when generating credential report.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.generate_credential_report]
  ENDMETHOD.


  METHOD get_credential_report.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_credential_report]
    TRY.
        oo_result = lo_iam->getcredentialreport( ).
        MESSAGE 'Retrieved credential report.' TYPE 'I'.
      CATCH /aws1/cx_iamcredrptnotpresen00.
        MESSAGE 'Credential report not present.' TYPE 'E'.
      CATCH /aws1/cx_iamcredrptexpiredex.
        MESSAGE 'Credential report expired.' TYPE 'E'.
      CATCH /aws1/cx_iamcredrptnotreadyex.
        MESSAGE 'Credential report not ready.' TYPE 'E'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when getting credential report.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_credential_report]
  ENDMETHOD.


  METHOD get_account_password_policy.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.get_account_password_policy]
    TRY.
        oo_result = lo_iam->getaccountpasswordpolicy( ).
        MESSAGE 'Retrieved account password policy.' TYPE 'I'.
      CATCH /aws1/cx_iamnosuchentityex.
        MESSAGE 'No password policy exists.' TYPE 'E'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when getting password policy.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.get_account_password_policy]
  ENDMETHOD.


  METHOD list_saml_providers.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.list_saml_providers]
    TRY.
        oo_result = lo_iam->listsamlproviders( ).
        MESSAGE 'Retrieved SAML provider list.' TYPE 'I'.
      CATCH /aws1/cx_iamservicefailureex.
        MESSAGE 'Service failure when listing SAML providers.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.list_saml_providers]
  ENDMETHOD.


  METHOD create_service_linked_role.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iam) = /aws1/cl_iam_factory=>create( lo_session ).

    " snippet-start:[iam.abapv1.create_service_linked_role]
    TRY.
        oo_result = lo_iam->createservicelinkedrole(
          iv_awsservicename = iv_aws_service_name
          iv_description = iv_description ).
        MESSAGE 'Service-linked role created successfully.' TYPE 'I'.
      CATCH /aws1/cx_iamentityalrdyexex.
        MESSAGE 'Service-linked role already exists.' TYPE 'E'.
      CATCH /aws1/cx_iamlimitexceededex.
        MESSAGE 'Role limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_iamservicenotsuppedex.
        MESSAGE 'Service does not support service-linked roles.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[iam.abapv1.create_service_linked_role]
  ENDMETHOD.
ENDCLASS.
