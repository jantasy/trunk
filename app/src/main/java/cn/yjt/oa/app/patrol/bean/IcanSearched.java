package cn.yjt.oa.app.patrol.bean;

/**
 * Created by 熊岳岳 on 2015/9/16.
 */
public abstract class IcanSearched {
    public abstract String getSearchString();
    public abstract boolean isSelected();
    public abstract void setSelected(boolean selected);
}
