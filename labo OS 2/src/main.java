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
	static Ram RAM = new Ram();
	static List<Process> processenlijst = new ArrayList<Process>();
	static int clock = 0;
	static int adres;
	static int schrijfopdracht;
	static int pid;
	static ArrayList<Toestand> toestandenLijst = new ArrayList<Toestand>();
	static List<Instructie> instructielijst = new ArrayList<Instructie>();
	static int huidigeInst;
	static int GUIstap = 0;

	@FXML
	private Label timer;
	@FXML
	private Label schrijf;
	@FXML
	private Label proces;
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
				pid=instructielijst.get(i).pid;
				functies.get(instructielijst.get(i).operation).run();
				clock++;
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Process proc: processenlijst){
			System.out.println(proc.pid);
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
		if (GUIstap < toestandenLijst.size()) {
			Toestand toestand = toestandenLijst.get(GUIstap);
			timer.setText(String.valueOf(toestand.clock));
			schrijf.setText(String.valueOf(toestand.shrijfopdracht));
			proces.setText(String.valueOf(toestand.huidigProces.pid));
			instructie.setText(String.valueOf(toestand.instructie));
			hvAdres.setText(String.valueOf(toestand.huidigAdres));
			hFrame.setText(String.valueOf(toestand.frame));
			hOffset.setText(String.valueOf(toestand.offset));
			vVAdres.setText(String.valueOf(toestand.volgendAdres));
			vFrame.setText(String.valueOf(toestand.volgendFrame));
			vOffset.setText(String.valueOf(toestand.volgendOffset));
			setRamTable(toestand);
			setPageTable(toestand);
		}

		GUIstap++;
	}

	public void vooruitAlles(ActionEvent event) {
		System.out.println("test 2");
	}

	public void setRamTable(Toestand toestand) {
		ramTable.getItems().clear();
		Process p = null;
		for (int z = 0; z < toestand.ram.processen.length; z++) {
			if (toestand.ram.processen[z] != -1) {
				System.out.println(toestand.ram.processen[z]);
				p = processenlijst.get(toestand.ram.processen[z]);
				for (int d = 0; d < p.pageTable.size(); d++) {
					if (p.pageTable.get(d).frameNummer == z) {
						ramTable.getItems().add(new TabelEntry(d, z, p.pid));
					}
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
				pageTable.getItems().add(new TabelEntry(z,p.pageTable.get(z).presentBit,p.pageTable.get(z).modifyBit,p.pageTable.get(z).lastAccesTime,p.pageTable.get(z).frameNummer));
		}
		framenr.setCellValueFactory(new PropertyValueFactory<>("frameNummer"));
		pagenr.setCellValueFactory(new PropertyValueFactory<>("pageNummer"));
		acces.setCellValueFactory(new PropertyValueFactory<>("lastAccesTime"));
		modify.setCellValueFactory(new PropertyValueFactory<>("modifyBit"));
		present.setCellValueFactory(new PropertyValueFactory<>("presentBit"));

	}

	public static void doeStart() {
		Process p = new Process(pid);
		System.out.println("procesid " +p.pid);
		processenlijst.add(pid, p);
		LRUStart();
		voegToestandToe();
		System.out.println("Ik doe start");
	}

	public static void doeRead() {
		System.out.println("Ik doe read");
	}

	public static void doeWrite() {
		System.out.println("Ik doe write");
	}

	public static void doeTerminate() {

		RAM.verwijderProcess(pid, processenlijst);
		voegToestandToe();
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

	public static void voegToestandToe() {
		Instructie inst = instructielijst.get(huidigeInst);
		Set<Process> aanwezigeProc = new HashSet<Process>();
		for (int j = 0; j < RAM.processen.length; j++) {
			if (RAM.processen[j] != -1) {
				aanwezigeProc.add(processenlijst.get(RAM.processen[j]));
			}
		}
		System.out.println(huidigeInst);
		System.out.println(inst.pid);
		System.out.println( processenlijst.get(inst.pid).pid);
		Toestand toestand = null;
		if (huidigeInst < instructielijst.size() - 1) {
			toestand = new Toestand(0, clock, inst.operation, inst.adress, instructielijst.get(huidigeInst + 1).adress,
					new Ram(RAM), aanwezigeProc, processenlijst.get(inst.pid));
		} else {
			toestand = new Toestand(0, clock, inst.operation, inst.adress, 0, RAM, aanwezigeProc,
					processenlijst.get(inst.pid));
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





