package com.example.emotion_classification;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserAccount{

    private String idToken;
    private String nickNameId;
    private String passWord;


    public UserAccount(){

    }
    public String getIdToken(){ return idToken;}
    public void setIdToken(String idToken){
        this.idToken = idToken;
    }
    public String getNickNameId(){
        return nickNameId;
    }
    public void setNickNameId(String nickNameId){
        this.nickNameId = nickNameId;
    }
    public String getPassWord(){
        return passWord;
     }
    public void setPassWord(String passWord){
        this.passWord = passWord;
    }

}