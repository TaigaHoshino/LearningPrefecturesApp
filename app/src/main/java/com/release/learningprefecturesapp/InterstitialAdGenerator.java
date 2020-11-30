package com.release.learningprefecturesapp;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

public class InterstitialAdGenerator {
    private boolean EmulatorTest = false;
    private String UnitID;
    // publisher ID
    private String AdMobID = "ca-app-pub-5584040938629320/1917550627";
    // Test ID
    private String EmulatorTestID = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd interstitialAd;
    // インタースティシャル用のView
    private View viewAd = null;

    private static int counter;

    private int interval = 4;
    // ５回に１回
    //private int interval = 5;

    InterstitialAdGenerator(Context context){

        if(EmulatorTest){
            UnitID = EmulatorTestID;
        }
        else{
            UnitID = AdMobID;
        }

        Log.d("debug", UnitID);

        // インタースティシャルを作成する。
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(UnitID);

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Log.d("debug", "onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
                Log.d("debug", message);
            }
        });

        AdRequest adRequest;
        if(EmulatorTest){
            // Test
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(EmulatorTestID)
                    .build();
        }
        else{
            // 広告リクエストを作成する (本番)
            adRequest = new AdRequest.Builder().build();
        }


        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);
    }

    void showInterstitial() {
        // ランダムに表示させる場合
        if(interval <= counter){
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                Random random = new Random();
                interval = 3 + random.nextInt(3);
                counter = 0;
            } else {
                Log.d("debug", "Interstitial ad was not ready to be shown...... ");
            }

        }
        else{
            counter++;
        }
    }

    // Gets a string error reason from an error code.
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "ERROR_CODE_INTERNAL_ERROR";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "ERROR_CODE_INVALID_REQUEST";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "ERROR_CODE_NETWORK_ERROR";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "ERROR_CODE_NO_FILL";
                break;
        }
        return errorReason;
    }
}
