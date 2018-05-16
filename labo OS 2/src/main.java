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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class main extends Application {
	static String getFrom;
	static Ram RAM = new Ram();
	static List<Process> processenlijst = new ArrayList<Process>();
	static int clock = 0;
	static int adres;
	static int schrijfopdracht = 0;
	static int pid;
	static ArrayList<Toestand> toestandenLijst = new ArrayList<Toestand>();
	static ArrayList<Toestand> toestandenLijst1 = new ArrayList<Toestand>();
	static ArrayList<Toestand> toestandenLijst2 = new ArrayList<Toestand>();
	static ArrayList<Toestand> toestandenLijst3 = new ArrayList<Toestand>();
	static List<Instructie> instructielijst = new ArrayList<Instructie>();
	static int huidigeInst;
	static int GUIstap = 0;
	static int verwijderopdracht = 0;

	@FXML
	private Label timer;
	@FXML
	private Label schrijf;
	@FXML
	private Label proces;
	@FXML
	private Label proces2;
	@FXML
	private Label instructie;
	@FXML
	private Label hvAdres;
	@FXML
	private Label hFrame;
	@FXML
	private Label hOffset;
	@FXML
	private Label vVAdres;
	@FXML
	private Label vFrame;
	@FXML
	private Label vOffset;
	@FXML
	private Label verwijder;
	@FXML
	private Label totaal;
	@FXML
	private Label vRAdres;
	@FXML
	private Label hRAdres;
	@FXML
	private Label vProces;
	@FXML
	private Label vInstructie;

	// pageTabel view
	@FXML
	private TableView<TabelEntry> pageTable;
	@FXML
	private TableColumn<TabelEntry, Integer> pagenr;
	@FXML
	private TableColumn<TabelEntry, Integer> present;
	@FXML
	private TableColumn<TabelEntry, Integer> modify;
	@FXML
	private TableColumn<TabelEntry, Integer> acces;
	@FXML
	private TableColumn<TabelEntry, Integer> framenr;

	@FXML
	private TableView<TabelEntry> ramTable;
	@FXML
	private TableColumn<TabelEntry, Integer> rframenr;
	@FXML
	private TableColumn<TabelEntry, Integer> rpagenr;
	@FXML
	private TableColumn<TabelEntry, Integer> rPid;

	public static void main(String[] args) {

		leesEnVoerUit("Instructions_30_3.xml");
		leesEnVoerUit("Instructions_20000_4.xml");
		leesEnVoerUit("Instructions_20000_20.xml");
		launch(args);

	}

	public static void leesEnVoerUit(String file) {
		schrijfopdracht = 0;
		verwijderopdracht = 0;
		clock = 0;
		instructielijst = new ArrayList<Instructie>();
		processenlijst = new ArrayList<Process>();
		RAM = new Ram();
		String at;
		Instructie p;
		getFrom = file;

		Map<String, Runnable> functies = new HashMap<String, Runnable>();
		functies.put("Start", () -> doeStart());
		functies.put("Read", () -> doeRead());
		functies.put("Write", () -> doeWrite());
		functies.put("Terminate", () -> doeTerminate());

		try {

			File fXmlFile = new File(file);
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
				pid = instructielijst.get(i).pid;
				adres = instructielijst.get(i).adress;
				functies.get(instructielijst.get(i).operation).run();
				clock++;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Scheduling_User_Interface.fxml"));

		Scene scene = new Scene(root, 1500, 1000);

		stage.setTitle("FXML Welcome");
		stage.setScene(scene);
		// stage.setFullScreen(true);
		stage.show();
	}

	public void vooruitEen(ActionEvent event) {
		if (GUIstap < toestandenLijst.size()) {
			Toestand toestand = toestandenLijst.get(GUIstap);
			setGUI(toestand);
		}

		GUIstap++;
	}

	public void achteruitEen(ActionEvent event) {
		GUIstap--;
		if (GUIstap < toestandenLijst.size() && GUIstap > -1) {
			Toestand toestand = toestandenLijst.get(GUIstap);
			setGUI(toestand);
		}
	}

	public void vooruitAlles(ActionEvent event) {
		Toestand toestand = toestandenLijst.get(toestandenLijst.size() - 1);
		setGUI(toestand);
		GUIstap = toestandenLijst.size();
	}

	public void andereFile(ActionEvent event) {
		System.out.println("andere");
		String file = "Instructions_20000_4.xml";
		schrijfopdracht = 0;
		verwijderopdracht = 0;
		//leesEnVoerUit(file);
		GUIstap = 0;
		toestandenLijst=toestandenLijst2;
		vooruitEen(new ActionEvent());
	}

	public void laatsteFile(ActionEvent event) {
		System.out.println("laatste");
		String file = "Instructions_20000_20.xml";
		schrijfopdracht = 0;
		verwijderopdracht = 0;
	//	leesEnVoerUit(file);
		GUIstap = 0;
		toestandenLijst=toestandenLijst3;
		vooruitEen(new ActionEvent());
	}

	public void eersteFile(ActionEvent event) {
		System.out.println("eerste");
		String file = "Instructions_30_3.xml";
		schrijfopdracht = 0;
		verwijderopdracht = 0;
	//	leesEnVoerUit(file);
		GUIstap = 0;
		toestandenLijst=toestandenLijst1;
		vooruitEen(new ActionEvent());
	}

	public void setGUI(Toestand toestand) {
		int writeRemove = toestand.shrijfopdracht + toestand.verwijderopdracht;
		int volgendReeeladres;
		int reeeladres;
		if (toestand.frame == -1) {
			reeeladres = 0;

		} else {
			reeeladres = toestand.frame * 4096 + toestand.offset;

		}
		if (toestand.volgendFrame == -1) {
			volgendReeeladres = 0;
		} else {
			volgendReeeladres = toestand.volgendFrame * 4096 + toestand.volgendOffset;
		}
		timer.setText(String.valueOf(toestand.clock));
		schrijf.setText(String.valueOf(toestand.shrijfopdracht));
		verwijder.setText(String.valueOf(toestand.verwijderopdracht));
		totaal.setText(String.valueOf(writeRemove));
		proces.setText(String.valueOf(toestand.huidigProces.pid));
		proces2.setText(String.valueOf(toestand.huidigProces.pid));
		instructie.setText(String.valueOf(toestand.instructie));
		hvAdres.setText(String.valueOf(toestand.huidigAdres));
		hFrame.setText(String.valueOf(toestand.frame));
		hOffset.setText(String.valueOf(toestand.offset));
		vVAdres.setText(String.valueOf(toestand.volgendAdres));
		vFrame.setText(String.valueOf(toestand.volgendFrame));
		vOffset.setText(String.valueOf(toestand.volgendOffset));
		hRAdres.setText(String.valueOf(reeeladres));
		vRAdres.setText(String.valueOf(volgendReeeladres));
		vProces.setText(String.valueOf(toestand.volgendProces));
		vInstructie.setText(String.valueOf(toestand.vOperatie));
		setRamTable(toestand);
		setPageTable(toestand);
	}

	public static void voegToestandToe() {

		Instructie inst = instructielijst.get(huidigeInst);

		Set<Process> aanwezigeProc = new HashSet<Process>();
		Toestand toestand = null;

		for (int j = 0; j < RAM.processen.length; j++) {
			if (RAM.processen[j] != -1) {
				aanwezigeProc.add(processenlijst.get(RAM.processen[j]));
			}
		}

		if (huidigeInst < instructielijst.size() - 1) {
			Instructie vInst = instructielijst.get(huidigeInst + 1);
			toestand = new Toestand(schrijfopdracht, verwijderopdracht, clock, inst.operation, inst.adress,
					vInst.adress, new Ram(RAM), aanwezigeProc, new Process(processenlijst.get(inst.pid)),
					processenlijst, vInst.operation, vInst.pid);
		} else {
			toestand = new Toestand(schrijfopdracht, verwijderopdracht, clock, inst.operation, inst.adress, 0, RAM,
					aanwezigeProc, new Process(processenlijst.get(inst.pid)), processenlijst, "X", -1);
		}
		if (getFrom.equals("Instructions_30_3.xml")) {
			toestandenLijst1.add(toestand);
		}else if (getFrom.equals("Instructions_20000_4.xml")){
			toestandenLijst2.add(toestand);
		}else{
			toestandenLijst3.add(toestand);
		}
		
	}

	public void setRamTable(Toestand toestand) {
		ramTable.getItems().clear();
		Process p = null;
		boolean inRam;

		for (int z = 0; z < toestand.ram.processen.length; z++) {
			inRam = false;
			if (toestand.ram.processen[z] != -1) {
				p = toestand.processenlijst.get(toestand.ram.processen[z]);
				for (int d = 0; d < p.pageTable.size(); d++) {
					if (p.pageTable.get(d).frameNummer == z) {
						ramTable.getItems().add(new TabelEntry(d, z, p.pid));
						inRam = true;
						break;
					}

				}
				if (!inRam) {
					ramTable.getItems().add(new TabelEntry(-1, z, p.pid));
				}

			} else {
				ramTable.getItems().add(new TabelEntry(-1, z, -1));
			}
		}
		rframenr.setCellValueFactory(new PropertyValueFactory<>("frameNummer"));
		rpagenr.setCellValueFactory(new PropertyValueFactory<>("pageNummer"));
		rPid.setCellValueFactory(new PropertyValueFactory<>("procesid"));

	}

	public void setPageTable(Toestand toestand) {
		pageTable.getItems().clear();
		Process p = toestand.huidigProces;
		for (int z = 0; z < p.pageTable.size(); z++) {
			pageTable.getItems().add(new TabelEntry(z, p.pageTable.get(z).presentBit, p.pageTable.get(z).modifyBit,
					p.pageTable.get(z).lastAccesTime, p.pageTable.get(z).frameNummer));
		}
		framenr.setCellValueFactory(new PropertyValueFactory<>("frameNummer"));
		pagenr.setCellValueFactory(new PropertyValueFactory<>("pageNummer"));
		acces.setCellValueFactory(new PropertyValueFactory<>("lastAccesTime"));
		modify.setCellValueFactory(new PropertyValueFactory<>("modifyBit"));
		present.setCellValueFactory(new PropertyValueFactory<>("presentBit"));

	}

	public static void doeStart() {
		Process p = new Process(pid);
		System.out.println("procesid " + p.pid);
		processenlijst.add(pid, p);
		LRUStart(-1, false);
		voegToestandToe();
		System.out.println("Ik doe start");
	}

	public static void doeRead() {
		int page = getPage(adres);
		// we kijken of de page nog niet aanwezig is in het RAM geheugen
		if (!processenlijst.get(pid).checkAanwezigFrame(page, false, clock)) {
			// indien er nog geen page van dit proces in het RAM geheugen zit
			// moet er plaats worden gemaakt
			if (processenlijst.get(pid).framenummers.size() == 0) {
				LRUStart(page, false);
			} else {
				LRUReadWrite(page, false);
			}
		}

		voegToestandToe();
	}

	public static void doeWrite() {
		System.out.println("Ik doe write");
		int frame = getPage(adres);
		if (!processenlijst.get(pid).checkAanwezigFrame(frame, true, clock)) {

			if (processenlijst.get(pid).framenummers.size() == 0) {
				LRUStart(frame, true);
			} else {
				System.out.println("extra toevoegen " + frame + " " + adres + " " + pid);
				LRUReadWrite(frame, true);
			}
		}

		voegToestandToe();
	}

	public static void doeTerminate() {

		RAM.verwijderProcess(pid, processenlijst);

		voegToestandToe();
		System.out.println("Ik doe terminate");
	}

	public static void LRUStart(int page, boolean write) {

		System.out.println("LRU");

		RAM.nieuwProcess(pid, processenlijst);
		System.out.println("verwijderopdracht 1 " + verwijderopdracht);
		verwijderopdracht = verwijderopdracht + RAM.verwijderOpdracht;
		System.out.println("verwijderopdracht 2 " + verwijderopdracht);

		int frame = RAM.getFrameFrom(pid);
		if (page != -1) {
			processenlijst.get(pid).useFrame(page, clock, write, frame);
			schrijfopdracht++;
		}

	}

	public static void LRUReadWrite(int page, boolean write) {
		/*
		 * fragment met laagste accesTime dat van proces zelf is
		 */

		processenlijst.get(pid).vervangLU(page, write, clock);
		System.out.println("page is " + page);
		verwijderopdracht = verwijderopdracht + processenlijst.get(pid).verwijderOpdracht;
		System.out.println("LRU");
		schrijfopdracht++;

	}

	public static void printFrames() {
		for (Process process : processenlijst) {
			System.out.println("Process id: " + process.pid);
			process.printFramenummers();
			System.out.println();
		}
		System.out.println();
	}

	public static int getPage(int st) {
		double temp = (double) st / 4096;
		// indien makkelijker-> veranderen
		int temp2 = st / 4096;
		double offset = (temp - (double) temp2) * 4096;

		return temp2;
	}

}
