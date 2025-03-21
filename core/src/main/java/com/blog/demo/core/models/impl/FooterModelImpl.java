package com.blog.demo.core.models.impl;

import com.blog.demo.core.models.FooterModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(
        adaptables = Resource.class,
        adapters = FooterModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModelImpl implements FooterModel {

    @ValueMapValue
    private String backToTopLink;

    @ValueMapValue
    private String copyrightText;

    @SlingObject
    private Resource componentResource;


    @Override
    public String getCopyrightText() {
        return copyrightText;
    }

    @Override
    public List<Map<String, String>> getFooterLinks() {
        List<Map<String, String>> footerLinksList = new ArrayList<>();
        Resource linksResource = componentResource.getChild("actions");

        if (linksResource != null) {
            for (Resource item : linksResource.getChildren()) {
                Map<String, String> linkMap = new HashMap<>();
                ValueMap valueMap = item.getValueMap();

                String linkText = valueMap.get("linkText", String.class);
                String linkURL = valueMap.get("linkURL", String.class);

                // Append .html if not already present
                if (linkURL != null && !linkURL.endsWith(".html")) {
                    linkURL += ".html";
                }

                linkMap.put("text", linkText);
                linkMap.put("url", linkURL);
                footerLinksList.add(linkMap);
            }
        }
        return footerLinksList;
    }

}
