package com.trofinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.address.DistrictResponse;
import com.trofinder.dto.response.address.ProvinceResponse;
import com.trofinder.dto.response.address.WardResponse;
import com.trofinder.service.interfaces.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/api/provinces")
    public List<ProvinceResponse> getAllProvinces() {

        return addressService.getAllProvinces();

    }

    @GetMapping("/api/provinces/{provinceId}/districts")
    public List<DistrictResponse> getDistricts(
            @PathVariable Long provinceId) {

        return addressService.getDistricts(provinceId);

    }

    @GetMapping("/api/districts/{districtId}/wards")
    public List<WardResponse> getWards(
            @PathVariable Long districtId) {

        return addressService.getWards(districtId);

    }

}