package com.qbw.refreshloadlayout;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.StringRes;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qbw.l.L;
import com.qinbaowei.refreshloadlayout.R;


public class RefreshLoadLayout extends SwipeRefreshLayout {

    /**
     * 加载更多的状态
     * 正在加载，加载失败，没有更多
     */
    private int mLoadStatus = Load.STATUS_LOADING;
    /**
     * 是否显示加载更多
     */
    private boolean mShowStatusUi;
    /**
     * 是否正在执行加载更多的任务
     */
    private boolean mTaskLoading;
    /**
     * 是否正在执行刷新任务
     */
    private boolean mTaskRefreshing;

    private View mRefreshLoadView;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadListener mOnLoadListener;

    private Handler mHandler = new Handler();
    private SetRefreshingRunn mSetRefreshingRunn;

    private boolean mLoadEnabled = true;
    private boolean mRefreshEnable = true;

    private float mInitialDownYValue = 0;
    private int miniTouchSlop;

    public RefreshLoadLayout(Context context) {
        super(context);
        init(null);
    }

    public RefreshLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        Context context = getContext();
        miniTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 8;
        //setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        //setDistanceToTriggerSync((int) getResources().getDimension(R.dimen.dp100));
        mSetRefreshingRunn = new SetRefreshingRunn(this);
    }

    public interface OnLoadListener {
        void onLoad();
    }

    public interface OnLoadFailedListener {
        void onRetryLoad();
    }

    public void onComplete(boolean isRefresh) {
        onComplete(isRefresh, 500);
    }

    public void onComplete(boolean isRefresh, long delayRefresh) {
        if (isRefresh) {
            onCompleteWhenRefresh(false, delayRefresh);
        } else {
            setLoading(false);
        }
    }

    private boolean isWorking() {
        boolean b1 = isRefreshing();
        boolean b2 = isLoading();
        //L.GL.d("isRefreshing=%b, isLoading=%b", b1, b2);
        return b1 || b2;
    }

    private boolean isCanLoad() {
        boolean b1 = isWorking();
        boolean b2 = Load.isLoadFailed(mLoadStatus);
        boolean b3 = Load.isLoadNoMoreData(mLoadStatus);
        //L.GL.d("isWorking=%b, isLoadFailed=%b, isLoadNoMore=%b", b1, b2, b3);
        return !(b1 || b2 || b3) && mLoadEnabled;
    }

    public boolean isLoadEnabled() {
        return mLoadEnabled;
    }

    public void setLoadEnabled(boolean loadEnabled) {
        mLoadEnabled = loadEnabled;
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
        mOnRefreshListener = listener;
    }

    private void notifyRefreshing() {
        if (mOnRefreshListener != null) {
            setStatusNoMoreData(false);
            mOnRefreshListener.onRefresh();
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        onSetRefreshing(refreshing);
        super.setRefreshing(refreshing);
    }

    public void autoRefreshWithoutUi() {
        onSetRefreshing(true);
    }

    private void onCompleteWhenRefresh(final boolean refreshing, long delay) {
        mHandler.removeCallbacks(mSetRefreshingRunn);
        onSetRefreshing(refreshing);
        mHandler.postDelayed(mSetRefreshingRunn.refreshing(refreshing), delay);
    }

    private static class SetRefreshingRunn implements Runnable {

        private RefreshLoadLayout mRefreshLoadLayout;

        private boolean mIsRefreshing;

        public SetRefreshingRunn(RefreshLoadLayout refreshLoadLayout) {
            mRefreshLoadLayout = refreshLoadLayout;
        }

        @Override
        public void run() {
            mRefreshLoadLayout.setRefreshing(mIsRefreshing);
        }

        public Runnable refreshing(boolean refreshing) {
            mIsRefreshing = refreshing;
            return this;
        }
    }

    private void onSetRefreshing(boolean refreshing) {
        mTaskRefreshing = refreshing;
        if (mTaskRefreshing) {
            notifyRefreshing();
        }
    }

    public boolean isLoading() {
        return mTaskLoading;
    }

    @Override
    public boolean isRefreshing() {
        return mTaskRefreshing || super.isRefreshing();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mRefreshEnable = enabled;
    }

    private void setLoading(boolean loading) {
        mTaskLoading = loading;
        if (mTaskLoading && mOnLoadListener != null) {
            super.setEnabled(false);
            mOnLoadListener.onLoad();
        } else {
            super.setEnabled(mRefreshEnable);
        }
    }

    public void setStatusLoading(boolean showStatusUi) {
        setShowStatusAndUi(showStatusUi, Load.STATUS_LOADING);
    }

    public void setStatusNoMoreData(boolean showStatusUi) {
        setShowStatusAndUi(showStatusUi, Load.STATUS_LOAD_NO_MORE_DATA);
    }

    public void setStatusFailed(boolean showStatusUi) {
        setShowStatusAndUi(showStatusUi, Load.STATUS_LOAD_FAILED);
    }

    private void setShowStatusAndUi(boolean showLoading, int status) {
        mShowStatusUi = showLoading;
        mLoadStatus = status;
        /*if (mShowStatusUi) {
            showLoad();
        } else {
            removeLoad();
        }*/
    }

    public int getLoadStatus() {
        return mLoadStatus;
    }

    /*private void showLoad() {
        BaseAdapter baseAdapter = getAdapter();
        if (baseAdapter == null) {
            L.GL.logE("baseAdapter==null");
            return;
        }
        baseAdapter.showLoad(mLoadStatus);
    }

    private void removeLoad() {
        BaseAdapter baseAdapter = getAdapter();
        if (baseAdapter == null) {
            L.GL.logE("baseAdapter==null");
            return;
        }
        baseAdapter.removeLoad();
    }*/


    public RecyclerView getRecyclerView() {
        int gc = getChildCount();
        RecyclerView view = null;
        for (int i = 0; i < gc; i++) {
            if (getChildAt(i) instanceof RecyclerView) {
                view = (RecyclerView) getChildAt(i);
                break;
            }
        }
        return view;
    }

    /*private BaseAdapter getAdapter() {
        BaseAdapter baseAdapter = null;
        if (mRefreshLoadView instanceof RecyclerView) {
            RecyclerView.Adapter adapter = ((RecyclerView) mRefreshLoadView).getAdapter();
            if (adapter instanceof BaseAdapter) {
                baseAdapter = (BaseAdapter) adapter;
            }
        }
        return baseAdapter;
    }*/

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        setOnLoadListener(getRecyclerView(), onLoadListener);
    }

    public void setOnLoadListener(View view, OnLoadListener onLoadListener) {
        mOnLoadListener = onLoadListener;
        mRefreshLoadView = view;
        addOnScrollListener();
    }

    /*public void setOnLoadFailedListener(OnLoadFailedListener onLoadFailedListener) {
        RecyclerView recyclerView = getRecyclerView();
        BaseAdapter adapter = (BaseAdapter) recyclerView.getAdapter();
        adapter.setOnLoadFailedListener(onLoadFailedListener);
    }*/

    private void addOnScrollListener() {
        if (mOnLoadListener != null && mRefreshLoadView != null) {
            if (mRefreshLoadView instanceof RecyclerView) {
                ((RecyclerView) mRefreshLoadView).addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0) {//上拉
                            if (isCanLoad()) {
                                if ((mShowStatusUi && RecyclerViewScrollUtil.isContentNearBottom(
                                        recyclerView,
                                        5)) || RecyclerViewScrollUtil.isContentNearBottom(
                                        recyclerView)) {//最后一个可见的时候
                                    setLoading(true);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownYValue = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float yDiff = ev.getY() - mInitialDownYValue;
                if (yDiff < miniTouchSlop) {
                    return false;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public static class Load extends BaseObservable {

        public static final int STATUS_LOADING = 0;
        public static final int STATUS_LOAD_FAILED = 1;
        public static final int STATUS_LOAD_NO_MORE_DATA = 2;

        private int status;

        public Load(int status) {
            this.status = status;
        }

        @Bindable
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
            notifyPropertyChanged(BR.status);
        }

        public static String getLoadText(Context context, int status) {
            if (status == STATUS_LOADING) {
                return context.getResources().getString(R.string.rlm_loading);
            } else if (status == STATUS_LOAD_FAILED) {
                return context.getResources().getString(R.string.rlm_load_failed);
            } else if (status == STATUS_LOAD_NO_MORE_DATA) {
                return context.getResources().getString(R.string.rlm_nomore);
            }
            return "";
        }

        public static void setLoadFailed(Load load) {
            load.setStatus(STATUS_LOAD_FAILED);
        }

        public static boolean isLoadFailed(int status) {
            return status == STATUS_LOAD_FAILED;
        }

        public static boolean isLoadNoMoreData(int status) {
            return status == STATUS_LOAD_NO_MORE_DATA;
        }

        public static void setLoadNoMoreData(Load load) {
            load.setStatus(STATUS_LOAD_NO_MORE_DATA);
        }

        public static boolean isLoadLoading(int status) {
            return status == STATUS_LOADING;
        }

        public static void setLoadLoading(Load load) {
            load.setStatus(STATUS_LOADING);
        }
    }

    public String getStringResource(@StringRes int stringRes) {
        return getContext().getResources().getString(stringRes);
    }

}
