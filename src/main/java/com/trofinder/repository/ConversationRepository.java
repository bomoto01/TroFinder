package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trofinder.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE c.tenant.id = :userId OR c.landlord.id = :userId ORDER BY c.createdAt DESC")
    List<Conversation> findByUserId(@Param("userId") Long userId);

    Optional<Conversation> findByRoomIdAndTenantId(Long roomId, Long tenantId);

    @Query("SELECT c FROM Conversation c WHERE c.id = :id AND (c.tenant.id = :userId OR c.landlord.id = :userId)")
    Optional<Conversation> findByIdAndParticipant(@Param("id") Long id, @Param("userId") Long userId);
}
