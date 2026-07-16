package com.trofinder.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.service.importdata.AddressImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AddressImportController {

    private final AddressImportService addressImportService;

    @PostMapping("/api/admin/address/import")
    public ResponseEntity<String> importAddress() throws Exception {

        addressImportService.importAddress();

        return ResponseEntity.ok("Import thành công.");

    }

}