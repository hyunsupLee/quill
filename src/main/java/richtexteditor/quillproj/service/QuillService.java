package richtexteditor.quillproj.service;


import richtexteditor.quillproj.model.Quill;

import java.util.List;

public interface QuillService {
    List<Quill> getAllPosts();
    Quill getPostById(Long id);
    Quill savePost(Quill post);
    void deletePost(Long id);
}
