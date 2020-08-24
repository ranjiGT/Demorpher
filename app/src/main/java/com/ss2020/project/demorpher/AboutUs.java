package com.ss2020.project.demorpher;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUs extends AppCompatActivity {

    View about_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String decsription = "The De-Morpher app is developed as a part of IT-Security Project - Summer Semester 2020 under the guidance of Computer Science Department, Otto von Guericke University, Magdeburg, Germany.\n Team Members/Developers: " +
                "\nDarshit Shah \nEndi Haxhiraj \nRanjiraj Nair \nNikhilkumar Italiya " ;


        about_us = (View) findViewById(R.id.about_us_view);

        View about_us_page = new AboutPage(this)
                .isRTL(false)
                .setDescription(decsription)
                .setImage(R.drawable.app_icon_title)
                .addGitHub("iamitaliya/Demorpher")
                .create();

        setContentView(about_us_page);


    }


}
