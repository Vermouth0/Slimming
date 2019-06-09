package com.swufe.wy.slimming;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotFragment extends Fragment {

	private int sign= - 1 ; 				//控制列表的展开
	private String[] food_type_array;		//食物类型数组
	private List<FoodType> food_list;		//数据集合
	private ExpandableListView data_list;	//实现ListView的折叠
	private Bitmap[] bitmaps;				//图片资源
	private int[] ids;					//图片资源ID数组

    public HotFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_hot, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tv = getView().findViewById(R.id.hotTextView1);
        tv.setText("热量");
    }


    protected void initValues() {
        ids = new int[]{R.mipmap.hot_gu, R.mipmap.hot_cai,
                R.mipmap.hot_guo, R.mipmap.hot_rou, R.mipmap.hot_dan,
                R.mipmap.hot_yv, R.mipmap.hot_nai, R.mipmap.hot_he,
                R.mipmap.hot_jun, R.mipmap.hot_you};			//准备图片的资源ID数组
        bitmaps = new Bitmap[ids.length];
        for (int i = 0;i <ids.length ; i++){
            bitmaps[i] = BitmapFactory.decodeResource(getResources(),ids[i]);
        }
        food_type_array = new String[]{"五谷类",
                "蔬菜类", "水果类", "肉类",
                "蛋类", "水产类", "奶类",
                "饮料类", "菌藻类", "油脂类"};		//准备显示名称的数组
        food_list = new ArrayList<>();				//创建食物集合
//构造数据源
        DBHelper dbHelper = new DBHelper();
        Cursor cursor = dbHelper.selectAllDataOfTable("hot");//查询数据库中的数据
        for (int i = 0; i <10; i++) {				//循环
            FoodType foodType = null;				//创建食物类型对象
            List<FoodMessage> foods = null;			//创建对应类型的食物集合
            int counts = 1;						//用于计数
            while (cursor.moveToNext()) {			//循环查询
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String hot = cursor.getString(cursor.getColumnIndex("hot"));
                String type_name = cursor.getString(cursor.getColumnIndex("type_name"));
                if (counts == 1) {
                    foodType = new FoodType();		//实例化对象
                    foods = new ArrayList<>();		//实例化对象
                    foodType.setFood_type(type_name);//实例化对象
                }
                FoodMessage foodMessage = new FoodMessage();
                foodMessage.setFood_name(name);		//存入食物名称
                foodMessage.setHot(hot);			//存入食物热量
                foods.add(foodMessage);				//添加到对应类型的集合中
                foodType.setFood_list(foods);
                if (counts == 20) {
                    food_list.add(foodType);			//向集合中添加数据
                    break;
                }
                counts++;
            }
        }
        cursor.close();							//关闭游标
    }

    //实例化控件
    protected void initViews() {
        data_list = getView().findViewById(R.id.food_list);
    }


    //绑定适配器
    protected void setViewsFunction() {
        MyFoodAdapter adapter = new MyFoodAdapter();
        data_list.setAdapter(adapter);
    }
    /**
     * 设置点击展开一个其余的都收起
     */

    protected void setViewsListener() {
        data_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                if (sign == -1) {
                    // 展开被选的group
                    data_list.expandGroup(groupPosition);
                    // 设置被选中的group置于顶端
                    data_list.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                } else if (sign == groupPosition) {
                    data_list.collapseGroup(sign);
                    sign = -1;
                } else {
                    data_list.collapseGroup(sign);
                    // 展开被选的group
                    data_list.expandGroup(groupPosition);
                    // 设置被选中的group置于顶端
                    data_list.setSelectedGroup(groupPosition);
                    sign = groupPosition;
                }
                return true;
            }
        });
    }

    //适配器
    class MyFoodAdapter extends BaseExpandableListAdapter {

        //Group的数量
        @Override
        public int getGroupCount() {
            return food_list.size();
        }
        //每个Group中的Child的数量
        @Override
        public int getChildrenCount(int groupPosition) {
            return food_list.get(groupPosition).getFood_list().size();
        }
        //获取对应位置的Group
        @Override
        public Object getGroup(int groupPosition) {
            return food_list.get(groupPosition);
        }
        //获取对应位置中的Child
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return food_list.get(groupPosition).getFood_list().get(childPosition);
        }
        //获取对应位置的Group的ID
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        //获取对应位置的Child的ID
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        //判断同一个ID是否指向同一个对象
        @Override
        public boolean hasStableIds() {
            return true;
        }
        //获取Group的视图
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder;
            if (convertView == null){
                holder = new GroupViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.hot_group_item , null);
                holder.image =  convertView.findViewById(R.id.group_image);
                holder.title = convertView.findViewById(R.id.group_title);
                convertView.setTag(holder);
            }else {
                holder = (GroupViewHolder) convertView.getTag();
            }
            holder.image.setImageBitmap(bitmaps[groupPosition]);
            holder.title.setText(food_type_array[groupPosition]);
            return convertView;
        }
        //获取child的视图
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if (convertView == null){
                holder = new ChildViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.hot_child_item,null);
                holder.name = convertView.findViewById(R.id.food_name);
                holder.hot =  convertView.findViewById(R.id.food_hot);
                convertView.setTag(holder);
            }else {
                holder = (ChildViewHolder) convertView.getTag();
            }
            FoodMessage food = food_list.get(groupPosition).getFood_list().get(childPosition);
            holder.name.setText(food.getFood_name());
            holder.hot.setText(food.getHot()+"千卡/克");
            return convertView;
        }
        //判断child是否可以被选择
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class GroupViewHolder{
        ImageView image;
        TextView title;
    }
    class ChildViewHolder{
        TextView name,hot;
    }
}
