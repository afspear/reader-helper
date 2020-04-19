package com.spear;

import java.util.Arrays;
import java.util.List;

public class Punctuation {
  public static String fix(String input) {
    StringBuilder builder = new StringBuilder();
    String normalizedInput = normalizeInput(input);
    String[] splittedInput = normalizedInput.split(" ");

    for(int index = 0; index < splittedInput.length; index++) {
      String chunk = splittedInput[index];
      int punctuationIndex = indexOfContainedPunctuation(chunk);
      if(punctuationIndex == -1) {
        //There's nothing to do with this chunk
        builder.append(chunk + " ");
        continue;
      }

      switch(chunk.charAt(punctuationIndex)) {
        case '.':
        case ':':
        case '\'':
          fixForOthers(builder,chunk,isPunctuationAtBegin(punctuationIndex));
          break;
        case ',':
          fixForComma(builder,chunk,isPunctuationAtBegin(punctuationIndex));
          break;
      }
    }

    return builder.toString();
  }

  private static String normalizeInput(String input) {
    String newInput = input.trim();
    return newInput.replaceAll(" +", " ");
  }

  private static final List<Character> Punctuations = Arrays.asList('\'','.',':',',');
  private static int indexOfContainedPunctuation(String input) {
    if (Punctuations.contains(input.charAt(0))) {
      return 0;
    }
    else if(Punctuations.contains(input.charAt(input.length() - 1))) {
      return input.length() - 1;
    }
    return -1;
  }

  private static void fixForOthers(StringBuilder builder, String chunk, boolean punctuationAtBegin) {
    if(punctuationAtBegin) {
      builder.deleteCharAt(builder.length() - 1);
      builder.append(chunk + " ");
    } else {
      builder.append(chunk);
    }
  }

  private static void fixForComma(StringBuilder builder, String chunk, boolean punctuationAtBegin) {
    if(punctuationAtBegin) {
      builder.deleteCharAt(builder.length() - 1);
      builder.append(", " + chunk.substring(1) + " ");
    } else {
      builder.append(chunk + ", ");
    }
  }

  private static boolean isPunctuationAtBegin(int punctuationIndex){
    return punctuationIndex == 0;
  }
}
