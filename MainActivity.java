package jp.co.oceanize.rocket_preview.dev;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.co.oceanize.rocket_preview.dev.adapters.ProjectAdapter;
import jp.co.oceanize.rocket_preview.dev.base.BaseActivity;
import jp.co.oceanize.rocket_preview.dev.constants.Constants;
import jp.co.oceanize.rocket_preview.dev.fragments.ProjectListFragment;
import jp.co.oceanize.rocket_preview.dev.interfaces.ClickInterface;
import jp.co.oceanize.rocket_preview.dev.models.ProjectObject;
import jp.co.oceanize.rocket_preview.dev.utils.CorrectSizeUtil;
import jp.co.oceanize.rocket_preview.dev.utils.FragmentClearBackStack;
import jp.co.oceanize.rocket_preview.dev.utils.GlobalUtils;

import static jp.co.oceanize.rocket_preview.dev.fragments.AppPreviewFragment.EXTRA_MESSAGE;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    CorrectSizeUtil mCorrectSize = null;
    private Context mContext = null;
    private ProjectListFragment projectListFragment;
    private final String KEY_FRAGMENT_LIST = "fragmentlist";
    private final String KEY_FOOTER_BUTTON = "footerbutton";
    public static FragmentManager FragmentManagerDashBoard;
    public static Stack<Fragment> FragmentStack;
    public static ArrayList<String> mArrFragmentTag = new ArrayList<>();
    //header
    public static RelativeLayout project_list_header;
    public static RelativeLayout chat_header;
    public static RelativeLayout main_head;
    public static RelativeLayout search_layout;
    public static RelativeLayout category_lay;
    public static RelativeLayout chatting_header;
    public static RelativeLayout category_list;
    private ImageView btn_setting = null;
    private ArrayList<ProjectObject> mList = null;

    public  static  EditText searchItem;
    public  static  EditText search_tanent;
    public  static TextView category;
    public  static AppCompatImageView btn_search;
    public  static ImageView hide_search_header;
    public  static ImageView close;

    public static final int PROJECT_LIST=1;
    public static final int CHAT_TAB=2;
    public static final int CHAT=3;
    private long mDurationVisible = 3000L;
    private long mDurationGone = 1000L;
    CorrectSizeUtil correctSizeUtil;
    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder= ButterKnife.bind(this);
        mContext = this;
        FragmentManagerDashBoard = getSupportFragmentManager();
        FragmentStack = new Stack<>();
        correctSizeUtil=CorrectSizeUtil.getInstance(this);
        correctSizeUtil.correctSize();
        findViews();

        setListenersForViews();

        showFragment(PROJECT_LIST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void findViews() {
        //appbar items
        project_list_header = (RelativeLayout) findViewById(R.id.project_list_header);
        chat_header = (RelativeLayout) findViewById(R.id.chat_header);
        chatting_header = (RelativeLayout) findViewById(R.id.chatting_header);
        main_head = (RelativeLayout) findViewById(R.id.main_head);
        search_layout = (RelativeLayout) findViewById(R.id.search_layout);
        category_lay = (RelativeLayout) findViewById(R.id.category_lay);
        category_list = (RelativeLayout) findViewById(R.id.category_list);
        btn_setting = (ImageView) findViewById(R.id.btn_setting);
        searchItem = (EditText) findViewById(R.id.searchItem);
        search_tanent = (EditText) findViewById(R.id.search_tanent);
        btn_search = (AppCompatImageView) findViewById(R.id.btn_search);
        hide_search_header = (ImageView) findViewById(R.id.hide_search_header);
        close = (ImageView) findViewById(R.id.close);
        category = (TextView) findViewById(R.id.category);
        project_list_header.setBackground(GlobalUtils.background);
        chat_header.setBackground(GlobalUtils.background);

    }
    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());

        return outtoLeft;
    }
    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    @OnClick(R.id.hide_search_header)
    public void searchHeaderHide()
    {


       // search_layout.startAnimation(outToLeftAnimation());
       // search_layout.setVisibility(View.INVISIBLE);

        TranslateAnimation animate = new TranslateAnimation(0,search_layout.getWidth(),0,0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        search_layout.startAnimation(animate);
      //  search_layout.setVisibility(View.GONE);
        main_head.setVisibility(View.VISIBLE);
        //  search_layout.setVisibility(View.GONE);

    }
    @OnClick(R.id.btn_search)
public void searchHeaderShow()
{
    search_layout.setVisibility(View.VISIBLE);
    search_layout.startAnimation(inFromRightAnimation());
    main_head.setVisibility(View.INVISIBLE);
}
    @OnClick(R.id.close)
    public void hideCategory()
    {
        slideDown(category_list);
    }

    @OnClick(R.id.category_lay)
    public void ShowCategory()
    {
        slideUp(category_list);
        final LoopView loopView = (LoopView) findViewById(R.id.loopView);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add("item " + i);
        }
        //设置是否循环播放
//        loopView.setNotLoop();
        //滚动监听
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
            }
        });

        //设置原始数据
        loopView.setItems(list);


    }

