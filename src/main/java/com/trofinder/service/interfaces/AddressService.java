package com.trofinder.service.interfaces;

import java.util.List;

import com.trofinder.dto.response.address.DistrictResponse;
import com.trofinder.dto.response.address.ProvinceResponse;
import com.trofinder.dto.response.address.WardResponse;

public interface AddressService {

    List<ProvinceResponse> getAllProvinces();

    List<DistrictResponse> getDistricts(Long provinceId);

    List<WardResponse> getWards(Long districtId);

}