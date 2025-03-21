package com.blog.demo.core.models.impl;

import com.blog.demo.core.models.HeaderModel;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Model(
        adaptables = SlingHttpServletRequest.class, adapters = HeaderModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModelImpl implements HeaderModel {

    @SlingObject
    ResourceResolver resolver;

    @ValueMapValue
    private String logoPath;

    @ValueMapValue
    @Default(values = "To The New")
    private String websiteName;

    @SlingObject
    private Resource componentResource;

    @Override
    public String getLogoPath() {
        return logoPath;
    }

    @Override
    public String getWebsiteName() {
        return websiteName;
    }

    @Override
    public List<Map<String, String>> getMenuItems() {
        List<Map<String, String>> menuItemsList = new ArrayList<>();
        Resource menuItems = componentResource.getChild("actions");

        if (menuItems != null) {
            for (Resource item : menuItems.getChildren()) {
                ValueMap vm = item.getValueMap();
                String linkPath = vm.get("link", String.class);

                // Fetch the target resource where 'hideInNav' is stored
                if (linkPath != null) {
                    PageManager pageManager = resolver.adaptTo(PageManager.class);
                    Page blogPage = pageManager.getPage(linkPath);
//                    Resource linkedResource = componentResource.getResourceResolver().getResource(linkPath);
                    String hideInNav =  blogPage.getProperties().get("hideInNav","Default");
                        Map<String, String> menuItemMap = new HashMap<>();
                        menuItemMap.put("Title", vm.get("title", ""));
                        menuItemMap.put("Link", linkPath+".html");
                        menuItemMap.put("hideProp", hideInNav);
                        menuItemsList.add(menuItemMap);
                }
            }
        }
        return menuItemsList;
    }

}