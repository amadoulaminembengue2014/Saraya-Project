package com.vlm.cityhall.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vlm.cityhall.Service.CertificateOfDeathService;

@RestController
@RequestMapping("/CertificateOfDeath")
public class CertificateOfDeathResource {
    @Autowired
    private CertificateOfDeathService certificateOfDeathService;
}