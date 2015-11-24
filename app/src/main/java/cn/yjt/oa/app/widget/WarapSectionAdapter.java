package cn.yjt.oa.app.widget;

import android.view.View;
import android.view.ViewGroup;

public class WarapSectionAdapter extends SectionAdapter {

	private SectionAdapter[] adapters;
	
	public void setWarapedAdapters(SectionAdapter... adapters) {
		this.adapters = adapters;
	}
	
	@Override
	public int getSectionCount() {
		int count = 0;
		for (SectionAdapter adapter:adapters) {
			count += adapter.getSectionCount();
		}
		return count;
	}

	@Override
	public int getItemCountAtSection(int section) {
		for (SectionAdapter adapter:adapters) {
			int sectionCount = adapter.getSectionCount();
			if (section < sectionCount)
				return adapter.getItemCountAtSection(section);
			else
				section -= sectionCount;
		}
		return 0;
	}

	@Override
	public Object getSection(int section) {
		for (SectionAdapter adapter:adapters) {
			int sectionCount = adapter.getSectionCount();
			if (section < sectionCount)
				return adapter.getSection(section);
			else
				section -= sectionCount;
		}
		return null;
	}

	@Override
	public Object getItem(int section, int position) {
		for (SectionAdapter adapter:adapters) {
			int sectionCount = adapter.getSectionCount();
			if (section < sectionCount)
				return adapter.getItem(section, position);
			else
				section -= sectionCount;
		}
		return null;
	}

	@Override
	public View getSectionView(int section, View convertView, ViewGroup parent) {
		for (SectionAdapter adapter:adapters) {
			int sectionCount = adapter.getSectionCount();
			if (section < sectionCount)
				return adapter.getSectionView(section, convertView, parent);
			else
				section -= sectionCount;
		}
		return null;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		for (SectionAdapter adapter:adapters) {
			int sectionCount = adapter.getSectionCount();
			if (section < sectionCount)
				return adapter.getItemView(section, position, convertView, parent);
			else
				section -= sectionCount;
		}
		return null;
	}
	
	@Override
	public int getItemViewType(int position) {
		int type = super.getItemViewType(position);
		return type;
	}

}
