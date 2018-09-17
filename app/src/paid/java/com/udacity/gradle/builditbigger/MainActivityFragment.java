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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.udacity.gradle.builditbigger.backend.jokesApi.JokesApi;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivityFragment extends Fragment {

  private ViewGroup buttonContainer;
  private View progressIndicator;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_main, container, false);

    buttonContainer = root.findViewById(R.id.button_container);
    progressIndicator = root.findViewById(R.id.progress_indicator);

    Button jokeButton = root.findViewById(R.id.tell_joke_button);
    jokeButton.setOnClickListener(v -> new EndpointsAsyncTask(this).execute());

    return root;
  }

  public void tellJoke(String joke) {
    Intent intent = new Intent(getActivity(), DisplayJokeActivity.class);
    intent.putExtra(DisplayJokeActivity.JOKE_ID, joke);
    startActivity(intent);
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
    private JokesApi jokesApiService;
    private String applicationName;

    EndpointsAsyncTask(MainActivityFragment mainActivityFragment) {
      this.mainActivityFragment = new WeakReference<>(mainActivityFragment);
      MainActivity mainActivity = (MainActivity) mainActivityFragment.getActivity();
      if (mainActivity != null) {
        idlingResource = mainActivity.getIdlingResource();
        applicationName = mainActivity.getString(R.string.app_name);
      }
    }

    @Override
    protected void onPreExecute() {
      idlingResource.setIdleState(false);
      mainActivityFragment.get().showProgress(true);
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
          mainActivityFragment.get().tellJoke(s);
        } else {
          Context context = mainActivityFragment.get().getContext();
          Toast.makeText(context, "Backend is not responding", Toast.LENGTH_LONG).show();
        }
        mainActivityFragment.get().showProgress(false);
      }
      idlingResource.setIdleState(true);
    }
  }
}
