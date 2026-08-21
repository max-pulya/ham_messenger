package com.example.smswin1251;

import android.app.Activity;
import android.widget.TextView;

import java.util.TimerTask;

public class Animation {
    int cnt=0;
    int animNumber=0;
    Runnable runnable=null;
    public String[] rotating_text={
        "       ___\n" +
        " \\ /   \\ /  |\\ /|\n" +
        "  -    ---  |   |\n" +
        " / \\   / \\  |   |\n"
            ,
        "       ___\n" +
        "  |/   \\ /  |\\|/\n" +
        "  -    ---  |  -\n" +
        "  |\\   / \\  |  \\\n"
            ,
        "        _\n" +
        "    | | | | |\n" +
        "     :  - |:|\n" +
        "    | |/|\\| |\n"
            ,
        "        _\n" +
        "      |\\|/|\n" +
        "       ---|\n" +
        "      |/|\\|\n"
            ,
        "        _\n" +
        "    | | | | |\n" +
        "    |:| -  :\n" +
        "    | |/|\\| |\n"
            ,
        "       ___\n" +
        " \\|/|  \\ /   \\|\n" +
        " -  |  ---    -\n" +
        " /  |  / \\   /|\n"
            ,
        "       ___\n" +
        "|\\ /|  \\ /   \\ /\n" +
        "|   |  ---    -\n" +
        "|   |  / \\   / \\\n"
    };
    public  TimerTask getTimerTask(TextView anim, Activity activity){
        return new TimerTask() {
            @Override
            public void run() {
                if (runnable==null){
                    runnable = new Runnable(){
                        @Override
                        public void run() {
                            if (cnt<7){
                                anim.setText(rotating_text[cnt]);
                            } else if (cnt<67) {
                                anim.setText(rotating_text[6]);
                            } else if (cnt<74) {
                                anim.setText(rotating_text[6-(cnt-67)]);
                            } else {
                                anim.setText(rotating_text[0]);
                            }
                            cnt+=1;
                            if (cnt>=134) cnt=0;
                        }
                };
                };
                activity.runOnUiThread(runnable);


            }
        };
    }
}
