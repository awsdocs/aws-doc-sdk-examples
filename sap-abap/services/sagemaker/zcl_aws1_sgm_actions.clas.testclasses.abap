" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_sgm_actions DEFINITION FOR TESTING DURATION MEDIUM RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_sgm TYPE REF TO /aws1/if_sgm.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_sgm_actions TYPE REF TO zcl_aws1_sgm_actions.
    DATA av_lrole TYPE /aws1/sgmrolearn.
    DATA av_file_content TYPE /aws1/s3_streamingblob.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.
    METHODS list_training_jobs FOR TESTING.
    METHODS list_notebook_instances FOR TESTING.
    METHODS list_models FOR TESTING.
    METHODS list_algorithms FOR TESTING.
    METHODS create_model FOR TESTING.
    METHODS create_endpoint FOR TESTING.
    METHODS create_transform_job FOR TESTING.
    METHODS create_training_job FOR TESTING.
    METHODS delete_endpoint FOR TESTING.
    METHODS delete_model FOR TESTING.
    METHODS describe_training_job FOR TESTING.

ENDCLASS.       "ltc_Zcl_Aws1_Sgm_Actions


CLASS ltc_zcl_aws1_sgm_actions IMPLEMENTATION.
  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_sgm = /aws1/cl_sgm_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_sgm_actions = NEW zcl_aws1_sgm_actions( ).

    "Training data.
    av_file_content = /aws1/cl_rt_util=>string_to_xstring(
      |0 0:75 1:6.4 17:1 25:1 95:1 325:1\n| &&
      |0 0:59 1:7.3 16:1 19:1 296:1 328:1\n| &&
      |0 0:66 1:6.6 9:1 25:1 193:1 330:1\n| &&
      |0 0:64 1:6.9 9:1 21:1 146:1 330:1\n| &&
      |0 0:65 1:8.699999999999999 9:1 29:1 252:1 325:1\n| &&
      |0 0:61 1:6.1 14:1 29:1 42:1 325:1\n| &&
      |0 0:81 1:8.300000000000001 12:1 29:1 166:1 325:1\n| &&
      |0 0:43 1:3.9 10:1 27:1 310:1 330:1\n| &&
      |0 0:69 1:6.9 8:1 25:1 95:1 325:1\n| &&
      |1 0:88 1:8.6 9:1 21:1 204:1 330:1\n| &&
      |0 0:55 1:7.8 12:1 22:1 140:1 325:1\n| &&
      |0 0:62 1:6 16:1 29:1 95:1 325:1\n| &&
      |0 0:66 1:7.8 15:1 19:1 204:1 330:1\n| &&
      |0 0:85 1:5.8 7:1 19:1 295:1 328:1\n| &&
      |0 0:80 1:6.8 12:1 29:1 166:1 325:1\n| &&
      |0 0:70 1:3.6 7:1 28:1 95:1 330:1\n| &&
      |1 0:81 1:6.2 10:1 27:1 61:1 328:1\n| &&
      |0 0:77 1:7.1 7:1 30:1 222:1 326:1\n| &&
      |0 0:39 1:3.1 7:1 27:1 102:1 330:1\n| &&
      |0 0:55 1:5.7 4:1 21:1 278:1 330:1\n| &&
      |1 0:87 1:7.6 11:1 19:1 310:1 328:1\n| &&
      |0 0:62 1:6.2 9:1 25:1 149:1 325:1\n| &&
      |0 0:80 1:8 4:1 24:1 230:1 325:1\n| &&
      |0 0:68 1:4.9 7:1 30:1 298:1 330:1\n| &&
      |0 0:73 1:9 4:1 26:1 204:1 330:1\n| &&
      |0 0:66 1:7.2 9:1 19:1 204:1 330:1\n| &&
      |0 0:66 1:8.199999999999999 8:1 26:1 38:1 325:1\n| &&
      |0 0:86 1:8.199999999999999 4:1 23:1 209:1 325:1\n| &&
      |0 0:81 1:6.4 8:1 29:1 95:1 325:1\n| &&
      |0 0:71 1:7.1 5:1 29:1 95:1 325:1\n| &&
      |0 0:79 1:7.9 5:1 22:1 42:1 325:1\n| &&
      |1 0:64 1:6.4 9:1 22:1 252:1 330:1\n| &&
      |0 0:78 1:8.300000000000001 9:1 29:1 42:1 325:1\n| &&
      |1 0:91 1:9 8:1 19:1 305:1 328:1\n| &&
      |0 0:84 1:6.8 10:1 29:1 95:1 326:1\n| &&
      |0 0:85 1:8.199999999999999 7:1 27:1 322:1 328:1\n| &&
      |0 0:33 1:7 4:1 27:1 166:1 325:1\n| &&
      |0 0:73 1:3 7:1 27:1 42:1 328:1\n| &&
      |0 0:81 1:8 6:1 29:1 95:1 325:1\n| &&
      |0 0:59 1:6.8 2:1 25:1 75:1 325:1\n| &&
      |0 0:85 1:7.5 9:1 29:1 249:1 325:1\n| &&
      |0 0:79 1:7.6 5:1 29:1 95:1 325:1\n| &&
      |0 0:63 1:6.9 10:1 29:1 95:1 325:1\n| &&
      |1 0:95 1:6.8 10:1 23:1 252:1 325:1\n| &&
      |0 0:47 1:6.1 6:1 19:1 95:1 330:1\n| &&
      |0 0:81 1:8.6 14:1 19:1 249:1 328:1\n| &&
      |0 0:50 1:7 9:1 27:1 193:1 328:1\n| &&
      |0 0:88 1:7.9 10:1 29:1 95:1 326:1\n| &&
      |1 0:74 1:6.1 10:1 29:1 166:1 325:1\n| &&
      |0 0:82 1:8.1 17:1 29:1 95:1 325:1\n| &&
      |0 0:67 1:7.2 10:1 19:1 95:1 328:1\n| &&
      |0 0:67 1:6.7 6:1 23:1 307:1 326:1\n| &&
      |0 0:77 1:7.9 10:1 29:1 95:1 325:1\n| &&
      |0 0:68 1:5.9 7:1 19:1 61:1 328:1\n| &&
      |1 0:81 1:7.5 10:1 19:1 310:1 326:1\n| &&
      |0 0:62 1:7.5 4:1 26:1 278:1 326:1\n| &&
      |1 0:53 1:6.9 5:1 26:1 149:1 325:1\n| &&
      |0 0:78 1:8.1 2:1 26:1 315:1 326:1\n| &&
      |0 0:54 1:5.4 17:1 29:1 79:1 325:1\n| &&
      |0 0:89 1:8.4 10:1 25:1 252:1 326:1\n| &&
      |1 0:75 1:5.6 2:1 26:1 209:1 325:1\n| &&
      |0 0:78 1:8.300000000000001 9:1 29:1 252:1 325:1\n| &&
      |0 0:75 1:8.199999999999999 7:1 28:1 198:1 325:1\n| &&
      |0 0:72 1:7 7:1 27:1 256:1 330:1\n| &&
      |0 0:57 1:4.8 9:1 21:1 71:1 330:1\n| &&
      |0 0:58 1:5.9 10:1 27:1 204:1 330:1\n| &&
      |0 0:74 1:8.300000000000001 17:1 28:1 224:1 330:1\n| &&
      |0 0:48 1:7.1 14:1 26:1 249:1 330:1\n| &&
      |0 0:82 1:7.3 10:1 22:1 276:1 330:1\n| &&
      |0 0:66 1:7.4 6:1 25:1 307:1 325:1\n| &&
      |1 0:70 1:8.699999999999999 9:1 19:1 95:1 325:1\n| &&
      |0 0:86 1:8.4 7:1 23:1 295:1 326:1\n| &&
      |0 0:42 1:4.2 17:1 25:1 204:1 330:1\n| &&
      |0 0:75 1:7.8 4:1 27:1 75:1 325:1\n| &&
      |0 0:93 1:9.300000000000001 9:1 19:1 71:1 330:1\n| &&
      |0 0:63 1:7.8 17:1 22:1 279:1 330:1\n| &&
      |1 0:71 1:5.7 4:1 24:1 193:1 326:1\n| &&
      |0 0:42 1:5 16:1 19:1 256:1 326:1\n| &&
      |0 0:53 1:5.2 16:1 22:1 249:1 330:1\n| &&
      |0 0:64 1:6.4 12:1 19:1 282:1 330:1\n| &&
      |0 0:60 1:7 14:1 19:1 209:1 325:1\n| &&
      |0 0:86 1:7.4 16:1 21:1 95:1 328:1\n| &&
      |0 0:91 1:8.9 17:1 29:1 249:1 325:1\n| &&
      |1 0:82 1:7.8 10:1 26:1 61:1 328:1\n| &&
      |1 0:92 1:8.300000000000001 16:1 22:1 95:1 330:1\n| &&
      |1 0:85 1:8.800000000000001 9:1 26:1 252:1 325:1\n| &&
      |0 0:79 1:7.5 9:1 29:1 252:1 325:1\n| &&
      |1 0:61 1:5.3 14:1 28:1 35:1 325:1\n| &&
      |0 0:75 1:7.7 7:1 28:1 95:1 330:1\n| &&
      |0 0:68 1:8.5 14:1 29:1 279:1 326:1\n| &&
      |1 0:67 1:5 16:1 27:1 71:1 328:1\n| &&
      |0 0:79 1:8.5 12:1 26:1 103:1 330:1\n| &&
      |0 0:61 1:5.1 16:1 29:1 105:1 330:1\n| &&
      |1 0:72 1:8.699999999999999 12:1 26:1 71:1 330:1\n| &&
      |0 0:60 1:6.3 9:1 20:1 307:1 328:1\n| &&
      |0 0:67 1:7.8 9:1 29:1 39:1 325:1\n| &&
      |0 0:53 1:5.6 9:1 26:1 189:1 330:1\n| &&
      |1 0:98 1:7.7 8:1 29:1 42:1 330:1\n| &&
      |0 0:76 1:6.7 7:1 28:1 75:1 330:1\n| &&
      |1 0:74 1:5.3 7:1 28:1 95:1 330:1\n|
  ).

  ENDMETHOD.

  METHOD create_training_job.

    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatetrnjobrsp.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define role Amazon Resource Name (ARN).
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Create training data in Amazon Simple Storage Service (Amazon S3).
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Testing.
    ao_sgm_actions->create_training_job(
      EXPORTING
        iv_training_job_name            = lv_training_job_name
        iv_role_arn                     = av_lrole
        iv_trn_data_s3datatype          = cv_trn_data_s3datatype
        iv_trn_data_s3datadistribution  = cv_trn_data_s3datadistribution
        iv_trn_data_s3uri                = lv_trn_data_s3uri
        iv_trn_data_compressiontype     = cv_trn_data_compressiontype
        iv_trn_data_contenttype         = cv_trn_data_contenttype
        iv_val_data_s3datatype          = cv_val_data_s3datatype
        iv_val_data_s3datadistribution  = cv_val_data_s3datadistribution
        iv_val_data_s3uri                = lv_val_data_s3uri
        iv_val_data_compressiontype      = cv_val_data_compressiontype
        iv_val_data_contenttype          = cv_val_data_contenttype
        iv_hp_max_depth                 = cv_hp_max_depth
        iv_hp_scale_pos_weight          = cv_hp_scale_pos_weight
        iv_hp_num_round                  = cv_hp_num_round
        iv_hp_objective                  = cv_hp_objective
        iv_hp_subsample                  = cv_hp_subsample
        iv_hp_eval_metric                = cv_hp_eval_metric
        iv_hp_eta                        = cv_hp_eta
        iv_training_image               = cv_training_image
        iv_training_input_mode          = cv_training_input_mode
        iv_instance_count               = cv_instance_count
        iv_instance_type                = cv_instance_type
        iv_volume_sizeingb              = cv_volume_sizeingb
        iv_s3_output_path               = lv_s3_output_path
        iv_max_runtime_in_seconds       = cv_max_runtime_in_seconds
      IMPORTING
       oo_result              = lo_result
    ).

    lv_found = abap_false.
    IF lo_result->has_trainingjobarn( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Training Job cannot be found|
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Clean up.
    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD list_training_jobs.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Create training data in Amazon S3.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Testing.
    ao_sgm_actions->list_training_jobs(
      EXPORTING
        iv_name_contains = lv_training_job_name
        iv_max_results = cv_max_results
      IMPORTING
        oo_result = lo_list_result
    ).

    "Validation.
    lv_found = abap_false.
    LOOP AT lo_list_result->get_trainingjobsummaries( ) INTO DATA(lo_job).
      IF lo_job->has_trainingjobname( ) = 'X'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Training job cannot be found|
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Clean up.
    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD describe_training_job.

    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_job_status TYPE /aws1/sgmtrainingjobstatus VALUE 'InProgress'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Testing describe training job method.
    CALL METHOD ao_sgm_actions->describe_training_job
      EXPORTING
        iv_training_job_name = lv_training_job_name
      IMPORTING
        oo_result            = lo_list_result.

    lv_found = abap_false.

    IF lo_list_result->get_trainingjobstatus( ) = cv_job_status.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Training job status is incorrect|
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Clean up.
    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD create_model.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_model_data_url TYPE /aws1/sgmurl.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define Amazon S3 parameters for data.
    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Define model name.
    lv_model_name = 'code-example-model-' && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.
    lv_model_data_url = 's3://' && lv_bucket_name && '/sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.


    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Test the create_model method.
    CALL METHOD ao_sgm_actions->create_model
      EXPORTING
        iv_model_name         = lv_model_name
        iv_execution_role_arn = av_lrole
        iv_model_data_url     = lv_model_data_url
        iv_container_image    = cv_container_image
      IMPORTING
        oo_result             = lo_result.

    lv_found = abap_false.

    IF lo_result->has_modelarn( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Model cannot be found|
    ).

    "Clean up.
    ao_sgm->deletemodel(
        iv_modelname = lv_model_name
    ).

    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD list_models.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_model_data_url TYPE /aws1/sgmurl.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.
    DATA lo_model_list_result TYPE REF TO /aws1/cl_sgmlistmodelsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define Amazon S3 parameters.
    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Define model name.
    lv_model_name = 'code-example-model-' && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.
    lv_model_data_url = 's3://' && lv_bucket_name && '/sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Run method to create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Create an ABAP internal table for the container image based on input variables.
    CREATE OBJECT lo_primarycontainer
      EXPORTING
        iv_image        = cv_container_image
        iv_modeldataurl = lv_model_data_url.

    "Create a new model via so_sgm.
    CALL METHOD ao_sgm->createmodel
      EXPORTING
        iv_modelname        = lv_model_name
        iv_executionrolearn = av_lrole
        io_primarycontainer = lo_primarycontainer.

    "Call list_models via so_sgm_actions.
    CALL METHOD ao_sgm_actions->list_models
      EXPORTING
        iv_name_contains = lv_model_name
      IMPORTING
        oo_result        = lo_model_list_result.

    lv_found = abap_false.

    LOOP AT lo_model_list_result->get_models( ) INTO DATA(lo_models).
      IF lo_models->get_modelname( ) = lv_model_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Model cannot be found|
    ).

    "Clean up.
    ao_sgm->deletemodel(
        iv_modelname = lv_model_name
    ).

    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD delete_model.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_model_data_url TYPE /aws1/sgmurl.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.
    DATA lo_model_list_result TYPE REF TO /aws1/cl_sgmlistmodelsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define Amazon S3 parameters.
    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Define model name.
    lv_model_name = 'code-example-model-' && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.
    lv_model_data_url = 's3://' && lv_bucket_name && '/sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Create an ABAP internal table for the container image based on input variables.
    CREATE OBJECT lo_primarycontainer
      EXPORTING
        iv_image        = cv_container_image
        iv_modeldataurl = lv_model_data_url.

    "Create a new model via so_sgm.
    CALL METHOD ao_sgm->createmodel
      EXPORTING
        iv_modelname        = lv_model_name
        iv_executionrolearn = av_lrole
        io_primarycontainer = lo_primarycontainer.

    "Test the ao_sgm_actions delete_model method.
    CALL METHOD ao_sgm_actions->delete_model
      EXPORTING
        iv_model_name = lv_model_name.

    "List the deleted model via so_sgm.
    lo_model_list_result = ao_sgm->listmodels(
      iv_namecontains = lv_model_name
    ).

    lv_found = abap_false.

    "The model should be deleted.
    LOOP AT lo_model_list_result->get_models( ) INTO DATA(lo_models).
      IF lo_models->get_modelname( ) = lv_model_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Model was not deleted|
    ).

    "Clean up.
    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD create_endpoint.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_model_data_url TYPE /aws1/sgmurl.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.
    DATA lo_model_list_result TYPE REF TO /aws1/cl_sgmlistmodelsoutput.
    DATA lv_endpoint_name TYPE /aws1/sgmendpointname.
    DATA lv_endpoint_config_name TYPE /aws1/sgmendpointconfigname.
    DATA lv_endpoint_variant_name TYPE /aws1/sgmvariantname.
    DATA lo_endpoint_output TYPE REF TO /aws1/cl_sgmcreateendptoutput.
    DATA lo_endpoint_result TYPE REF TO /aws1/cl_sgmdescrendptoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define Amazon S3 parameters.
    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define endpoint parameters.
    CONSTANTS cv_ep_instance_type TYPE  /aws1/sgminstancetype VALUE 'ml.m4.xlarge'.
    CONSTANTS cv_ep_initial_instance_count  TYPE  /aws1/sgminitialtaskcount VALUE  '1'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Define model name.
    lv_model_name = 'code-example-model-' && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.

    "Define endpoint name.
    lv_endpoint_name = 'code-example-endpoint-' && lv_uuid_16.
    lv_endpoint_config_name =  'code-example-endpoint-cfg-' && lv_uuid_16.
    lv_endpoint_variant_name =  'code-example-endpoint-variant-' && lv_uuid_16.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.
    lv_model_data_url = 's3://' && lv_bucket_name && '/sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Run method to create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Create an ABAP internal table for the container image based on input variables.
    CREATE OBJECT lo_primarycontainer
      EXPORTING
        iv_image        = cv_container_image
        iv_modeldataurl = lv_model_data_url.

    "Create a new model via so_sgm.
    CALL METHOD ao_sgm->createmodel
      EXPORTING
        iv_modelname        = lv_model_name
        iv_executionrolearn = av_lrole
        io_primarycontainer = lo_primarycontainer.

    "Test the create_endpoint method.
    CALL METHOD ao_sgm_actions->create_endpoint
      EXPORTING
        iv_model_name             = lv_model_name
        iv_endpoint_config_name   = lv_endpoint_config_name
        iv_endpoint_name          = lv_endpoint_name
        iv_instance_type          = cv_ep_instance_type
        iv_variant_name           = lv_endpoint_variant_name
        iv_initial_instance_count = cv_ep_initial_instance_count
      IMPORTING
        oo_result                 = lo_endpoint_output.

    lv_found = abap_false.

    IF lo_endpoint_output->has_endpointarn( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Endpoint not found|
    ).

    "Wait for endpoint creation to be completed.
    lo_endpoint_result = ao_sgm->describeendpoint( iv_endpointname = lv_endpoint_name ).
    WHILE lo_endpoint_result->get_endpointstatus( ) <> 'InService'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_endpoint_result = ao_sgm->describeendpoint( iv_endpointname = lv_endpoint_name ).
    ENDWHILE.

    "Clean up.
    ao_sgm->deleteendpoint(
        iv_endpointname = lv_endpoint_name
    ).

    ao_sgm->deleteendpointconfig(
      iv_endpointconfigname = lv_endpoint_config_name
    ).

    ao_sgm->deletemodel(
        iv_modelname = lv_model_name
    ).

    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD delete_endpoint.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_model_data_url TYPE /aws1/sgmurl.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.
    DATA lo_model_list_result TYPE REF TO /aws1/cl_sgmlistmodelsoutput.
    DATA lv_endpoint_name TYPE /aws1/sgmendpointname.
    DATA lv_endpoint_config_name TYPE /aws1/sgmendpointconfigname.
    DATA lv_endpoint_variant_name TYPE /aws1/sgmvariantname.
    DATA lo_endpoint_output TYPE REF TO /aws1/cl_sgmcreateendptoutput.
    DATA lo_endpoint_result TYPE REF TO /aws1/cl_sgmdescrendptoutput.
    DATA lo_endpoint_list_result TYPE REF TO /aws1/cl_sgmlistendptsoutput.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define Amazon S3 parameters.
    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define endpoint parameters.
    CONSTANTS cv_ep_instance_type TYPE  /aws1/sgminstancetype VALUE 'ml.m4.xlarge'.
    CONSTANTS cv_ep_initial_instance_count  TYPE  /aws1/sgminitialtaskcount VALUE  '1'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Define model name.
    lv_model_name = 'code-example-model-' && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.

    "Define endpoint name.
    lv_endpoint_name = 'code-example-endpoint-' && lv_uuid_16.
    lv_endpoint_config_name =  'code-example-endpoint-cfg-' && lv_uuid_16.
    lv_endpoint_variant_name =  'code-example-endpoint-variant-' && lv_uuid_16.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.
    lv_model_data_url = 's3://' && lv_bucket_name && '/sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP objects for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Run method to create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Create an ABAP internal table for the container image based on input variables.
    CREATE OBJECT lo_primarycontainer
      EXPORTING
        iv_image        = cv_container_image
        iv_modeldataurl = lv_model_data_url.

    "Create a new model via so_sgm.
    CALL METHOD ao_sgm->createmodel
      EXPORTING
        iv_modelname        = lv_model_name
        iv_executionrolearn = av_lrole
        io_primarycontainer = lo_primarycontainer.

    "Create an endpoint.
    DATA lt_production_variants TYPE /aws1/cl_sgmproductionvariant=>tt_productionvariantlist.
    DATA lo_production_variants TYPE REF TO /aws1/cl_sgmproductionvariant.
    DATA lo_ep_config_result TYPE REF TO /aws1/cl_sgmcreateendptcfgout.


    CREATE OBJECT lo_production_variants
      EXPORTING
        iv_variantname          = lv_endpoint_variant_name
        iv_modelname            = lv_model_name
        iv_initialinstancecount = cv_ep_initial_instance_count
        iv_instancetype         = cv_ep_instance_type.

    INSERT lo_production_variants INTO TABLE lt_production_variants.

    "Create an endpoint configuration.
    lo_ep_config_result = ao_sgm->createendpointconfig(
      iv_endpointconfigname = lv_endpoint_config_name
      it_productionvariants = lt_production_variants
    ).

    "Create an endpoint.
    lo_endpoint_output = ao_sgm->createendpoint(
        iv_endpointconfigname = lv_endpoint_config_name
        iv_endpointname = lv_endpoint_name
    ).

    lv_found = abap_false.

    IF lo_endpoint_output->has_endpointarn( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Endpoint not found|
    ).

    "Wait for endpoint creation to be completed.
    lo_endpoint_result = ao_sgm->describeendpoint( iv_endpointname = lv_endpoint_name ).
    WHILE lo_endpoint_result->get_endpointstatus( ) <> 'InService'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_endpoint_result = ao_sgm->describeendpoint( iv_endpointname = lv_endpoint_name ).
    ENDWHILE.

    "Testing.
    ao_sgm_actions->delete_endpoint(
        iv_endpoint_name = lv_endpoint_name
        iv_endpoint_config_name = lv_endpoint_config_name
    ).

    WAIT UP TO 30 SECONDS.

    "Check if endpoint exists.
    lo_endpoint_list_result = ao_sgm->listendpoints(
      iv_namecontains = lv_endpoint_name
    ).

    lv_found = abap_false.

    "The endpoint should be deleted.
    LOOP AT lo_endpoint_list_result->get_endpoints( ) INTO DATA(lo_endpoints).
      IF lo_endpoints->get_endpointname( ) = lv_endpoint_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = |Endpoint was not deleted|
    ).

    "Cleaning up via ao_sgm.
    ao_sgm->deletemodel(
        iv_modelname = lv_model_name
    ).

    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).


  ENDMETHOD.

  METHOD list_notebook_instances.

    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlstnotebookinsts01.
    DATA lv_notebook_name TYPE /aws1/sgmnotebookinstancename.
    DATA lo_notebook_result TYPE REF TO /aws1/cl_sgmdscnotebookinstout.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_instancetype TYPE /aws1/sgminstancetype VALUE 'ml.t3.medium'.

    "Define ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define notebook name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_notebook_name =  'code-example-sgm-notebook-' && lv_uuid_16.

    "Create a notebook instance.
    ao_sgm->createnotebookinstance(
      EXPORTING
        iv_notebookinstancename = lv_notebook_name
        iv_instancetype         = cv_instancetype
        iv_rolearn              = av_lrole
    ).

    "Waiter.
    lo_notebook_result = ao_sgm->describenotebookinstance( iv_notebookinstancename = lv_notebook_name ).
    WHILE lo_notebook_result->get_notebookinstancestatus( ) <> 'InService'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_notebook_result = ao_sgm->describenotebookinstance( iv_notebookinstancename = lv_notebook_name ).
    ENDWHILE.

    " Test the list notebook instance.
    ao_sgm_actions->list_notebook_instances(
      EXPORTING
        iv_name_contains = lv_notebook_name
      IMPORTING
        oo_result = lo_list_result
    ).

    lv_found = abap_false.

    LOOP AT lo_list_result->get_notebookinstances( ) INTO DATA(lo_notebook).
      IF lo_notebook->get_notebookinstancename( ) = lv_notebook_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Notebook cannot be found|
    ).

    "Stop notebook instance before deletion.
    ao_sgm->stopnotebookinstance(
        iv_notebookinstancename = lv_notebook_name
        ).

    "Waiter.
    lo_notebook_result = ao_sgm->describenotebookinstance( iv_notebookinstancename = lv_notebook_name ).
    WHILE lo_notebook_result->get_notebookinstancestatus( ) <> 'Stopped'.
      IF sy-index = 30.
        EXIT.               "maximum 10 minutes
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_notebook_result = ao_sgm->describenotebookinstance( iv_notebookinstancename = lv_notebook_name ).
    ENDWHILE.

    "Delete notebook.
    ao_sgm->deletenotebookinstance(
       iv_notebookinstancename = lv_notebook_name
        ).

  ENDMETHOD.

  METHOD create_transform_job.

    DATA lo_hyperparameters_w TYPE REF TO /aws1/cl_sgmhyperparameters_w.
    DATA lt_hyperparameters TYPE /aws1/cl_sgmhyperparameters_w=>tt_hyperparameters.
    DATA lt_input_data_config TYPE /aws1/cl_sgmchannel=>tt_inputdataconfig.
    DATA lo_trn_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_trn_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_trn_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_val_channel TYPE REF TO /aws1/cl_sgmchannel.
    DATA lo_val_datasource TYPE REF TO /aws1/cl_sgmdatasource.
    DATA lo_val_s3datasource TYPE REF TO /aws1/cl_sgms3datasource.
    DATA lo_output_data_config TYPE REF TO  /aws1/cl_sgmoutputdataconfig.
    DATA lo_resource_config  TYPE REF TO /aws1/cl_sgmresourceconfig.
    DATA lo_algorithm_specification TYPE REF TO /aws1/cl_sgmalgorithmspec.
    DATA lo_list_result TYPE REF TO /aws1/cl_sgmlisttrnjobsrsp.
    DATA lo_stopping_condition TYPE REF TO  /aws1/cl_sgmstoppingcondition.
    DATA lv_training_job_name TYPE /aws1/sgmtrainingjobname.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_result TYPE REF TO /aws1/cl_sgmcreatemodeloutput.
    DATA lv_model_name TYPE /aws1/sgmmodelname.
    DATA lv_model_data_url TYPE /aws1/sgmurl.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_file_content TYPE /aws1/s3_streamingblob.
    DATA lv_trn_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_val_data_s3uri TYPE /aws1/sgms3uri.
    DATA lv_s3_output_path TYPE /aws1/sgms3uri.
    DATA lv_s3_transform_output_path TYPE /aws1/sgms3uri.
    DATA lv_model_key TYPE /aws1/s3_objectkey.
    DATA lo_training_result TYPE REF TO /aws1/cl_sgmdescrtrnjobrsp.
    DATA lo_primarycontainer TYPE REF TO /aws1/cl_sgmcontainerdefn.
    DATA lo_model_list_result TYPE REF TO /aws1/cl_sgmlistmodelsoutput.
    DATA lo_tf_result TYPE REF TO /aws1/cl_sgmcretransformjobrsp.
    DATA lv_transform_job_name TYPE /aws1/sgmtransformjobname.
    DATA lv_transform_data_s3uri TYPE   /aws1/sgms3uri.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    "Define Amazon S3 parameters.
    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-sgm-'.
    CONSTANTS cv_transform_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/transform/transform.libsvm'.
    CONSTANTS cv_train_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/train/train.libsvm'.
    CONSTANTS cv_val_key TYPE /aws1/s3_objectkey VALUE 'sagemaker/validation/validation.libsvm'.

    "Define hyperparameters.
    CONSTANTS cv_hp_max_depth TYPE   /aws1/sgmhyperparametervalue    VALUE '3'.
    CONSTANTS cv_hp_scale_pos_weight TYPE   /aws1/sgmhyperparametervalue    VALUE '2.0'.
    CONSTANTS cv_hp_num_round TYPE   /aws1/sgmhyperparametervalue    VALUE '100'.
    CONSTANTS cv_hp_objective  TYPE  /aws1/sgmhyperparametervalue     VALUE 'binary:logistic'.
    CONSTANTS cv_hp_subsample TYPE   /aws1/sgmhyperparametervalue    VALUE '0.5'.
    CONSTANTS cv_hp_eta TYPE  /aws1/sgmhyperparametervalue    VALUE   '0.1'.
    CONSTANTS cv_hp_eval_metric TYPE   /aws1/sgmhyperparametervalue    VALUE 'auc'.

    "Define training data.
    CONSTANTS cv_trn_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_trn_data_s3datadistribution TYPE  /aws1/sgms3datadistribution        VALUE 'FullyReplicated'.
    CONSTANTS cv_trn_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_trn_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define validation data.
    CONSTANTS cv_val_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_val_data_s3datadistribution TYPE  /aws1/sgms3datadistribution      VALUE 'FullyReplicated'.
    CONSTANTS cv_val_data_compressiontype TYPE   /aws1/sgmcompressiontype    VALUE 'None'.
    CONSTANTS cv_val_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define training parameters.
    CONSTANTS cv_training_image TYPE /aws1/sgmalgorithmimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.
    CONSTANTS cv_training_input_mode TYPE /aws1/sgmtraininginputmode VALUE  'File'.
    CONSTANTS cv_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.
    CONSTANTS cv_volume_sizeingb TYPE /aws1/sgmvolumesizeingb VALUE '10'.
    CONSTANTS cv_max_runtime_in_seconds TYPE /aws1/sgmmaxruntimeinseconds VALUE '1800'.
    CONSTANTS cv_max_results TYPE /aws1/sgmmaxresults VALUE '1'.

    "Define transform data.
    CONSTANTS cv_tf_data_s3datatype TYPE  /aws1/sgms3datatype       VALUE 'S3Prefix'.
    CONSTANTS cv_tf_data_compressiontype TYPE  /aws1/sgmcompressiontype     VALUE 'None'.
    CONSTANTS cv_tf_data_contenttype TYPE   /aws1/sgmcontenttype    VALUE 'libsvm'.

    "Define transform parameters.
    CONSTANTS cv_tf_instance_count TYPE /aws1/sgmtraininginstancecount VALUE '1'.
    CONSTANTS cv_tf_instance_type TYPE /aws1/sgmtraininginstancetype VALUE 'ml.c4.2xlarge'.

    "Define role ARN.
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

    "Define job name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_training_job_name = 'code-example-trn-job-' && lv_uuid_16.
    TRANSLATE lv_training_job_name TO LOWER CASE.

    "Define model name.
    lv_model_name = 'code-example-model-' && lv_uuid_16.
    TRANSLATE lv_model_name TO LOWER CASE.

    "Define job name.
    lv_transform_job_name = 'code-example-transform-job-' && lv_uuid_16.
    TRANSLATE lv_transform_job_name TO LOWER CASE.

    "Create training data in Amazon S3.
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_trn_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_train_key.
    lv_val_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_val_key.
    lv_transform_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_transform_key.
    lv_s3_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/'.
    lv_s3_transform_output_path = 's3://' && lv_bucket_name && '/' && 'sagemaker/transform/'.
    lv_model_key = 'sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.
    lv_model_data_url = 's3://' && lv_bucket_name && '/sagemaker/' && lv_training_job_name && '/output/model.tar.gz'.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
      iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_val_key
          iv_body = av_file_content
    ).

    ao_s3->putobject(
          iv_bucket = lv_bucket_name
          iv_key = cv_transform_key
          iv_body = av_file_content
    ).

    "Create ABAP internal table for hyperparameters based on input variables.
    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_max_depth.
    INSERT VALUE #( key = 'max_depth' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eta.
    INSERT VALUE #( key = 'eta' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_eval_metric.
    INSERT VALUE #( key = 'eval_metric' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_scale_pos_weight.
    INSERT VALUE #( key = 'scale_pos_weight' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_subsample.
    INSERT VALUE #( key = 'subsample' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_objective.
    INSERT VALUE #( key = 'objective' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    CREATE OBJECT lo_hyperparameters_w EXPORTING iv_value = cv_hp_num_round.
    INSERT VALUE #( key = 'num_round' value = lo_hyperparameters_w )  INTO TABLE lt_hyperparameters.

    "Create ABAP internal table for data based on input variables.
    "Training data.
    CREATE OBJECT lo_trn_s3datasource
      EXPORTING
        iv_s3datatype             = cv_trn_data_s3datatype
        iv_s3datadistributiontype = cv_trn_data_s3datadistribution
        iv_s3uri                  = lv_trn_data_s3uri.

    CREATE OBJECT lo_trn_datasource
      EXPORTING
        io_s3datasource = lo_trn_s3datasource.

    CREATE OBJECT lo_trn_channel
      EXPORTING
        iv_channelname     = 'train'
        io_datasource      = lo_trn_datasource
        iv_compressiontype = cv_trn_data_compressiontype
        iv_contenttype     = cv_trn_data_contenttype.

    INSERT lo_trn_channel INTO TABLE lt_input_data_config.

    "Validation data.
    CREATE OBJECT lo_val_s3datasource
      EXPORTING
        iv_s3datatype             = cv_val_data_s3datatype
        iv_s3datadistributiontype = cv_val_data_s3datadistribution
        iv_s3uri                  = lv_val_data_s3uri.

    CREATE OBJECT lo_val_datasource
      EXPORTING
        io_s3datasource = lo_val_s3datasource.

    CREATE OBJECT lo_val_channel
      EXPORTING
        iv_channelname     = 'validation'
        io_datasource      = lo_val_datasource
        iv_compressiontype = cv_val_data_compressiontype
        iv_contenttype     = cv_val_data_contenttype.

    INSERT lo_val_channel INTO TABLE lt_input_data_config.

    "Create an ABAP object for algorithm specification based on input variables.
    CREATE OBJECT lo_algorithm_specification
      EXPORTING
        iv_trainingimage     = cv_training_image
        iv_traininginputmode = cv_training_input_mode.

    "Create an ABAP object for resource configuration.
    CREATE OBJECT lo_resource_config
      EXPORTING
        iv_instancecount  = cv_instance_count
        iv_instancetype   = cv_instance_type
        iv_volumesizeingb = cv_volume_sizeingb.

    "Create an ABAP object for output data configuration.
    CREATE OBJECT lo_output_data_config
      EXPORTING
        iv_s3outputpath = lv_s3_output_path.

    "Create an ABAP object for stopping condition.
    CREATE OBJECT lo_stopping_condition
      EXPORTING
        iv_maxruntimeinseconds = cv_max_runtime_in_seconds.

    "Create a training job.
    ao_sgm->createtrainingjob(
      iv_trainingjobname           = lv_training_job_name
      iv_rolearn                   = av_lrole
      it_hyperparameters           = lt_hyperparameters
      it_inputdataconfig           = lt_input_data_config
      io_algorithmspecification    = lo_algorithm_specification
      io_outputdataconfig          = lo_output_data_config
      io_resourceconfig            = lo_resource_config
      io_stoppingcondition         = lo_stopping_condition
    ).

    "Wait for training job to be completed.
    lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    WHILE lo_training_result->get_trainingjobstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_training_result = ao_sgm->describetrainingjob( iv_trainingjobname = lv_training_job_name ).
    ENDWHILE.

    "Create an ABAP object for the container image based on input variables.
    CREATE OBJECT lo_primarycontainer
      EXPORTING
        iv_image        = cv_container_image
        iv_modeldataurl = lv_model_data_url.

    "Create a new model.
    CALL METHOD ao_sgm->createmodel
      EXPORTING
        iv_modelname        = lv_model_name
        iv_executionrolearn = av_lrole
        io_primarycontainer = lo_primarycontainer.

    ao_sgm_actions->create_transform_job(
      EXPORTING
        iv_tf_model_name            = lv_model_name
        iv_tf_job_name              = lv_transform_job_name
        iv_tf_data_s3datatype       = cv_tf_data_s3datatype
        iv_tf_data_s3uri            = lv_transform_data_s3uri
        iv_tf_data_compressiontype  = cv_tf_data_compressiontype
        iv_tf_data_contenttype      = cv_tf_data_contenttype
        iv_instance_count           = cv_tf_instance_count
        iv_instance_type            = cv_tf_instance_type
        iv_s3_output_path           = lv_s3_transform_output_path
      IMPORTING
       oo_result              = lo_tf_result
    ).

    lv_found = abap_false.

    IF lo_tf_result->has_transformjobarn( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Transform Job cannot be found|
    ).

    "Transform jobs and logs cannot be deleted and are retained indefinitely.

    "Clean up.
    ao_sgm->deletemodel(
        iv_modelname = lv_model_name
    ).

    ao_s3->deleteobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_train_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_val_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = cv_transform_key
    ).

    ao_s3->deleteobject(
        iv_bucket = lv_bucket_name
        iv_key = lv_model_key
    ).

    ao_s3->deletebucket(
        iv_bucket = lv_bucket_name
    ).

  ENDMETHOD.

  METHOD list_algorithms.

    DATA lv_algorithm_name TYPE /aws1/sgmentityname.
    DATA lo_trainingspecification  TYPE REF TO /aws1/cl_sgmtrainingspec.
    DATA lo_sgmchannelspec TYPE REF TO /aws1/cl_sgmchannelspec.
    DATA lt_sgmchannelspec  TYPE  /aws1/cl_sgmchannelspec=>tt_channelspecifications.
    DATA lo_sgmtrninstancetypes_w TYPE REF TO /aws1/cl_sgmtrninstancetypes_w.
    DATA lt_supportedtrninstancetypes TYPE  /aws1/cl_sgmtrninstancetypes_w=>tt_traininginstancetypes.
    DATA lo_algorithms_result TYPE REF TO /aws1/cl_sgmlistalgsoutput.
    DATA lv_instance_type TYPE /aws1/sgmtraininginstancetype.
    DATA lo_sgmcontenttypes_w TYPE REF TO /aws1/cl_sgmcontenttypes_w.
    DATA lt_supportedcontenttypes TYPE /aws1/cl_sgmcontenttypes_w=>tt_contenttypes.
    DATA lo_sgmcompressiontypes_w TYPE REF TO /aws1/cl_sgmcompressiontypes_w.
    DATA lt_supportedcompressiontypes TYPE /aws1/cl_sgmcompressiontypes_w=>tt_compressiontypes.
    DATA lo_supportedinputmodes TYPE REF TO /aws1/cl_sgminputmodes_w.
    DATA lt_supportedinputmodes TYPE /aws1/cl_sgminputmodes_w=>tt_inputmodes.
    DATA lo_des_algorithm_result TYPE REF TO /aws1/cl_sgmdescribealgoutput.
    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lv_uuid_16 TYPE sysuuid_x16.

    CONSTANTS cv_container_image TYPE /aws1/sgmcontainerimage VALUE '123456789012.abc.ecr.us-east-1.amazonaws.com/sagemaker-xgboost:1.5-1'.

    "Define name.
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_algorithm_name = 'code-example-algorithm-' && lv_uuid_16.
    TRANSLATE lv_algorithm_name TO LOWER CASE.

    "Define training specification.
    CREATE OBJECT lo_sgmtrninstancetypes_w EXPORTING iv_value = 'ml.m5.large'.
    INSERT lo_sgmtrninstancetypes_w  INTO TABLE lt_supportedtrninstancetypes.

    CREATE OBJECT lo_sgmcontenttypes_w EXPORTING iv_value = 'S3Prefix'.
    INSERT lo_sgmcontenttypes_w  INTO TABLE lt_supportedcontenttypes.

    CREATE OBJECT lo_sgmcompressiontypes_w EXPORTING iv_value = 'None'.
    INSERT lo_sgmcompressiontypes_w  INTO TABLE lt_supportedcompressiontypes.

    CREATE OBJECT lo_supportedinputmodes EXPORTING iv_value = 'File'.
    INSERT lo_supportedinputmodes  INTO TABLE lt_supportedinputmodes .

    CREATE OBJECT lo_sgmchannelspec
      EXPORTING
        iv_name                      = 'train'
        it_supportedcontenttypes     = lt_supportedcontenttypes
        it_supportedcompressiontypes = lt_supportedcompressiontypes
        it_supportedinputmodes       = lt_supportedinputmodes
        iv_isrequired                = ' '.

    INSERT lo_sgmchannelspec INTO TABLE lt_sgmchannelspec.

    CREATE OBJECT lo_trainingspecification
      EXPORTING
        iv_trainingimage             = cv_container_image
        it_supportedtrninstancetypes = lt_supportedtrninstancetypes
        it_trainingchannels          = lt_sgmchannelspec.

    "Create algorithm.
    ao_sgm->createalgorithm(
          iv_algorithmname           = lv_algorithm_name
          io_trainingspecification   = lo_trainingspecification
      ).

    "Testing list algorithm.
    ao_sgm_actions->list_algorithms(
     EXPORTING
      iv_name_contains       = lv_algorithm_name
     IMPORTING
      oo_result             = lo_algorithms_result
      ).

    "Validation.
    lv_found = abap_false.

    LOOP AT lo_algorithms_result->get_algorithmsummarylist( ) INTO DATA(lo_algorithms).
      IF lo_algorithms->get_algorithmname( ) = lv_algorithm_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Algorithm cannot be found|
    ).

    "Waiter.
    lo_des_algorithm_result = ao_sgm->describealgorithm( iv_algorithmname = lv_algorithm_name ).
    WHILE lo_des_algorithm_result->get_algorithmstatus( ) <> 'Completed'.
      IF sy-index = 30.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_des_algorithm_result = ao_sgm->describealgorithm( iv_algorithmname = lv_algorithm_name ).
    ENDWHILE.

    "Clean up.
    ao_sgm->deletealgorithm(
     EXPORTING
      iv_algorithmname       = lv_algorithm_name
      ).

  ENDMETHOD.

ENDCLASS.
