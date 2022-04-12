package uz.greenstar.jolybell.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class CategoryDTO {
    private String id;
    private boolean active;
    private String name;
    private String url;
    private String createDate;
    private String username;
}
