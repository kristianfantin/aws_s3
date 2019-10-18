package com.kristian.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParamsIn {

    private S3KeyDTO s3KeyDTO;
    private BucketS3 bucket;
    private String wordLookingFor;
    private DateToStartFind dateToStartFind;

}
