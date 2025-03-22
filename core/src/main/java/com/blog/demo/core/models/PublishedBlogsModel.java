package com.blog.demo.core.models;

import com.blog.demo.core.models.impl.PublishedBlogsModelImpl;

import java.util.List;

public interface PublishedBlogsModel {

    // Retrieves a list of blogs (Each blog contains data - title, description, image, link, and date)
    List<PublishedBlogsModelImpl.BlogData> getBlogs();
}
