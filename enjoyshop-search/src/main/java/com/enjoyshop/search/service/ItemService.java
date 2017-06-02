package com.enjoyshop.search.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.service.ApiService;
import com.enjoyshop.search.pojo.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ItemService {

    @Autowired
    private ApiService apiService;

    @Value("${ENJOYSHOP_MANAGE_URL}")
    private String ENJOYSHOP_MANAGE_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Item queryItemById(Long itemId) {
        String url = ENJOYSHOP_MANAGE_URL + "/rest/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isEmpty(jsonData)) {
                return null;
            }
            return MAPPER.readValue(jsonData, Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
