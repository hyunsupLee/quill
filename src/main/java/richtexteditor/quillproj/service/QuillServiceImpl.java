package richtexteditor.quillproj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import richtexteditor.quillproj.model.Quill;
import richtexteditor.quillproj.repository.QuillRepository;

import java.util.List;

@Service
public class QuillServiceImpl implements QuillService {

    @Autowired
    private QuillRepository quillRepository;

    @Override
    public List<Quill> getAllPosts() {
        return quillRepository.findAll();
    }

    @Override
    public Quill getPostById(Long id) {
        return quillRepository.findById(id).orElse(null);
    }

    @Override
    public Quill savePost(Quill post) {
        return quillRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        quillRepository.deleteById(id);
    }
}
