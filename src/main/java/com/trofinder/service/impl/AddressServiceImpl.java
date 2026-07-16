package com.trofinder.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trofinder.dto.response.address.DistrictResponse;
import com.trofinder.dto.response.address.ProvinceResponse;
import com.trofinder.dto.response.address.WardResponse;
import com.trofinder.mapper.AddressMapper;
import com.trofinder.repository.DistrictRepository;
import com.trofinder.repository.ProvinceRepository;
import com.trofinder.repository.WardRepository;
import com.trofinder.service.interfaces.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final ProvinceRepository provinceRepository;

    private final DistrictRepository districtRepository;

    private final WardRepository wardRepository;

    private final AddressMapper addressMapper;

    @Override
    public List<ProvinceResponse> getAllProvinces() {

        return addressMapper.toProvinceResponses(
                provinceRepository.findAllByOrderByNameAsc());

    }

    @Override
    public List<DistrictResponse> getDistricts(Long provinceId) {

        return addressMapper.toDistrictResponses(
                districtRepository.findByProvinceIdOrderByNameAsc(provinceId));

    }

    @Override
    public List<WardResponse> getWards(Long districtId) {

        return addressMapper.toWardResponses(
                wardRepository.findByDistrictIdOrderByNameAsc(districtId));

    }

}