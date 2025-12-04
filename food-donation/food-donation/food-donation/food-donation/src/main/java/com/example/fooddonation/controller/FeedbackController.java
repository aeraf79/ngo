package com.example.fooddonation.controller;

import com.example.fooddonation.entity.FeedbackDTO;
import com.example.fooddonation.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackDTO feedback) {
        FeedbackDTO saved = feedbackService.saveFeedback(feedback);
        return ResponseEntity.ok(saved);
    }
}
