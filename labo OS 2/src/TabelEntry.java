
public class TabelEntry {

	private int pageNummer;
	

	private int presentBit;
	private int modifyBit;
	private int lastAccesTime;
	private int frameNummer;
	private int procesid;

	public TabelEntry(int pnr, int p, int m, int l, int fnr) {
		pageNummer = pnr;
		presentBit = p;
		modifyBit = m;
		lastAccesTime = l;
		frameNummer = fnr;
	}

	public TabelEntry(int pageNummer, int frameNummer, int procesid) {
		this.pageNummer = pageNummer;
		this.frameNummer = frameNummer;
		this.procesid = procesid;
	}

	public int getPageNummer() {
		return pageNummer;
	}

	public int getPresentBit() {
		return presentBit;
	}

	public int getModifyBit() {
		return modifyBit;
	}

	public int getLastAccesTime() {
		return lastAccesTime;
	}

	public int getFrameNummer() {
		return frameNummer;
	}

	public int getProcesid() {
		return procesid;
	}


}