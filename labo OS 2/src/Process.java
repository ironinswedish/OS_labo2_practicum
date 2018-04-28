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

	public Process(int p) {
		pid = p;
		pageTable = new ArrayList<TablePageEntry>();
		for (int a = 0; a < 16; a++) {
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
