package com.kristian.controller;

import com.kristian.controller.dto.BucketS3;
import com.kristian.controller.dto.DateToStartFind;
import com.kristian.controller.dto.ParamsIn;
import com.kristian.controller.dto.output.ResponseLocalization;
import com.kristian.domain.service.AWSS3;
import com.kristian.domain.service.FileService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Api(tags = { "interfaceController" })
@RestController
@RequestMapping("/api")
public class InterfaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceController.class);
    private BucketS3 bucket;

    private final AWSS3 s3;
    private final FileService fileService;

    @Autowired
    public InterfaceController(AWSS3 s3, FileService fileService) {
        this.s3 = s3;
        this.fileService = fileService;
    }

    @PostMapping
    public List<ResponseLocalization> postOrder(HttpServletRequest request, @RequestBody ParamsIn paramsIn) {
        bucket = paramsIn.getBucket();

        List<String> fileNames = getFileNames(paramsIn);
        int quantityFiles = fileNames.size();
        LOGGER.info(String.format("Files Quantity: %d", quantityFiles));

        return fileService.getResponse(fileNames, paramsIn, quantityFiles);
    }

    private List<String> getFileNames(ParamsIn paramsIn) {
        DateToStartFind date = paramsIn.getDateToStartFind();
        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
        return s3.getFileNames(paramsIn, bucket, localDate);
    }

}
