package com.udacity.gradle.builditbigger;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleIdlingResource implements IdlingResource {

  @Nullable private volatile ResourceCallback callback;
  private AtomicBoolean isIdle = new AtomicBoolean(true);

  @Override
  public String getName() {
    return getClass().getName();
  }

  @Override
  public boolean isIdleNow() {
    return isIdle.get();
  }

  @Override
  public void registerIdleTransitionCallback(ResourceCallback callback) {
    this.callback = callback;
  }

  public void setIdleState(boolean isIdle) {
    this.isIdle.set(isIdle);
    final ResourceCallback callback = this.callback;
    if (isIdle && callback != null) {
      callback.onTransitionToIdle();
    }
  }
}
