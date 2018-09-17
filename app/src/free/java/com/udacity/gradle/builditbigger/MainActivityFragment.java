package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alexbaryzhikov.presenter.DisplayJokeActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.udacity.gradle.builditbigger.backend.jokesApi.JokesApi;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivityFragment extends Fragment {

  private final AtomicBoolean interstitialIsOn = new AtomicBoolean(false);
  private volatile String joke;
  private ViewGroup buttonContainer;
  private View progressIndicator;
  private InterstitialAd interstitialAd;
  private AdRequest interstitialAdRequest;
  private SimpleIdlingResource idlingResource;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_main, container, false);

    buttonContainer = root.findViewById(R.id.button_container);
    progressIndicator = root.findViewById(R.id.progress_indicator);

    idlingResource = ((MainActivity) requireActivity()).getIdlingResource();
    // Wait for interstitial ad to load
    idlingResource.setIdleState(false);

    AdView adView = root.findViewById(R.id.adView);
    // Create an ad request. Check logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();
    adView.loadAd(adRequest);

    // Interstitial ad
    interstitialAd = new InterstitialAd(requireContext());
    interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
    interstitialAdRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();
    interstitialAd.loadAd(interstitialAdRequest);
    interstitialAd.setAdListener(new AdListener() {

      @Override
      public void onAdLoaded() {
        idlingResource.setIdleState(true);
      }

      @Override
      public void onAdOpened() {
        idlingResource.setIdleState(true);
      }

      @Override
      public void onAdClosed() {
        idlingResource.setIdleState(false);
        interstitialIsOn.set(false);
        interstitialAd.loadAd(interstitialAdRequest);
        tellJoke();
      }
    });

    Button jokeButton = root.findViewById(R.id.tell_joke_button);
    jokeButton.setOnClickListener(v -> new EndpointsAsyncTask(this).execute());

    return root;
  }

  public synchronized void tellJoke() {
    if (TextUtils.isEmpty(joke) || interstitialIsOn.get()) {
      return;
    }
    Intent intent = new Intent(getActivity(), DisplayJokeActivity.class);
    intent.putExtra(DisplayJokeActivity.JOKE_ID, joke);
    joke = null;
    startActivity(intent);
    idlingResource.setIdleState(true);
  }

  public void setJoke(String joke) {
    this.joke = joke;
  }

  public void setInterstitialIsOn(Boolean status) {
    interstitialIsOn.set(status);
  }

  public void showProgress(boolean visibility) {
    if (visibility) {
      buttonContainer.setVisibility(View.INVISIBLE);
      progressIndicator.setVisibility(View.VISIBLE);
    } else {
      buttonContainer.setVisibility(View.VISIBLE);
      progressIndicator.setVisibility(View.INVISIBLE);
    }
  }

  private static class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {

    private final WeakReference<MainActivityFragment> mainActivityFragment;
    private SimpleIdlingResource idlingResource;
    private String applicationName;
    private JokesApi jokesApiService;

    EndpointsAsyncTask(MainActivityFragment mainActivityFragment) {
      this.mainActivityFragment = new WeakReference<>(mainActivityFragment);
      MainActivity mainActivity = (MainActivity) mainActivityFragment.requireActivity();
      idlingResource = mainActivity.getIdlingResource();
      applicationName = mainActivity.getString(R.string.app_name);
    }

    @Override
    protected void onPreExecute() {
      idlingResource.setIdleState(false);
      mainActivityFragment.get().showProgress(true);
      if (mainActivityFragment.get().interstitialAd.isLoaded()) {
        mainActivityFragment.get().setInterstitialIsOn(true);
        mainActivityFragment.get().interstitialAd.show();
      }
    }

    @Override
    protected String doInBackground(Void... voids) {
      if (jokesApiService == null) {
        jokesApiService = new JokesApi.Builder(AndroidHttp.newCompatibleTransport(),
            new AndroidJsonFactory(), null)
            .setApplicationName(applicationName)
            .setRootUrl("http://10.0.2.2:8080/_ah/api/")
            .setGoogleClientRequestInitializer(abstractGoogleClientRequest ->
                abstractGoogleClientRequest.setDisableGZipContent(true))
            .build();
      }
      try {
        return jokesApiService.provideJoke().execute().getJoke();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String s) {
      if (mainActivityFragment.get() != null) {
        if (!TextUtils.isEmpty(s)) {
          mainActivityFragment.get().setJoke(s);
          mainActivityFragment.get().tellJoke();
        } else {
          Context context = mainActivityFragment.get().getContext();
          Toast.makeText(context, R.string.not_responding, Toast.LENGTH_LONG).show();
        }
        mainActivityFragment.get().showProgress(false);
      }
      idlingResource.setIdleState(true);
    }
  }
}
