package com.example.triviaapp.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.data.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.modes.LuckPlayModeActivity;
import com.example.triviaapp.game.modes.PlayActivity;
import com.example.triviaapp.game.ui.rank.RankFragment;
import com.example.triviaapp.game.ui.profile.ProfileFragment;
import com.example.triviaapp.game.ui.menu.MenuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.data.LoggedUserData.EXMIC;
import static com.example.triviaapp.data.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.data.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.data.LoggedUserData.optionList;

public class GameActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private ProfileFragment profileFragment;
    private RankFragment rankFragment;
    private MenuFragment menuFragment;
    private HelpFragment helpFragment;
    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private MenuItem profile, score, game, help, signOut;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private boolean navigationListenerSet = false;
    private int lastFragment = 1;

    String continueButtonPopUpTextString, infoTextViewPopUpTextStringAudio, describeAudio, describeCommandAudio, describePopUpCommandsAudio, connectedToastAudio,
    connectionLostToastAudio, invalidCommandToastAudio, placeAudio, withAudio, pointsAudio, gameModesHelpAudio;

    String voiceInput = null;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    Locale selectedLanguage;

    private AlertDialog.Builder dialogBuilder = null;
    private AlertDialog dialog;
    private ImageView xImageViewPopUp;
    private TextView infoTextViewPopUp;
    private Button continueButtonPopUp;

    Date date;

    private TextToSpeech textToSpeech;
    private boolean connectionListenerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initialize();
        initializeViews();
        setToolbar();
        LoadFragment();

    }

    private void initialize() {
        speechInitialize();
        setTextToSpeechListener();

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.container);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationDrawerOpen, R.string.navigationDrawerClose) {
            public void onDrawerOpened(View drawerView) {
                navigationView.bringToFront();

            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fragmentManager.addOnBackStackChangedListener(this);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        chooseLanguage();

    }

    private void initializeViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.nav_view);
        navigationView = findViewById(R.id.nav_drawer_view);
        profileFragment = new ProfileFragment();
        rankFragment = new RankFragment();
        menuFragment = new MenuFragment();
        helpFragment = new HelpFragment();
        activeFragment = profileFragment;
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        profile = bottomNavigationView.getMenu().findItem(R.id.navigation_profile);
        score = bottomNavigationView.getMenu().findItem(R.id.navigation_score);
        game = bottomNavigationView.getMenu().findItem(R.id.navigation_game);
        help = navigationView.getMenu().findItem(R.id.navigation_help);
        signOut = navigationView.getMenu().findItem(R.id.navigation_sign_out);
        chooseLanguage();

    }

    private void setMenuItemsAndAudiosForEnglishLanguage() {
        profile.setTitle(R.string.title_profileEn);
        score.setTitle(R.string.title_scoreEn);
        game.setTitle(R.string.title_gameEn);
        help.setTitle(R.string.helpMenuItemProfileEn);
        signOut.setTitle(R.string.signOutMenuItemProfileEn);

        infoTextViewPopUpTextStringAudio = getString(R.string.infoTextViewMenuEn);
        continueButtonPopUpTextString = getString(R.string.nextButtonLogMenuPlayEn);

        describeAudio = getString(R.string.describeAudioMenuEn);
        describeCommandAudio = getString(R.string.describeCommandsAudioMenuEn);
        describePopUpCommandsAudio = getString(R.string.describeCommandsPopUpAudioMenuEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);
        gameModesHelpAudio = getString(R.string.howToPlayTextViewHelpEn) +
                "\n" + getString(R.string.helpTextHowToPlayEn) +
                "\n" + getString(R.string.howToScoreTextViewHelpEn) +
                "\n"  + getString(R.string.helpTextHowToScoreEn);
        placeAudio = getString(R.string.placeAudioMenuEn);
        withAudio = getString(R.string.withAudioMenuEn);
        pointsAudio = getString(R.string.pointsTextViewAudioRankEn);

    }

    private void setMenuItemsAndAudiosForRomanianLanguage() {
        profile.setTitle(R.string.title_profileRou);
        score.setTitle(R.string.title_scoreRou);
        game.setTitle(R.string.title_gameRou);
        help.setTitle(R.string.helpMenuItemProfileRou);
        signOut.setTitle(R.string.signOutMenuItemProfileRou);

        infoTextViewPopUpTextStringAudio = getString(R.string.infoTextViewMenuRou);
        continueButtonPopUpTextString = getString(R.string.nextButtonLogMenuPlayRou);

        describeAudio = getString(R.string.describeAudioMenuRou);
        describeCommandAudio = getString(R.string.describeCommandsAudioMenuRou);
        describePopUpCommandsAudio = getString(R.string.describeCommandsPopUpAudioMenuRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioRou);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioRou);
        gameModesHelpAudio = getString(R.string.howToPlayTextViewHelpRou) +
                "\n" + getString(R.string.helpTextHowToPlayRou) +
                "\n" + getString(R.string.howToScoreTextViewHelpRou) +
                "\n"  + getString(R.string.helpTextHowToScoreRou);
        placeAudio = getString(R.string.placeAudioMenuRou);
        withAudio = getString(R.string.withAudioMenuRou);
        pointsAudio = getString(R.string.pointsTextViewAudioRankRou);

    }

    private void chooseLanguage() {
        switch (LoggedUserData.language) {
            case "english":
                setMenuItemsAndAudiosForEnglishLanguage();
                selectedLanguage = Locale.ENGLISH;
                break;
            case "romanian":
                setMenuItemsAndAudiosForRomanianLanguage();
                selectedLanguage = Locale.getDefault();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
        }

    }

    private HashMap<String, Object> populateMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", LoggedUserData.loggedUserEmail);
        map.put("loginCode", LoggedUserData.loggedUserLoginCode);
        map.put("gamesWon", LoggedUserData.loggedGamesWon);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower5050", LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("superpowerCorrectAnswer", LoggedUserData.loggedSuperPowerCorrectAnswer);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("dailyQuestionTime", LoggedUserData.loggedUserDailyQuestionTime);
        map.put("luckModeTime", LoggedUserData.loggedUserLuckModeTime);
        return map;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                fragmentManager.beginTransaction().remove(profileFragment).commitNow();
                fragmentManager.beginTransaction().remove(rankFragment).commitNow();
                fragmentManager.beginTransaction().remove(menuFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, profileFragment, "1").hide(activeFragment).show(profileFragment).commit();
                activeFragment = profileFragment;
                lastFragment = 1;
                return true;

            case R.id.navigation_score:
                fragmentManager.beginTransaction().remove(profileFragment).commitNow();
                fragmentManager.beginTransaction().remove(rankFragment).commitNow();
                fragmentManager.beginTransaction().remove(menuFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, rankFragment, "2").hide(activeFragment).show(rankFragment).commit();
                activeFragment = rankFragment;
                lastFragment = 2;
                return true;

            case R.id.navigation_game:
                fragmentManager.beginTransaction().remove(profileFragment).commitNow();
                fragmentManager.beginTransaction().remove(rankFragment).commitNow();
                fragmentManager.beginTransaction().remove(menuFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, menuFragment, "3").hide(activeFragment).show(menuFragment).commit();
                activeFragment = menuFragment;
                lastFragment = 3;
                return true;
            case R.id.navigation_help:
                fragmentManager.beginTransaction().remove(helpFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, helpFragment, "4").addToBackStack(null).hide(activeFragment).show(helpFragment).commit();
                bottomNavigationView.setVisibility(View.GONE);
                return true;
            case R.id.navigation_sign_out:
                signOut();

        }
        return false;

    }

    private void LoadFragment() {
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, profileFragment, "1").commit();

    }

    @Override
    public void onBackStackChanged() {
        displayToolbarButton();

    }

    private void displayToolbarButton() {
        boolean fragmentBackStackPopulated = fragmentManager.getBackStackEntryCount() > 0;

        if (fragmentBackStackPopulated) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);

            if (!navigationListenerSet) {
                toggle.setToolbarNavigationClickListener(v -> {
                    fragmentManager.popBackStackImmediate();
                    fragmentManager.beginTransaction().remove(activeFragment).commitNow();
                    fragmentManager.beginTransaction().add(R.id.nav_host_fragment, activeFragment, String.valueOf(lastFragment)).show(activeFragment).commit();
                    bottomNavigationView.setVisibility(View.VISIBLE);
                });
                navigationListenerSet = true;

            }

        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            navigationListenerSet = false;

        }

    }

    private void signOut() {
        LoggedUserData.onResumeFromAnotherActivity = true;
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        firebaseAuth.signOut();
        finishAndRemoveTask();

    }

    private void destroySpeaker() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech = null;

        }

    }

    @Override
    protected void onDestroy() {
        destroySpeaker();
        super.onDestroy();

    }

    private void verifyTextToSpeechListenerStatus(int status) {
        if (status == TextToSpeech.SUCCESS) {
            setProgressListener();
            int result = textToSpeech.setLanguage(selectedLanguage);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(0.75f);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getBaseContext(), "Language not supported!", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(getBaseContext(), "Initialization failed!", Toast.LENGTH_SHORT).show();

        }

    }

    private void setTextToSpeechListener() {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            checkOptions(describeAudio, "Base");
            setConnectionListener();

        });

    }

    private void setProgressListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {


            }

            @Override
            public void onDone(String utteranceId) {
                speechRecognizer.destroy();
                Runnable runnable = () -> {
                    if (optionList.get(EXMIC).isValue()) {
                        if (dialogBuilder == null) {
                            getSpeechInput("Base");

                        } else {
                            getSpeechInput("PopUp");

                        }

                    }

                };

                runOnUiThread(runnable);

            }

            @Override
            public void onError(String utteranceId) {

            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {

            }

        });

    }

    private void speak(String text, int queueMode) {
        if (textToSpeech == null) {
            Log.d("Game", "NULL SPEAK OBJECT");

        }
        if(textToSpeech != null) {
            textToSpeech.speak(text, queueMode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        }else{
            if(optionList.get(EXMIC).isValue()){
                if(dialogBuilder == null){
                     getSpeechInput("Base");
                }else{
                    getSpeechInput("PopUp");

                }

            }

        }

    }


    private void speechInitialize() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);

    }

    private void getSpeechInput(String screen) {
        speechRecognizer.startListening(speechIntent);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                Log.d("Error", String.valueOf(error));
                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    getSpeechInput(screen);

                }

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                voiceInput = result.get(0);
                Log.d("Game Activity voice input " + screen + ":", result.get(0));
                switch (LoggedUserData.language) {
                    case "english":
                        switch (screen) {
                            case "Base":
                                speechInputEn(voiceInput);
                                break;
                            case "PopUp":
                                speechInputPopUpEn(voiceInput);
                                break;

                        }
                        break;
                    case "romanian":
                        switch (screen) {
                            case "Base":
                                speechInputRou(voiceInput);
                                break;
                            case "PopUp":
                                speechInputPopUpRou(voiceInput);
                                break;

                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);

                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

        });

    }

    private void invalidVoiceInput(String screen) {
        Toast.makeText(this, invalidCommandToastAudio, Toast.LENGTH_SHORT).show();
        checkOptions(invalidCommandToastAudio, screen);

    }

    private void checkOptions(String feedback, String screen) {
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(feedback, QUEUE_ADD);

        } else {
            if (optionList.get(EXMIC).isValue()) {
                getSpeechInput(screen);

            }

        }

    }

    private void speechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "place":
                placeAudioFeedback();
                return;
            case "profile":
                onNavigationItemSelected(profile);
                break;
            case "rank":
                onNavigationItemSelected(score);
                break;
            case "game":
                onNavigationItemSelected(game);
                break;
            case "help":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(gameModesHelpAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("Base");

                }
                return;
            case "sign out":
                onNavigationItemSelected(signOut);
                break;
            case "settings":
                editDataActivity();
                return;
            case "describe":
            case "described":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("Base");

                }
                return;
            case "Classic mode":
            case "classic mode":
                openGameSettingsActivity();
                return;
            case "daily question":
                dailyQuestionPopUp();
                return;
            case "lucky mode":
                luckModeActivity();
                return;
            case "commands":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeCommandAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("Base");

                }
                return;
            default:
                invalidVoiceInput("Base");
                return;

        }
        getSpeechInput("Base");

    }

    private void speechInputRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "punctaj":
                placeAudioFeedback();
                return;
            case "profil":
                onNavigationItemSelected(profile);
                break;
            case "clasament":
                onNavigationItemSelected(score);
                break;
            case "joc":
                onNavigationItemSelected(game);
                break;
            case "ajutor":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(gameModesHelpAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("Base");

                }
                return;
            case "deconectare":
                onNavigationItemSelected(signOut);
                break;
            case "setări":
                editDataActivity();
                return;
            case "descriere":
            case "descrie":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("Base");

                }
                return;
            case "Modul clasic":
            case "modul clasic":
                openGameSettingsActivity();
                return;
            case "întrebarea zilei":
                dailyQuestionPopUp();
                return;
            case "modul norocos":
                luckModeActivity();
                return;
            case "comenzi":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeCommandAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("Base");

                }
                return;
            default:
                invalidVoiceInput("Base");
                return;

        }
        getSpeechInput("Base");

    }

    private void speechInputPopUpEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "next":
                continueButtonPopUp.performClick();
                return;
            case "exit":
                xImageViewPopUp.performClick();
                return;
            case "describe":
            case "described":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(infoTextViewPopUpTextStringAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("PopUp");

                }
                return;
            case "commands":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describePopUpCommandsAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("PopUp");

                }
                return;
            default:
                invalidVoiceInput("PopUp");

        }

    }

    private void speechInputPopUpRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "continuă":
                continueButtonPopUp.performClick();
                return;
            case "ieșire":
                xImageViewPopUp.performClick();
                return;
            case "descriere":
            case "descrie":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(infoTextViewPopUpTextStringAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("PopUp");

                }
                return;
            case "comenzi":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describePopUpCommandsAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput("PopUp");

                }
                return;
            default:
                invalidVoiceInput("PopUp");

        }

    }

    private void placeAudioFeedback() {
        String text = placeAudio + SPACESTRING + LoggedUserData.loggedUserPlace + SPACESTRING + withAudio +
                SPACESTRING + LoggedUserData.loggedUserPoints + SPACESTRING + pointsAudio + ".";
        checkOptions(text, "Base");

    }

    public void editDataActivity() {
        Intent intent = new Intent(this, EditDataActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    public void openGameSettingsActivity() {
        speechRecognizer.destroy();
        Intent intent = new Intent(this, GameSettingsActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    public void dailyQuestionPopUp() {
        speechRecognizer.destroy();
        dialogBuilder = new AlertDialog.Builder(this);
        View questionPopUpView = getLayoutInflater().inflate(R.layout.template_question_pop_up, null);
        xImageViewPopUp = questionPopUpView.findViewById(R.id.xImageViewPopUp);
        infoTextViewPopUp = questionPopUpView.findViewById(R.id.templateInfoTextViewPopUp);
        continueButtonPopUp = questionPopUpView.findViewById(R.id.continueButtonPopUp);

        infoTextViewPopUp.setText(infoTextViewPopUpTextStringAudio);
        continueButtonPopUp.setText(continueButtonPopUpTextString);

        dialogBuilder.setView(questionPopUpView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        checkOptions(infoTextViewPopUpTextStringAudio, "PopUp");

        xImageViewPopUp.setOnClickListener((v) -> {
            speechRecognizer.destroy();
            checkOptions(describeAudio, "Base");
            dialog.dismiss();
            dialogBuilder = null;

        });
        continueButtonPopUp.setOnClickListener((v) -> continueToDailyQuestion());

    }

    private void continueToDailyQuestion() {
        dialog.dismiss();
        updateGameModesTime("DailyQuestion");
        LoggedUserData.dailyQuestion = true;
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    public void luckModeActivity() {
        updateGameModesTime("LuckMode");
        Intent intent = new Intent(this, LuckPlayModeActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    private void updateGameModesTime(String control) {
        date = new Date();
        switch (control) {
            case "DailyQuestion":
                LoggedUserData.loggedUserDailyQuestionTime = date.getTime();
                break;
            case "LuckMode":
                LoggedUserData.loggedUserLuckModeTime = date.getTime();
                break;

        }

        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && textToSpeech != null) {
                    Log.d("Game", "connectionListener");
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        connected();

                    } else {
                        lossConnection();

                    }
                }
                connectionListenerStatus = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Connection listener cancelled!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void connected() {
        Toast.makeText(getApplicationContext(), connectedToastAudio, Toast.LENGTH_SHORT).show();
        speechRecognizer.destroy();
        if (dialogBuilder == null) {
            checkOptions(connectedToastAudio, "Base");
        } else {
            checkOptions(connectedToastAudio, "PopUp");

        }

    }

    private void lossConnection() {
        Toast.makeText(getApplicationContext(), connectionLostToastAudio, Toast.LENGTH_SHORT).show();
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(connectionLostToastAudio, QUEUE_ADD);

        }

    }


}