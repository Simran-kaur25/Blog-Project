package com.blog.demo.core.models;

import java.util.List;
import java.util.Map;

public interface HeaderModel {
    String getLogoPath();
    String getWebsiteName();
    List<Map<String, String>> getMenuItems();
}