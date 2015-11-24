package cn.yjt.oa.app.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;


public abstract class TimeLineAdapter extends SectionAdapter {

	class Entry implements Comparable<Entry> {
		private Object item;
		private Date dateTime;
		
		@Override
		public int compareTo(Entry another) {
			return comparator.compare(dateTime, another.dateTime);
		}
	}
	
	public interface DateItem {
		Date getDate();
	}
	
	private static final Comparator<Date> DATE_DESC = new Comparator<Date>() {

		@Override
		public int compare(Date lhs, Date rhs) {
			return rhs.compareTo(lhs);
		}
	};
	
	private static final Comparator<Date> DATE_ASC = new Comparator<Date>() {

		@Override
		public int compare(Date lhs, Date rhs) {
			return lhs.compareTo(rhs);
		}
	};
	
	private final Comparator<Date> comparator;
	
	private final TreeMap<Date, ArrayList<Entry>> items;
	
	public TimeLineAdapter(boolean desc) {
		comparator = desc ? DATE_DESC : DATE_ASC;
		items = new TreeMap<Date, ArrayList<Entry>>(comparator);
	}
	
	public TimeLineAdapter() {
		this(true);
	}
	
	public void addEntry(Date dateTime, Object item) {
		Entry entry = new Entry();
		entry.dateTime = dateTime;
		entry.item = item;
		
		Date date = new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDate());
		ArrayList<Entry> entries = items.get(date);
		if (entries == null) {
			entries = new ArrayList<Entry>();
			items.put(date, entries);
		}
		
		entries.add(entry);
		Collections.sort(entries);
	}
	
	public void addEntry(DateItem item) {
		Entry entry = new Entry();
		Date dateTime = item.getDate();
		entry.dateTime = dateTime;
		entry.item = item;
		
		Date date = new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDate());
		ArrayList<Entry> entries = items.get(date);
		if (entries == null) {
			entries = new ArrayList<Entry>();
			items.put(date, entries);
		}
		
		entries.add(entry);
		Collections.sort(entries);
	}
	
	public void addEntries(Collection<? extends DateItem> items) {
		ArrayList<ArrayList<Entry>> changedArrayList = new ArrayList<ArrayList<Entry>>();
		for (DateItem item:items) {
			Entry entry = new Entry();
			Date dateTime = item.getDate();
			entry.dateTime = dateTime;
			entry.item = item;
			
			Date date = new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDate());
			ArrayList<Entry> entries = this.items.get(date);
			if (entries == null) {
				entries = new ArrayList<Entry>();
				this.items.put(date, entries);
			} else {
				changedArrayList.add(entries);
			}
			
			entries.add(entry);
		}
		for (ArrayList<Entry> list:changedArrayList) {
			Collections.sort(list);
		}
		
	}
	
	private void removeItem(ArrayList<Entry> entries, Object item) {
		if (entries != null) {
			Iterator<Entry> it = entries.iterator();
			while (it.hasNext()) {
				Entry entry = it.next();
				if (item != null) {
					if (item.equals(entry.item)) {
						it.remove();
					}
				} else {
					if (entry.item == null) {
						it.remove();
					}
				}
			}
		}
	}
	
	public void removeEntry(Date dateTime, Object item) {
		Date date = new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDate());
		ArrayList<Entry> entries = this.items.get(date);
		
		removeItem(entries, item);
		if (entries != null && entries.isEmpty()) {
			this.items.remove(date);
		}
	}
	
	public void removeEntry(DateItem item) {
		Date dateTime = item.getDate();
		Date date = new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDate());
		ArrayList<Entry> entries = this.items.get(date);
		
		removeItem(entries, item);
		if (entries != null && entries.isEmpty()) {
			this.items.remove(date);
		}
	}
	
	public void removeEntries(Collection<? extends DateItem> items) {
		for (DateItem item:items) {
			removeEntry(item);
		}
		
	}
	
	public void clear() {
		items.clear();
	}

	@Override
	public int getSectionCount() {
		return items.keySet().size();
	}

	@Override
	public int getItemCountAtSection(int section) {
		Iterator<Date> it = items.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i == section)
				return items.get(it.next()).size();
			it.next();
			i++;
		}
		return 0;
	}

	@Override
	public Object getSection(int section) {
		Iterator<Date> it = items.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i == section)
				return it.next();
			it.next();
			i++;
		}
		return null;
	}
	
	public Date getSectionDate(int section) {
		return (Date) getSection(section);
	}

	@Override
	public Object getItem(int section, int position) {
		Date sectionKey = (Date) getSection(section);
		ArrayList<Entry> entries = items.get(sectionKey);
		return entries.get(position).item;
	}
	
	public int getSectionItemCount(int section){
		Date sectionKey = (Date) getSection(section);
		ArrayList<Entry> entries = items.get(sectionKey);
		return entries.size();
	}
}
