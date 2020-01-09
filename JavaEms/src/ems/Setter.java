package ems;

public interface Setter{
	void getDependencies(Launcher launch);
	//Not for low ranks(less than 2)
		boolean setPerformance(int id,int score);
		//Only for HIGH ranks. Return the auto increment id. (more than 3)
		int setNewEmployee(String passWord,String name,int rank);
		//Only for ADMIN (rank 5)
		boolean setRank(int id,int rank);
		void saveAttendance();
}
