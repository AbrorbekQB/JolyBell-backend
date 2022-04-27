package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.handbook.DistrictDTO;
import uz.greenstar.jolybell.dto.handbook.ProvinceDTO;
import uz.greenstar.jolybell.entity.DistrictEntity;
import uz.greenstar.jolybell.entity.DistrictEntity_;
import uz.greenstar.jolybell.entity.ProvinceEntity;
import uz.greenstar.jolybell.repository.DistrictRepository;
import uz.greenstar.jolybell.repository.ProvinceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandbookService {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;


    public List<ProvinceDTO> provinceList() {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findAll();
        return provinceEntityList.stream().map(provinceEntity -> {
            ProvinceDTO provinceDTO = new ProvinceDTO();
            BeanUtils.copyProperties(provinceEntity, provinceDTO);
            return provinceDTO;
        }).collect(Collectors.toList());
    }

    public List<DistrictDTO> districtList(String provinceId) {
        List<DistrictEntity> districtEntityList = districtRepository.findAllByProvinceId(provinceId);
        return districtEntityList.stream().map(districtEntity -> {
            DistrictDTO districtDTO = new DistrictDTO();
            BeanUtils.copyProperties(districtEntity, districtDTO);
            return districtDTO;
        }).collect(Collectors.toList());
    }
}
