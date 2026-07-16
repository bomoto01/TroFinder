package com.trofinder.dto.address;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProvinceJson {

    private int code;

    private String name;

    private String codename;

    @JsonProperty("division_type")
    private String divisionType;

    @JsonProperty("phone_code")
    private int phoneCode;

    private List<DistrictJson> districts;

}