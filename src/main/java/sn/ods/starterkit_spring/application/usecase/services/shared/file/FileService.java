package sn.ods.starterkit_spring.application.usecase.services.implement.file;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sn.ods.starterkit_spring.domain.enums.FileCode;
import sn.ods.starterkit_spring.application.usecase.services.interfaces.file.IFile;
import sn.ods.starterkit_spring.infrastructure.config.exceptions.APIException;
import sn.ods.starterkit_spring.presentation.dto.responses.APIMessage;
import sn.ods.starterkit_spring.presentation.dto.responses.FileRspDTO;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Abdou Karim CISSOKHO
 * @created 08/11/2023-16:22
 * @project gestion_courriers
 */


@Service
@RequiredArgsConstructor
public class FileService implements IFile {

    @Value("${app.file.regex-splitter}")
    private String regexSplitter;

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() throws APIException {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new APIException(APIMessage.WS_AMAZON_ERROR, "Could not create upload folder!");
        }
    }

    @Override
    public FileRspDTO storeFile(MultipartFile file, String directory, boolean checkRegexSplitter) throws APIException {
        FileRspDTO result;
        String key = "";
        String fileName = file.getOriginalFilename();

        if (file.isEmpty() || fileName == null)
            throw new APIException(APIMessage.REQUIRED_FILE, "Attached document");
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }

            if (checkRegexSplitter) {
                List<String> stringList = Arrays.stream(fileName.split(regexSplitter)).collect(Collectors.toList());

                if (stringList.size() != 2)
                    throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, String.format("The format (%s) of the attachments files is incorrect!", fileName));
                else {
                    key = stringList.get(0);
                    fileName = StringUtils.cleanPath(stringList.get(1));

                    if (!FileCode.findByName(key))
                        throw new APIException(APIMessage.NOT_FOUND, "FileCode " + key);
                }
            }


            if (fileName.contains("..")) {
                throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, "Filename contains invalid path sequence " + fileName);
            }

          /*  if (file.getBytes().length > (1024 * 1024)) {
                throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, "File size exceeds maximum limit");
            }


           */
            String extension = FilenameUtils.getExtension(fileName);
            InputStream is = file.getInputStream();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            String generatedName = UUID.randomUUID() + "_" + formatter.format(date) + "." + extension;



            result = FileRspDTO.builder()
                    .originalName(fileName)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileCode( RandomStringUtils.randomAlphanumeric(8))
                    .build();

            if (org.apache.commons.lang3.StringUtils.isNotBlank(directory)) {
                Path subDirPath = root.resolve(directory);
                Files.createDirectories(subDirPath);

                Path targetLocation = subDirPath.resolve(generatedName);
                Files.copy(is, targetLocation, StandardCopyOption.REPLACE_EXISTING);


                result.setGeneratedName(directory + "/" + generatedName);
            } else {
                Path targetLocation = root.resolve(generatedName);
                Files.copy(is, targetLocation, StandardCopyOption.REPLACE_EXISTING);

                result.setGeneratedName(generatedName);
            }


            if (checkRegexSplitter) result.setFileCode(FileCode.valueOf(key.toUpperCase()).name());
            return result;
        } catch (IOException e) {

            String msg = String.format("Failed to store file %s", file.getName());
            throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, msg);
        }
    }

    @Override
    public List<FileRspDTO> storeMultipleFiles(MultipartFile[] files, String directory, boolean checkRegexSplitter) throws APIException {
        List<FileRspDTO> objects;

        if (Objects.isNull(files) || files.length == 0)
            throw new APIException(APIMessage.REQUIRED_FILE, "Attached documents");
        objects = Arrays.stream(files).map(file -> {
            try {
                return storeFile(file, directory, checkRegexSplitter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        return objects;
    }

/*    private Path getUploadDirLocation() {
        return Paths.get(uploadFileProperties.getUploadDir()).toAbsolutePath().normalize();
    }*/

    @Override
    public Resource load(String filename, HttpServletRequest request) throws APIException {
        try {
            Path file = Paths.get(uploadPath)
                    .resolve(filename).normalize();

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, "Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, "Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(uploadPath)
                .toFile());
    }

    @Override
    public List<Path> loadAll() throws APIException {
        try {
            Path root = Paths.get(uploadPath);
            if (Files.exists(root)) {
                return Files.walk(root, 1)
                        .filter(path -> !path.equals(root))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new APIException(APIMessage.FILE_FORMAT_INCORRECT, "Could not list the files!");
        }
    }
}
