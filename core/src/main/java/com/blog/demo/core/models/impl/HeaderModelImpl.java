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


    // Injecting resource resolver for accessing content repository
    @SlingObject
    ResourceResolver resolver;

    // Injecting logoPath property from the current resource
    @ValueMapValue
    private String logoPath;

    // Injecting the websiteName property
    @ValueMapValue
    @Default(values = "To The New")
    private String websiteName;

    // Injecting the current component resource
    @SlingObject
    private Resource componentResource;

    // Getter method to retrieve the logoPath
    @Override
    public String getLogoPath() {
        return logoPath;
    }

    // Getter method to retrieve the website name
    @Override
    public String getWebsiteName() {
        return websiteName;
    }

    @Override
    public List<Map<String, String>> getMenuItems() {
        // List to hold the menu items
        List<Map<String, String>> menuItemsList = new ArrayList<>();

        // Retrieve the 'actions' child resource under the current component
        Resource menuItems = componentResource.getChild("actions");

        if (menuItems != null) {
            for (Resource item : menuItems.getChildren()) {
                // Get the properties of the current child node
                ValueMap vm = item.getValueMap();
                String linkPath = vm.get("link", String.class);

                // Fetch the target resource where 'hideInNav' is stored
                if (linkPath != null) {
                    // Get the PageManager to fetch page properties
                    PageManager pageManager = resolver.adaptTo(PageManager.class);
                    Page blogPage = pageManager.getPage(linkPath);
                    // Retrieve the "hideInNav" property of the page
                    String hideInNav =  blogPage.getProperties().get("hideInNav","Default");

                    // Create a map to hold the properties of the current menu item
                        Map<String, String> menuItemMap = new HashMap<>();

                        menuItemMap.put("Title", vm.get("title", ""));
                        menuItemMap.put("Link", linkPath+".html");
                        menuItemMap.put("hideProp", hideInNav);

                    // Add the menu item map to the list of menu items
                        menuItemsList.add(menuItemMap);
                }
            }
        }
        return menuItemsList;
    }

}