package org.me.mobilesecurity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.me.mobilesecurity.R;
import org.me.mobilesecurity.bean.ChildBean;
import org.me.mobilesecurity.bean.GroupBean;
import org.me.mobilesecurity.db.CommonNumberDao;

import java.util.List;

public class CommonNumberActivity extends AppCompatActivity {
    private ExpandableListView mListView;
    private List<GroupBean> mGroupDatas;
    private int mCurrentOpenGroup = -1;// 没有一个是打开的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mListView = (ExpandableListView) findViewById(R.id.cn_listview);
    }

    private void initData() {
        mGroupDatas = CommonNumberDao.getGroupDatas(this);
        mListView.setAdapter(new CommonNumberAdapter());
    }

    private void initEvent() {
        //点击child条目的时候拨打电话
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                ChildBean bean = mGroupDatas.get(groupPosition).childrenDatas
                        .get(childPosition);
                String number = bean.getNumber();

                // 拨号
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);

                return false;
            }
        });

        //点击group控制展开与关闭
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // 如果点击的当前条目是关闭的，就去展开，要求置顶显示
                if (mCurrentOpenGroup != groupPosition) {
                    // 展开
                    mListView.expandGroup(groupPosition);
                    // 关闭已经打开的
                    mListView.collapseGroup(mCurrentOpenGroup);
                    // 置顶
                    mListView.setSelectedGroup(groupPosition);
                    // 记录当前打开
                    mCurrentOpenGroup = groupPosition;
                } else {
                    // 点击的是打开的，关闭当前
                    mListView.collapseGroup(groupPosition);
                    // 记录没有一个是打开的
                    mCurrentOpenGroup = -1;
                }

                // 是否需要系统实现点击行为
                return true;
            }
        });
    }

    private class CommonNumberAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            if (mGroupDatas != null) {
                return mGroupDatas.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (mGroupDatas != null) {
                GroupBean bean = mGroupDatas.get(groupPosition);
                if (bean != null) {
                    List<ChildBean> childBeans = bean.childrenDatas;
                    return childBeans.size();
                }
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (mGroupDatas != null) {
                return mGroupDatas.get(groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (mGroupDatas != null) {
                GroupBean bean = mGroupDatas.get(groupPosition);
                if (bean != null) {
                    List<ChildBean> childrenDatas = bean.childrenDatas;
                    if (childrenDatas != null) {
                        return childrenDatas.get(childPosition);
                    }
                }
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(CommonNumberActivity.this, R.layout.group_item, null);
                holder = new GroupViewHolder();
                convertView.setTag(holder);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.item_group_tv_title);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            GroupBean bean = mGroupDatas.get(groupPosition);
            holder.tvTitle.setText(bean.getTitle());

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(CommonNumberActivity.this, R.layout.child_item, null);
                holder = new ChildViewHolder();
                convertView.setTag(holder);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_child_tv_name);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.item_child_tv_number);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            ChildBean bean = (ChildBean) getChild(groupPosition, childPosition);
            holder.tvName.setText(bean.getName());
            holder.tvNumber.setText(bean.getNumber());

            return convertView;
        }

        // 用来控制child条目是否可以被点击
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    static private class GroupViewHolder {
        TextView tvTitle;
    }

    static private class ChildViewHolder {
        TextView tvName;
        TextView tvNumber;
    }
}
