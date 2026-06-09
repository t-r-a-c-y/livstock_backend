package com.example.livestock.repository;

import com.example.livestock.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByActiveTrue(Pageable pageable);

    @Query("select m from Message m where m.active = true and (m.sender.id = :userId or m.receiver.id = :userId)")
    Page<Message> findActiveConversationForUser(Long userId, Pageable pageable);
}
