package com.fiap.mindcare_diary.services;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.FirebaseMessagingException;

@Service
public class PushNotificationService {

    public String sendNotification(Message message) {
        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.out.println("Erro ao enviar notificação: " + e.getMessage());
            return null;
        }
    }
}
