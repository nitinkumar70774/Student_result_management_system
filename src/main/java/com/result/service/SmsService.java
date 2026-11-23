package com.result.service;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;
    
    public void sendResultNotification(String phoneNumber, String studentName, 
                                      String semester, String resultLink) {
        
        try {
            Twilio.init(accountSid, authToken);
            
            String formattedNumber = phoneNumber;
            if (!phoneNumber.startsWith("+")) {
                formattedNumber = "+91" + phoneNumber;
            }
            
            String messageBody = String.format(
                "Dear %s, your %s results have been declared! Click: %s",
                studentName, semester, resultLink
            );
            
            Message message = Message.creator(
                new PhoneNumber(formattedNumber),
                new PhoneNumber(twilioPhoneNumber),
                messageBody
            ).create();
            
            System.out.println("✅ SMS sent to: " + formattedNumber);
            
        } catch (Exception e) {
            System.err.println("❌ SMS failed: " + e.getMessage());
        }
    }
}