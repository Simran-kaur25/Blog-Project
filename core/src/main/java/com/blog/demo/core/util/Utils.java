package com.blog.demo.core.util;

import com.blog.demo.core.models.impl.PublishedBlogsModelImpl;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(PublishedBlogsModelImpl.class);
    private static final String DEFAULT_IMAGE = "/content/dam/default.jpg";

    public static String getImagePath(SlingHttpServletRequest request, Page page) {
        String ImagePath = page.getPath() + "/jcr:content/cq:featuredimage/file/jcr:content";
        ResourceResolver resolver = request.getResourceResolver();
        Resource ImageNode = resolver.getResource(ImagePath);
        String ImageLink = "";
        try {
            ValueMap properties = ImageNode.getValueMap();
            if (properties.containsKey("jcr:data")) { // Check if binary data exists
                ImageLink = resolver.map(request, ImagePath) + "/jcr:data";
            }
        } catch (Exception e) {
            return "{\"message\" : \" Could not get Banner Image\"";
        }
        return ImageLink;
    }
}
