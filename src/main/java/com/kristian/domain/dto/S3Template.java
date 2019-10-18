package com.kristian.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class S3Template {

    private String file;
    private Date dataCreated;

}
