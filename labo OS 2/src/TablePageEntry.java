
class TablePageEntry {
	int presentBit;
	int modifyBit;
	int lastAccesTime;
	int frameNummer;

	public TablePageEntry() {
		frameNummer = -1;
		lastAccesTime = -1;
		presentBit = 0;
		modifyBit = 0;
	}

	public TablePageEntry(int p, int m, int l, int f) {
		presentBit = p;
		modifyBit = m;
		lastAccesTime = l;
		frameNummer = f;
	}
	
	public TablePageEntry(TablePageEntry p){
		presentBit=p.presentBit;
		modifyBit = p.modifyBit;
		lastAccesTime=p.lastAccesTime;
		frameNummer = p.frameNummer;
	}

	public void setTableEntry(int clock, boolean write, int frame) {
		presentBit = 1;
		lastAccesTime = clock;
		frameNummer = frame;
		if (write)
			modifyBit = 1;
		else
			modifyBit = 0;
	}

	public void printEntry() {
		System.out.print("presentbit: " + presentBit + " modifybit: " + modifyBit + " framenummer:" + frameNummer
				+ " la: " + lastAccesTime);
	}

	public int getPresentBit() {
		return presentBit;
	}

	public void setPresentBit(int presentBit) {
		this.presentBit = presentBit;
	}

	public int getModifyBit() {
		return modifyBit;
	}

	public void setModifyBit(int modifyBit) {
		this.modifyBit = modifyBit;
	}

	public int getLastAccesTime() {
		return lastAccesTime;
	}

	public void setLastAccesTime(int lastAccesTime) {
		this.lastAccesTime = lastAccesTime;
	}

	public int getFrameNummer() {
		return frameNummer;
	}

	public void setFrameNummer(int frameNummer) {
		this.frameNummer = frameNummer;
	}
}
