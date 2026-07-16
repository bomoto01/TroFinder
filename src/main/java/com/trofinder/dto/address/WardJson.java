package com.trofinder.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WardJson {

    private int code;

    private String name;

    private String codename;

    @JsonProperty("division_type")
    private String divisionType;

    @JsonProperty("district_code")
    private int districtCode;

}