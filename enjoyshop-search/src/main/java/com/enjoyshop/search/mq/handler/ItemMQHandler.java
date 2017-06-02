package com.enjoyshop.search.mq.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;

import com.enjoyshop.search.pojo.Item;
import com.enjoyshop.search.service.ItemService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemMQHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private HttpSolrServer httpSolrServer;

    @Autowired
    private ItemService itemService;

    public void execute(String msg) {
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            Long itemId = jsonNode.get("itemId").asLong();
            String type = jsonNode.get("type").asText();
            if (StringUtils.equals(type, "insert") || StringUtils.equals(type, "update")) {
                // 从后台系统中查询商品数据
                Item item = this.itemService.queryItemById(itemId);
                if (item != null) {
                    this.httpSolrServer.addBean(item);
                    this.httpSolrServer.commit();
                }
            } else if (StringUtils.equals(type, "delete")) {
                // 删除索引数据
                this.httpSolrServer.deleteById(String.valueOf(itemId));
                this.httpSolrServer.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
