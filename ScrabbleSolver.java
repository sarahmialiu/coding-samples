import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class ScrabbleSolver {
    private DictionaryHashTable dictionary;
    private int[] pointValues;

    public ScrabbleSolver() throws FileNotFoundException {
        dictionary = new DictionaryHashTable();
        pointValues = new int[]{1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10, 0};

        System.out.println("Welcome to Scrabble Solver!");
        load("dictionaries/most_common.txt");

        printSuggestions(suggest(getInput()));
    }

    public void load(String file) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(file));
        while (scan.hasNext()) dictionary.addWord(scan.next().toLowerCase());
    }

    //Used to collect given letters from user input, returns the letters as a String
    public String getInput() {
        LinkedList<Character> letters = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        Scanner scan = new Scanner(System.in);
        boolean hasNext = true;

        //Keeps prompting user input until specified to stop
        while (hasNext){
            System.out.println("Enter a LETTER, or type 'NO' when finished. To enter a BLANK TILE, type 'BLANK.'");
            System.out.println("Two BLANK TILES maximum.");
            String next = scan.next();
            char ch = next.toUpperCase().charAt(0);

            if (next.equals("NO") || next.equals("No") || next.equals("no")) hasNext = false;
            else if (next.equals("BLANK") || next.equals("Blank") || next.equals("blank")) letters.add(' ');
            else if (next.length() == 1 && ((int) ch < 91 && (int) ch > 64)) letters.add(ch);
            else System.out.println("Invalid input, please enter a single letter");
        }

        while (!letters.isEmpty()) sb.append(letters.remove());
        return sb.toString();
    }

    //Given a String of given letters as input, returns a Linked List of viable words
    public LinkedList<String> suggest(String input){
        LinkedList<String> suggestions = new LinkedList<>();
        int[] inputLetters = countLetters(input);

        //If there is a blank tile, this method executes "suggestBlank" instead
        if (inputLetters[26] != 0) return suggestBlank(input);

        //For each letter in the input, iterates through the Linked List associated with that letter in "dictionary"
        for (int i = 0; i < 26; i++){
            if (inputLetters[i] != 0){
                StringLinkedList letterWords = dictionary.getLetterWords(i);

                //Iterates through each word in the starting letter's Linked List
                while (letterWords.hasNext()){
                    String currentWord = letterWords.head.data;
                    int[] dictionaryLetters = countLetters(currentWord);
                    boolean viable = true;

                    //If the dictionary letter tallies are all less than or equal to the inputted letter tallies,
                    //the word is "viable" and is added to a Linked List of suggestions
                    for (int j = 0; j < dictionaryLetters.length; j++){
                        if (dictionaryLetters[j] > inputLetters[j]) viable = false;
                    }

                    if (viable) suggestions.add(currentWord);
                    letterWords.head = letterWords.head.next;
                }
            }
        }

        return suggestions;
    }

    //Called if there is a blank tile present in the input
    public LinkedList<String> suggestBlank(String input) {
        LinkedList<String> suggestions = new LinkedList<>();
        int[] inputLetters = countLetters(input);

        //Runs if there is 1 blank tile
        if (inputLetters[26] == 1) {
            //For each letter in the input, iterates through the Linked List associated with that letter in "dictionary"
            for (int i = 0; i < 26; i++){
                StringLinkedList letterWords = dictionary.getLetterWords(i);

                //Iterates through each word in the starting letter's Linked List
                while (letterWords.hasNext()) {
                    String currentWord = letterWords.head.data;
                    int[] dictionaryLetters = countLetters(currentWord);
                    boolean viable = true;
                    boolean usedBlank = false;

                    //Checks if the dictionary letter tallies are less, with a cushion of a blank tile
                    //which can be any one letter
                    for (int j = 0; j < dictionaryLetters.length; j++) {
                        if (dictionaryLetters[j] > inputLetters[j] && usedBlank) viable = false;
                        else if (dictionaryLetters[j] == inputLetters[j] + 1) usedBlank = true;
                        else if (dictionaryLetters[j] > inputLetters[j]) viable = false;
                    }

                    if (viable) suggestions.add(currentWord);
                    letterWords.head = letterWords.head.next;
                }
            }
        }
        //Runs if there are 2 blank tiles
        else if (inputLetters[26] == 2) {
            //For each letter in the input, iterates through the Linked List associated with that letter in "dictionary"
            for (int i = 0; i < 26; i++){
                StringLinkedList letterWords = dictionary.getLetterWords(i);

                //Iterates through each word in the starting letter's Linked List
                while (letterWords.hasNext()) {
                    String currentWord = letterWords.head.data;
                    int[] dictionaryLetters = countLetters(currentWord);
                    boolean viable = true;
                    boolean usedBlank1 = false;
                    boolean usedBlank2 = false;

                    //Checks if the dictionary letter tallies are less, with a cushion of two blank tiles
                    //which can be any two letters
                    for (int j = 0; j < dictionaryLetters.length; j++) {
                        if (dictionaryLetters[j] > inputLetters[j]) {
                            if (usedBlank1 && usedBlank2) viable = false;
                            else if (dictionaryLetters[j] == inputLetters[j] + 2 && usedBlank1) viable = false;
                            else if (dictionaryLetters[j] == inputLetters[j] + 2){
                                usedBlank1 = true;
                                usedBlank2 = true;
                            } else if (dictionaryLetters[j] == inputLetters[j] + 1 && usedBlank1) usedBlank2 = true;
                            else if (dictionaryLetters[j] == inputLetters[j] + 1) usedBlank1 = true;
                            else viable = false;
                        }
                    }

                    if (viable) suggestions.add(currentWord);
                    letterWords.head = letterWords.head.next;
                }
            }
        }

        return suggestions;
    }

    //Given a String, calculates and returns the point value
    public int calculateWordValue(String word){
        int totalValue = 0;
        char[] letters = word.toUpperCase().toCharArray();

        for (char letter : letters) {
            if ((int) letter != 32) totalValue += pointValues[(int) letter - 65];
        }

        return totalValue;
    }

    public void printSuggestions(LinkedList<String> suggestedWords){
        System.out.println("Here are the viable words for your letters: ");
        for (String word : suggestedWords){
            System.out.println(word + ": " + calculateWordValue(word) + " points");
        }
    }

    //Given a String, tallies the number of each letter and returns the tallies and in int[]
    public int[] countLetters(String str){
        int[] letterCounts = new int[27];
        char[] letters = str.toUpperCase().toCharArray();

        for (char letter : letters){
            if ((int) letter == 32) letterCounts[26]++;
            else letterCounts[(int) letter - 65]++;
        }

        return letterCounts;
    }

    public static void main(String[] args) throws FileNotFoundException {
        new ScrabbleSolver();
    }
}
