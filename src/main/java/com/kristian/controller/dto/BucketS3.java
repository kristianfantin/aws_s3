package com.kristian.controller.dto;

import lombok.Setter;

@Setter
public class BucketS3 {

    private String bucket;
    private String folder;

    public String getBucket() {
        return bucket;
    }

    public String getFolder() {
        return folder;
    }

}
