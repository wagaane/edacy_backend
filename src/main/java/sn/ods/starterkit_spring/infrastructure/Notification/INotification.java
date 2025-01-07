package sn.ods.starterkit_spring.infrastructure.Notification;


import sn.ods.starterkit_spring.infrastructure.Notification.model.Notification;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;

public interface INotification {
    Response<Object> notifyUser(Notification notification);
    Response<Object> getNotifiesByUser(int page, int pageSize,Long idUser,  String codeProfile);
    Response<Object> notifyIsRead(Long idNotification);
    Response<Object> getListNotifiesByUser(Long idUser, String codeProfile);
}
