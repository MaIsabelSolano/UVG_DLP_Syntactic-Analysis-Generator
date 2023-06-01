package Scanner;

import src.AFD;
import src.Terminator;
import src.Token;
import src.Symbol;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileWriter;
public class Scan {

    public static void main (String[] args) {

		// Tokens de-serialization
		ArrayList<Token> tokens = new ArrayList<>();
		try {
            FileInputStream fileIn = new FileInputStream("output/TokensSerialized.ser");
            ObjectInputStream input = new ObjectInputStream(fileIn);

            ArrayList<?> rawTokens = (ArrayList<?>) input.readObject();

            tokens = new ArrayList<>();
            for (Object rawToken : rawTokens) {
                tokens.add((Token) rawToken);
            }
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return;
        }

		// Automata restoring
        String fileName = "output/Automata.txt";
		String automataInfo = "";
		AFD automata;
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
				String line;
				while ((line = reader.readLine()) != null) {
					automataInfo += line + "\n";
				}

                automata = new AFD(automataInfo);

				String toRead = "";
				String testFilename = "input/ToRead.txt";
				try (BufferedReader readertr = new BufferedReader(new FileReader(testFilename))) {
					String linetr;
					while ((linetr = readertr.readLine()) != null) {
						toRead += linetr + "\n";
					}
					continuosRead(toRead, automata, tokens);
					
				} catch (IOException e) { 
					e.printStackTrace();

				}
                
		} catch (IOException e) { 
			e.printStackTrace();

        }

		
    }

	static public void continuosRead(String toRead, AFD automata, ArrayList<Token> tokens) {

		// String[0]: Token given
		// String[1]: Token found
		ArrayList<String[]> association = new ArrayList<>();
		
		String currentString = "";
		Symbol lastSymbol = null;
		String lastToken = "";
		String lastWord = "";

		for (int i = 0; i < toRead.length(); i ++) {
			currentString += toRead.charAt(i);
			String testString = currentString + "$";

			System.out.println(testString);

			if (automata.Simulate(testString)) {
				lastSymbol = automata.simulateGetSymbol(testString);
				Terminator EndSymbol = (Terminator) lastSymbol;
				lastToken = EndSymbol.getAssociatedToken().substring(0, EndSymbol.getAssociatedToken().length() - 1);
				lastWord = currentString;

				System.out.println("Last token: " + lastToken);

			} else {

				if (lastSymbol != null) {
					// Stopped reading a valid token

					// look ahead 
					if (i + 1 < toRead.length()) {
						System.out.println("Look ahead");
						String lookAhead = currentString + toRead.charAt(i + 1) + "$";
						System.out.println("look" + lookAhead);
						if (automata.Simulate(lookAhead)) {
							System.out.println("Entra al look ahead");
							lastSymbol = automata.simulateGetSymbol(lookAhead);
							Terminator EndSymbol = (Terminator) lastSymbol;
							lastToken = EndSymbol.getAssociatedToken().substring(0, EndSymbol.getAssociatedToken().length() - 1);
							lastWord = currentString + toRead.charAt(i + 1);
							currentString += toRead.charAt(i + 1);
			
							System.out.println("Last token (LA): " + lastToken);
							System.out.println("curent String: " + currentString);

							i ++;
						} else {

							// else 
							String temp[] = {lastWord, lastToken};
							association.add(temp);
							lastWord = "";
							lastToken = "";
							currentString = "";
							lastSymbol = null;
							// step back 
							i--;
						} 
					} else {

						// else 
						String temp[] = {lastWord, lastToken};
						association.add(temp);
						lastWord = "";
						lastToken = "";
						currentString = "";
						lastSymbol = null;
						// step back 
						i--;
					}
				} else {
					// Encountered an error
					if (currentString.length() > 0) {
						System.out.println("last word ERROR: " + lastWord);
						String temp[] = {currentString, "ERROR"};
						association.add(temp);
						lastWord = "";
						lastToken = "";
						currentString = "";
						lastSymbol = null;
					} 
				}
				
			}

		}

		

		ArrayList<String> tokenWithReturnNames = new ArrayList<>();
		for (Token t: tokens) {
			if (t.getFunction() != null) {
				if (t.getFunction().length() > 0) {
					// has any value, get the name of the token
					tokenWithReturnNames.add(t.getLexeme());
				}
			}
			
		}

		for (String s[]:association) {
			if (tokenWithReturnNames.contains(s[1])) {
				// The current token can be replaced
				for (Token t: tokens) {
					if (t.getLexeme().equals(s[1])) {
						// current token matches
						if (t.getFunction().startsWith("return")) {
							String replaceRet = t.getFunction().replace("return", "").trim();
							s[1] = replaceRet;
						}
					}
				}
			}
			
		}

		String AssociationFileInfo = "WORD\t->\tTOKEN";
		for (String s[]:association) {
			if (s[0].equals(" ")) AssociationFileInfo += "\n\\s\t->\t" + s[1];
			else if (s[0].equals("\t")) AssociationFileInfo += "\n\\t\t->\t" + s[1];
			else if (s[0].equals("\n")) AssociationFileInfo += "\n\\n\t->\t" + s[1];
			else AssociationFileInfo += "\n"+s[0]+"\t->\t" + s[1];
		}

		try {
            FileWriter fileWriter = new FileWriter("output/Tokens.txt");
            fileWriter.write(AssociationFileInfo);
            fileWriter.close();
            System.out.println("Tokens.txt File created and written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}


