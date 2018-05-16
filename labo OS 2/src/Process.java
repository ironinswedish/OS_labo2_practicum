import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

class Process {
	int pid;
	List<TablePageEntry> pageTable;
	int gealloceerd;
	Set<Integer> framenummers = new HashSet<Integer>();
	int schrijfOpdracht=0;
	int verwijderOpdracht=0;

	public Process(int p) {
		pid = p;
		pageTable = new ArrayList<TablePageEntry>();

		for (int a = 0; a < 16; a++) {
			pageTable.add(new TablePageEntry());
		}
	}
	
	public Process(){
		pid =-1;
		pageTable = new ArrayList<TablePageEntry>();
		for (int a = 0; a < 16; a++) {
			pageTable.add(new TablePageEntry());
		}
	}
	
	public Process(Process p){
		pid =p.pid;
		
		gealloceerd = p.gealloceerd;
		framenummers = p.framenummers;
		pageTable = new ArrayList<TablePageEntry>();
		for (int a = 0; a < 16; a++) {
			pageTable.add(new TablePageEntry(p.pageTable.get(a)));
		}
		
	}

	public void printFramenummers() {
		for (Integer i : framenummers) {
			System.out.print(i + " ");
		}
	}

	public void printTable() {
		System.out.println("________________________" + "PROCESS:" + pid + "_________________________");
		for (TablePageEntry tpe : pageTable) {
			tpe.printEntry();
			System.out.println();
		}
	}

	public List<Integer> verwijderFrames(int aantal) {

		List<Integer> vrijgekomenPlaatsen = new ArrayList<Integer>();
		Comparator<TablePageEntry> ATcomp = new AccesTimeComparator();
		List<Integer> vrijeFrames = new ArrayList<Integer>();
		vrijeFrames.addAll(framenummers);
		verwijderOpdracht=0;

		// Alle pageEntries die in het RAM zitten in de priorityqueue steken.
		// Vervolgens de eerste 'aantal' pages van deze queue eruit halen
		// Stel lijst te klein, kiezen voor random idle frames weg te geven.
		PriorityQueue<TablePageEntry> ATqueue = new PriorityQueue<TablePageEntry>(16, ATcomp);

		// Eerst de idle's checken om te verwijderen

		System.out.println("proces: " + pid);
		for (TablePageEntry tpe : pageTable) {
			if (tpe.getPresentBit() == 1) {
				ATqueue.add(tpe);
				vrijeFrames.remove((Integer) tpe.getFrameNummer());
			}
		}
		for (Integer i : vrijeFrames) {
			System.out.println(i);
		}

		int verwijderd = 0;
		for (int a = 0; a < aantal; a++) {
			if (!vrijeFrames.isEmpty()) {
				framenummers.remove(vrijeFrames.get(0));
				vrijgekomenPlaatsen.add(vrijeFrames.get(0));
				vrijeFrames.remove(0);
				verwijderd++;
			}
		}

		int aantalOver = aantal - verwijderd;
		for (int a = 0; a < aantal; a++) {
			if (!ATqueue.isEmpty() && aantalOver > 0) {
				ATqueue.peek().setPresentBit(0);
				ATqueue.peek().setModifyBit(0);
				framenummers.remove(ATqueue.peek().getFrameNummer());
				vrijgekomenPlaatsen.add(ATqueue.peek().getFrameNummer());
				ATqueue.remove();
				verwijderd++;
				aantalOver--;
				verwijderOpdracht++;
			}
		}

		return vrijgekomenPlaatsen;

	}

	public boolean checkAanwezigFrame(int page, boolean write, int clock) {
		if (pageTable.get(page).getPresentBit() == 1) {
			if (write) {
				pageTable.get(page).setModifyBit(1);
				pageTable.get(page).setLastAccesTime(clock);
			}
			return true;
		}
		return false;
	}

	public void vervangLU(int page, boolean write, int clock) {
		schrijfOpdracht=0;
		verwijderOpdracht=0;
		// we zoeken de of er nog vrije gealoceerde ruimte is in het RAM
		int count = 0;
		for (TablePageEntry t : pageTable) {
			if (t.getPresentBit() == 1)
				count++;
		}
		if (count == framenummers.size()) {
			// indien alle gealloceerde frames gebruikt worden zoeken we LRU
			// frame en vervangen we deze
			boolean first = true;
			TablePageEntry LU = null;
			TablePageEntry temp = null;
			int index = -1;
			for (int j = 0; j < pageTable.size(); j++) {
				temp = pageTable.get(j);
				if (temp.getPresentBit() == 1) {
					if (first) {
						LU = temp;
						index = j;
						first = false;
					} else if (temp.getLastAccesTime() < LU.getLastAccesTime()) {
						LU = temp;
						index = j;
					}
				}

			}
			pageTable.get(index).setPresentBit(0);
			pageTable.get(index).setModifyBit(0);
			int frame = pageTable.get(index).getFrameNummer();
			useFrame(page, clock, write, frame);
			
			verwijderOpdracht++;

		} else {
			boolean free = true;
			for (Integer i : framenummers) {
				free = true;
				//als frame nummer deel is van process checken naar de pages die het zelfde framenummer heeft staat deze op present dan is de frame niet vrij
				for (int j = 0; j < pageTable.size(); j++) {
					if (pageTable.get(j).getFrameNummer() == i && pageTable.get(j).getPresentBit() == 1) {
						free = false;
					}
				}
				if (free) {
					useFrame(page, clock, write, i);
					break;
				}
			}
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
		verwijderOpdracht = 0;
		List<Integer> lijst = new ArrayList<Integer>();
		for (TablePageEntry tpe : pageTable) {
			if (tpe.getFrameNummer() != -1) {
				lijst.add(tpe.getFrameNummer());
			}
			tpe.setFrameNummer(-1);
			tpe.setPresentBit(0);
			tpe.setModifyBit(0);
			verwijderOpdracht++;
		}
		return lijst;

	}

	public void getInRam(List<Integer> lijst) {
		framenummers = new HashSet<Integer>();
		for (Integer i : lijst) {
			framenummers.add(i);
		}

	}

	public void useFrame(int page, int clock, boolean write, int frame) {
		pageTable.get(page).setLastAccesTime(clock);
		pageTable.get(page).setPresentBit(1);
		pageTable.get(page).setFrameNummer(frame);
		if (write)
			pageTable.get(page).setModifyBit(1);
		else
			pageTable.get(page).setModifyBit(0);
	}

	public void addFrame(Integer integer) {
		framenummers.add(integer);

	}

}
