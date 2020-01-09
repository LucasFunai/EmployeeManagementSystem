package ems;


//third one to boot
 public interface Login{
	//If there is no accounts, accept "12345" as id and "setup" as password
	//If there is, evaluate using storage.
	 void getDependencies(Launcher launch);
	boolean userIsValid(int Username, char[] Password);
	int currentId();
	void initiate();
	
}
