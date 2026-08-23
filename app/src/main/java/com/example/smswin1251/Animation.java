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
        " / \\   / \\  |   |\n\n"
            ,
        "       ___\n" +
        "  |/   \\ /  |\\|/\n" +
        "  -    ---  |  -\n" +
        "  |\\   / \\  |  \\\n\n"
            ,
        "        _\n" +
        "    | | | | |\n" +
        "     :  - |:|\n" +
        "    | |/|\\| |\n\n"
            ,
        "        _\n" +
        "      |\\|/|\n" +
        "       ---|\n" +
        "      |/|\\|\n\n"
            ,
        "        _\n" +
        "    | | | | |\n" +
        "    |:| -  :\n" +
        "    | |/|\\| |\n\n"
            ,
        "       ___\n" +
        " \\|/|  \\ /   \\|\n" +
        " -  |  ---    -\n" +
        " /  |  / \\   /|\n\n"
            ,
        "       ___\n" +
        "|\\ /|  \\ /   \\ /\n" +
        "|   |  ---    -\n" +
        "|   |  / \\   / \\\n\n"
    };
    String[] mask ={
            " ___   ___   ___ ",
            "|\\|/| |\\|/| |\\|/|",
            "|---| |---| |---|",
            "|/|\\| |/|\\| |/|\\|",
            " ---   ---   --- "
    };
    String[] masksony ={
            " _     _     _  ",
            "| |   | |   | |  ",
            " - -   - -   - - ",
            "  | |   | |   | |",
            "   -     -     - "
    };
    String[] ham ={
            "       ___       ",
            " \\ /   \\ /  |\\ /|",
            "  -    ---  |   |",
            " / \\   / \\  |   |",
            "                 ",
    };

    public  TimerTask getTimerTask(TextView anim, Activity activity){
        return new TimerTask() {
            @Override
            public void run() {
                if (runnable==null ){
                    runnable = new Runnable(){
                        @Override
                        public void run() {
                            switch (animNumber){
                                case 0:
                                    int masklength;
                                    String animFrame="";
                                    if (cnt<=17)masklength=cnt;
                                    else if (cnt<=35) masklength=35-cnt;
                                    else masklength=0;
                                    for (int i=0;i<5;i++){
                                        animFrame+=mask[i].subSequence(0,masklength);
                                        animFrame+=ham[i].subSequence(masklength,17);
                                        animFrame+="\n";
                                    }
                                    anim.setText(animFrame);
                                    cnt+=1;
                                    if (cnt>70){
                                        cnt=0;
                                        animNumber=1;
                                    }
                                    break;
                                case 1:
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
                                    if (cnt>=134) {
                                        cnt = 0;
                                        animNumber = 0;
                                    }
                                    break;

                            }


                            }

                };
                };
                activity.runOnUiThread(runnable);


            }
        };
    }
}
