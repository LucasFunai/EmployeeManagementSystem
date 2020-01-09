package ems;

import java.util.Arrays;

public class LocalLogin implements Login{
	LocalDataHandler get;
	int currentId;
	protected final int SETUPID = 12345;

	public LocalLogin(){

	}

	

	public int currentId() {
		return currentId;
	}
	public boolean userIsValid(int id,char[] pass) {
		if(id == 12345 && String.valueOf(pass).equals("setup")) {
			if(get.returnStorage().isEmpty()) {
				return true;
			} else {
				return false;
			}
		}
		if(get.getClass().getName() == "ems.LocalDataHandler") {
			String value = get.returnStorage().get(id);
			if(value == null) {
				return false;
			}
			int start = value.indexOf("Ps:") + 3;
			int end = value.indexOf("<!>PsEnd");
			char[] password = value.substring(start,end).toCharArray();
			if(Arrays.equals(pass, password)) {
				currentId = id;
				pass = null;
				password = null;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void getDependencies(Launcher launch) {
		get = (LocalDataHandler) launch.returnHandler();
		
	}



	@Override
	public void initiate() {
		//Unnessesary for this implementation.
		
	}
}

