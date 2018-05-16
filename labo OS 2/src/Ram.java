import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Ram {
	int aantalProc;
	int[] processen;
	Set<Integer> processenIds = new HashSet<Integer>();
	int verwijderOpdracht;

	public Ram() {
		processen = new int[12];
		aantalProc = 0;
	}
	
	public Ram(Ram ram){
		aantalProc = ram.aantalProc;
		processen = new int[12];
		for (int i=0;i<ram.processen.length;i++){
			processen[i]=ram.processen[i];
		}
		processenIds = new HashSet<Integer>();
		for (Integer id:processenIds){
			processenIds.add(id);
		}
		
	}

	public void printRam() {
		for (int i = 0; i < 12; i++) {
			System.out.print(processen[i] + " ");
		}
		System.out.println();
	}

	public int getFrameFrom(int pid) {
		for (int i = 0; i < processen.length; i++) {
			if (processen[i] == pid)
				return i;
		}
		return 0;
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
			verwijderOpdracht = processenlijst.get(pid).verwijderOpdracht;
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
			processenIds.add(id);
			aantalProc++;
		} else {

			List<Integer> vrijgekomenFrames = new ArrayList<Integer>();
			int teVerwijderenPerProcess = (12 / aantalProc - 12 / (aantalProc + 1));
			for (Integer i : processenIds) {
				vrijgekomenFrames.addAll(processenlijst.get(i).verwijderFrames(teVerwijderenPerProcess));
				for (Integer ids : vrijgekomenFrames) {
				}
			}
			for (Integer i : vrijgekomenFrames) {
				processen[i] = id;
			}

			processenlijst.get(id).getInRam(vrijgekomenFrames);
			processenIds.add(id);
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

	public int verwijderProcess(int pid, List<Process> processenlijst) {
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
		//
		return processenlijst.get(pid).schrijfOpdracht;
		// SchrijvenNaarPersistentGeheugen++

	}
}
