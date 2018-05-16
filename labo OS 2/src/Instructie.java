class Instructie {
	String operation;
	int pid;
	int adress;

	public Instructie() {
	}

	public Instructie(int a, String o, int b) {
		pid = a;
		operation = o;
		adress = b;
	}

	public int getPid() {
		return pid;
	}

	public int getAdress() {
		return adress;
	}

}