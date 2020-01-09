package ems;

import java.io.FileNotFoundException;

public interface Encryption{
	boolean encryptAndSave(byte[] input) throws FileNotFoundException;
	boolean decryptAndLoad(byte[] input);
	byte[] tableToBinary();
}
