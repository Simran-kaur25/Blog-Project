package com.blog.demo.core.models;

import java.util.List;
import java.util.Map;

public interface FooterModel {
    String getCopyrightText();
    List<Map<String, String>> getFooterLinks();
}
