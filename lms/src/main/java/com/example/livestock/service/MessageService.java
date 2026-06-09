package com.example.livestock.service;

import com.example.livestock.dto.MessageReplyRequest;
import com.example.livestock.dto.MessageRequest;
import com.example.livestock.dto.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    MessageResponse sendToAdmin(MessageRequest request);
    MessageResponse reply(MessageReplyRequest request);
    Page<MessageResponse> findAll(Pageable pageable);
    Page<MessageResponse> findMine(Pageable pageable);
}
