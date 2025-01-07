package sn.ods.starterkit_spring.infrastructure.Notification;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sn.ods.starterkit_spring.infrastructure.Notification.model.Notification;

public interface NotificationRepository  extends JpaRepository<Notification, Long>, QuerydslPredicateExecutor<Notification> {
    @Query("SELECT COUNT(not) FROM Notification not WHERE not.isRead = :isRead ")
    Long notificationsNotRead(boolean isRead);
}
