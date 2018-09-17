package com.udacity.gradle.builditbigger.backend;

import com.alexbaryzhikov.jokeslib.JokeSmith;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

@Api(
    name = "jokesApi",
    version = "v1",
    namespace = @ApiNamespace(
        ownerDomain = "backend.builditbigger.gradle.udacity.com",
        ownerName = "backend.builditbigger.gradle.udacity.com"
    )
)
public class JokesEndpoint {

  @ApiMethod(name = "provideJoke")
  public Joke provideJoke() {
    return new Joke(JokeSmith.tellJoke());
  }
}
