package com.blog.demo.core.models;

import com.blog.demo.core.models.impl.PublishedBlogsModelImpl;

import java.util.List;

public interface PublishedBlogsModel {
    List<PublishedBlogsModelImpl.BlogData> getBlogs();
}
