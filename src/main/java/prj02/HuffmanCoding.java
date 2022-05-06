package prj02;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DecimalFormat;

import HashTable.*;
import List.*;
import SortedList.*;
import Tree.*;
import com.sun.source.doctree.SeeTree;
import utils.BinaryTreePrinter;
import utils.IntegerComparator;
import utils.StringComparator;


/**
 * The Huffman Encoding Algorithm
 *
 * This is a data compression algorithm designed by David A. Huffman and published in 1952
 *
 * What it does is it takes a string and by constructing a special binary tree with the frequencies of each character.
 * This tree generates special prefix codes that make the size of each string encoded a lot smaller, thus saving space.
 *
 * @author Fernando J. Bermudez Medina (Template)
 * @author A. ElSaid (Review)
 * @author Jonathan Rivera Chico <802-19-6401>(Implementation)
 * @version 2.0
 * @since 10/16/2021
 */
public class HuffmanCoding {

	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		String data = load_data("input1.txt"); //You can create other test input files and add them to the inputData Folder

		/*If input string is not empty we can encode the text using our algorithm*/
		if(!data.isEmpty()) {
			Map<String, Integer> fD = compute_fd(data);
			BTNode<Integer,String> huffmanRoot = huffman_tree(fD);
			Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
			String output = encode(encodedHuffman, data);
			process_results(fD, encodedHuffman,data,output);
			BinaryTreePrinter.print(huffmanRoot);
		} else {
			System.out.println("Input Data Is Empty! Try Again with a File that has data inside!");
		}

	}

	/**
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 *
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/*If input file is empty just return an empty string, if not just extract the data*/
			String extracted = in.readLine();
			if(extracted != null)
				line = extracted;

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	/**
	 * This method takes an string and created its frequency
	 * distribution mapping according on how the characters repeat
	 * in the string
	 *
	 * @param inputString - The string to find the frequency distribution
	 * @return Frequency distribution map created from the string
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		/* TODO Compute Symbol Frequency Distribution of each character inside input string */
		Map<String,Integer> FD = new HashTableSC<>( inputString.length() ,new SimpleHashFunction<>());
		for(int i = 0; i < inputString.length(); i++){ // Loop through every character in the string
			if(!FD.containsKey(Character.toString(inputString.charAt(i)))) FD.put(Character.toString(inputString.charAt(i)),1); //If the character does not exist in the map, put it with value 1
			else  FD.put(Character.toString(inputString.charAt(i)),FD.get(Character.toString(inputString.charAt(i)))+1);//If it does exist already, add 1 to the count
		}
		return FD;
	}


	/**
	 * Takes the frequency distribution and builds
	 * the huffman tree according to the values in the
	 * map, returning the root of the tree
	 *
	 * @param fD - Frequency disctribution map
	 * @return huffman tree root
	 */
	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> fD) {

		BTNode<Integer,String> rootNode = new BTNode<>();
		List<String> Klist = fD.getKeys(); // List of all the keys in the hash map
		SortedLinkedList<BTNode<Integer,String>> Slist = new SortedLinkedList<BTNode<Integer, String>>();

		//Loops through the list of keys and adds it into the sorted linked list
		for(String s:Klist){
			BTNode<Integer,String> newNode = new BTNode<>(); // creates the to be added node
			// sets the value and key
			newNode.setKey(fD.get(s));
			newNode.setValue(s);

			Slist.add(newNode); // adds it into the sorted linked list
		}

		/*
			This loop takes the first two elements and create a new node with
			both of their values combined and the two respective nodes as their
			children and deletes those two nodes from the list, it repeats
			these steps until there is just one element in the list
		 */
		while(Slist.size() > 1){
			BTNode<Integer,String> newNode = new BTNode<>();
			newNode.setKey(Slist.get(0).getKey() + Slist.get(1).getKey()); // Combines the key of the elements
			newNode.setValue(Slist.get(0).getValue() + Slist.get(1).getValue()); // Combines the values of the elements
			//Sets the right and left child of the combined parent node
			newNode.setRightChild(Slist.get(1));
			newNode.setLeftChild(Slist.get(0));
			// removes the elements
			Slist.remove(Slist.get(1));
			Slist.remove(Slist.get(0));
			Slist.add(newNode);
		}
		return Slist.get(0); // returns the only element in the list because its the root
	}

	/**
	 * This method creates a new map and uses the auxiliary method
	 * "huffman_code_help" by adding the list and an empty string.
	 *
	 * @param huffmanRoot
	 * @return the encoded huffman map
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> huffmanRoot) {
		Map<String,String> result = new HashTableSC<>(new SimpleHashFunction<>());
		return huffman_code_help(huffmanRoot,result,"");
	}

	/**
	 * Takes the encoded map and a desired input string
	 * and encodes said string by finding the symbol
	 * in the map and merging its values in a string
	 *
	 * @param encodingMap - Encoded huffman tree
	 * @param inputString - The string that has to be encoded
	 * @return encoded - The encoded string
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) {
		String encoded = "";
		//Loops through the input string taking each character and finding its encoded counterpart
		for(int i = 0 ; i < inputString.length(); i++){
			String symbol = Character.toString(inputString.charAt(i)); // Finds the character in the encoded map
			encoded += encodingMap.get(symbol); // adds it into the result string
		}
		return encoded;
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable, the input string,
	 * and the output string, and prints the results to the screen (per specifications).
	 *
	 * Output Includes: symbol, frequency and code.
	 * Also includes how many bits has the original and encoded string, plus how much space was saved using this encoding algorithm
	 *
	 * @param fD Frequency Distribution of all the characters in input string
	 * @param encodedHuffman Prefix Code Map
	 * @param inputData text string from the input file
	 * @param output processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String output) {
		/*To get the bytes of the input string, we just get the bytes of the original string with string.getBytes().length*/
		int inputBytes = inputData.getBytes().length;

		/**
		 * For the bytes of the encoded one, it's not so easy.
		 *
		 * Here we have to get the bytes the same way we got the bytes for the original one but we divide it by 8,
		 * because 1 byte = 8 bits and our huffman code is in bits (0,1), not bytes.
		 *
		 * This is because we want to calculate how many bytes we saved by counting how many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes will give us how much space we "chopped off"
		 *
		 * So we have to subtract that "chopped off" percentage to the total (which is 100%)
		 * and that's the difference in space required
		 */
		String savings =  d.format(100 - (( (float) (outputBytes / (float)inputBytes) ) * 100));


		/**
		 * Finally we just output our results to the console
		 * with a more visual pleasing version of both our Hash Tables in decreasing order by frequency.
		 *
		 * Notice that when the output is shown, the characters with the highest frequency have the lowest amount of bits.
		 *
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer,String>> sortedList = new SortedLinkedList<BTNode<Integer,String>>();

		/* To print the table in decreasing order by frequency, we do the same thing we did when we built the tree
		 * We add each key with it's frequency in a node into a SortedList, this way we get the frequencies in ascending order*/
		for (String key : fD.getKeys()) {
			BTNode<Integer,String> node = new BTNode<Integer,String>(fD.get(key),key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order,
		 * we just traverse the list backwards and start printing the nodes key (character) and value (frequency)
		 * and find the same key in our prefix code "Lookup Table" we made earlier on in huffman_code().
		 *
		 * That way we get the table in decreasing order by frequency
		 * */
		for (int i = sortedList.size() - 1; i >= 0; i--) {
			BTNode<Integer,String> node = sortedList.get(i);
			System.out.println(node.getValue() + "\t" + node.getKey() + "\t    " + encodedHuffman.get(node.getValue()));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output);
		System.out.println("Decoded String: \n" + decodeHuff(output, encodedHuffman) + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + (int) outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}


	/*************************************************************************************
	 ** ADD ANY AUXILIARY METHOD YOU WISH TO IMPLEMENT TO FACILITATE YOUR SOLUTION HERE **
	 *************************************************************************************/

	/**
	 * This recursive auxiliary method takes the parameters from
	 * the huffman_code method with an added string
	 * creates the encoded counterpart from each element in the tree that is a leaf
	 * and returns a map of all the encoded characters
	 *
	 * @param huffmanRoot - Decoded huffman tree root
	 * @param m - The map which will be added the codes
	 * @param code - An empty string that by using recursion will convert into a decoded character
	 * @return m - The encoded map
	 */
	public static Map<String, String> huffman_code_help(BTNode<Integer,String> huffmanRoot,Map<String,String> m,String code){
		if(huffmanRoot == null){
			return m;
		}
		if(huffmanRoot.getLeftChild() == null && huffmanRoot.getRightChild() == null) m.put(huffmanRoot.getValue(),code); //if the current node is a leaf, put it into the map with its encoded string
		huffman_code_help(huffmanRoot.getLeftChild(),m,code+"0"); // Going left in a huffman_tree is code: 0
		huffman_code_help(huffmanRoot.getRightChild(),m,code+"1"); // Going right in a huffman_tree is code: 1

		return m;
	}

	/**
	 * Auxiliary Method that decodes the generated string by the Huffman Coding Algorithm
	 *
	 * Used for output Purposes
	 *
	 * @param output - Encoded String
	 * @param lookupTable
	 * @return The decoded String, this should be the original input string parsed from the input file
	 */
	public static String decodeHuff(String output, Map<String, String> lookupTable) {
		String result = "";
		int start = 0;
		List<String>  prefixCodes = lookupTable.getValues();
		List<String> symbols = lookupTable.getKeys();

		/*looping through output until a prefix code is found on map and
		 * adding the symbol that the code that represents it to result */
		for(int i = 0; i <= output.length();i++){

			String searched = output.substring(start, i);

			int index = prefixCodes.firstIndex(searched);

			if(index >= 0) { //Found it
				result= result + symbols.get(index);
				start = i;
			}
		}
		return result;
	}



}
