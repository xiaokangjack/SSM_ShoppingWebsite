package com.enjoyshop.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertieService {

    @Value("${ENJOYSHOP_SSO_URL}")
    public String ENJOYSHOP_SSO_URL;

}
