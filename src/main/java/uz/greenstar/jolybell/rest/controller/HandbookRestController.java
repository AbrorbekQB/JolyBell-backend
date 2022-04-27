package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.greenstar.jolybell.dto.handbook.DistrictDTO;
import uz.greenstar.jolybell.dto.handbook.ProvinceDTO;
import uz.greenstar.jolybell.service.HandbookService;

import java.util.List;

@RestController
@RequestMapping(path = "/handbook")
@RequiredArgsConstructor
public class HandbookRestController {
    private final HandbookService handbookService;

    @GetMapping("/province")
    public List<ProvinceDTO> provinceList() {
        return handbookService.provinceList();
    }

    @GetMapping("/district/{provinceId}")
    public List<DistrictDTO> districtList(@PathVariable("provinceId") String provinceId) {
        return handbookService.districtList(provinceId);
    }
}
