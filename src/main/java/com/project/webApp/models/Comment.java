package com.project.webApp.models;


import jakarta.persistence.*;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private Date sentAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;
    public Comment(){
    }
    public Comment(String message, User user){
        this.message = message;
        this.author = user;
        sentAt = new Date();
    }

//    public String getSentAt() {
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm d MMMM yyyy");
//        return formatter.format(sentAt);
//    }
}
