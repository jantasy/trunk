package cn.yjt.oa.app.task;

public class InitedByMeFragment extends TaskBaseFragment {

    @Override
    protected String getRequestType() {
        return "FROM_ME";
    }
    
}
