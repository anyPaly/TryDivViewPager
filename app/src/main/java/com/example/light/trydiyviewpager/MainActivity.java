package com.example.light.trydiyviewpager;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.light.trydiyviewpager.databinding.MainActivityBinding;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends AppCompatActivity {
    
    private int imagegIds[] = new int[]{
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e
    };
    private  String[] titles = new String[]{
            "**不低俗，我就不能低俗",
            "朴树又回来啦！在唱经典老歌引万人大合唱",
            "揭秘北京电影如何升级",
            "乐视网TV版大派送",
            "热血反杀"
    };
    
    private ArrayList<ImageView> images;
    private  ArrayList<View> dots;
    private TextView title;
    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private int oldPostition = 0;
    private int currentItem = 0;
    private ScheduledExecutorService scheduledExecutorService;
    private static String NOTIFY = "NOTIFY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_activity);
        final MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        EventBus.getDefault().register(this);
        images = new ArrayList<ImageView>();
        for (int i = 0; i < imagegIds.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imagegIds[i]);
            images.add(imageView);
        }
        
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.add(findViewById(R.id.dot_4));
        
        //title = (TextView) findViewById(R.id.title);
        //title.setText(titles[0]);
        binding.setUser(new User(titles[0]));
        mViewPager = (ViewPager) findViewById(R.id.vp);
        adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }

            @Override
            public void onPageSelected(int position) {
                //title.setText(titles[position]);
                binding.setUser(new User(titles[position]));
                dots.get(oldPostition).setBackgroundResource(R.drawable.dot_nomal);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                oldPostition = position;
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    
    private  class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return images.size();
        }
        
        // 是否是同一张图片
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            //super.destroyItem(container, position, object);
            //view.removeView(view.getChildAt(position));
            //view.removeViewAt(position);
            view.removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(images.get(position));
            
            return images.get(position);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 每隔2秒钟切换一张图片
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
    }
    
    // 切换图片
    private class  ViewPagerTask implements Runnable{

        @Override
        public void run() {
            currentItem = (currentItem + 1) % imagegIds.length;
            // 更新界面
            //handler.sendEmptyMessage(0);
            //handler.obtainMessage().sendToTarget();
            EventBus.getDefault().post(new MessageEvent(NOTIFY));
        }
    }
    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // 设置当前页面
            mViewPager.setCurrentItem(currentItem);
        }
    };

    @Subscribe(threadMode = ThreadMode.MainThread) // ThreadMode is optional here
    public void onMessageEvent(MessageEvent event) {
        mViewPager.setCurrentItem(currentItem);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
