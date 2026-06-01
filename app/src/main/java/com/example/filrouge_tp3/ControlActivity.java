package com.example.filrouge_tp3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;


/**
 * ControlActivity acts as the main orchestrator (Controller) of the application.
 * It manages the interaction between two distinct areas:
 * <ul>
 * <li><b>MenuFragment:</b> A static-like fragment (14% surface) providing navigation controls.</li>
 * <li><b>Main Content Area:</b> A dynamic zone (86% surface) displaying various ScreenFragments.</li>
 * </ul>
 *
 * <p>The activity is responsible for:
 * <ul>
 * <li>Handling fragment transactions and maintaining a clean BackStack.</li>
 * <li>Managing state persistence (e.g., current menu index and navigation flags) during configuration changes.</li>
 * <li>Providing a communication bridge between fragments via {@link Menuable} and {@link Notifiable} interfaces.</li>
 * </ul>
 * * <p>Layout distribution:
 * Screen1Fragment (dynamic) - 6/7 of the surface.
 * MenuFragment (static) - 1/7 of the surface.</p>
 *
 * @author F. Rallo
 * @version 1.1 - March 2026
 * @see Menuable
 * @see Notifiable
 */
public class ControlActivity extends AppCompatActivity implements Menuable, Notifiable, Picturable {
    private static final String DATA_IS_STARTING = "sauvegarde";
    private static final String DATA_MENU_NUMBER = "num";
    private final String TAG = "frallo "+getClass().getSimpleName();
    private Fragment mainFragment;
    private MenuFragment menu;
    private boolean isStarting = true;
    private Fragment[] tabFragments;
    private int menuNumber;
    private Screen5Fragment screen5;
    private IssueManager issueManager;
    private IssueController issueController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        issueManager = IssueManager.getInstance();
        issueController = new IssueController(issueManager);
        screen5 = Screen5Fragment.newInstance(issueManager, issueController);
        issueManager.addView(screen5);

        tabFragments = new Fragment[]{ new Screen1Fragment(), new Screen2Fragment(),
                                       new Screen3Fragment(), new Screen4Fragment(),
                                       screen5, new Screen6Fragment(),
                                       new Screen7Fragment() };

        // Start with "<#step> -->"  when not savedInstanceState
        if(savedInstanceState == null) {
             menuNumber = 0;
        }
        Log.d(TAG,"menuNumber "+menuNumber);


        Intent intent = getIntent();
        if(intent!=null){
            menuNumber = intent.getIntExtra(getString(R.string.index),0);
            Log.d(TAG,"received menu#"+menuNumber);
        }


        Bundle args = new Bundle();
        args.putInt(getString(R.string.index), menuNumber);

        if (savedInstanceState == null) {
            menu = new MenuFragment();
            menu.setArguments(args);

            mainFragment = tabFragments[menuNumber];

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_menu, menu);
            transaction.replace(R.id.fragment_main, mainFragment);
            transaction.commit();
        }
    }


    @Override
    public void onPictureTaken(String photoPath) {
        Log.d(TAG, "Photo reçue : " + photoPath);
    }

    @Override
    public void onMenuChange(int index) {
        menuNumber = index;
        //Log.d(TAG, "Menu change ==>" + menuNumber);
        mainFragment = tabFragments[index];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, mainFragment);

        if (!isStarting) {
            transaction.addToBackStack(null);
        } else {
            isStarting = false;
        }

        transaction.commit();
    }

    @Override
    public void onFragmentDisplayed(int fragmentId) {
        Log.d(TAG, "onFragmentDisplayed ==>" + fragmentId);
        if(menuNumber != fragmentId){
            menuNumber = fragmentId;
            menu.setCurrentActivatedIndex(menuNumber);
        }
    }

    @Override
    public void onClick(int numFragment) {
        Log.d(TAG, "Menu " + numFragment +" has clicked!");
    }

    @Override
    public void onDataChange(int numFragment, Object data, int actionCode, Object argsAction) {
        Log.d(TAG, "received data from fragment#" + numFragment + " actionCode=" + actionCode);

        if (numFragment == Screen4Fragment.FRAGMENT_ID
                && actionCode == Screen4Fragment.ACTION_VIEW_MAP) {
            navigateToFragment(Screen5Fragment.FRAGMENT_ID);

        } else if (numFragment == Screen2Fragment.FRAGMENT_ID
                && actionCode == Screen2Fragment.ACTION_CLICK_ITEM) {
            Issue issue = (Issue) data;
            IssueManager.getInstance().addReportedIssue(issue);
            navigateToFragment(Screen5Fragment.FRAGMENT_ID);

        } else if (numFragment == Screen2Fragment.FRAGMENT_ID
                && actionCode == Screen2Fragment.ACTION_RATING_CHANGE) {
            Log.d(TAG, "Rating changed : " + argsAction + " for " + ((Issue) data).getTitle());

        } else if (numFragment == Screen3Fragment.FRAGMENT_ID
                && actionCode == Screen3Fragment.ACTION_NEW_ISSUE) {
            Issue newIssue = (Issue) data;
            String protocol = (String) argsAction;
            Log.d(TAG, "Nouveau signalement : " + newIssue.getTitle() + " | Protocole : " + protocol);
            IssueManager.getInstance().addReportedIssue(newIssue);
            Screen4Fragment detailFragment = Screen4Fragment.newInstance(newIssue);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }




    private void navigateToFragment(int index) {
        menuNumber = index;
        menu.setCurrentActivatedIndex(menuNumber);

        if (index == Screen5Fragment.FRAGMENT_ID) {
            issueManager.removeView(screen5);
            screen5 = Screen5Fragment.newInstance(issueManager, issueController);
            issueManager.addView(screen5);
            tabFragments[index] = screen5;
        }

        mainFragment = tabFragments[index];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, mainFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        issueManager.removeView(screen5);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"restaure menuNumber "+menuNumber);
        outState.putBoolean(DATA_IS_STARTING, isStarting);
        outState.putInt(DATA_MENU_NUMBER, menuNumber);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isStarting = savedInstanceState.getBoolean(DATA_IS_STARTING);
        menuNumber = savedInstanceState.getInt(DATA_MENU_NUMBER);
    }


}
