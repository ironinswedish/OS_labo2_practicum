import java.util.Comparator;

class AccesTimeComparator implements Comparator<TablePageEntry> {
	@Override
	public int compare(TablePageEntry o1, TablePageEntry o2) {
		return o1.getLastAccesTime() - o2.getLastAccesTime();
	}
}
