package ems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LocalPrinter implements Printer{

	public void printInfo(String info) {
		System.out.println(info);
	}

	@Override
	public String printAndGetInput(String info) {
		System.out.println(info);
		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		try {
			input = reader.readLine();
		} catch (IOException e) {
			
		}
		return input;
	}


}