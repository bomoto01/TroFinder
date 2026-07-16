package com.trofinder.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.trofinder.dto.response.address.DistrictResponse;
import com.trofinder.dto.response.address.ProvinceResponse;
import com.trofinder.dto.response.address.WardResponse;
import com.trofinder.entity.District;
import com.trofinder.entity.Province;
import com.trofinder.entity.Ward;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    ProvinceResponse toProvinceResponse(Province province);

    List<ProvinceResponse> toProvinceResponses(List<Province> provinces);

    DistrictResponse toDistrictResponse(District district);

    List<DistrictResponse> toDistrictResponses(List<District> districts);

    WardResponse toWardResponse(Ward ward);

    List<WardResponse> toWardResponses(List<Ward> wards);

}