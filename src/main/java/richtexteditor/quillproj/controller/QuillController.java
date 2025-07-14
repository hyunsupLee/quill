package richtexteditor.quillproj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import richtexteditor.quillproj.model.Quill;
import richtexteditor.quillproj.service.QuillService;
import richtexteditor.quillproj.service.S3Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class QuillController {
	final String HTML_FILE_PATH = "src/main/resources/static/html/";
	
    @Autowired
    private QuillService quillService;
    @Autowired
    private S3Service s3Service;

    @GetMapping({"","/","/create"})
    public String index(Model model) {
        List<Quill> posts = quillService.getAllPosts();
        model.addAttribute("posts", posts);
        return "index";
    }


    @PostMapping("/save")
    public String savePost(@RequestParam(required = false) Long id,
                           @RequestParam String title,
                           @RequestParam String content) throws IOException {

        Quill quill;
        if (id != null && id > 0) {
            quill = quillService.getPostById(id);
            if (quill == null) {
                return "redirect:/edit/" + id;   // 동적 경로
            }
        } else {
            quill = new Quill();
            String fileName = UUID.randomUUID() + ".html";
            quill.setHtmlFileName(fileName);
        }

        String filePath = HTML_FILE_PATH + quill.getHtmlFileName();
        Path path = Paths.get(filePath);

        // 파일 저장 or 업데이트
        Files.write(path, content.getBytes());

        // DB 저장
        quill.setTitle(title);
        quillService.savePost(quill);

        // S3 업로드
        String s3Url = s3Service.upload(path);   // 필요하면 반환 URL을 quill.setUrl(s3Url)

        return "redirect:/create";
    }


    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable Long id, Model model) throws IOException {
        Quill post = quillService.getPostById(id);
        if (post == null) {
         
            return "redirect:/edit/{id}";
        }

        String content = new String(Files.readAllBytes(Paths.get(HTML_FILE_PATH + post.getHtmlFileName())));
        model.addAttribute("post", post);
        model.addAttribute("content", content);
        return "edit";
    }
    
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        Quill post = quillService.getPostById(id);
        if (post != null) {
            try {
                Files.deleteIfExists(Paths.get(HTML_FILE_PATH + post.getHtmlFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            quillService.deletePost(id);
        }
        return "redirect:/create";
    }
}
