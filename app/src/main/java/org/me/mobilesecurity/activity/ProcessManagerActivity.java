package org.me.mobilesecurity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.PkgBean;
import org.me.mobilesecurity.bean.ProcessBean;
import org.me.mobilesecurity.business.ProcessProvider;
import org.me.mobilesecurity.view.ProgressStateView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProcessManagerActivity extends Activity implements
        StickyListHeadersListView.OnHeaderClickListener, View.OnClickListener {
    private ProgressStateView mPsvProcess;
    private ProgressStateView mPsvMemory;
    private StickyListHeadersListView mListView;
    private Button mBtnAll;
    private Button mBtnReverse;
    private ImageView mIvClean;
    private View mLoadingView;

    private List<ProcessBean> mDatas;

    //private LetterToast mToast;
    private ProcessAdapter mAdapter;

    private Set<PkgBean> mPkgs;// 记录对应的应用
    private int mRunningProcessCount;
    private int mTotalProcessCount;
    private long mUsedMemory;
    private long mTotalMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        //mToast = new LetterToast(this);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mPsvProcess = (ProgressStateView) findViewById(R.id.pm_psv_proces);
        mPsvMemory = (ProgressStateView) findViewById(R.id.pm_psv_memory);
        mListView = (StickyListHeadersListView) findViewById(R.id.pm_listview);
        mBtnAll = (Button) findViewById(R.id.pm_btn_all);
        mBtnReverse = (Button) findViewById(R.id.pm_btn_reverse);
        mIvClean = (ImageView) findViewById(R.id.pm_iv_clean);
        mLoadingView = findViewById(R.id.include_ll_loading);
    }

    private void initData() {
        // 1.设置进程数据

        mRunningProcessCount = ProcessProvider.getRunningProcessCount(this);
        mTotalProcessCount = ProcessProvider.getTotalProcessCount(this);
        loadProcess();

        // 2. 已经使用了的内存（当前运行的进程销毁的内存总和）,手机总内存是多大

        mUsedMemory = ProcessProvider.getUsedMemory(this);
        mTotalMemory = ProcessProvider.getTotalMemory(this);
        loadMemory();

        // 3.给listview加载数据
        loadListView();
    }

    private void loadMemory() {
        long freeMemory = mTotalMemory - mUsedMemory;
        int memoryProgress = (int) (mUsedMemory * 100f / mTotalMemory + 0.5f);

        mPsvMemory.setLeftText("占用内存:"
                + Formatter.formatFileSize(this, mUsedMemory));
        mPsvMemory.setRightText("剩余内存:"
                + Formatter.formatFileSize(this, freeMemory));
        mPsvMemory.setProgress(memoryProgress);
    }

    private void loadProcess() {
        int processProgress = (int) (mRunningProcessCount * 100f
                / mTotalProcessCount + 0.5f);

        mPsvProcess.setLeftText("正在运行" + mRunningProcessCount + "个");
        mPsvProcess.setRightText("可有进程" + mTotalProcessCount + "个");
        mPsvProcess.setProgress(processProgress);
    }

    private void loadListView() {
        // 加载数据mDatas
        // 假数据
        // mDatas = new ArrayList<ProcessBean>();
        //
        // List<PkgBean> list = new ArrayList<PkgBean>();
        // for (int i = 0; i < 30; i++) {
        // PkgBean bean = new PkgBean();
        // bean.name = "应用-" + i;
        // bean.packageName = "package-" + i;
        //
        // list.add(bean);
        // }
        //
        // Random rdm = new Random();
        // for (PkgBean pkg : list) {
        // for (int i = 0; i < rdm.nextInt(10); i++) {
        //
        // ProcessBean bean = new ProcessBean();
        // bean.memory = rdm.nextLong();
        // bean.pkg = pkg;
        // bean.processName = pkg.packageName + ":" + i;
        //
        // mDatas.add(bean);
        // }
        // }

        mLoadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mDatas = ProcessProvider.getProcesses(ProcessManagerActivity.this);
                // 进行排序,根据应用的名称
//                Collections.sort(mDatas, new Comparator<ProcessBean>() {
//
//                    @Override
//                    public int compare(ProcessBean lhs, ProcessBean rhs) {
//                        String lFirstLetter = lhs.pkg.firstLetter;
//                        String rFirstLetter = rhs.pkg.firstLetter;
//                        return lFirstLetter.compareToIgnoreCase(rFirstLetter);
//                    }
//                });

                // 初始化记录所有应用程序
                mPkgs = new HashSet<PkgBean>();
                for (ProcessBean bean : mDatas) {
                    PkgBean pkg = bean.pkg;
                    mPkgs.add(pkg);
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mLoadingView.setVisibility(View.GONE);
                        // 给listview设置adapter
                        mAdapter = new ProcessAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });

            }
        }).start();

    }

    private void initEvent() {
        // 设置 item条目的点击
        mListView.setOnHeaderClickListener(this);
        // button
        mBtnAll.setOnClickListener(this);
        mBtnReverse.setOnClickListener(this);
        // 清理
        mIvClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnAll) {
            clickAll();
        } else if (v == mBtnReverse) {
            clickReverse();
        } else if (v == mIvClean) {
            clickClean();
        }
    }

    private void clickClean() {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        // 杀死选中的应用
        // for (PkgBean pkg : mPkgs) {
        // // 找到选中的应用
        // if (pkg.isChecked) {
        // // 杀死应用
        // ProcessProvider.killProcess(this, pkg.packageName);
        // }
        // }

        int killCount = 0;
        long releaseMemory = 0;
        Map<Integer, Long> map = new HashMap<Integer, Long>();

        Iterator<PkgBean> iterator = mPkgs.iterator();

        while (iterator.hasNext()) {
            PkgBean pkg = iterator.next();
            // 找到选中的应用
            if (pkg.isChecked) {
                // 杀死应用
                ProcessProvider.killProcess(this, pkg.packageName);

                // 内存移除
                iterator.remove();

                ListIterator<ProcessBean> listIterator = mDatas.listIterator();
                while (listIterator.hasNext()) {
                    ProcessBean bean = listIterator.next();
                    if (bean.pkg.packageName.equals(pkg.packageName)) {
                        // 移除
                        listIterator.remove();

                        // 获得进程的内存
                        long memory = bean.memory;
                        map.put(bean.pid, memory);
                    }
                }
            }
        }

        // 计算杀死的个数和释放的内存
        for (Map.Entry<Integer, Long> me : map.entrySet()) {
            releaseMemory += me.getValue();
        }
        killCount = map.size();

        // UI更新
        mAdapter.notifyDataSetChanged();

        if (killCount == 0) {
            Toast.makeText(this, "没有可杀死的进程", Toast.LENGTH_SHORT).show();
        } else {
            String text = "杀死了" + killCount + "个进程,释放了"
                    + Formatter.formatFileSize(this, releaseMemory) + "内存";
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

            // 顶部ui改变
            mRunningProcessCount -= killCount;
            loadProcess();

            mUsedMemory -= releaseMemory;
            loadMemory();
        }
    }

    private void clickReverse() {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        // 如果是已经选中，就不选中，否则相反
        for (PkgBean bean : mPkgs) {
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            bean.isChecked = !bean.isChecked;
        }

        // UI更新
        mAdapter.notifyDataSetChanged();
    }

    private void clickAll() {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        // 将所有的checkbox选中
        // 找到所有的pkgbean，设置数据ischecked--》true
        for (PkgBean bean : mPkgs) {
            // 如果是当前的应用，就不让选中
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            bean.isChecked = true;
        }

        // UI更新
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header,
                              int itemPosition, long headerId, boolean currentlySticky) {
        // 点击条目头
        PkgBean pkg = mDatas.get(itemPosition).pkg;
        if (pkg.packageName.equals(getPackageName())) {
            // 是当前程序，不继续
            return;
        }

        pkg.isChecked = !pkg.isChecked;

        // UI更新
        mAdapter.notifyDataSetChanged();
    }

    private class ProcessAdapter extends BaseAdapter implements
            StickyListHeadersAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                convertView = View.inflate(ProcessManagerActivity.this,
                        R.layout.item_process, null);
                holder = new ItemViewHolder();
                convertView.setTag(holder);
                holder.tvProcessName = (TextView) convertView
                        .findViewById(R.id.item_process_tv_name);
                holder.tvProcessSize = (TextView) convertView
                        .findViewById(R.id.item_process_tv_size);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
            }

            // 数据设置
            ProcessBean bean = mDatas.get(position);
            holder.tvProcessName.setText(bean.processName);
            holder.tvProcessSize.setText("占用:"
                    + Formatter.formatFileSize(ProcessManagerActivity.this,
                    bean.memory));

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView,
                                  ViewGroup parent) {
            HeaderViewHolder holder = null;
            if (convertView == null) {
                // 没有复用
                convertView = View.inflate(ProcessManagerActivity.this,
                        R.layout.item_process_header, null);
                holder = new HeaderViewHolder();
                convertView.setTag(holder);
                holder.ivIcon = (ImageView) convertView
                        .findViewById(R.id.item_ph_iv_icon);
                holder.tvName = (TextView) convertView
                        .findViewById(R.id.item_ph_tv_name);
                holder.cbChecked = (CheckBox) convertView
                        .findViewById(R.id.item_ph_cb_checked);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            // 数据设置
            ProcessBean processBean = mDatas.get(position);
            PkgBean pkg = processBean.pkg;

            if (pkg.icon == null) {
                holder.ivIcon.setImageResource(R.drawable.ic_default);
            } else {
                holder.ivIcon.setImageDrawable(pkg.icon);
            }
            holder.tvName.setText(pkg.name);

            // 设置选择框选中
            holder.cbChecked.setChecked(pkg.isChecked);

            // 是否显示checkbox,判断是否是当前的应用
            if (pkg.packageName.equals(getPackageName())) {
                // 说明是当前的应用
                holder.cbChecked.setVisibility(View.GONE);
            } else {
                holder.cbChecked.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            ProcessBean bean = mDatas.get(position);
            PkgBean pkg = bean.pkg;// 表示唯一的应用程序
            // pkg.packageName --> hashcode
            return pkg.hashCode();
        }
    }

    private static class HeaderViewHolder {
        ImageView ivIcon;
        TextView tvName;
        CheckBox cbChecked;
    }

    private static class ItemViewHolder {
        TextView tvProcessName;
        TextView tvProcessSize;
    }
    
}
