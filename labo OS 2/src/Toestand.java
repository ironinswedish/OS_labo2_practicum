import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Toestand {
	ArrayList<Process> processenlijst;
	int shrijfopdracht;
	int verwijderopdracht;
	int clock;
	String instructie;
	int huidigAdres;
	int huidigReeelAdres;
	int volgendAdres;
	int volgendReeelAdres;
	int frame;
	int offset;
	int volgendFrame;
	int volgendOffset;
	Ram ram;
	Process huidigProces;
	int volgendProces;
	String vOperatie;
	Set<Process> aanwezigeProcessen;

	public Toestand() {

	}

	public Toestand(int shrijfopdracht, int verwijderopdracht, int clock, String instr, int hAdres, int vAdres, Ram ram,
			Set<Process> aanwezigeProcessen, Process hp, List<Process> proclijst, String vOperatie, int vp) {
		this.shrijfopdracht = shrijfopdracht;
		this.verwijderopdracht = verwijderopdracht;
		this.clock = clock;
		this.huidigAdres = hAdres;
		this.instructie = instr;
		int pagenumber = (int) Math.floor(hAdres / Math.pow(2, 12));
		int volgendPagenumber = (int) Math.floor(vAdres / Math.pow(2, 12));

		this.frame = hp.pageTable.get((int) Math.floor(hAdres / Math.pow(2, 12))).frameNummer;
		this.offset = (int) (((hAdres / Math.pow(2, 12)) - pagenumber) * Math.pow(2, 12));

		this.volgendAdres = vAdres;
		if (vp == hp.pid) {
			this.volgendFrame = hp.pageTable.get((int) Math.floor(vAdres / Math.pow(2, 12))).frameNummer;
		} else {
			this.volgendFrame = -1;
		}

		this.volgendOffset = (int) (((vAdres / Math.pow(2, 12)) - volgendPagenumber) * Math.pow(2, 12));

		this.ram = ram;
		this.aanwezigeProcessen = aanwezigeProcessen;
		this.huidigProces = hp;
		this.processenlijst = new ArrayList<Process>();
		for (int i = 0; i < proclijst.size(); i++) {
			processenlijst.add(new Process(proclijst.get(i)));
		}
		this.volgendProces = vp;
		this.vOperatie = vOperatie;
	}

	public int getShrijfopdracht() {
		return shrijfopdracht;
	}

	public void setShrijfopdracht(int shrijfopdracht) {
		this.shrijfopdracht = shrijfopdracht;
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public String getInstructie() {
		return instructie;
	}

	public void setInstructie(String instructie) {
		this.instructie = instructie;
	}

	public int getHuidigAdres() {
		return huidigAdres;
	}

	public void setHuidigAdres(int huidigAdres) {
		this.huidigAdres = huidigAdres;
	}

	public int getHuidigReeelAdres() {
		return huidigReeelAdres;
	}

	public void setHuidigReeelAdres(int huidigReeelAdres) {
		this.huidigReeelAdres = huidigReeelAdres;
	}

	public int getVolgendAdres() {
		return volgendAdres;
	}

	public void setVolgendAdres(int volgendAdres) {
		this.volgendAdres = volgendAdres;
	}

	public int getVolgendReeelAdres() {
		return volgendReeelAdres;
	}

	public void setVolgendReeelAdres(int volgendReeelAdres) {
		this.volgendReeelAdres = volgendReeelAdres;
	}

	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Ram getRam() {
		return ram;
	}

	public void setRam(Ram ram) {
		this.ram = ram;
	}

	public Set<Process> getAanwezigeProcessen() {
		return aanwezigeProcessen;
	}

	public void setAanwezigeProcessen(Set<Process> aanwezigeProcessen) {
		this.aanwezigeProcessen = aanwezigeProcessen;
	}

}
