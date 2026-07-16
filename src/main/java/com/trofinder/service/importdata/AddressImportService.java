package com.trofinder.service.importdata;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trofinder.dto.address.ProvinceJson;
import com.trofinder.entity.Province;
import com.trofinder.entity.Ward;
import com.trofinder.repository.DistrictRepository;
import com.trofinder.repository.ProvinceRepository;
import com.trofinder.repository.WardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressImportService {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ObjectMapper objectMapper;

    public void importAddress() {

        try {

            InputStream inputStream =
                    new ClassPathResource("data/vietnam-address.json")
                            .getInputStream();

            List<ProvinceJson> provinces =
                    objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<ProvinceJson>>() {
                            });

            log.info("Import {} provinces", provinces.size());

            for (ProvinceJson provinceJson : provinces) {

                Province province = saveProvince(provinceJson);

                importDistricts(
                        province,
                        provinceJson
                );
            }

            log.info("Import address success.");

        } catch (Exception e) {

            log.error("Import address failed", e);

            throw new RuntimeException(e);

        }

    }

    private Province saveProvince(ProvinceJson provinceJson) {

        return provinceRepository
                .findByCode(String.valueOf(provinceJson.getCode()))
                .orElseGet(() -> {

                    Province province = Province.builder()
                            .name(provinceJson.getName())
                            .code(String.valueOf(provinceJson.getCode()))
                            .build();

                    return provinceRepository.save(province);

                });

    }
        private void importDistricts(
            Province province,
            ProvinceJson provinceJson
    ) {

        if (provinceJson.getDistricts() == null
                || provinceJson.getDistricts().isEmpty()) {
            return;
        }

        for (var districtJson : provinceJson.getDistricts()) {

            var district = districtRepository
                    .findByCode(String.valueOf(districtJson.getCode()))
                    .orElseGet(() -> {

                        var newDistrict = com.trofinder.entity.District.builder()
                                .province(province)
                                .name(districtJson.getName())
                                .code(String.valueOf(districtJson.getCode()))
                                .build();

                        return districtRepository.save(newDistrict);

                    });

            importWards(district, districtJson);

        }

    }

    private void importWards(
            com.trofinder.entity.District district,
            com.trofinder.dto.address.DistrictJson districtJson
    ) {

        if (districtJson.getWards() == null
                || districtJson.getWards().isEmpty()) {
            return;
        }

        for (var wardJson : districtJson.getWards()) {

            wardRepository
                    .findByCode(String.valueOf(wardJson.getCode()))
                    .orElseGet(() -> {

                        var ward = Ward.builder()
                                .district(district)
                                .name(wardJson.getName())
                                .code(String.valueOf(wardJson.getCode()))
                                .build();

                        return wardRepository.save(ward);

                    });

        }

    }

}
