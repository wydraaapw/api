package org.example.api.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.api.dto.NotificationResponse;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.Notification;
import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.repository.NotificationRepository;
import org.example.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private NotificationService self;

    public List<NotificationResponse> getUserNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));

        return notificationRepository.findAllByUserId(user.getId(), Sort.by("sentAt").descending())
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getTitle(), n.getContent(), n.getSentAt(), n.isRead()))
                .toList();
    }

    public long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Użytkownik nie istnieje"));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Powiadomienie nie istnieje"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Notification> notifications = notificationRepository.findAllByUserId(user.getId(), Sort.unsorted());
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Async
    @Transactional
    public void notifyUser(User user, String title, String content) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Async
    public void notifyAdmins(String title, String content) {
        userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_ADMIN && u.isActive())
                .forEach(admin ->
                    self.notifyUser(admin, title, content)
                );
    }
}