package com.trofinder.dto.address;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DistrictJson {

    private int code;

    private String name;

    private String codename;

    @JsonProperty("division_type")
    private String divisionType;

    @JsonProperty("province_code")
    private int provinceCode;

    private List<WardJson> wards;

}