//category_lay

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            Log.e("onSaveInstanceState",""+mArrFragmentTag.size());
            outState.putStringArrayList(KEY_FRAGMENT_LIST, mArrFragmentTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mArrFragmentTag = savedInstanceState.getStringArrayList(KEY_FRAGMENT_LIST);
  Log.e("onRestoreInstanceState",""+mArrFragmentTag.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","call  "+mArrFragmentTag.size());
       // initProjectList(PROJECT_LIST);

        if(mArrFragmentTag.size()>0) {

            showHeader(mArrFragmentTag.get(mArrFragmentTag.size()-1) + "");
        }else
        {
            showFragment(PROJECT_LIST);
            showHeader(PROJECT_LIST + "");
        }
    }

    @Override
    public void onBackPressed() {

        btnBackPressed();

    }

    public void showFragment(int fragment) {

        Log.e("showFragment",fragment+"");
        FragmentClearBackStack.clearBackStack(FragmentManagerDashBoard);
        FragmentManagerDashBoard.popBackStack();
        FragmentStack.clear();
        FragmentTransaction transaction = FragmentManagerDashBoard.beginTransaction();
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag("" + fragment);
        if (newFragment == null)
        {
            Log.e("newFragment",fragment+"");
            switch (fragment) {
                case PROJECT_LIST:

                    newFragment = new ProjectListFragment();
                    break;
            }
            Log.e("showFragment",fragment+"");

            mArrFragmentTag.add(fragment + "");

        }
        mArrFragmentTag.add(  PROJECT_LIST+"");
        transaction.replace(R.id.rlt_fragment, newFragment);
        FragmentStack.push(newFragment);
        transaction.commit();


    }

    public static void gotoNextpage( Fragment fragment,int tag)
    {
        showHeader(tag+"");
        FragmentTransaction transaction = MainActivity.FragmentManagerDashBoard.beginTransaction();//.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        transaction.add(R.id.rlt_fragment, fragment,""+tag);
       // transaction.setCustomAnimations(R.anim.stand_by, R.anim.left_to_right);
        transaction.setCustomAnimations(R.anim.right_to_left, R.anim.stand_by, R.anim.stand_by, R.anim.left_to_right);
      //  transaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        MainActivity.FragmentStack.lastElement().onPause();
        transaction.hide(MainActivity.FragmentStack.lastElement());
        MainActivity.FragmentStack.push(fragment);
        transaction.commit();
        mArrFragmentTag.add(tag + "");
    }

    void btnBackPressed(){

        Log.e("btnBackPressed", " fragment stack size" + FragmentStack.size());
        findTagId();
        if(FragmentStack.size()>1){
            FragmentTransaction transaction;
            transaction = FragmentManagerDashBoard
                    .beginTransaction().setCustomAnimations(R.anim.stand_by, R.anim.left_to_right);
            //transaction.setCustomAnimations(R.anim.slide_enter,R.anim.slide_exit);
            FragmentStack.lastElement().onPause();
            transaction.remove(FragmentStack.pop());
       FragmentStack.lastElement().onResume();
            transaction.show(FragmentStack.lastElement());
            transaction.commit();
        }
        else
        {
            overridePendingTransition(R.anim.stand_by, R.anim.left_to_right);
            // transaction.setCustomAnimations(R.anim.slide_enter,R.anim.slide_exit);
        }

    }
    public void backToPrev(View v)
    {
        btnBackPressed();
    }
    private void findTagId()
    {
        if(mArrFragmentTag.size()>1) {

            Log.e("mArrFragmentTag 1", "" + mArrFragmentTag.size());
            mArrFragmentTag.remove(mArrFragmentTag.size() - 1);
            Log.e("mArrFragmentTag 2", "" + mArrFragmentTag.size()+" "+mArrFragmentTag.get(mArrFragmentTag.size()-1));
            //  int fargment_tag=mArrFragmentTag.get(mArrFragmentTag.size()-1);
            showHeader(mArrFragmentTag.get(mArrFragmentTag.size()-1));

        }
    }
    public static void showHeader(String heater)
    {

        Log.e("showHeader",""+heater);
        if(heater.equals((PROJECT_LIST+""))) {
            chat_header.setVisibility(View.GONE);
            chatting_header.setVisibility(View.GONE);
            project_list_header.setVisibility(View.VISIBLE);

        }
        else if(heater.equals((""+CHAT_TAB)))
        {
            chatting_header.setVisibility(View.GONE);
            project_list_header.setVisibility(View.GONE);
            chat_header.setVisibility(View.VISIBLE);
            main_head.setVisibility(View.VISIBLE);
        }
        else if(heater.equals((""+CHAT)))
        {
            project_list_header.setVisibility(View.GONE);
            chat_header.setVisibility(View.GONE);
            chatting_header.setVisibility(View.VISIBLE);
        }


    }

    private void setListenersForViews() {
        btn_setting.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                settingPage();
                break;
        }
    }

    private void settingPage() {
        // Intent i = new Intent(this, SettingActivity.class);
        Intent i = new Intent(this, SettingActivityPreview.class);
        startActivity(i);

        overridePendingTransition(R.anim.right_to_left, R.anim.stand_by);
    }

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    private void initProjectList(int fragment) {
        showHeader(fragment+"");
      //  Fragment newFragment = getSupportFragmentManager().findFragmentByTag("" + fragment);
        //if (newFragment == null) {

            projectListFragment = new ProjectListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


            Log.e("initChatFragment",fragment+" call");
            transaction.add(R.id.rlt_fragment, projectListFragment, fragment+"");
            transaction.addToBackStack(null);
            transaction.commit();
//        } else {
//            Log.e("show",fragment+" call");
//            getSupportFragmentManager().beginTransaction().show(newFragment).commit();
//
//        }
    }


}
