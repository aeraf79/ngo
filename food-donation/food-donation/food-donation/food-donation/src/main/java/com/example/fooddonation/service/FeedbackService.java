package com.example.fooddonation.service;

import com.example.fooddonation.entity.FeedbackDTO;
import com.example.fooddonation.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public FeedbackDTO saveFeedback(FeedbackDTO feedback) {
        return feedbackRepository.save(feedback);
    }
}
