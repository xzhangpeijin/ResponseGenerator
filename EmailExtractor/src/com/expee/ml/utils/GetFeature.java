/*
Input: Email message
Output:
A) Bag of words, ordered by index in the vector
B) For each email, outputs a vector of
1. Length of email (bytes)
2. Length of email (words)
3. Number of '?'
4. Number of questioning words (like "Who Why How When Where")
5. Number of "formal" words. (like "Sir", "Yours sincerely")
6. Bag of words, listed in order of the bag of words given.
7. Number of replies this email has
Other things we can do:
Metadata features:
1. If replied to sender earlier
2. If sender sent email but user ignored.
3. Sender email address. Internal email, external, from .com?
4. Many people in CC? Blasted email
*/
package com.expee.ml.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
public class GetFeature {
  private static final int MIN_BOW_PERCENT = 1;
  private static final int MAX_BOW_PERCENT = 50;
  private static final int MIN_THEME_PERCENT = 1;
  private static final int MAX_THEME_PERCENT = 10;
  private static final int MIN_COUNT = 200;
  private static final int MINWORDLEN = 2;
  private static final Set<String> QUESTION_SET = new HashSet<String>(Arrays.asList(
      "Could", "Would", "Who", "When", "Where", "What", 
      "Why", "How", "Is", "Are", "Will", "May", "Might"));
  private static final Set<String> FORMAL_SET = new HashSet<String>(Arrays.asList(
      "Yours", "Sincerely", "Sir", "Regards", "Madam"));
  private static final Set<String> MEETING_SET = new HashSet<String>(Arrays.asList(
      "reminder", "meeting", "location", "date", "time"));
  private static final Set<String> REPLY_SET = new HashSet<String>(Arrays.asList(
      "reply", "rsvp", "respond", "response", "acknowledge", "email"));
  private static final String[] TRIGGER_PHRASE_ARRAY = {
      "follow up", "let me know", "let us know", "feel free", "help us", "get back"};
  private static final Set<String> STOPWORDS_SET = new HashSet<String>(Arrays.asList(
  "xbcc","textplain","your","xfilename","charsetusascii","xfrom","xorigin","time","xfolder","email","contenttransferencoding","contenttype","nonprivilegedpst","mimeversion","a", "able", "about", "above", "abst", "accordance", "according", "accordingly", "across", "act", "actually", "added", "adj", "affected", "affecting", "affects", "after", "afterwards", "again", "against", "ah", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "announce", "another", "any", "anybody", "anyhow", "anymore", "anyone", "anything", "anyway", "anyways", "anywhere", "apparently", "approximately", "are", "aren", "arent", "arise", "around", "as", "aside", "ask", "asking", "at", "auth", "available", "away", "awfully", "b", "back", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "begin", "beginning", "beginnings", "begins", "behind", "being", "believe", "below", "beside", "besides", "between", "beyond", "biol", "both", "brief", "briefly", "but", "by", "c", "ca", "came", "can", "cannot", "cant", "cause", "causes", "certain", "certainly", "co", "com", "come", "comes", "contain", "containing", "contains", "could", "couldnt", "d", "date", "did", "didnt", "different", "do", "does", "doesnt", "doing", "done", "dont", "down", "downwards", "due", "during", "e", "each", "ed", "edu", "effect", "eg", "eight", "eighty", "either", "else", "elsewhere", "end", "ending", "enough", "especially", "et", "et-al", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "except", "f", "far", "few", "ff", "fifth", "first", "five", "fix", "followed", "following", "follows", "for", "former", "formerly", "forth", "found", "four", "from", "further", "furthermore", "g", "gave", "get", "gets", "getting", "give", "given", "gives", "giving", "go", "goes", "gone", "got", "gotten", "h", "had", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hed", "hence", "her", "here", "hereafter", "hereby", "herein", "heres", "hereupon", "hers", "herself", "hes", "hi", "hid", "him", "himself", "his", "hither", "home", "how", "howbeit", "however", "hundred", "i", "id", "ie", "if", "ill", "im", "immediate", "immediately", "importance", "important", "in", "inc", "indeed", "index", "information", "instead", "into", "invention", "inward", "is", "isnt", "it", "itd", "itll", "its", "itself", "ive", "j", "just", "k", "keep", "keeps", "kept", "kg", "km", "know", "known", "knows", "l", "largely", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "line", "little", "ll", "look", "looking", "looks", "ltd", "m", "made", "mainly", "make", "makes", "many", "may", "maybe", "me", "mean", "means", "meantime", "meanwhile", "merely", "mg", "might", "million", "miss", "ml", "more", "moreover", "most", "mostly", "mr", "mrs", "much", "mug", "must", "my", "myself", "n", "na", "name", "namely", "nay", "nd", "near", "nearly", "necessarily", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "ninety", "no", "nobody", "non", "none", "nonetheless", "noone", "nor", "normally", "nos", "not", "noted", "nothing", "now", "nowhere", "o", "obtain", "obtained", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "omitted", "on", "once", "one", "ones", "only", "onto", "or", "ord", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "owing", "own", "p", "page", "pages", "part", "particular", "particularly", "past", "per", "perhaps", "placed", "please", "plus", "poorly", "possible", "possibly", "potentially", "pp", "predominantly", "present", "previously", "primarily", "probably", "promptly", "proud", "provides", "put", "q", "que", "quickly", "quite", "qv", "r", "ran", "rather", "rd", "re", "readily", "really", "recent", "recently", "ref", "refs", "regarding", "regardless", "regards", "related", "relatively", "research", "respectively", "resulted", "resulting", "results", "right", "run", "s", "said", "same", "saw", "say", "saying", "says", "sec", "section", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sent", "seven", "several", "shall", "she", "shed", "shell", "shes", "should", "shouldnt", "show", "showed", "shown", "showns", "shows", "significant", "significantly", "similar", "similarly", "since", "six", "slightly", "so", "some", "somebody", "somehow", "someone", "somethan", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specifically", "specified", "specify", "specifying", "still", "stop", "strongly", "sub", "substantially", "successfully", "such", "sufficiently", "suggest", "sup", "sure","which","while","andor","making","within","turn","want","these","there","unless","week","their","long","amount","include","very","today","well","were","without","days","currently","full","they","them","then","includes","than","feel","dear","what","when","who","why","where","how","thanks","with","that","would","those","through"));
  private static final Set<String> SPAM_SET = new HashSet<String>(Arrays.asList(
      "free", "Ad", "$", "$$", "$$$", "gift", "mortgage", "save", "aging", "marketing", "credit", "refund", "sample", "trial", "ad", "viagra"));
  public static void makeEmailSetFeatures(Set<Email> emails, String output, String testOutput) throws IOException {
    PrintWriter writer = new PrintWriter(new File(output));
    PrintWriter writerTest = new PrintWriter(new File(testOutput));
    
    Map<String, Integer> wordEmailCount = new HashMap<String, Integer>();
    int numEmailWhichAreChild = 0;
    for (Email email: emails) {
      if (!email.getIsChild()) {
        continue;
      }
      numEmailWhichAreChild++;
      String text = email.getText();
      if (text == null) continue;
      String[] wordArray = text.split("\\s");
      Set<String> wordSet = new HashSet<String>();
      for (String word : wordArray) {
        String strippedLowerWord = word.replaceAll("[^a-zA-Z]","").toLowerCase();
        if (strippedLowerWord.length() > MINWORDLEN) {
          wordSet.add(strippedLowerWord);
        }
      }
      for (String word : wordSet) {
        if (wordEmailCount.containsKey(word)) {
          wordEmailCount.put(word, wordEmailCount.get(word)+1);
        } else {
          wordEmailCount.put(word, 1);
        }
      }
    }

    System.out.println("Done making count map for subjects");
    String header = "";
    header += "Byte Length,Word Length,Num Question,Num Question Words,Num Formal Words,";
    header += "Num Paragraphs,Paragraph Density,Num Recipients,Is Sender Enron,";
    header += "Num Meeting Words,Num Replyrelated words, Num Spam words, Num Trigger Phrases, NumXto, Number times recipient mentioned, ";

    int minThemeCount = (numEmailWhichAreChild * MIN_THEME_PERCENT) / 100;
    int maxThemeCount = (numEmailWhichAreChild * MAX_THEME_PERCENT) / 100;
    System.out.println(maxThemeCount);
    List<String> themeList = new ArrayList<String>();
    int idx = 0;
    Map<String, Integer> themeMap = new HashMap<String, Integer>();
    for (Entry<String, Integer> entry : wordEmailCount.entrySet()) {
      if (entry.getValue() >= minThemeCount && entry.getValue() <= maxThemeCount) {
        String word = entry.getKey();
        // System.out.println(word);
        if (!(STOPWORDS_SET.contains(word)) && word.length() > 3) {
          themeList.add(word);
          // header += ("(Theme)" + word + ",");
          themeMap.put(word, idx);
          idx++;
        }
      }
    }

    int minBowCount = (numEmailWhichAreChild * MIN_BOW_PERCENT) / 100;
    int maxBowCount = (numEmailWhichAreChild * MAX_BOW_PERCENT) / 100;
    System.out.println(maxBowCount);
    List<String> bowList = new ArrayList<String>();
    idx = 0;
    Map<String, Integer> bowMap = new HashMap<String, Integer>();
    for (Entry<String, Integer> entry : wordEmailCount.entrySet()) {
      if (entry.getValue() >= minBowCount && entry.getValue() <= maxBowCount) {
        String word = entry.getKey();
        // System.out.println(word);
        if (!(STOPWORDS_SET.contains(word))) {
          bowList.add(word);
          header += ("(BOW) " + word + ",");
          bowMap.put(word, idx);
          idx++;
        }
      }
    }
    System.out.println(themeMap.size() + " " + bowMap.size());

    header += ("Has Reply, Num Replies, Word Length of Reply");
    for (String word : themeList) {
      header += (",(Reply theme) " + word);
    }
    // System.out.println(header);
    writer.println(header);
    writerTest.println(header);
    for (Email email : emails) {
      GetFeature.preprocessEmail(themeMap, bowMap, email);
    }
    for (Email email : emails) {
      GetFeature.printEmailFeatures(writer, email, true);
    }
    writer.flush();
    writer.close();
    for (Email email : emails) {
      GetFeature.printEmailFeatures(writerTest, email, false);
    }
    writerTest.flush();
    writerTest.close();
  }

  public static void preprocessEmail(
    Map<String, Integer> themeMap, Map<String, Integer> bowMap, Email email)  throws IOException {
    int themeMapSize = themeMap.size();
    int[] themes = email.getThemes();
    themes = new int[themeMapSize];
    for (int i = 0; i < themeMapSize; i++) {
      themes[i] = 0;
    }

    String text = email.getText();
    if (text != null) {
      String[] textWordArray = text.split("\\s");
      for (String word : textWordArray) {
        String strippedLowerWord = word.replaceAll("[^a-zA-Z]","").toLowerCase();
        if (themeMap.containsKey(strippedLowerWord)) {
          themes[themeMap.get(strippedLowerWord)]++;
        }
      }
    }
    email.setThemes(themes);
    int numWords = email.getText().split("\\s").length;
    email.setWordCount(numWords);

    int bowMapSize = bowMap.size();
    int[] bows = email.getBow();
    bows = new int[bowMapSize];
    for (int i = 0; i < bowMapSize; i++) {
      bows[i] = 0;
    }

    text = email.getText();
    if (text != null) {
      String[] textWordArray = text.split("\\s");
      for (String word : textWordArray) {
        String strippedLowerWord = word.replaceAll("[^a-zA-Z]","").toLowerCase();
        if (bowMap.containsKey(strippedLowerWord)) {
          bows[bowMap.get(strippedLowerWord)]++;
        }
      }
    }
    email.setBow(bows);
  }
  
  public static boolean purge(long uid, boolean isTrain) {
    if (isTrain) {
      return (uid%5 == 0);
    }
    else {
      return (uid%5 != 0);
    }
  }

  public static void printEmailFeatures(
      PrintWriter writer, Email email, boolean is_training)  throws IOException {
    if (purge(email.getuid(), is_training)) {
      return;
    }
    String to = email.getTo();
    int numRecipients = 1;
    if (to != null) {
      numRecipients = to.split("\\w@\\w").length - 1;
    }

    String xto = email.getXTo();
    String[] xtoArray = xto.split("\\s+");
    Set<String> toNamesSet = new HashSet<String>();
    for (String s : xtoArray) {
      if (s.length() > 1) {
        toNamesSet.add(s.toLowerCase().replaceAll("[^a-zA-Z]", ""));
      }
    }
    int numXto = toNamesSet.size();

    int isEnron = 0;
    String sender = email.getSender();
    if (sender != null && sender.toLowerCase().contains("@enron")) {
      isEnron = 1;
    }
    String msg = email.getText();
    String[] paragraphArray = msg.split("\n\\w");
    int numParagraphs = paragraphArray.length;
    int paragraphDensity = 0;
    for (String paragraph: paragraphArray) {
      int numWordsInParagraph = paragraph.split("\\s").length;
      paragraphDensity += numWordsInParagraph;
    }
    if (numParagraphs > 0) {
      paragraphDensity /= numParagraphs;
    }

    String[] wordArray = msg.split("\\s");
    
    int numWords = wordArray.length;
    email.setWordCount(numWords);
    int numQuestionMarks = 0;
    for (int i = 0; i < msg.length(); i++) {
      if (msg.charAt(i) == '?') {
        numQuestionMarks++;
      }
    }
    
    int numQuestionWords = 0;
    int numFormalWords = 0;
    int numMeetingWords = 0;
    int numReplyWords = 0;
    int numSpamWords = 0;
    int numToNameWords = 0;
    
    for (String word : wordArray) {
      String strippedWord = word.replaceAll("[^\\w]","");
      String strippedLowerWord = strippedWord.toLowerCase();
      
      if (strippedLowerWord.length() == 0) continue;
      // Preserve upper case to differentiate "Is...?" "Are you...?" from "...is..."
      if (QUESTION_SET.contains(strippedWord)) {
        numQuestionWords++;
      }
      // Preserve upper case to differentiate "Yours (truly)" from "(this is) yours"
      if (FORMAL_SET.contains(strippedWord)) {
        numFormalWords++;
      }
      if (MEETING_SET.contains(strippedLowerWord)) {
        numMeetingWords++;
      }
      if (REPLY_SET.contains(strippedLowerWord)) {
        numReplyWords++;
      }
      if (SPAM_SET.contains(strippedLowerWord)) {
        numSpamWords++;
      }
      if (toNamesSet.contains(strippedLowerWord)) {
        numToNameWords++;
      }
    }
    String msglower = msg.toLowerCase();
    int numPhrases = 0;
    for (String phrase : TRIGGER_PHRASE_ARRAY) {
      if (msglower.contains(phrase)) {
        numPhrases++;
      }
    }
    Set<Email> children = email.getChildren();
    for (Iterator<Email> iterator = children.iterator(); iterator.hasNext();) {
        Email emailc = iterator.next();
        if ((purge(emailc.getuid(), is_training))) {
            // Remove the current element from the iterator and the list.
            iterator.remove();
        }
    }
    int numChildren = children.size();
    int averageChildrenSize = 0;
    if (numChildren > 0) {
      for (Email child: children) {
        int numWordsInChild = child.getWordCount();
        averageChildrenSize += numWordsInChild;
      }
      averageChildrenSize /= numChildren;
    }
    // Now compute themes
    int[] themes = email.getThemes();
    int len = themes.length;
    int[] childrenThemes = new int[themes.length];
    if (numChildren > 0) {
      for (Email child: children) {
        int[] childThemes = child.getThemes();
        for (int i = 0; i < childrenThemes.length; i++) {
          childrenThemes[i] += childThemes[i];
        }
      }
    }

    //Message length (bytes)
    writer.print(Math.log(msg.length()+1) + ",");
    // Message length (words)
    writer.print(Math.log(numWords+1) + ",");
    // Number of question marks
    writer.print(Math.log(numQuestionMarks+1) + ",");
    // Number of formal words:
    writer.print(Math.log(numFormalWords+1) + ",");
    // Number of interrogative words
    writer.print(Math.log(numQuestionWords+1) + ",");
    // Number of paragraphs
    writer.print(Math.log(numParagraphs+1) + ",");
    // Paragraph density
    writer.print(Math.log(paragraphDensity+1) + ",");
    // Number of recipients
    writer.print(Math.log(numRecipients+1) + ",");
    // Is sender domain from enron.com?
    writer.print(isEnron + ",");
    // Meeting words (number)
    writer.print(Math.log(numMeetingWords+1) + ",");
    // Reply-related words
    writer.print(Math.log(numReplyWords+1) + ",");
    // Spam-related words
    writer.print(Math.log(numSpamWords+1) + ",");
    // Number of trigger phrases
    writer.print(Math.log(numPhrases+1) + ",");
    // Num X-To recipients
    writer.print(Math.log(numXto+1) + ",");
    // Number of words in email that match recipient list
    writer.print(Math.log(numToNameWords+1) + ",");
    // // Bag of words for themes
    // for (int i = 0; i < themes.length; i++) {
    //   writer.print((themes[i]>0) + ",");
    // }
    // Bag of words for words
    int[] bows = email.getBow();
    for (int i = 0; i < bows.length; i++) {
      writer.print((bows[i]>0) + ",");
    }
    //Did the email have a reply
    writer.print((numChildren>0) + ",");
    // Number of replies this email has
    writer.print(numChildren + ",");
    // Average children size
    writer.print(averageChildrenSize);
    // Children theme words. Average number of words per child.
    if (numChildren > 0) {
      for (int i = 0; i < childrenThemes.length; i++) {
        writer.print("," + (childrenThemes[i]>0));
      }
    }
    else {
      for (int i = 0; i < childrenThemes.length; i++) {
        writer.print("," + "NO_CHILD");
      }
    }
    writer.println("");
  }
}
