package sn.wagaane.task_app.infrastructure.Notification;


import sn.wagaane.task_app.infrastructure.Notification.model.Notification;
import sn.wagaane.task_app.presentation.dto.responses.Response;

public interface INotification {
    Response<Object> notifyUser(Notification notification);
    Response<Object> getNotifiesByUser(int page, int pageSize,Long idUser,  String codeProfile);
    Response<Object> notifyIsRead(Long idNotification);
    Response<Object> getListNotifiesByUser(Long idUser, String codeProfile);
}
