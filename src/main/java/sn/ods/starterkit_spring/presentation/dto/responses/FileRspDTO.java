package sn.ods.starterkit_spring.presentation.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ods.starterkit_spring.domain.model.file.File;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRspDTO {

    private Long id;
    private String originalName;
    private String generatedName;
    private String fileCode;
    private String downloadUrl;
    private String fileType;
    private long fileSize;


    public static File toEntity(FileRspDTO dto){
     return    File.builder()
                .id(dto.id)
                .originalName(dto.originalName)
                .fileCode(dto.fileCode)
                .downloadUrl(dto.downloadUrl)
                .fileType(dto.fileType)
                .generatedName(dto.generatedName)
                .fileSize(dto.fileSize )
                .build();
    }


}
