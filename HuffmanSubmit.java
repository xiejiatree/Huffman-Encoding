// Import any package as required
import java.io.*;
import java.util.*;

public class HuffmanSubmit implements Huffman {
	/*
	 * Huffman Node implementation from scratch.
	 * Was originally a superclass for leafnodes, but was simpler to just have
	 * an optional character field
	 * implements vs. extends (inheritance in a class) vs. inherits
	 * interface: abstract class to group related methods, use comparable methods to
	 * compare nodes.
	 */
	public static class HNode implements Comparable<HNode> {
		// data stored within a node.
		int frequency = 0;
		HNode leftChild;
		HNode rightChild;
		char character;

		// Constructor, Nodes were built with getter and setters for this program.
		public HNode(){
		}
		
		//Safety, check before doing getCharacter()
		public boolean isLeafNode(){
		return (this.leftChild == null && this.rightChild == null);
		}

		public int getFrequency(){
			return this.frequency;
		}

		public void setFrequency(int frequency){
			this.frequency = frequency;
		}

		public HNode getLeftChild(){
			return leftChild;
		}

		public void setLeftChild(HNode leftChild){
			this.leftChild = leftChild;
		}

		public HNode getRightChild(){
			return rightChild;
		}

		public void setRightChild(HNode rightChild){
			this.rightChild = rightChild;
		}

		@Override
		public int compareTo(HNode n){
			return Integer.compare(this.frequency, n.getFrequency());
		}

		public String toString(){
			String s = String.valueOf("Frequency "+this.getFrequency())+" :Left child: "+(this.getLeftChild())+" :Right Child: "+this.getRightChild();
			return s; 
		}

		public void setCharacter(char character){
			this.character = character;
		}

		public char getCharacter(){
			return this.character;
		}
	}
	
	/**
	 * @param inputFile
	 * @return A hashmap that maps the file's characters to their frequency values
	 */
	public static HashMap<Character, Integer> makeFreqTable(String inputFile) {
		//read file directly from binaryinput stream
		BinaryIn b = new BinaryIn(inputFile);
		HashMap<Character, Integer> freqtable = new HashMap<>();
		while(!b.isEmpty()){
			//the character from the binary inputstream
			char ch = b.readChar();
			//add a new character
			if (!freqtable.containsKey(ch)) {
				freqtable.put(ch, 1);
			} 
			//Add value to an existing character
			else {
				freqtable.put(ch, freqtable.get(ch) + 1);
			}
		}
		System.out.println(freqtable);
		return freqtable;
	}

	
	/**
	 * @param freqTable
	 * @return The character with the highest frequency.
	 */
	public static Map.Entry<Character,Integer> getMax(HashMap<Character,Integer> freqTable){
		Map.Entry<Character, Integer> entryWithMaxValue = null;
		for(Map.Entry<Character,Integer> entry: freqTable.entrySet()){
			if(entryWithMaxValue == null || entry.getValue().compareTo(entryWithMaxValue.getValue())>0){
				entryWithMaxValue = entry;
			}
		}
		return entryWithMaxValue;
	}
	//Sort the hashmap into an ArrayList<String>
	public static ArrayList<String> sortFreq(HashMap<Character,Integer> freqTable){
		ArrayList<String> holder = new ArrayList<String>();
		while(freqTable.size()>0){
			Map.Entry<Character,Integer> entry = getMax(freqTable);
			String frequency = String.valueOf(entry.getValue());
			String charVal = toBinary((int)entry.getKey());
			holder.add(charVal+":"+frequency);
			freqTable.remove(entry.getKey());
		}
		return holder;
	}

