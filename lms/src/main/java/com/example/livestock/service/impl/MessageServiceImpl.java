package com.example.livestock.service.impl;

import com.example.livestock.dto.MessageReplyRequest;
import com.example.livestock.dto.MessageRequest;
import com.example.livestock.dto.MessageResponse;
import com.example.livestock.entity.Animal;
import com.example.livestock.entity.Message;
import com.example.livestock.entity.User;
import com.example.livestock.enums.MessageStatus;
import com.example.livestock.enums.NotificationType;
import com.example.livestock.enums.Role;
import com.example.livestock.exception.BadRequestException;
import com.example.livestock.exception.ForbiddenException;
import com.example.livestock.exception.ResourceNotFoundException;
import com.example.livestock.mapper.DtoMapper;
import com.example.livestock.repository.AnimalRepository;
import com.example.livestock.repository.MessageRepository;
import com.example.livestock.repository.OwnerRepository;
import com.example.livestock.repository.UserRepository;
import com.example.livestock.security.CurrentUserService;
import com.example.livestock.service.MessageService;
import com.example.livestock.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Override
    public MessageResponse sendToAdmin(MessageRequest request) {
        User ownerUser = currentUserService.getCurrentUser();
        User admin = userRepository.findByRoleAndActiveTrue(Role.ADMIN).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active admin user found"));
        Animal animal = null;
        if (request.animalId() != null) {
            var owner = ownerRepository.findByUserIdAndActiveTrue(ownerUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner profile not found"));
            animal = animalRepository.findByIdAndOwnerIdAndActiveTrue(request.animalId(), owner.getId())
                    .orElseThrow(() -> new ForbiddenException("Animal does not belong to current owner"));
        }
        Message message = new Message();
        message.setSender(ownerUser);
        message.setReceiver(admin);
        message.setAnimal(animal);
        message.setSubject(request.subject());
        message.setMessageBody(request.messageBody());
        Message saved = messageRepository.save(message);
        notificationService.notify(admin, "Message received", ownerUser.getFullName() + " sent a message", NotificationType.MESSAGE_RECEIVED);
        return DtoMapper.toMessage(saved);
    }

    @Override
    public MessageResponse reply(MessageReplyRequest request) {
        User admin = currentUserService.getCurrentUser();
        Message original = messageRepository.findById(request.messageId())
                .filter(Message::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        if (original.getSender().getRole() != Role.OWNER) {
            throw new BadRequestException("Can only reply to owner messages");
        }
        original.setMessageStatus(MessageStatus.REPLIED);
        Message reply = new Message();
        reply.setSender(admin);
        reply.setReceiver(original.getSender());
        reply.setAnimal(original.getAnimal());
        reply.setSubject("RE: " + original.getSubject());
        reply.setMessageBody(request.messageBody());
        reply.setMessageStatus(MessageStatus.REPLIED);
        Message saved = messageRepository.save(reply);
        notificationService.notify(original.getSender(), "Admin replied", "Admin replied to: " + original.getSubject(), NotificationType.MESSAGE_RECEIVED);
        return DtoMapper.toMessage(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> findAll(Pageable pageable) {
        return messageRepository.findByActiveTrue(pageable).map(DtoMapper::toMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> findMine(Pageable pageable) {
        return messageRepository.findActiveConversationForUser(currentUserService.getCurrentUser().getId(), pageable)
                .map(DtoMapper::toMessage);
    }
}
