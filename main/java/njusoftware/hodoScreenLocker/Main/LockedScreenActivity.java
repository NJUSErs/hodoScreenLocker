package njusoftware.hodoScreenLocker.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.View.OnTouchListener;

import njusoftware.hodoScreenLocker.R;
import njusoftware.hodoScreenLocker.TimeFactory.TimeFactory;
import njusoftware.hodoScreenLocker.Toolkit.UnitConverter;

/*
* 这个类是锁屏界面的Activity
*由监听屏幕熄灭的Service启动
*有件难以理解的事，onCreate里的第一行，就是调用父类的onCreate的时候会报错
* 错误类型我还看不懂……然而能过并且完全不影响运行？！匪夷所思的事（笑哭.jpg
* 查了下无果，StackOverFlow上也有人有同样的问题然而并没有得到解决……
* 新建其他Activity，甚至新建其他工程进行测试都是这样……我合理怀疑是ide的问题……
* 由于我太鶸，实在不会解决，各位能解决就解决下吧，不过没什么影响就是了……
* */
public class LockedScreenActivity extends AppCompatActivity {
    private TextView theClock, timeOfTheDay, theDate, dayOfTheWeek, theLunarCalendar, theSolarTermAndHoliday, ReminderArea, BottomInfoBar,
            RightNumber, RightIcon, LeftNumber, LeftIcon, CentralIcon, LeftText1, LeftText2, RightText1, RightText2, LeftArrow, RightArrow;
    private ViewGroup OpreateArea;
    private TimeChangeReceiver tcr;
    public static boolean isRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ECHO", "Create " + this.getPackageName());
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keylock = keyguardManager.newKeyguardLock(Activity.KEYGUARD_SERVICE);
//        keylock.disableKeyguard();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();      //隐藏标题栏
        setContentView(R.layout.activity_locked_screen);
        getAllView();
        addAllListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRun = true;
        refreshAll();
//        Log.i("ECHO", "Start " + this.toString() + " Task Id:" + this.getTaskId());
//
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = am.getRunningTasks(10);
//        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfoList) {
//            Log.i("ECHO", "id: " + runningTaskInfo.id);
//            Log.i("ECHO", "description: " + runningTaskInfo.description);
//            Log.i("ECHO", "number of activities: " + runningTaskInfo.numActivities);
//            Log.i("ECHO", "topActivity: " + runningTaskInfo.topActivity);
//            Log.i("ECHO", "baseActivity: " + runningTaskInfo.baseActivity.toString());
//        }   //这些在调试查看堆栈时使用
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.i("ECHO", "Restart " + this.toString() + " Task Id:" + this.getTaskId());
        refreshTimeArea();
        refreshReminderArea();
        refreshBottomArea();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i("ECHO", "Resume " + this.toString() + " Task Id:" + this.getTaskId());
        refreshTimeArea();
        refreshReminderArea();
        refreshBottomArea();
    }

    @Override
    protected void onDestroy() {
        isRun = false;
//        Log.i("ECHO", "Destory " + this.toString() + " Task Id:" + this.getTaskId());
        super.onDestroy();
        unregisterReceiver(tcr);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            Intent intent = new Intent(LockedScreenActivity.this, LockedScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ECHO", "Stop " + this.toString() + " Task Id:" + this.getTaskId());
    }

    private void getAllView() {
        theClock = (TextView) findViewById(R.id.theClock);
        timeOfTheDay = (TextView) findViewById(R.id.timeOfTheDay);//根据选择的时间格式决定是否需要这个View,打算在这个Activity被service启动时传入相应参数，根据参数来加一个if即可
        theDate = (TextView) findViewById(R.id.theDate);
        dayOfTheWeek = (TextView) findViewById(R.id.dayOfTheWeek);
        theLunarCalendar = (TextView) findViewById(R.id.theLunarCalendar);
        theSolarTermAndHoliday = (TextView) findViewById(R.id.theHoliday);
        ReminderArea = (TextView) findViewById(R.id.ReminderArea);
        RightIcon = (TextView) findViewById(R.id.RightIcon);
        LeftIcon = (TextView) findViewById(R.id.LeftIcon);
        RightNumber = (TextView) findViewById(R.id.RightNumber);
        LeftNumber = (TextView) findViewById(R.id.LeftNumber);
        CentralIcon = (TextView) findViewById(R.id.CentralIcon);
        OpreateArea = (ViewGroup) findViewById(R.id.OpreateArea);
        BottomInfoBar = (TextView) findViewById(R.id.BottomInfoBar);
        LeftText1 = (TextView) findViewById(R.id.LeftText1);
        LeftText2 = (TextView) findViewById(R.id.LeftText2);
        RightText1 = (TextView) findViewById(R.id.RightText1);
        RightText2 = (TextView) findViewById(R.id.RightText2);
        LeftArrow = (TextView) findViewById(R.id.LeftArrow);
        RightArrow = (TextView) findViewById(R.id.RightArrow);
    }

    private void refreshAll() {
        updateTime();
        refreshTimeArea();
        refreshReminderArea();
        refreshBottomArea();
        refreshOpreateArea();
        adaptTheSize();
    }

    private void refreshTimeArea() {
        //时间变化时要改变内容，但不应将农历区域切换回日期，所以分开写
        //其实这个也和update没啥关系，所以原因你们懂得
        theLunarCalendar.setAlpha(0);
        theSolarTermAndHoliday.setAlpha(0);
        theDate.setAlpha(1);
        dayOfTheWeek.setAlpha(1);

    }

    private void refreshOpreateArea() {
        RightText2.setAlpha(0);
        LeftText2.setAlpha(0);
        //此处需要图片的素材，先留空
        //虽然叫update其实只是为了起名的统一，只会调用一次啦
    }

    private void updateTime() {
    /*更新时间方法*/
        TimeFactory t = new TimeFactory();
        theClock.setText(t.get(TimeFactory.CLOCK));
        timeOfTheDay.setText(t.get(TimeFactory.TIME_OF_DAY));
        theDate.setText(t.get(TimeFactory.DATE));
        dayOfTheWeek.setText(t.get(TimeFactory.DAY_OF_WEEK));
        theLunarCalendar.setText(t.get(TimeFactory.LUNAR_CALENDAR));
        theSolarTermAndHoliday.setText(t.get(TimeFactory.SOLAR_TERM_AND_HOLIDAY));//假日和节气放一块儿
        t = null;//听说显式地置为null会好一点？
    }

    private void refreshReminderArea() {
        /*更新备忘录方法
        * 在这个activity里只进行setText的操作，内容应该由其他类提供
        * */
        //这里只是demo
        ReminderArea.append("测试1：下午物联网项目洽谈\n");
        ReminderArea.append("15:05\n");//我要吐槽预览图和文档，既然是显示当日日程为什么还要加日期啊，还有精确到秒是要干什么啊
        ReminderArea.append("测试2\n");
        ReminderArea.append("16:00\n");
        ReminderArea.append("测试3\n");
        ReminderArea.append("16:30");
//        ReminderArea.append("测试4\n");
//        ReminderArea.append("17:00");
        /*
        * 这个部分应该不会是用这种方法添加文本的，这里只是个最最简陋的测试
        * 我构想的架构这样：
        * 将这里的ScrollView里面嵌套一个LinearLayout
        * 所有备忘提醒相关的类在同一个包Reminder中：
        * >Notification类，自定义类，用于记录每条备忘消息，实现comparable，以时间来作为比较依据
        * >一个Activity，可由主界面启动，用于输入备忘（先不考虑对接OA系统等），将每条备忘以Nofification类保存，并将其递交给Notebook
        * >Notebook类，实现Serializable或者Parcelable，或者直接加上写入和读取.log文件的方法。其中有一个Collecions，每条记录放置于此，并且按时间sort，每次启动这个类都会对
        * >Factory类，每次调用这里的updateReminder就会通知这个类读取备忘录记录，进行筛选（今日内和是否已经过了那个时间），将符合条件的做成TextView的ArrayList
        * 最后这里的updateReminder将返回的ArrayList里的所有TextView加到布局里去
        * 这边真的真的还蛮麻烦的orz
        * （不过反正不是我写ヽ(ﾟ∀ﾟ)ﾉ）
        *
        * */
    }

    private void refreshBottomArea() {
        /*底部滚动条部分，要监听积分变化
        * 数据来源于其他类
        * */
        Resources resources = getResources();
        String string = String.format(resources.getString(R.string.Info), 50, 60);
        //用格式化字符串来动态显示，可以在String里事先准备好一些格式化模板
        BottomInfoBar.setText(string);
    }

    private void adaptTheSize() {
        /*根据屏幕调整各个组件的大小，并且有最小值*/
        float tempSize = Math.max(UnitConverter.px2dip(this, (float) 0.064 * getResources().getDisplayMetrics().heightPixels), 40);//转换下sp和px
        theClock.setTextSize(tempSize);
        timeOfTheDay.setTextSize(tempSize / 2);
        theDate.setTextSize(tempSize / 2);
        dayOfTheWeek.setTextSize(tempSize / 2);
        theLunarCalendar.setTextSize(tempSize / 2);
        theSolarTermAndHoliday.setTextSize(tempSize / 2);
        //底部的似乎不必设置就蛮合适的，需要不同尺寸屏幕的测试
    }

    private void addAllListener() {
        /*准备监听器*/
        //监听触摸农历
        theLunarCalendar.setOnTouchListener(new ViewChanger());
        theSolarTermAndHoliday.setOnTouchListener(new ViewChanger());
        //监听分钟、日期变化（广播接收器）
        tcr = new TimeChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        this.registerReceiver(tcr, intentFilter);
        //监听滑动
        CentralIcon.setOnTouchListener(new ScreenUnlocker());
        //监听积分变化（需要自己写listener）
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键,这个方法不用写任何东西
    }

    private class ViewChanger implements OnTouchListener {
        @Override
        /*触摸查看农历等，即隐藏日期TextView，显示农历TextView*/
        public boolean onTouch(View v, MotionEvent event) {
            theLunarCalendar.setAlpha(1 - theLunarCalendar.getAlpha());
            theSolarTermAndHoliday.setAlpha(1 - theSolarTermAndHoliday.getAlpha());
            theDate.setAlpha(1 - theDate.getAlpha());
            dayOfTheWeek.setAlpha(1 - dayOfTheWeek.getAlpha());
            return false;
        }
    }

    private class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK))
                updateTime();//每分钟更新时间
            else if (action.equals(Intent.ACTION_DATE_CHANGED))
                refreshReminderArea();//更新备忘录，好吧也不仅仅是每天更新，这边是比较复杂的，要重写
        }
    }

    private class ScreenUnlocker implements OnTouchListener {
        /*实现左右划动的功能*/
        int lastX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    OpreateArea.offsetLeftAndRight(x - lastX);//实现随划动改变位置
                    if (OpreateArea.getX() > 0) {
                        LeftText2.setAlpha(Math.min((OpreateArea.getX() / 400),1));//这些效果……自己运行了看吧
                        hideIcons(OpreateArea.getX(), true);
                        if (OpreateArea.getX() > getResources().getDisplayMetrics().widthPixels * 0.44) {//实现右划解锁,这个数值是大略地推算了下的
                            finish();
                        }
                    }
                    if (OpreateArea.getX() < 0) {
                        RightText2.setAlpha(Math.min(((-OpreateArea.getX()) / 400), 1));
                        hideIcons(-OpreateArea.getX(), false);
                        if (OpreateArea.getX() < -getResources().getDisplayMetrics().widthPixels * 0.44)
                            share("http://www.baidu.com/");//这只是个demo
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    OpreateArea.offsetLeftAndRight(-(int) OpreateArea.getX());//松开手指后回到原位置
                    RightText1.setAlpha(1);
                    RightNumber.setAlpha(1);
                    RightIcon.setAlpha(1);
                    RightArrow.setAlpha(1);
                    LeftText1.setAlpha(1);
                    LeftNumber.setAlpha(1);
                    LeftIcon.setAlpha(1);
                    LeftArrow.setAlpha(1);
                    RightText2.setAlpha(0);
                    LeftText2.setAlpha(0);
                    break;
            }
            return true;
        }

        private void hideIcons(float f, boolean direction) {
            float temp = (1 - OpreateArea.getX() / 540) /20;
            if (!direction) {
                RightArrow.setAlpha(temp);
                RightText1.setAlpha(temp);
                RightIcon.setAlpha(temp);
                RightNumber.setAlpha(temp);
            }
            else {
                LeftArrow.setAlpha(temp);
                LeftText1.setAlpha(temp);
                LeftIcon.setAlpha(temp);
                LeftNumber.setAlpha(temp);
            }
        }
    }

    private void share(String URI) {
        //这里的URI会变化所以作为参数传入
        Uri uri = Uri.parse(URI);
        Intent shareSomething = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(shareSomething);
    }
}