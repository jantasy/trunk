package cn.yjt.oa.app.industry;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.IndustryTagGroupInfo;
import cn.yjt.oa.app.beans.IndustryTagInfo;

public class IndustryAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<IndustryTagGroupInfo> industryTagGroupList = Collections
			.emptyList();

	public IndustryAdapter(Context context) {
		this.mContext = context;

	}

	@Override
	public int getGroupCount() {
		return industryTagGroupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return industryTagGroupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return industryTagGroupList.get(groupPosition).getIndustryTags()
				.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder groupHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.industry_group_item, null);
			convertView.setTag(GroupViewHolder.initView(convertView));
		}
		groupHolder = (GroupViewHolder) convertView.getTag();
		IndustryTagGroupInfo tagGroupInfo = (IndustryTagGroupInfo) getGroup(groupPosition);
		groupHolder.industryColumn.setText(tagGroupInfo.getName());

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder childHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.industry_child_grid, null);
			convertView.setTag(ChildViewHolder.initView(convertView));
		}
		childHolder = (ChildViewHolder) convertView.getTag();
		IndustryGriadAdapter adapter = new IndustryGriadAdapter(groupPosition);
		childHolder.gridView.setAdapter(adapter);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public List<IndustryTagGroupInfo> getIndustryTagGroupList() {
		return industryTagGroupList;
	}

	public void setIndustryTagGroupList(
			List<IndustryTagGroupInfo> industryTagGroupList) {
		this.industryTagGroupList = industryTagGroupList;
	}

	private class IndustryGriadAdapter extends BaseAdapter {
		private int groupPosition;

		public IndustryGriadAdapter(int groupPosition) {
			this.groupPosition = groupPosition;
		}

		@Override
		public int getCount() {
			return industryTagGroupList.get(groupPosition).getIndustryTags()
					.size();
		}

		@Override
		public Object getItem(int position) {
			return industryTagGroupList.get(groupPosition).getIndustryTags()
					.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GridViewHolder gridHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.industry_child_grid_item, null);
				convertView.setTag(GridViewHolder.initView(convertView));
			}
			gridHolder = (GridViewHolder) convertView.getTag();
			final IndustryTagInfo tagInfo = (IndustryTagInfo) getItem(position);
			gridHolder.industryTagName.setText(tagInfo.getName());
			if (tagInfo.getIsUsed() == 1) {
				gridHolder.industryTagName.setChecked(true);
			} else {
				gridHolder.industryTagName.setChecked(false);
			}
			gridHolder.industryTagName
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								tagInfo.setIsUsed(1);
							} else {
								tagInfo.setIsUsed(0);
							}
						}
					});
			return convertView;
		}

	}

	private static class GroupViewHolder {
		private TextView industryColumn;

		public static GroupViewHolder initView(View view) {
			GroupViewHolder holder = new GroupViewHolder();
			holder.industryColumn = (TextView) view
					.findViewById(R.id.industry_column);
			return holder;
		}
	}

	private static class ChildViewHolder {
		private GridView gridView;

		public static ChildViewHolder initView(View view) {
			ChildViewHolder childHolder = new ChildViewHolder();
			childHolder.gridView = (GridView) view.findViewById(R.id.gridView);
			return childHolder;
		}
	}

	private static class GridViewHolder {
		private CheckBox industryTagName;

		public static GridViewHolder initView(View view) {
			GridViewHolder gridHolder = new GridViewHolder();
			gridHolder.industryTagName = (CheckBox) view.findViewById(R.id.industry_tag_name);
			return gridHolder;
		}
	}

}
