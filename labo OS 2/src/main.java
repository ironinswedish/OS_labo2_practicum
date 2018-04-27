import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {
	static Ram RAM = new Ram();
	static List<Process> processenlijst = new ArrayList<Process>();
	static int clock =0;
	static int adres;
	static int schrijfopdracht;
	static int pid = 0;
	static ArrayList<Toestand> toestandenLijst = new ArrayList<Toestand>();
	static List<Instructie> instructielijst = new ArrayList<Instructie>();
	static int huidigeInst;

	public static void main(String[] args) {

		String at;
		Instructie p;

		Map<String, Runnable> functies = new HashMap<String, Runnable>();
		functies.put("Start", () -> doeStart());
		functies.put("Read", () -> doeRead());
		functies.put("Write", () -> doeWrite());
		functies.put("Terminate", () -> doeTerminate());

		try {

			File fXmlFile = new File("Instructions_30_3.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("instruction");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					pid = Integer.parseInt(eElement.getElementsByTagName("processID").item(0).getTextContent());
					at = eElement.getElementsByTagName("operation").item(0).getTextContent();
					adres = Integer.parseInt(eElement.getElementsByTagName("address").item(0).getTextContent());

					p = new Instructie(pid, at, adres);
					instructielijst.add(p);

				}
			}

			for (int i = 0; i < instructielijst.size(); i++) {
				huidigeInst = i;
				functies.get(instructielijst.get(i).operation).run();
				clock++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Scheduling_User_Interface.fxml"));

		Scene scene = new Scene(root, 300, 275);

		stage.setTitle("FXML Welcome");
		stage.setScene(scene);
		stage.show();
	}

	public void vooruitEen(ActionEvent event) {
		System.out.println("test");
		System.out.println(toestandenLijst.size());
	}

	public void vooruitAlles(ActionEvent event) {
		System.out.println("test 2");
	}

	public static void doeStart() {
		Process p = new Process(pid);
		processenlijst.add(pid, p);
		LRUStart();
		System.out.println("Ik doe start");
	}

	public static void doeRead() {
		System.out.println("Ik doe read");
	}

	public static void doeWrite() {
		System.out.println("Ik doe write");
	}

	public static void doeTerminate() {
<<<<<<< HEAD
		RAM.verwijderProcess(pid,processenlijst);
=======
>>>>>>> refs/remotes/origin/master
		System.out.println("Ik doe terminate");
	}

	public static void LRUStart() {

		System.out.println("LRU");

		RAM.nieuwProcess(pid, processenlijst);
		
		
		voegToestandToe();
		

		/*
		 * 4 processen in ram -> 1 proces verwijderen => met laagste totale
		 * acces Time 0-3 processen => per proces de 2^(3-n) met laagste acces
		 * Time
		 */

	}
	
	public static void voegToestandToe(){
		Instructie inst = instructielijst.get(huidigeInst);
		List<Process> aanwezigeProc = new ArrayList<Process>();
		for (int j=0; j<RAM.processen.length; j++){
			aanwezigeProc.add(processenlijst.get(RAM.processen[j]));
		}
		Toestand toestand = null;
		if (huidigeInst < instructielijst.size() - 1) {
			toestand = new Toestand(0, clock, inst.operation, inst.adress, instructielijst.get(huidigeInst + 1).adress,
					new Ram(RAM),aanwezigeProc);
		} else {
			toestand = new Toestand(0, 1, inst.operation, inst.adress, 0,
					RAM,aanwezigeProc);
		}
		toestandenLijst.add(toestand);
	}

	public static void LRULaagstTotaal() {
		/*
		 * totale acces time van fragments in proces optellen -> gene met
		 * laagste vervangen
		 * 
		 * 
		 */
	}

	public static void LRULaagsteFragments(int aantalProc) {
		/*
		 * de 2^(3-n) fragments met laagste accesTime uit ram halen
		 */
	}

	public static void LRUReadWrite() {
		/*
		 * fragment met laagste accesTime dat van proces zelf is
		 */
	}

	public static int aantalProcInRam() {
		return 1;
	}
}

class Process {
	int pid;
	List<TablePageEntry> pageTable;
	int gealloceerd;
	Set<Integer> framenummers = new HashSet<Integer>();

	public Process(int p) {
		pid = p;
		pageTable = new ArrayList<TablePageEntry>();
		for (int a = 0; a < 15; a++) {
			pageTable.add(new TablePageEntry());

		}
	}

	public void setGealloceerd(int a) {
		gealloceerd = a;
	}

	public int getMaxAccesTime() {
		int max = 0;
		for (TablePageEntry tpe : pageTable) {
			if (tpe.getLastAccesTime() > max && tpe.getPresentBit() == 1) {
				max = tpe.getLastAccesTime();
			}
		}
		return max;
	}

	public List<Integer> verwijderFrames(int aantal) {
		List<Integer> vrijgekomenPlaatsen = new ArrayList<Integer>();
		Comparator<TablePageEntry> ATcomp = new AccesTimeComparator();

		// Alle pageEntries die in het RAM zitten in de priorityqueue steken.
		// Vervolgens de eerste 'aantal' pages van deze queue eruit halen
		// Stel lijst te klein, kiezen voor random idle frames weg te geven.
		PriorityQueue<TablePageEntry> ATqueue = new PriorityQueue<TablePageEntry>(16, ATcomp);

		for (TablePageEntry tpe : pageTable) {
			if (tpe.getPresentBit() == 1) {
				ATqueue.add(tpe);
			}
		}
		int verwijderd = 0;
		int aantalOver = 0;
		for (int a = 0; a < aantal; a++) {
			if (!ATqueue.isEmpty()) {
				ATqueue.peek().setPresentBit(0);
				ATqueue.peek().setModifyBit(0);
				framenummers.remove(ATqueue.peek().getFrameNummer());
				vrijgekomenPlaatsen.add(ATqueue.peek().getFrameNummer());
				ATqueue.remove();
				verwijderd++;
			}
		}
		aantalOver = aantal - verwijderd;
		for (Integer i : framenummers) {
			if (aantalOver > 0) {
				vrijgekomenPlaatsen.add(i);
				framenummers.remove(i);
				aantalOver--;
			} else {
				break;
			}
		}
		return vrijgekomenPlaatsen;

	}

	public List<Integer> removeOutOfRam() {
		List<Integer> lijst = new ArrayList<Integer>();
		for (TablePageEntry tpe : pageTable) {
			if (tpe.getFrameNummer() != -1) {
				lijst.add(tpe.getFrameNummer());
			}
			tpe.setFrameNummer(-1);
			tpe.setPresentBit(0);
			tpe.setModifyBit(0);
			// SchrijfOpdrachtenVerhoogd
		}
		return lijst;
	}

	public void getInRam(List<Integer> lijst) {
		framenummers = new HashSet<Integer>();
		for (Integer i : lijst) {
			framenummers.add(i);
		}

	}

	public void addFrame(Integer integer) {
		framenummers.add(integer);

	}

}

class AccesTimeComparator implements Comparator<TablePageEntry> {
	@Override
	public int compare(TablePageEntry o1, TablePageEntry o2) {
		return o1.getLastAccesTime() - o2.getLastAccesTime();
	}
}

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

class Instructie {
	String operation;
	int pid;
	int adress;

	public Instructie() {
	}

	public Instructie(int a, String o, int b) {
		a = pid;
		operation = o;
		adress = b;
	}

}

class Toestand {

	int shrijfopdracht;
	int clock;
	String instructie;
	int huidigAdres;
	int huidigReeelAdres;
	int volgendAdres;
	int volgendReeelAdres;
	int frame;
	int offset;
	Ram ram;
	List<Process> aanwezigeProcessen;

	public Toestand() {

	}

	public Toestand(int shrijfopdracht, int clock, String instr, int hAdres, int vAdres, Ram ram,
			List<Process> aanwezigeProcessen) {
		this.shrijfopdracht = shrijfopdracht;
		this.clock = clock;
		this.huidigAdres = hAdres;
		this.instructie = instr;
		this.frame = (int) Math.floor(hAdres/Math.pow(2, 12));
		this.offset = (int) ((hAdres/Math.pow(2, 12)-frame)*Math.pow(2, 12));
		this.volgendAdres = vAdres;
		this.frame = (int) Math.floor(vAdres/Math.pow(2, 12));
		this.offset = (int) ((vAdres/Math.pow(2, 12)-frame)*Math.pow(2, 12));
		this.ram = ram;
		this.aanwezigeProcessen = aanwezigeProcessen;
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

	public List<Process> getAanwezigeProcessen() {
		return aanwezigeProcessen;
	}

	public void setAanwezigeProcessen(List<Process> aanwezigeProcessen) {
		this.aanwezigeProcessen = aanwezigeProcessen;
	}

}

class Ram {
	int aantalProc;
	int[] processen;
	Set<Integer> processenIds = new HashSet<Integer>();
	

	public Ram() {
		processen = new int[12];
		aantalProc = 0;
	}
	
	public Ram(Ram ram){
		aantalProc=ram.aantalProc;
		processen = ram.processen;
		processenIds=ram.processenIds;
	}
	

	public void nieuwProcess(int id, List<Process> processenlijst) {
		if (aantalProc == 4) {
			int laagsteClock = 100000000;
			int pid = 0;
			for (Integer i : processenIds) {
				if (processenlijst.get(i).getMaxAccesTime() < laagsteClock) {
					pid = i;
					laagsteClock = processenlijst.get(i).getMaxAccesTime();
				}
			}
			processenIds.remove(pid);
			processenIds.add(id);
			List<Integer> lijst = processenlijst.get(pid).removeOutOfRam();
			processenlijst.get(id).getInRam(lijst);

			for (Integer i : lijst) {
				processen[i] = id;
			}

		} else if (aantalProc == 0) {
			List<Integer> alleFrames = new ArrayList<Integer>();
			alleFrames.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
			processenlijst.get(id).getInRam(alleFrames);
			for (int a = 0; a < 12; a++) {
				processen[a] = id;
			}
			aantalProc++;
		} else {
			List<Integer> vrijgekomenFrames = new ArrayList<Integer>();
			int teVerwijderenPerProcess = (12 / aantalProc - 12 / (aantalProc + 1));
			for (Integer i : processenIds) {
				vrijgekomenFrames.addAll(processenlijst.get(i).verwijderFrames(teVerwijderenPerProcess));

			}
			for (Integer i : vrijgekomenFrames) {
				processen[i] = id;
			}

			processenlijst.get(id).getInRam(vrijgekomenFrames);
			aantalProc++;
		}
	}

	public int getAantalProc() {
		return aantalProc;
	}

	public void setAantalProc(int aantalProc) {
		this.aantalProc = aantalProc;
	}

	public int[] getProcessen() {
		return processen;
	}

	public void setProcessen(int[] processen) {
		this.processen = processen;
	}

	public void verwijderProcess(int pid, List<Process> processenlijst) {
		List<Integer> vrijgekomenFrames = new ArrayList<Integer>();
		for (int a = 0; a < 12; a++) {
			if (processen[a] == pid) {
				processen[a] = -1;
				vrijgekomenFrames.add(a);
			}
		}
		aantalProc--;
		processenIds.remove(pid);

		for (Integer i : processenIds) {
			for (int a = 0; a < 12 / ((aantalProc + 1) * aantalProc); a++) {
				processenlijst.get(i).addFrame(vrijgekomenFrames.get(0));
				processen[vrijgekomenFrames.get(0)] = i;
				vrijgekomenFrames.remove(vrijgekomenFrames.get(0));
			}
		}
		processenlijst.get(pid).removeOutOfRam();
		// SchrijvenNaarPersistentGeheugen++

	}
}
