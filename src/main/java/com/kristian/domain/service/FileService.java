package com.kristian.domain.service;

import com.amazonaws.services.s3.model.S3Object;
import com.kristian.controller.dto.ParamsIn;
import com.kristian.controller.dto.output.ResponseLocalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    private final AWSS3 s3;

    @Autowired
    public FileService(AWSS3 s3) {
        this.s3 = s3;
    }

    public List<ResponseLocalization> getResponse(List<String> filenames, ParamsIn paramsIn, int quantityFiles) {
        List<ResponseLocalization> response = new ArrayList<>();

        int i = 1;
        for (String filename : filenames) {
            LOGGER.info(i + " from " + quantityFiles + ": Filename: " + filename);

            InputStream inputStream = download(paramsIn, filename);
            String wordLookingFor = paramsIn.getWordLookingFor();
            response = getCurrentList(filename, inputStream, wordLookingFor);
            i++;
            if (!response.isEmpty()) {
                LOGGER.info("Figured out: Filename: " + filename);
                break;
            }
        }

        return response;
    }

    private List<ResponseLocalization> getCurrentList(String filename, InputStream inputStream, String wordLookingFor) {
        List<ResponseLocalization> currentList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            int line = 1;
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                if (readLine.contains(wordLookingFor))
                    currentList.add(getResponseLocalization(filename, line));
                line++;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return currentList;
    }

    private ResponseLocalization getResponseLocalization(String filename, int line) {
        ResponseLocalization localization = new ResponseLocalization();
        localization.setLine(line);
        localization.setFilename(filename);
        return localization;
    }

    private InputStream download(ParamsIn paramsIn, String filename) {
        S3Object download = s3.download(paramsIn, paramsIn.getBucket().getBucket(), filename);
        return download.getObjectContent();
    }

}
