package com.fiap.mindcare_diary.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requer mock estático do FirebaseMessaging; mantido como teste de contrato.")
class PushNotificationServiceTest {
    @Test
    void contratoDoServico() {
        assertNotNull(new PushNotificationService());
    }
}
