package bwei.admin.wurui1505b20170630;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class MainActivity extends AppCompatActivity implements XListView.IXListViewListener {
    private Banner banner;
    private Message msg;
    private XListView xlistview;
    private int pay = 0;
    private MyBase adapter;
    private List<Data.ListBean>list;
    private List<String>listpic;
    private String url = "http://qhb.2dyt.com/Bwei/news?postkey=1503d&type=6&page="+pay;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            String str = msg.obj.toString();
            Data data = gson.fromJson(str,Data.class);
            list.addAll(data.getList());
            adapter.notifyDataSetChanged();

            listpic.addAll(data.getListViewPager());
            banner.setImageLoader(new MyLoader());
            banner.setImages(listpic);
            banner.start();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         banner = (Banner) findViewById(R.id.banner);
         xlistview = (XListView) findViewById(R.id.xlistview);
        xlistview.setPullLoadEnable(true);
        xlistview.setXListViewListener(this);
        list = new ArrayList<>();
        listpic = new ArrayList<>();
        isNetworkAvailable(MainActivity.this);
        adapter = new MyBase();
        xlistview.setAdapter(adapter);
    }
    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                shuju();
            }else{
                AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("系统提示")
                        .setMessage("当前无网络连接，是否连接网络？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //全部设置
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                // 跳转到wifi设置
                               // startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                        finish();
                    }
                }).show();
            }
        }
        return false;
    }

    private void shuju() {
        new Thread(){
            @Override
            public void run() {
                Utils utils = new Utils();
                String path = utils.getData(url);
                msg = Message.obtain();
                msg.obj = path;
                mHandler.sendMessage(msg);
            }
        }.start();
    }
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(msg);
                Gson gson = new Gson();
                String str = msg.obj.toString();
                Data data = gson.fromJson(str,Data.class);
                list.addAll(data.getList());
                Log.d("bbb",list.size()+"");
                adapter.notifyDataSetChanged();
                stoploder();
            }
        },2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(msg);
                Gson gson = new Gson();
                String str = msg.obj.toString();
                Data data = gson.fromJson(str, Data.class);
                list.addAll(data.getList());
                Log.d("aaa", list.size() + "");
                adapter.notifyDataSetChanged();
                stoploder();
            }
        },2000);
    }

    private  void stoploder()
    {
        xlistview.stopLoadMore();
        xlistview.stopRefresh();
    }
    //添加适配器
    class MyBase extends BaseAdapter{
        //用于进行区分listduo
        private static final int TYPE_1 = 0;
        private static final int TYPE_2 = 1;
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public int getItemViewType(int position) {

            if(position % 2 == 0){
                return TYPE_1;
            }else{
                return TYPE_2;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            ViewHolder2 viewHolder2 = null;
            int type = getItemViewType(position);
            switch (type){
                case TYPE_1:
                {
                    if(convertView == null) {
                        viewHolder = new ViewHolder();
                        convertView = convertView.inflate(MainActivity.this, R.layout.item1, null);
                        viewHolder.one_imag1 = (ImageView) convertView.findViewById(R.id.one_imag1);
                        viewHolder.one_imag2 = (ImageView) convertView.findViewById(R.id.one_imag2);
                        convertView.setTag(viewHolder);
                    }else{
                        viewHolder = (ViewHolder) convertView.getTag();
                    }
                    Glide.with(MainActivity.this).load(list.get(position).getPic()).into(viewHolder.one_imag1);
                    Glide.with(MainActivity.this).load(list.get(position).getPic()).into(viewHolder.one_imag2);
                }
                break;
                case TYPE_2:
                {
                    if(convertView == null){
                        viewHolder2 = new ViewHolder2();
                        convertView = convertView.inflate(MainActivity.this,R.layout.item2,null);
                        viewHolder2.two_imag1 = (ImageView) convertView.findViewById(R.id.two_imag1);
                        viewHolder2.two_imag2 = (ImageView) convertView.findViewById(R.id.two_imag2);
                        viewHolder2.two_imag3 = (ImageView) convertView.findViewById(R.id.two_imag3);
                        viewHolder2.two_imag4 = (ImageView) convertView.findViewById(R.id.two_imag4);
                        convertView.setTag(viewHolder2);
                    }else{
                        viewHolder2 = (ViewHolder2) convertView.getTag();
                    }
                    Glide.with(MainActivity.this).load(list.get(position).getPic()).into(viewHolder2.two_imag1);
                    Glide.with(MainActivity.this).load(list.get(position).getPic()).into(viewHolder2.two_imag2);
                    Glide.with(MainActivity.this).load(list.get(position).getPic()).into(viewHolder2.two_imag3);
                    Glide.with(MainActivity.this).load(list.get(position).getPic()).into(viewHolder2.two_imag4);
                }
                break;
            }

            return convertView;
        }

         class ViewHolder{
             ImageView one_imag1,one_imag2;

        }

         class ViewHolder2{
             ImageView two_imag1,two_imag2,two_imag3,two_imag4;
        }
    }
}
