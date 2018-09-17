package com.alexbaryzhikov.presenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayJokeActivity extends AppCompatActivity {

  public static final String JOKE_ID = "joke";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display_joke);

    Intent intent = getIntent();
    if (intent.hasExtra(JOKE_ID)) {
      ((TextView) findViewById(R.id.joke_text)).setText(intent.getStringExtra(JOKE_ID));
    }
  }
}
