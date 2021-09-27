package com.example.hc_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.hc_app.Adapter.FragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private ViewPager vp;
    private FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation  = findViewById(R.id.bottom_navigation);
        vp          = findViewById(R.id.viewpager_content);
        adapter     = new FragmentAdapter(getSupportFragmentManager(), FragmentAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vp.setAdapter(adapter);
        HandlerEvent();
    }

    private void HandlerEvent() {
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    vp.setCurrentItem(0);
                    break;
                case R.id.action_history:
                    vp.setCurrentItem(1);
                    break;
                case R.id.action_exercise:
                    vp.setCurrentItem(2);
                    break;
                case R.id.profile:
                    vp.setCurrentItem(3);
                    break;
                default:
                    vp.setCurrentItem(0);
            }
            return true;
        });
    }
}