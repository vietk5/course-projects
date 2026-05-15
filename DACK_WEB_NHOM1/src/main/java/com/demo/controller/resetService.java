package com.demo.controller;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

public class resetService {
    private final int LIMIT_MINUS = 10;
    static final String from = "shoplinhkien161025@gmail.com";
    static final String password = "avan kxwi pkjd mkrb";
    
    public String generateToken(){
        return UUID.randomUUID().toString();
    }
    
    public LocalDateTime expireDatetime(){
        return LocalDateTime.now().plusMinutes(LIMIT_MINUS);
        
    }
    
    public boolean isExpireTime(LocalDateTime time){
        return LocalDateTime.now().isAfter(time);
    }

    public boolean sendEmail(String to, String link, String name){
        
        //Khai báo các thuộc tính
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");//SMTP host
        props.put("mail.smtp.port", "587"); //TLS:587; SSL:465
        props.put("mail.smtp.auth", "true"); // true, cần phải đăng nhập 
        props.put("mail.smtp.starttls.enable", "true");//protocal TLS
        
        //Create Authenticator (lấy ra đc authenticator để đăng nhập vào gmail)
        Authenticator auth = new Authenticator() { 
            @Override // phương thức
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(from, password);
            }
        };
        // Tạo phiên gửi mail
        Session session = Session.getInstance(props,auth); // đăng nhập vào gmail với tài khoản authen
        // Soạn email để gửi
        MimeMessage msg = new MimeMessage(session); // tạo 1 message mới
        //đôi lúc ko connect đc nên phải để vào try catch
        try {
            //Kiểu nội dung
            msg.addHeader("Content-type", "text/html; charset=UTF-8"); // 
            msg.setFrom(from);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false)); // người nhận 
            msg.setSubject("Reset Password","UTF-8"); // Tiêu đề
            String content = "<h1>Xin chào " + name + "</h1>"+""
                    + "<p>Bạn vừa gửi yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>"
                    + "<p>Vui lòng nhấn vào nút bên dưới để đặt lại mật khẩu mới:</p>"
                    + "<a href="+link+">Đặt lại mật khẩu</a></p>"
                    + "<p>Nếu bạn không yêu cầu thao tác này, vui lòng bỏ qua email này. "
                    + "Liên kết sẽ hết hạn sau 10 phút vì lý do bảo mật.</p>"
                    + "<p>Trân trọng,"
                    + "<br>Đội ngũ hỗ trợ khách hàng<br>"
                    + "<strong>Shop Linh Kiện</strong></p>";
            msg.setContent(content,"text/html; charset=UTF-8");
            Transport.send(msg);//gửi mail
            System.out.println("Send sucessfully");
            return true;
        } catch (Exception e) {
            System.out.println("Send error");
            e.printStackTrace();
            return false;
        }
    }
}