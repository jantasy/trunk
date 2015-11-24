package cn.yjt.oa.app.contactlist.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.AreaUser;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.DeptDetailUserInfo;

public class ContactlistUtils {

	public static void showSoftInput(Context context, EditText editText) {

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, 0);
	}

	public static void hidenSoftInput(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		imm.hideSoftInputFromWindow(editText.getWindowToken(),
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public static String getFirstLetter(String source) {
		if (source == null) {
			return "";
		}
		String letter = "";
		List<HanziToPinyinToken> tokens = HanziToPinyinUtil.getToken(source);
		if (tokens.size() == 0) {
			return letter;
		}
		HanziToPinyinToken token = tokens.get(0);
		String pinyin = token.target;
		if (TextUtils.isEmpty(pinyin)) {
			return letter;
		}
		return pinyin.substring(0, 1).toUpperCase();
	}

	public static String getPinYin(String source) {
		List<HanziToPinyinToken> tokens = HanziToPinyinUtil.getToken(source);
		String pinyin = "";
		for (HanziToPinyinToken token : tokens) {
			pinyin += token.target;
		}
		return pinyin.toUpperCase();
	}

	public static boolean isLetter(char ch) {
		return Character.isLetter(ch);
	}

	public static boolean isLetterString(String string) {
		char[] arr = string.toCharArray();
		for (char c : arr) {
			if (!isLetter(c)) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean isEmptyList(List<T> list) {
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void sendMsg(Context context, String phoneNum) {
		Uri uri = Uri.parse("smsto:" + phoneNum);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		context.startActivity(intent);
	}

	public static void startCall(Context context, String phoneNum) {
		Uri uri = Uri.parse("tel:" + phoneNum);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}

	public static boolean isMobilePhoneNum(String value) {
		if (value == null)
			return false;
		if (value.length() > 11) {
			value = value.substring(value.length() - 11, value.length());
		} else if (value.length() < 11) {
			return false;
		}
		String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(value);
		return m.matches();
	}

	public static void sortGroupChild(List<ContactInfo> list, Context context) {
		final long id = AccountManager.getCurrent(context).getId();
		Collections.sort(list, new Comparator<ContactInfo>() {

			@Override
			public int compare(ContactInfo lhs, ContactInfo rhs) {
				int result = 0;
				if (lhs.getUserId() == id && rhs.getUserId() != id) {
					return -1;
				}
				if (lhs.getUserId() == id && rhs.getUserId() == id) {
					return 0;
				}
				if (lhs.getUserId() != id && rhs.getUserId() == id) {
					return 1;
				}
				result = getPinYin(lhs.getName()).compareTo(
						getPinYin(rhs.getName()));
				if (result == 0) {
					result = lhs.getName().compareTo(rhs.getName());
				}
				return result;
			}
		});
	}

	// public static String getDeptName(List<XNode> deptList,
	// DeptDetailUserInfo detailUserInfo) {
	// String deptName = "";
	// for (XNode xNode : deptList) {
	// if (xNode instanceof DeptDetailInfo) {
	// DeptDetailInfo info = (DeptDetailInfo) xNode;
	// if (info.getId() == detailUserInfo.getParentId()) {
	// deptName = info.getName();
	// return deptName;
	//
	// } else if (info.getChildren() != null) {
	// for (DeptDetailInfo info2 : info.getChildren()) {
	// if (info2.getId() == detailUserInfo.getParentId()) {
	// deptName = info.getName() + "/" + info2.getName();
	// break;
	// }
	// }
	// }
	// }
	// }
	// return deptName;
	// }

	public static String getDeptName(List<DeptDetailInfo> deptList,
			DeptDetailUserInfo detailUserInfo) {
		String deptName = "";
		for (DeptDetailInfo xNode : deptList) {
			DeptDetailInfo info = (DeptDetailInfo) xNode;
			if (info.getId() == detailUserInfo.getParentId()) {
				deptName = info.getName();
				return deptName;

			} else if (info.getChildren() != null) {
				for (DeptDetailInfo info2 : info.getChildren()) {
					if (info2.getId() == detailUserInfo.getParentId()) {
						deptName = info.getName() + "/" + info2.getName();
						break;
					}
				}
			}
		}
		return deptName;
	}

	public static void sortAreaUser(List<AreaUser> list) {
		Collections.sort(list, new Comparator<AreaUser>() {

			@Override
			public int compare(AreaUser lhs, AreaUser rhs) {
				return getPinYin(lhs.getName()).compareTo(getPinYin(rhs.getName()));
			}

		});
	}

}
