package uk.gov.digital.ho.egar.workflow.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;
import uk.gov.digital.ho.egar.workflow.api.exceptions.FileNotFoundWorkflowException;
import uk.gov.digital.ho.egar.workflow.api.exceptions.WorkflowException;
import uk.gov.digital.ho.egar.workflow.client.FileClient;
import uk.gov.digital.ho.egar.workflow.client.FileInfoClient;
import uk.gov.digital.ho.egar.workflow.client.GarClient;
import uk.gov.digital.ho.egar.workflow.model.rest.FileDetails;
import uk.gov.digital.ho.egar.workflow.model.rest.FileInformation;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileIdWithGarResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FileWithIdResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.FilesListGarResponse;
import uk.gov.digital.ho.egar.workflow.model.rest.response.GarSkeleton;
import uk.gov.digital.ho.egar.workflow.service.FileService;
import uk.gov.digital.ho.egar.workflow.service.behaviour.FileChecker;
import uk.gov.digital.ho.egar.workflow.service.behaviour.GarChecker;

@Service
public class FileBusinessLogic implements FileService {

    @Autowired
    private GarClient garClient;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private FileInfoClient fileInfoClient;

    @Autowired
    private GarChecker garChecker;

    @Autowired
    private FileChecker fileChecker;


    @Autowired
    private ConversionService conversionService;


    @Override
    public UUID uploadFileDetails(AuthValues authValues,
                                  UUID garUuid,
                                  FileDetails fileDetails) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        garChecker.checkGarExists(gar, garUuid);
        garChecker.checkGarIsAmendable(authValues, gar);

        List<UUID> fileUuids = gar.getFileIds();

        FileInformation fileInformation = fileInfoClient.retrieveFileInformation(authValues, fileDetails);
        long fileSize = fileInformation.getFileSize();

        fileChecker.checkNumberOfFiles(fileUuids);
        fileChecker.checkIndividualFileSize(fileSize);
        fileChecker.checkMaxSizeOfTotalFiles(authValues, fileUuids, fileSize);

        FileWithIdResponse fileWithId = fileClient.uploadFileInformation(authValues, fileInformation);

        fileUuids.add(fileWithId.getFileUuid());

        garClient.updateGar(authValues, gar.getGarUuid(), gar);

        return fileWithId.getFileUuid();
    }


    @Override
    public FilesListGarResponse getAllFiles(AuthValues authValues,
                                            UUID garUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        garChecker.checkGarExists(gar, garUuid);

        FilesListGarResponse response = conversionService.convert(gar, FilesListGarResponse.class);

        return response;
    }

    @Override
    public FileIdWithGarResponse getFile(AuthValues authValues,
                                         UUID garUuid,
                                         UUID fileUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        garChecker.checkGarExists(gar, garUuid);

        List<UUID> files = gar.getFileIds();

        if (files != null && files.contains(fileUuid)) {
            FileWithIdResponse fileWithID = fileClient.retrieveFileDetails(authValues, fileUuid);

            FileIdWithGarResponse response = new FileIdWithGarResponse();
            response.setFile(fileWithID);
            response.setGarUuid(garUuid);
            response.setUserUuid(gar.getUserUuid());

            return response;
        } else throw new FileNotFoundWorkflowException(garUuid, fileUuid);

    }

    @Override
    public void deleteFile(AuthValues authValues,
                           UUID garUuid,
                           UUID fileUuid) throws WorkflowException {

        GarSkeleton gar = garClient.getGar(authValues, garUuid);

        garChecker.checkGarExists(gar, garUuid);
        garChecker.checkGarIsAmendable(authValues, gar);

        List<UUID> files = gar.getFileIds();
        if (files != null && files.contains(fileUuid)) {
            files.remove(fileUuid);
            garClient.updateGar(authValues, gar.getGarUuid(), gar);

        } else {
            throw new FileNotFoundWorkflowException(garUuid, fileUuid);
        }

    }

}
