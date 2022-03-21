package uz.greenstar.jolybell.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DescriptionItem {
    private String id = UUID.randomUUID().toString();
    private String text;
}
