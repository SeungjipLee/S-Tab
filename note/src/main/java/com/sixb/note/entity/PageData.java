package com.sixb.note.entity;

import com.sixb.note.dto.pageData.FigureDto;
import com.sixb.note.dto.pageData.ImageDto;
import com.sixb.note.dto.pageData.PathDto;
import com.sixb.note.dto.pageData.TextBoxDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "PageData")
public class PageData {
    @Id
    private String id;
    private List<PathDto> paths;
    private List<FigureDto> figures;
    private List<TextBoxDto> textBoxes;
    private List<ImageDto> images;
}
