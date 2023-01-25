/*
    This program computes letter and word frequencies
    on a subset of documents in the Gutenberg corpus
    Author: Evan Brown
    Date Created: 02 Jan 2021
    Date Last Modified: 09 Jan 2021
*/

// represents files and directory pathnames 
// in an abstract manner
import java.io.File;

// reads data from files as streams of characters
import java.io.FileReader;

// reads text efficiently from character-input
// stream buffers 
import java.io.BufferedReader;

// for writing data to files
import java.io.PrintWriter;

// signals that an input/output (I/O) exception 
// of some kind has occurred
import java.io.IOException;

// compiled representation of a regular expressions
import java.util.regex.Pattern;

// matches a compiled regular expression with an input string
import java.util.regex.Matcher;

import java.util.*;


public class Ngrams {

    // no more than this many input files needs to be processed
    final static int MAX_NUMBER_OF_INPUT_FILES = 100;

    // an array to hold Gutenberg corpus file names
    static String[] inputFileNames = new String[MAX_NUMBER_OF_INPUT_FILES];

    static int fileCount = 0;


    // loads all files names in the directory subtree into an array
    // violates good programming practice by accessing a global variable (inputFileNames)
    public static void listFilesInPath(final File path) {
        for (final File fileEntry : path.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesInPath(fileEntry);
            } 
            else if (fileEntry.getName().endsWith((".txt")))  {
                inputFileNames[fileCount++] = fileEntry.getPath();
                // fileNameListWriter.println(fileEntry.getPath());
                // System.out.println(fileEntry.getName());
                // System.out.println(fileEntry.getAbsolutePath());
                // System.out.println(fileEntry.getCanonicalPath());
            }
        }
    }

    // returns index of a character in the alphabet 
    // uses zero-based indexing
    public static int getLetterValue(char letter) {
        return (int) Character.toUpperCase(letter) - 65;
    }
	
	public static void main(String[] args){

        // did the user provide correct number of command line arguments?
        // if not, print message and exit
        if (args.length != 3){
            System.err.println("Number of command line arguments must be 3");
            System.err.println("You have given " + args.length + " command line arguments");
            System.err.println("Incorrect usage. Program terminated");
            System.err.println("Correct usage: java Ngrams <path-to-input-files> <outfile-for-words> <outfile-for-char-counts");
            System.exit(1);
        }

        // extract input file name from command line arguments
        // this is the name of the file from the Gutenberg corpus
        String inputFileDirName = args[0];
        System.out.println("Input files directory path name is: " + inputFileDirName);

        // collects file names and write them to 
        listFilesInPath(new File (inputFileDirName));

        // System.out.println("Number of Gutenberg corpus files: " + fileCount);

        // br for efficiently reading characters from an input stream
        BufferedReader br = null;

        // wdWriter for writing extracted words to an output file
        PrintWriter wdWriter = null;
        
        // writers for each file to not have to open and close to reopen a new filename
        PrintWriter wdWriter2 = null; 
        PrintWriter wdWriter3 = null; 
        PrintWriter wdWriter4 = null; 

        // ccWriter for writing characters and their occurrence 
        // counts to an output file
        PrintWriter ccWriter = null;

        // wordPattern specifies pattern for words using a regular expression
        Pattern wordPattern = Pattern.compile("[a-zA-Z]+");

        // wordMatcher finds words by spotting word word patterns with input
        Matcher wordMatcher;

        // a line read from file
        String line;

        // an extracted word from a line
        String word;

        // letter characters
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        // open output file for writing words
        try {
            wdWriter = new PrintWriter(args[1], "UTF-8");
            wdWriter2 = new PrintWriter("wordFreq.txt");
            wdWriter3 = new PrintWriter("bigramFreq.txt");
            wdWriter4 = new PrintWriter("trigramFreq.txt");
            System.out.println(args[1] + " successfully opened for writing words");
        }
        catch (IOException ex){
            System.err.println("Unable to open " + args[1] + " for writing words");
            System.err.println("Program terminated\n");
            System.exit(1);
        }

        // array to keep track of character occurrence counts
        int[] charCountArray = new int[26];

        // initialization
        // lists to hold the scraped text
        TreeMap<String, Integer> uniFreq = new TreeMap<>();
        TreeMap<String, Integer> biFreq = new TreeMap<>();
        TreeMap<String, Integer> triFreq = new TreeMap<>();
        
        // word holders to check bi and tri grams
        String hTerm1 = "";
        String hTerm2 = "";
        String bGram = "";
        String tGram = "";
        int x = 0;
        
        // initialize character counts
        for (int index = 0; index < charCountArray.length; index++){
            charCountArray[index] = 0;
        }

        // process one file at a time
        for (int index = 0; index < fileCount; index++){

            // open the input file, read one line at a time, extract words
            // in the line, extract characters in a word, write words and
            // character counts to disk files
            try {
                // get a BufferedReader object, which encapsulates
                // access to a (disk) file
                br = new BufferedReader(new FileReader(inputFileNames[index]));

                // as long as we have more lines to process, read a line
                // the following line is doing two things: makes an assignment
                // and serves as a boolean expression for while test
                while ((line = br.readLine()) != null) {
                    // process the line by extracting words using the wordPattern
                    wordMatcher = wordPattern.matcher(line);

                    // process one word at a time
                    while ( wordMatcher.find() ) {
                        // extract the word
                        word = line.substring(wordMatcher.start(), wordMatcher.end());
                        // System.out.println(word);

                        // // convert the word to lowercase, and write to word file
                        word = word.toLowerCase();
                        wdWriter.println(word);
                        
                        // process characters in a word
                        for (int i = 0; i < word.length(); i++){
                            // System.out.println("word.charAt(i) " + word.charAt(i));
                            // System.out.println("alphabet.indexOf(word.charAt(i)) " + alphabet.indexOf(word.charAt(i)));
                            
                            // if the character is a letter, increment the 
                            // corresponding count, otherwise discard the character
                            if (Character.isLetter(word.charAt(i))) {
                                charCountArray[alphabet.indexOf(word.charAt(i))]++;
                            }
                        } // for
                        
                        
                        // Unigram frequencies list and counter
                        if (uniFreq.get(word) != null) {
                        	Integer j = uniFreq.get(word) + 1;
                        	uniFreq.put(word, j);
                        }
                        else {
                        	uniFreq.put(word, 1);
                        	// System.out.println("Created new word in list: " + word);
                        }
                        
                        // bigram frequencies list and counter
                        if(x >= 1) {
                        	bGram = hTerm1.concat(" " + word);
                        	if ((biFreq.get(bGram)) != null) {
                        		Integer j = biFreq.get(bGram) + 1;
                            	biFreq.put(bGram, j);
                        	}
                        	else {
                        		biFreq.put(bGram, 1);
                        	}
                        }
                        
                        // trigram frequencies list and counter
                        if(x >= 2) {
                            tGram = hTerm2.concat(" " + bGram);
                        	if (triFreq.get(tGram) != null) {
                            	Integer j = triFreq.get(tGram) + 1;
                               	triFreq.put(tGram, j);
                            }
                            else {
                            	triFreq.put(tGram, 1);
                            }
                        }
                        
                        // hold terms to look back when making bi / tri grams
                        hTerm2 = hTerm1;
                        hTerm1 = word;
                        x++;
                        
                    } // while - wordMatcher
                } // while - line
            } // try
            catch (IOException ex) {
                System.err.println("File " + inputFileNames[index] + " not found. Program terminated.\n");
                System.exit(1);
            }
        } // for -- process one file at a time

        // write letters and their counts to file named args[2]
        // open output file 2 for writing characters and their counts
        try {
            ccWriter = new PrintWriter(args[2], "UTF-8");
            System.out.println(args[2] + " successfully opened for writing character counts");
        }
        catch (IOException ex){
            System.err.println("Unable to open " + args[2] + " for writing character counts");
            System.err.println("Program terminated\n");
            System.exit(1);
        }

        for (int index = 0; index < charCountArray.length; index++){
            ccWriter.println(alphabet.charAt(index) + "\t" + charCountArray[index]);
        }
        
        // writing the n-grams to their respective file 
        for (Map.Entry m:uniFreq.entrySet()) {
        	wdWriter2.printf("%-20s", m.getKey());
            wdWriter2.printf("%s %n", m.getValue());
        }
        
        for (Map.Entry m:biFreq.entrySet()) {
        	wdWriter3.printf("%-25s", m.getKey());
            wdWriter3.printf("%s %n", m.getValue());
        }
        
        for(Map.Entry m:triFreq.entrySet()) {
            wdWriter4.printf("%-35s", m.getKey());
            wdWriter4.printf("%s %n", m.getValue()); 
        }
        
        // System.out.println("Writting end");
        
        // close buffered reader. gives error
        // needs a try ... catch block
        // br.close();

        // close output file 1
        wdWriter.close();
        wdWriter2.close();
        wdWriter3.close();
        wdWriter4.close();
        
        // close output file 2
        ccWriter.close();
        
	} // main()
} // class