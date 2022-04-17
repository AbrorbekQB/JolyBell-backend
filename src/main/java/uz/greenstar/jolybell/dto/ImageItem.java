package uz.greenstar.jolybell.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageItem {
    private String id = UUID.randomUUID().toString();
    private String url;
}
