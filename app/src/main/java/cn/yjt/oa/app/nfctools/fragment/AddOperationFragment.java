package cn.yjt.oa.app.nfctools.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.nfctools.NfcTagOperationView;
import cn.yjt.oa.app.nfctools.NfcTagOperationViewFactory;
import cn.yjt.oa.app.nfctools.NfcTagOperationViewGroup;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperator;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperatorConfig;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperatorGroup;
import cn.yjt.oa.app.widget.SectionAdapter;

public class AddOperationFragment extends NFCBaseFragment implements
		OnClickListener {

	private List<NfcTagOperator> selectedPositions = new ArrayList<NfcTagOperator>();
	private ListView listView;
	private NfcOperatorAdapter adapter;
	private List<NfcTagOperatorGroup> operatorGroups;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_operation_select, container,
				false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		view.findViewById(R.id.nfc_operator_confirm_btn).setOnClickListener(
				this);
		listView = (ListView) view.findViewById(R.id.nfc_operator_list);
		operatorGroups = NfcTagOperatorConfig.getInstance(getActivity())
				.getOperatorGroups();
		adapter = new NfcOperatorAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	class NfcOperatorAdapter extends SectionAdapter {
		private LayoutInflater inflater;

		public NfcOperatorAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getSectionCount() {
			return operatorGroups.size();
		}

		@Override
		public int getItemCountAtSection(int section) {
			return operatorGroups.get(section).getNfcTagOperators().size();
		}

		@Override
		public Object getSection(int section) {
			return operatorGroups.get(section);
		}

		@Override
		public Object getItem(int section, int position) {
			return operatorGroups.get(section).getNfcTagOperators()
					.get(position);
		}

		@Override
		public View getSectionView(int section, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.nfc_operator_section_item, parent, false);
			}
			TextView sectionName = (TextView) convertView
					.findViewById(R.id.nfc_section_name);
			NfcTagOperatorGroup group = (NfcTagOperatorGroup) getSection(section);
			sectionName.setText(group.getGroupName());
			return convertView;
		}

		@Override
		public View getItemView(final int section, final int position,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.nfc_operator_item,
						parent, false);
			}
			final View view = convertView;
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onItemClick(listView, view, section, position, getId());
				}
			});
			TextView operator = (TextView) convertView
					.findViewById(R.id.nfc_operator_name);
			TextView operation = (TextView) convertView
					.findViewById(R.id.nfc_operation);
			CheckBox select = (CheckBox) convertView
					.findViewById(R.id.nfc_operation_select);
			final NfcTagOperator nfcTagOperator = (NfcTagOperator) getItem(
					section, position);
			select.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					NfcTagOperation selectedOperation = nfcTagOperator
							.getSelectedOperation();
					if (isChecked) {
						if (selectedOperation.isSingleChoose()&&!selectedPositions.isEmpty()) {
							alertConfirmSelectSingleChooseDialog(nfcTagOperator);
						} else if (containsSingleChoose()) {
							alertConfirmSelectSingleChooseDialog(
									nfcTagOperator, getSingleChooseOperator());
						} else {
							selectedPositions.add(nfcTagOperator);
						}
					} else {
						selectedPositions.remove(nfcTagOperator);
					}
				}
			});

			Drawable drawable = nfcTagOperator.getIconDrawable(getActivity());
			drawable.setBounds(
					0,
					0,
					getResources().getDimensionPixelSize(
							R.dimen.operator_drawable), getResources()
							.getDimensionPixelSize(R.dimen.operator_drawable));
			operator.setText(nfcTagOperator.getOperatorName());
			NfcTagOperation selectedOperation = nfcTagOperator
					.getSelectedOperation();
			if (selectedOperation == null) {
				selectedOperation = nfcTagOperator.getNfcTagOperations().get(0);
			}
			operation.setText(selectedOperation.getOperationName());
			operator.setCompoundDrawables(drawable, null, null, null);
			select.setChecked(selectedPositions.contains(nfcTagOperator));
			return convertView;
		}

	}

	private boolean containsSingleChoose() {
		for (NfcTagOperator nfcTagOperator : selectedPositions) {
			if (nfcTagOperator.getSelectedOperation().isSingleChoose()) {
				return true;
			}
		}
		return false;
	}

	private NfcTagOperator getSingleChooseOperator() {
		for (NfcTagOperator nfcTagOperator : selectedPositions) {
			if (nfcTagOperator.getSelectedOperation().isSingleChoose()) {
				return nfcTagOperator;
			}
		}
		return null;
	}

	private void alertConfirmSelectSingleChooseDialog(
			final NfcTagOperator tagOperator) {
		AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(getActivity());
		builder.setTitle("提示")
				.setMessage(
						String.format("“%s”选项不能与其他选项共存，确认后勾选的其他选项将被取消。",
								tagOperator.getSelectedOperation()
										.getOperationText()))
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedPositions.clear();
						selectedPositions.add(tagOperator);
						adapter.notifyDataSetChanged();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						adapter.notifyDataSetChanged();
					}
				}).show().setCanceledOnTouchOutside(false);
	}

	private void alertConfirmSelectSingleChooseDialog(
			final NfcTagOperator tagOperator,
			final NfcTagOperator preCancelOperator) {
		AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(getActivity());
		builder.setTitle("提示")
				.setMessage(
						String.format("“%s”选项不能与其他选项共存，确认后勾选的“%s”选项将被取消。",
								preCancelOperator.getSelectedOperation()
										.getOperationText(),preCancelOperator.getSelectedOperation()
										.getOperationText()))
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedPositions.remove(preCancelOperator);
						selectedPositions.add(tagOperator);
						adapter.notifyDataSetChanged();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						adapter.notifyDataSetChanged();
					}
				}).show().setCanceledOnTouchOutside(false);
	}

	public void onItemClick(AdapterView<?> parent, View view, int section,
			int position, long id) {
		System.out.println("onItemClick");
		int[] detailPosition = SectionAdapter.getDetailPosition(adapter,
				position);
		System.out.println("position:" + position);
		System.out.println(Arrays.toString(detailPosition));
		NfcTagOperator operator = (NfcTagOperator) adapter.getItem(section,
				position);
		showSelectDialog(operator);
	}

	private void showSelectDialog(final NfcTagOperator nfcTagOperator) {
		System.out.println("showSelectDialog");
		final NfcTagOperationViewGroup nfcTagOperationViewGroup = createOperationViewGroup(nfcTagOperator);
		NfcTagOperation selectedOperation = nfcTagOperator
				.getSelectedOperation();
		if (selectedOperation == null) {
			selectedOperation = nfcTagOperator.getNfcTagOperations().get(0);
		}
		nfcTagOperationViewGroup.check(selectedOperation.getOperationId());

		AlertDialogBuilder.newBuilder(getActivity())
				.setView(nfcTagOperationViewGroup)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int checkedOperationId = nfcTagOperationViewGroup
								.getCheckedOperationId();
						NfcTagOperationView operationView = nfcTagOperationViewGroup
								.findViewByOperationId(checkedOperationId);
						nfcTagOperator.setSelectedOperation(operationView
								.getNfcTagOperation());
						adapter.notifyDataSetChanged();
					}
				}).setNegativeButton("取消", null).setTitle("选择选项").show();
	}

	private NfcTagOperationViewGroup createOperationViewGroup(
			NfcTagOperator nfcTagOperator) {
		NfcTagOperationViewGroup group = new NfcTagOperationViewGroup(
				getActivity());
		group.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		List<NfcTagOperation> nfcTagOperations = nfcTagOperator
				.getNfcTagOperations();
		for (int i = 0; i < nfcTagOperations.size(); i++) {
			NfcTagOperationView operationView = NfcTagOperationViewFactory
					.create(getActivity(), nfcTagOperations.get(i));
			group.addView(operationView.getView(), i,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		group.initFinish();
		return group;

	}

	@Override
	public void onClick(View v) {
		setResult((ArrayList<NfcTagOperator>) selectedPositions);
	}

	private void setResult(ArrayList<NfcTagOperator> operators) {
		Intent data = new Intent();
		data.putParcelableArrayListExtra("operators", operators);
		getActivity().setResult(Activity.RESULT_OK, data);
		getActivity().finish();
	}

}
