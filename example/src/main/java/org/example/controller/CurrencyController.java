package org.example.controller;

import org.example.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping("/input/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        System.out.println("receive file=" + filename);
        currencyService.handUploadFile(file.getInputStream(), filename);
        return ResponseEntity.ok("OK!");
    }

}
