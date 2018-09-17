package com.udacity.gradle.builditbigger;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.alexbaryzhikov.presenter.DisplayJokeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityFragmentTestPaid {

  @Rule
  public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

  private IdlingResource idlingResource;

  @Before
  public void registerIdlingResource() {
    idlingResource = intentsTestRule.getActivity().getIdlingResource();
    IdlingRegistry.getInstance().register(idlingResource);
  }

  @After
  public void unregisterIdlingResource() {
    if (idlingResource != null) {
      IdlingRegistry.getInstance().unregister(idlingResource);
    }
  }

  @Test
  public void jokeButtonPressedLaunchesPresenterActivity() {
    onView(withId(R.id.tell_joke_button)).perform(click());
    intended(allOf(
        hasComponent(DisplayJokeActivity.class.getName()),
        hasExtraWithKey(DisplayJokeActivity.JOKE_ID)
    ));
  }

  @Test
  public void asyncTaskFetchesAJoke() {
    onView(withId(R.id.tell_joke_button)).perform(click());
    onView(withId(R.id.joke_text)).check(matches(allOf(isDisplayed(), not(withText("")))));
  }
}
