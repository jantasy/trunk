package cn.yjt.oa.app.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SectionAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		int sectionCount = getSectionCount();
		int count = 0;
		for (int i=0; i<sectionCount; ++i) {
			//section
			count += 1;
			//sectionItems
			count += getItemCountAtSection(i);
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		int sectionCount = getSectionCount();
		for (int i=0; i<sectionCount; ++i) {
			if (position == 0) {
				return getSection(i);
			} else {
				position -= 1;
				int count = getItemCountAtSection(i);
				if (position < count) {
					return getItem(i, position);
				} else {
					position -= count;
				}
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		int sectionCount = getSectionCount();
		for (int i=0; i<sectionCount; ++i) {
			if (position == 0) {
				return getSectionId(i);
			} else {
				position -= 1;
				int count = getItemCountAtSection(i);
				if (position < count) {
					return getItemId(i, position);
				} else {
					position -= count;
				}
			}
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionCount = getSectionCount();
		for (int i=0; i<sectionCount; ++i) {
			if (position == 0) {
				return getSectionView(i, convertView, parent);
			} else {
				position -= 1;
				int count = getItemCountAtSection(i);
				if (position < count) {
					return getItemView(i, position, convertView, parent);
				} else {
					position -= count;
				}
			}
		}
		return null;
	}
	
	@Override
	public int getItemViewType(int position) {
		int sectionCount = getSectionCount();
		for (int i=0; i<sectionCount; ++i) {
			if (position == 0) {
				return getSectionViewType(i);
			} else {
				position -= 1;
				int count = getItemCountAtSection(i);
				if (position < count) {
					return getItemViewType(i, position);
				} else {
					position -= count;
				}
			}
		}
        return 0;
    }

	@Override
    public int getViewTypeCount() {
        return getSectionViewTypeCount() + getItemViewTypeCount();
    }
	
	public int getSectionViewTypeCount() {
		return 1;
	}
	
	public int getItemViewTypeCount() {
		return 1;
	}
	
	public int getSectionViewType(int section) {
		return 0;
	}
	
	public int getItemViewType(int section, int position) {
		return 1;
	}
	
	/**
	 * should unique with item id
	 * @param section
	 * @return
	 */
	public long getSectionId(int section) {
		return 0;
	}
	
	/**
	 * should unique with section id
	 * @param section
	 * @param postion
	 * @return
	 */
	public long getItemId(int section, int postion) {
		return 0;
	}
	
	public static boolean isSection(SectionAdapter adapter, int positionInAdapterView) {
		int sectionCount = adapter.getSectionCount();
		for (int i=0; i<sectionCount; ++i) {
			if (positionInAdapterView == 0) {
				return true;
			} else {
				positionInAdapterView -= 1;
				int count = adapter.getItemCountAtSection(i);
				if (positionInAdapterView >= count) {
					positionInAdapterView -= count;
				} else {
					break;
				}
			}
		}
		
		return false;
	}
	
	public static int[] getDetailPosition(SectionAdapter adapter, int positionInAdapterView) {
		int sectionCount = adapter.getSectionCount();
		for (int i=0; i<sectionCount; ++i) {
			if (positionInAdapterView == 0) {
				return new int[]{i};
			} else {
				positionInAdapterView -= 1;
				int count = adapter.getItemCountAtSection(i);
				if (positionInAdapterView >= count) {
					positionInAdapterView -= count;
				} else {
					return new int[]{i, positionInAdapterView};
				}
			}
		}
		
		return null;
	}
	
	public abstract int getSectionCount();
	public abstract int getItemCountAtSection(int section);
	public abstract Object getSection(int section);
	public abstract Object getItem(int section, int position);
	public abstract View getSectionView(int section, View convertView, ViewGroup parent);
	public abstract View getItemView(int section, int position, View convertView, ViewGroup parent);
	
}
