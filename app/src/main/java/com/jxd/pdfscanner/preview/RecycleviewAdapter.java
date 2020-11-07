package com.jxd.pdfscanner.preview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jxd.pdfscanner.R;
import com.jxd.pdfscanner.util.JXDLog;

import java.util.List;

public class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.ViewHolder>{
    private Context adapterContext;

    private List<PhotoBean> mTxList;//用以将适配完的子项储存的链表，它的泛型是之前的实体类



    public RecycleviewAdapter(List<PhotoBean> txList,Context context) {
        //链表的赋值
        mTxList = txList;
        adapterContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHode方法，我的理解就是对某个具体子项的操作，包括对具体控件的设置，包括且不限于的点击动作两个参数
       // A:ViewGroup parent主要用于调用其整个RecyclerView的上下文
       // B:第二个参数因为在方法里面没有调用，所以我也没看懂，从字面上看，这个参数是一个整型的控件类型？？？
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        //将子项的布局通过LayoutInflater引入
        final ViewHolder holder = new ViewHolder(view);
        //这里是子项的点击事件，RecyclerView的特点就是可以对子项里面单个控件注册监听，这也是为什么RecyclerView要摒弃ListView的setOnItemClickListener方法的原因
        holder.checkBox.setOnClickListener(v -> {
//            Tx tx = mTxList.get(holder.getAdapterPosition());
//            mTxList.remove(tx);//所谓的删除就是将子项从链表中remove
        });
        JXDLog.d("onCreateViewHolder===");
        return holder;//返回一个holder对象，给下个方法使用

    }

    @Override

    public void onBindViewHolder(ViewHolder holder, int position) {
        //用以将滚入屏幕的子项加载图片等的方法，两个参数
        //A:前面方法ViewHolder的对象；
        //B:子项的id
        PhotoBean photoBean = mTxList.get(position);//创建前面实体类的对象
        Glide.with(adapterContext).load(photoBean.getPhotoFile()).thumbnail(0.05f).into(holder.txImage);//
       // holder.txName.setText(photoBean.getPhotoName());//将具体值赋与子项对应的控件
        JXDLog.d("onBindViewHolder==="+photoBean.getPhotoName());
    }

    @Override
    public int getItemCount() {
        //用以返回RecyclerView的总共长度，这里直接使用了链表的长度（size）
        return mTxList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //内部静态类，用以定义TxApter.View的泛型
        ImageView txImage;
      //  TextView txName;//这两个是在子项布局里面具体的控件
        CheckBox checkBox;//这个是用于整个子项的控制的控件

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkbox);//这个是整个子项的控件
            txImage = view.findViewById(R.id.image_item);
          //  txName = view.findViewById(R.id.file_name);//通过R文件的id查找，找出子项的具体控件
        }
    }
}
