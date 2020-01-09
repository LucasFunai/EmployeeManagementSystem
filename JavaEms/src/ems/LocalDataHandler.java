package ems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LocalDataHandler implements EMSDataHandler,Encryption {
	private Base64Operator baseOp = new Base64Operator();
	final String STORAGEFILE = "EMSmemory.ems";
	final Pattern IDREGEX = Pattern.compile("IdId\\d+IdEnd");
	private HashMap<Integer,String> storage = new HashMap<Integer,String>();
	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<String> names = new ArrayList<String>();
	private String tempText = "Default";
	private Browser browse;
	private boolean quitReq;
	private boolean configured;
	private Login credentials;
	private LogFiler logFiler;
	//-----------------------Delimiters for the cryptographed file---------------
	final String IDDELIMETER = "IdId";
	final String IDEND = "IdEnd";
	final String ENCRYPTKEY = "EMScrypto.ems";
	final String PASSDELIMETER = "Ps:";
	final String PASSEND = "<!>PsEnd";
	final String NAMEDELIMETER = "Nm:";
	final String NAMEEND = "<!>NmEnd";
	final String SCOREDELIMETER = "Pf:";
	final String SCOREEND = "<!>PfEnd";
	final String RANKDELIMETER = "Rk:";
	final String RANKEND = "<!>RkEnd";
	final String UNITOVER = "!E!";
	final String TIMEDELIMETER = "T/";
	final String TIMEEND = "!";
	final String ATTENDANCEDELIMETER = "At:";
	final String ATTENDANCEEND = "<!>AtEnd";






	//-----------------------LOADING AND SAVING --------------------------



	public void initiate() {
		//Loads data or creates a file to store data.
		if(load()) {
			generateTable();
			configured = true;
		} else {
			this.setupDB(null, null, null);
			configured = true;
		}
	}


	public boolean save() {
		//Saves the entirety of storage to a file, encoded and encrypted.
		try {
			encryptAndSave(tableToBinary());
			return true;
		} catch (FileNotFoundException e) {
			logFiler.saveStackTrace(e);
			return false;
		}
	}



	void formStorage() throws IOException{
		//Takes decrypted and decoded file and creates a table using storage(a hashmap.)
		int id = 0;
		if(tempText != "Default") {
			String[] splitted = tempText.split(UNITOVER);
			for(String s:splitted) {
				Matcher match = IDREGEX.matcher(s);
				if(match.lookingAt()) {
					id = Integer.parseInt(match.group().replaceAll(IDDELIMETER, "").replaceAll(IDEND, ""));
					String info = (s.replace(match.group(), ""));
					storage.put(id, info+"!E!");
					continue;
				}
			}
		}





	}


	public void generateTable() {
		//Creates the id/name list.
		ids.clear();
		names.clear();
		for(int k:storage.keySet()) {
			String value = storage.get(k);
			String addName = value.substring(value.indexOf(NAMEDELIMETER) + 3,value.indexOf(NAMEEND));
			ids.add(String.valueOf(k));
			names.add(addName);
		}
		if(storage.keySet().isEmpty()) {
			ids.add("None");
			names.add("None");
		}
	}

	public boolean load() {
		//decode file, decrypt file, and load it on storage. returns false in case storage does not change.
		decryptAndLoad(decodedFile());
		try {
			formStorage();
			if (storage.isEmpty()){
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void getDependencies(Launcher launch) {
		//Gets reference to every class needed.
		browse = launch.returnBrowser();
		credentials = launch.returnLogin();
		logFiler = launch.returnLogFiler();
	}

	@Override
	public boolean isConfigured() {
		//Reduntant in this implementation
		return configured;
	}

	@Override
	public void saveAttendance(int timerMinutes, boolean timerStarted) {
		if(!timerStarted) {
			return;
		}
		String value = storage.get(credentials.currentId());
		int atIndex = value.indexOf(TIMEDELIMETER);
		StringBuilder sb = new StringBuilder();
		sb.append(value.substring(0, atIndex+1));
		sb.append(Integer.toString(timerMinutes));
		sb.append(value.substring(atIndex+1));
		storage.put(credentials.currentId(), sb.toString());
	}

	@Override
	public boolean setupDB(String address, String userName, String passWord) {
		File storageDir = new java.io.File("./storage/");
		boolean success = false;
		storageDir.mkdir();
		File storageFile = new java.io.File("./storage/"+STORAGEFILE);
		try {
			if(storageFile.createNewFile() && storageFile.canWrite()) {
				success = true;
			}

		} catch (IOException e) {
			logFiler.saveStackTrace(e);
			return false;
		}
		return success;
	}



	//-----------------------------GETTERS AND SETTERS-----------------------
	@Override
	public boolean setPerformance(int id,int score) {
		if(getRank(browse.returnBrowserId()) <= 1 || score > 10) {
			return false;
		} else {
			String oldValue = storage.get(id);
			String newValue = oldValue.replaceFirst(SCOREDELIMETER + "\\d+", SCOREDELIMETER+score);
			storage.put(id,newValue);
			return true;
		}
	}

	public void setAttendance() {
		DateFormat formatter = new SimpleDateFormat("yyyy:MM:dd");
		String oldValue = storage.get(browse.returnBrowserId());
		int start = oldValue.indexOf(ATTENDANCEDELIMETER);
		String newValue = oldValue.substring(0,start + 3) + formatter.format(browse.currentDate()) + 
				TIMEDELIMETER + oldValue.substring(start + 3) + ("!");
		if(!newValue.contains(ATTENDANCEEND)){
			newValue.concat(ATTENDANCEEND);
		}
		storage.put(browse.returnBrowserId(),newValue);
	}
	@Override
	public boolean setRank(int id, int rank) {
		if(storage.size() < 2 || getRank(browse.returnBrowserId()) >= 3) {
			if(storage.containsKey(id) == false) {
				return false;
			} else {
				String oldValue = storage.get(id);
				String newValue = oldValue.replaceAll(RANKDELIMETER + "\\d+" + RANKEND ,RANKDELIMETER + rank + RANKEND);
				storage.put(id, newValue);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public int setNewEmployee(String passWord, String name, int rank) {
		if(!storage.isEmpty() && getRank(credentials.currentId()) < 3){
			return -1;
		}
		if(!storage.isEmpty() && getRank(credentials.currentId()) <= rank) {
			return -1;
		}
		int i=0;
		for(Entry<Integer, String> id:storage.entrySet()) {
			i++;
		}
		StringBuilder sb = new StringBuilder();
		String value = "";
		if(storage.isEmpty()) {
			sb.append(PASSDELIMETER);
			sb.append(passWord);
			sb.append(PASSEND);
			sb.append(NAMEDELIMETER);
			sb.append(name);
			sb.append(NAMEEND);
			sb.append(SCOREDELIMETER + "0" + SCOREEND );
			sb.append(RANKDELIMETER + "5" + RANKEND);
			sb.append(ATTENDANCEDELIMETER);
			sb.append(TIMEDELIMETER);
			sb.append(TIMEEND);
			sb.append(ATTENDANCEEND);
			sb.append(UNITOVER);
			value =  sb.toString();
			tempText = sb.toString();

		} else {
			sb.append(PASSDELIMETER);
			sb.append(passWord);
			sb.append(PASSEND);
			sb.append(NAMEDELIMETER);
			sb.append(name);
			sb.append(NAMEEND);
			sb.append(SCOREDELIMETER + "0" + SCOREEND );
			sb.append(RANKDELIMETER);
			sb.append(Integer.toString(rank));
			sb.append(RANKEND);
			sb.append(ATTENDANCEDELIMETER);
			sb.append(TIMEDELIMETER);
			sb.append(TIMEEND);
			sb.append(ATTENDANCEEND);
			sb.append(UNITOVER);
			value =  sb.toString();
		}
		storage.put(i, value);
		try {
			formStorage();
		} catch (IOException e) {
			logFiler.saveStackTrace(e);
		}
		generateTable();
		return i;
	}

	@Override
	public ArrayList<String> returnAttendance(int userId,int id,int year,int month){
		String monthString = "";
		if((Math.log10(month) + 1) == 1){
			monthString = Integer.toString(month);
		} else {
			monthString = "0" + Integer.toString(month);
		}
		ArrayList<String> returnable = new ArrayList<String>();
		String oldValue = storage.get(id);
		int start = oldValue.indexOf(ATTENDANCEDELIMETER);
		String dates = oldValue.substring(start + 3);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i > dates.length(); i++) {
			if(String.valueOf(dates.charAt(i)) != TIMEEND) {
				sb.append(dates.charAt(i));
			} else {
				String str = sb.toString();
				if(str.contains(Integer.toString(year) + ":" + monthString)) {
					returnable.add(str);
				}
				sb.setLength(0);

			}
		}
		return returnable;
	}
	@Override
	public int getRank(int id) {
		if(storage.isEmpty()) {
			return 5;
		}
		if(!storage.get(id).contains(RANKDELIMETER)) {
			return -1;
		}
		String rank = storage.get(id).substring(storage.get(id).indexOf(RANKDELIMETER) + 3,storage.get(id).indexOf(RANKEND));
		int returnable = Integer.parseInt(rank);
		return returnable;
	}
	@Override
	public ArrayList<Integer> getPerformance(int userId,int id) {
		ArrayList<Integer> returnable = new ArrayList<Integer>();
		String perf = storage.get(id).substring(storage.get(id).indexOf(SCOREDELIMETER) + 3,storage.get(id).indexOf(SCOREEND));
		for(String s:perf.split(",")) {
			returnable.add(Integer.parseInt(s));
		}
		return returnable;

	}
	@Override
	public String getName(int id) {
		String name = storage.get(id).substring(storage.get(id).indexOf(NAMEDELIMETER) + 3,storage.get(id).indexOf(NAMEEND));
		return name;
	}
	@Override
	public ArrayList<String> returnNames(){
		return names;
	}
	@Override
	public ArrayList<String> returnIDs(){
		return ids;
	}

	public HashMap<Integer,String> returnStorage(){
		return storage;
	}

	//----------------ENCODING AND DECODING--------------------

	public String contentDebug() {
		try (FileInputStream reader = new FileInputStream(STORAGEFILE)){
			byte[] all = new byte[(int)(new File(STORAGEFILE)).length()];
			reader.read(all);
			boolean one = false;
			boolean two = false;
			int threeSpace = -1;
			for(int i=0;i < all.length;i++) {
				if(all[i] == 0) {
					if(one) {
						if(two) {
							threeSpace = i;
							break;
						} else {
							two = true;
						}
					} else {
						one = true;
					}
				} else {
					one = false;
					two = false;
				}
			}
			if(threeSpace != -1) {
				byte[] debugOutput = Arrays.copyOfRange(all, threeSpace + 3, all.length);
				return new String (baseOp.decode(debugOutput),StandardCharsets.UTF_8);
			}

		} catch (FileNotFoundException e) {
			logFiler.saveStackTrace(e);
			return "Error. File not found.";

		} catch (IOException e) {
			logFiler.saveStackTrace(e);
			return "Error. IOException";

		}
		return "Error.";


	}


	byte[] decodedFile() {
		int debugLen = (int) new File(STORAGEFILE).length();
		if(debugLen < 3) {
			return new byte[3];
		}
		byte[] all = new byte[debugLen];
		ArrayList<Byte> zeroBuffer = new ArrayList<Byte>();
		if(all.length > 0) {
			try (FileInputStream reader = new FileInputStream(STORAGEFILE)){
				reader.read(all);
				ArrayList<Byte> temporary = new ArrayList<Byte>();
				byte[] zeroByte = new byte[1];
				for(int i=0;i < debugLen;i++) {
					if(Byte.compare(all[i], zeroByte[0]) != 0) {
						if(zeroBuffer.isEmpty()) {
							temporary.add(all[i]);
						} else {
							temporary.addAll(zeroBuffer);
							zeroBuffer.clear();
							temporary.add(all[i]);
						}
					} else {
						if(zeroBuffer.size() > 3) {
							break;
						} else {
							zeroBuffer.add(all[i]);
						}
					}
				}

				byte[] returnable = new byte[temporary.size()];
				int i=0;
				for(Byte b: temporary) {
					returnable[i] = b.byteValue();
					i++;
				}




				return baseOp.decode(returnable);
			} catch (FileNotFoundException e) {
				logFiler.saveStackTrace(e);
				return new byte[3];
			} catch (IOException e) {
				logFiler.saveStackTrace(e);
				return new byte[3];
			}
		}
		return new byte[3];


	}


	public byte[] getEncryptKey() throws IOException{
		byte[] key = baseOp.encode("Thisisdakeydonttellanyon");
		byte[] truncated = new byte[16];
		for(byte i = 0;i < truncated.length;i++) {
			truncated[i] = key[i];
		}
		return truncated;

	}

	public byte[] tableToBinary() {
		List<Byte> wrappedByteList = new ArrayList<Byte>();
		for (int key :storage.keySet()){
			String content = storage.get(key);
			String storeId = IDDELIMETER + String.valueOf(key) + IDEND;
			String merged = storeId+content;
			byte[] coded = merged.getBytes(StandardCharsets.UTF_8);
			for(byte b:coded) {
				wrappedByteList.add(b);
			}
		}
		byte[] returnable = new byte[wrappedByteList.size()];
		for(int i = 0;i < wrappedByteList.size();i++) {
			returnable[i] = wrappedByteList.get(i).byteValue();
		}
		return returnable;
	}

	public byte[] nonEncodedTable() {
		List<Byte> wrappedByteList = new ArrayList<Byte>();
		for (int key :storage.keySet()){
			String content = storage.get(key);
			String storeId = IDDELIMETER + String.valueOf(key) + IDEND;
			String merged = storeId+content;
			byte[] coded = merged.getBytes(StandardCharsets.UTF_8);
			Byte[] wrapped = new Byte[coded.length];
			int i=0;
			for(byte b:coded) {
				wrapped[i++] = b;
			}

			for(Byte b:wrapped) {
				wrappedByteList.add(b);
			}

		}
		byte[] returnable = new byte[wrappedByteList.size()];
		for(int i = 0;i < wrappedByteList.size();i++) {
			returnable[i] = wrappedByteList.get(i).byteValue();
		}
		return returnable;
	}

	public boolean encryptAndSave(byte[] input) throws FileNotFoundException {
		try {
			//This should clear the file.
			new PrintWriter(STORAGEFILE).close();
			SecretKeySpec secretKey = new SecretKeySpec(getEncryptKey(), "AES");
			byte[] salt = new byte[8];
			SecureRandom random = new SecureRandom();
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			//Everything above does not change when changing modes
			random.nextBytes(salt);

			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, salt);
			cipher.init(Cipher.ENCRYPT_MODE,secretKey,parameterSpec);
			try (FileOutputStream  writer = new FileOutputStream(STORAGEFILE,true)){
				byte[] output = cipher.update(input);
				byte[] authTag = cipher.doFinal();
				byte[] toBePrinted = new byte[output.length + authTag.length];
				System.arraycopy(output, 0, toBePrinted, 0,output.length);
				System.arraycopy(authTag, 0, toBePrinted, output.length, authTag.length);
				byte[] merged = new byte[salt.length+toBePrinted.length];
				System.arraycopy(salt,0,merged,0,salt.length);
				System.arraycopy(toBePrinted, 0, merged, salt.length, toBePrinted.length);


				writer.write(baseOp.encode(merged));

				//				  byte[] debugOutputSalt = salt; byte[] debugOutputText = nonEncodedTable();
				//				  byte[] debugOutputKey = secretKey.getEncoded(); byte[] result =
				//				  Arrays.copyOf(debugOutputSalt, debugOutputSalt.length +
				//				  debugOutputText.length + debugOutputKey.length);
				//				  System.arraycopy(debugOutputText, 0, result, debugOutputSalt.length,
				//				  debugOutputText.length); System.arraycopy(debugOutputKey, 0, result,
				//				  debugOutputSalt.length + debugOutputText.length, debugOutputKey.length);
				//				  byte[] codedResult = baseOp.encode(result); writer.write(new byte[5]);
				//				  writer.write(codedResult); System.out.println(new String(codedResult));



			} catch (Exception e) {
				logFiler.saveStackTrace(e);
				return false;
			} 
		} catch (Exception e) {
			logFiler.saveStackTrace(e);
			return false;
		}	
		return true;



	}
	public boolean decryptAndLoad(byte[] input) {
		try {
			if((int) new File(STORAGEFILE).length() > 3) {
				SecretKeySpec secretKey = new SecretKeySpec(getEncryptKey(), "AES");
				byte[] salt = new byte[8];
				byte[] trimmedInput = new byte[input.length - 8];

				int i=0;
				for(byte b: input) {
					if(i<8) {
						salt[i] = b;
						i++;
					} else {
						trimmedInput[i-8] = b;
						i++;
					}
				}
				Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
				GCMParameterSpec parameterSpec = new GCMParameterSpec(128, salt);
				cipher.init(Cipher.DECRYPT_MODE,secretKey,parameterSpec);
				cipher.update(Arrays.copyOfRange(trimmedInput, 0, trimmedInput.length - 16));
				byte[] didFinal = cipher.doFinal(Arrays.copyOfRange(trimmedInput, trimmedInput.length - 16,
						trimmedInput.length));

				tempText = new String(didFinal,StandardCharsets.UTF_8);
				//For debugging. This prints the content, then only the salt, then the content without the salt.
				//				 try(FileInputStream reader = new FileInputStream(new File(STORAGEFILE))){
				//					    String debug;
				//						debug = contentDebug();
				//						browse.print(debug);
				//					}
				//					byte[] debugOutputSalt = salt;
				//					byte[] debugOutputText = trimmedInput;
				//					byte[] debugOutputKey = secretKey.getEncoded();
				//					byte[] result = Arrays.copyOf(debugOutputSalt, debugOutputSalt.length + debugOutputText.length +
				//							debugOutputKey.length);
				//					System.arraycopy(debugOutputText, 0, result, debugOutputSalt.length, debugOutputText.length);
				//					System.arraycopy(debugOutputKey, 0, result, debugOutputSalt.length + debugOutputText.length,
				//							debugOutputKey.length);
				//					browse.print(new String(result,StandardCharsets.UTF_8));
				//					browse.print(new String(baseOp.encode(result),StandardCharsets.UTF_8));
				//					browse.print(new String(baseOp.encode(salt),StandardCharsets.UTF_8));
				//					browse.print(new String(baseOp.encode(trimmedInput),StandardCharsets.UTF_8));




			}




		} catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | 
				NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			logFiler.saveStackTrace(e);

			return false;
		} 
		return true;
	}



}
