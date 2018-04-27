import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	static int clock;
	static int adres;
	static int schrijfopdracht;
	static int pid = 0;

	public static void main(String[] args) {

		String at;
		Instructie p;
		List<Instructie> instructielijst = new ArrayList<Instructie>();

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
					functies.get(at).run();
				}
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
		System.out.println("Ik doe terminate");
	}

	public static void LRUStart() {

		System.out.println("LRU");

		RAM.nieuwProcess(pid, processenlijst);

		/*
		 * 4 processen in ram -> 1 proces verwijderen => met laagste totale
		 * acces Time 0-3 processen => per proces de 2^(3-n) met laagste acces
		 * Time
		 */

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
		TablePageEntry t;
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

class toestand {

	int shrijfopdracht;
	int clock;
	String instructie;
	int adres;
	int reeelAdres;
	int frame;
	int offset;
	Ram ram;
	List<Process> aanwezigeProcessen;

	public toestand(int shrijfopdracht, int clock, String instructie, int adres, int reeelAdres, Ram ram,
			List<Process> aanwezigeProcessen) {
		this.shrijfopdracht = shrijfopdracht;
		this.clock = clock;
		this.instructie = instructie;
		this.adres = adres;
		this.reeelAdres = reeelAdres;
		this.ram = ram;
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
}