	/**
	 * @param freqTable HashMap of characters to values. 
	 * @param freqFile output file
	 * Writes the freqFile in the proper format based on the freqTable. 
	 */
	public static void makefreqFile(HashMap<Character, Integer> freqTable,String freqFile){
		ArrayList<String> sorted = sortFreq(freqTable);
		try {
			//In order, write each of the Strings into the output file
            BufferedWriter writer = new BufferedWriter(new FileWriter(freqFile));
            for (String entry : sorted) {
                writer.write(entry);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("freqFile error ");
        }
	}

	//A method to convert a decimal int to a binary string.
	public static String toBinary(int decimal) {
		String binary = "";
		//ok i just found out about Integer.parseInt(str, 2) but this works.
		while (decimal > 0) {
			int remainder = decimal % 2;
			binary = remainder + binary;
			decimal = decimal / 2;
		}
		//pads 0's until it has a length of 8. 
		if (binary.length() < 8) {
			int pad = 8 - binary.length();
			StringBuilder s = new StringBuilder();

			for (int i = 0; i < pad; i++) {
				s.append("0");
			}
			s.append(binary);
			binary = s.toString();
			return binary;
		} 
		else {
			return binary;
		}
	}

	/**
	 * @param freqFile
	 * @return
	 * Iterate throught the freqFile, changes the binary value of the ascii value
	 * of a character into a character. Makes LeafNodes. Places them in a queue.
	 * Comparable interface compares the frequency of these Nodes. 
	 */
	public static PriorityQueue<HNode> makeQueue(String freqFile){
		PriorityQueue<HNode> pq = new PriorityQueue<>();
		try(BufferedReader reader = new BufferedReader(new FileReader(freqFile))){
			String line;
			while((line = reader.readLine()) != null){
				String characterCode = (line.substring(0, 8));
				int ascii = Integer.parseInt(characterCode,2);
				int frequency = Integer.parseInt(line.substring(9));
				HNode node = new HNode();
				node.setFrequency(frequency);
				node.setCharacter((char)ascii);
				pq.offer(node);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return pq;
	}
	
	//For testing
	public static void printQueue(PriorityQueue<HNode> pq){
		while (!pq.isEmpty()) {
			System.out.println(pq.poll());
		}
	}

	/**
	 * @param pq the queue from makeQueue method. 
	 * @return the root Node of the tree
	 * Makes an empty Node
	 * 
	 * pq.poll() starts by grabbing the
	 * two lowest frequency nodes, setting them as left and right children. They
	 * are removed from pq. 
	 * 
	 * The parent has the Comparable frequency value of the two children combined. 
	 */
	public static HNode makeTree(PriorityQueue<HNode> pq) {
		while (pq.size() > 1) {
			HNode parent = new HNode();
			HNode left = pq.poll();
			HNode right = pq.poll();
			//Safe, a huffman tree is a complete binary tree. 
			parent.setFrequency(left.getFrequency()+right.getFrequency());
			parent.setLeftChild(left);
			parent.setRightChild(right);
			//At the end, pq will only have one parent node left
			pq.offer(parent);
		}
		return pq.poll();
	}

	/**
	 * @param root The highest Node. 
	 * @return a map of characters to their Huffman codes
	 */
	public static HashMap<Character, String> masterKey(HNode root) {
		HashMap<Character, String> hashMap = new HashMap<>();
		//Initial call starts from the root node, and gives makeCode and empty code string
		makeCode(root, "", hashMap);
		return hashMap;
	}

	/**
	 * @param node
	 * @param code
	 * @param hashMap
	 */
	private static void makeCode(HNode node, String code, HashMap<Character, String> hashMap) {
		//makeCode returns to masterKey once all of the recursive calls to makeCode have ended.
		if (node.isLeafNode()) {
			/**
			 * Once a LeafNode is reached, makeCode returns, and the original hashMap
			 * has been appropriately modified. 
			 */
			HNode leaf = node;
			Character x = leaf.getCharacter();
			hashMap.put(x, code);
			return;
		}
		/*
		 * Ignoring the conditional, there are now multiple instances of makeCode() occurring
		 * at once. makeCode will continue to traverse down the tree, appending 1 or 0
		 * to the interim code until a LeafNode is reached. 
		 * 
		 * This method traversal is safe, a Huffman tree is always complete. There is complete
		 * coverage of each Node.
		 */
		makeCode(node.getLeftChild(), code + "0", hashMap);
		makeCode(node.getRightChild(), code + "1", hashMap);
	}


	/**
	 * @param filename
	 * @param huffmanCodes
	 * @return
	 * Intermediate step. Reads through the file, and makes assigns huffman codes
	 */
	public static void writeBits(String inputFileName, String outputFileName,HashMap<Character, String> huffmanCodes) {
		BinaryOut outputStream = new BinaryOut(outputFileName);
		BinaryIn inputStream = new BinaryIn(inputFileName);
		while(!inputStream.isEmpty()){
			char ch = inputStream.readChar();
			String s = huffmanCodes.get(ch);
			for(int i = 0; i<s.length(); i++){
				if(s.charAt(i)=='0'){outputStream.write(false);}
				else{outputStream.write(true);}
			}
		}
			outputStream.close();
			outputStream.flush();
    }

	/**
	 * @param freqFile
	 * @return
	 * Makes a frequencytable for a masterkey from the freqFile
	 */
	public static HashMap<Character, Integer> dmakeFreqTable(String freqFile){
		HashMap<Character, Integer> dFreqTable = new HashMap<>();
		try { 
            // Open the file for reading
            BufferedReader reader = new BufferedReader(new FileReader(freqFile));
            String line;
            // Read through each line of the file
            while ((line = reader.readLine()) != null) {
                // Split the line into two parts using the colon separator
                String[] parts = line.split(":");

                // Convert the first part to a decimal integer
                int decimalAscii = Integer.parseInt(parts[0], 2);

                // Convert the decimal integer to a character
                char character = (char) decimalAscii;

                // Parse the second part as an integer
                int value = Integer.parseInt(parts[1]);

                // Add the character and value to the HashMap
                dFreqTable.put(character, value);
            }

            // Close the file reader
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
		return dFreqTable;
    }

	/**
	 * @param root rootNode of Huffman tree
	 * @param inputFile
	 * @param outputFile 
	 * This reads the encoded file. Reads booleans, traverses tree.
	 */
	public static void decoder(HNode root, String inputFile,String outputFile){
		BinaryIn x = new BinaryIn(inputFile);
		BinaryOut writeChars = new BinaryOut(outputFile);
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			HNode start = root;

			while(!x.isEmpty()){
				if(x.readBoolean()==false){
					start = start.getLeftChild();
				}
				else{
					start = start.getRightChild();
				}

				if(start.isLeafNode()){
					writeChars.write(start.getCharacter());
					start = root;
				}
			}
			writeChars.close();
			writer.close();
		}
		catch(IOException e){
			System.out.println("oof");
		}
	}

	public void encode(String inputFile, String outputFile, String freqFile) {
		HashMap<Character, Integer> y = makeFreqTable(inputFile);
		makefreqFile(y, freqFile);
		PriorityQueue<HNode> pq = makeQueue(freqFile);
		HashMap<Character,String> key = masterKey(makeTree(pq));
		//writeBits already returns something flushed.
		writeBits(inputFile, outputFile, key);
	}

	public void decode(String inputFile, String outputFile, String freqFile) {
		PriorityQueue<HNode> pq = makeQueue(freqFile);
		HNode root = makeTree(pq);
		decoder(root,inputFile,outputFile);
	}

	public static void main(String args[]){
		HuffmanSubmit x = new HuffmanSubmit();
		//x.encode("alice30.txt","output.enc","freqFile.txt");
		//x.decode("output.enc","output.dec","freqFile.txt");
		//x.encode("ur.jpg","ur.enc","freqFile2.txt");
		//x.decode("ur.enc","urdec.jpg","freqFile2.txt");
		
	}

	// comapare in terminal with with diff file1 file2	

}
