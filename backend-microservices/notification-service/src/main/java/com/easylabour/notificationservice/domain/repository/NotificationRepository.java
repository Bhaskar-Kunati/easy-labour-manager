package com.easylabour.notificationservice.domain.repository;

import com.easylabour.notificationservice.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    Long countByUserIdAndIsReadFalse(String userId);
    Optional<Notification> findByReferenceIdAndNotificationType(String referenceId, String notificationType);
}
