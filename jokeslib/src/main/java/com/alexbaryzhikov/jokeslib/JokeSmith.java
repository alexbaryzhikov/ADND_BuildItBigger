package com.alexbaryzhikov.jokeslib;

import java.util.Random;

public class JokeSmith {

  private static final String[] jokes = new String[]{
      "I dreamed about drowning in an ocean made out of orange soda last night.\n" +
          "It took me a while to work out it was just a Fanta sea.",
      "I've decided Hershey's chocolate is too feminist for my taste.\n" +
          "I'm switching to HisHey's.",
      "What do you call a hippie's wife?\n" +
          "Mississippi.",
      "Do I enjoy making courthouse puns?\n" +
          "Guilty.",
      "What do you call a sketchy Italian neighborhood?\n" +
          "The spaghetto.",
      "I can't decide if I want to pursue a career as a writer or a grifter.\n" +
          "I'm still weighing the prose and cons."
  };

  public static String tellJoke() {
    int i = new Random().nextInt(jokes.length);
    return jokes[i];
  }
}
