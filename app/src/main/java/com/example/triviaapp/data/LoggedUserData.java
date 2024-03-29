package com.example.triviaapp.data;

import androidx.appcompat.app.AppCompatActivity;

import com.example.triviaapp.data.Option;
import com.example.triviaapp.data.rank.User;

import java.util.HashMap;
import java.util.List;

public class LoggedUserData{
    public static String loggedUserName = "empty";
    public static String loggedUserEmail = "empty";
    public static String loggedUserPassword = "empty";
    public static String loggedUserLoginCode = "empty";
    public static String loggedUserKey = "empty";
    public static int loggedSuperPowerFiftyFifty = 0;
    public static int loggedSuperPowerCorrectAnswer = 0;
    public static int loggedGamesWon = 0;
    public static int loggedUserPlace = 0;
    public static int loggedUserPoints = 0;
    public static long loggedUserDailyQuestionTime = 0;
    public static long loggedUserLuckModeTime = 0;
    public static boolean loggedUserPasswordUpdateVerify;
    public static List<String> userNameList = null;
    public static HashMap<String, User> voiceCommandLoginData = null;
    public static List<User> ranksList = null;
    public static List<Option> optionList;
    public static long millis;

    public static final String EMPTYSTRING = "";
    public static final String SPACESTRING = " ";
    public static final int MIC = 0;
    public static final int SPEAKER = 1;
    public static final int SPORT = 2;
    public static final int GEO = 3;
    public static final int MATHS = 4;
    public static final int OTHERS = 5;
    public static final int EXMIC = 6;
    public static final int EXSPEAKER = 7;
    public static final int AUTODELOG = 8;

    public static String language= EMPTYSTRING;
    public static boolean dailyQuestion = false;

    public static boolean onResumeFromAnotherActivity = false;

}
