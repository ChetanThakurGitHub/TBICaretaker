package tbi.com.custom_calender;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tbi.com.R;
import tbi.com.helper.MonthDecoratorData;

public class CalendarCustomView extends LinearLayout {
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private static final int MAX_CALENDAR_COLUMN = 42;
    List<MonthDecoratorData.ResultMonthBean> resultMonth;
    List<DateModel> dateModels;
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private GridView calendarGridView;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter mAdapter;
    private LayoutInflater mInflater;

    public CalendarCustomView(Context context) {
        super(context);
    }


    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
        setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
    }

    public CalendarCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initializeUILayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        previousButton = view.findViewById(R.id.previous_month);
        nextButton = view.findViewById(R.id.next_month);
        currentDate = view.findViewById(R.id.display_current_date);
        calendarGridView = view.findViewById(R.id.calendar_grid);

        setUpCalendarAdapter();

    }

    private void setPreviousButtonClickEvent() {
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setNextButtonClickEvent() {
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setGridCellClickEvents() {
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    private void setUpCalendarAdapter() {

        List<Date> dayValueInCells = new ArrayList<Date>();
        dateModels = new ArrayList<>();

        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            DateModel dateModel = new DateModel();
            dateModel.setIsSelected("no");
            dateModel.setDate(mCal.getTime());
            dateModels.add(dateModel);

            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);

            if (dayValueInCells.size() == 14) {
                Date date = mCal.getTime();
                int month = date.getMonth();
                month++;
                int year = mCal.get(Calendar.YEAR);
                setAdapter(dateModels);
            }
        }

        String sDate = formatter.format(cal.getTime());
        currentDate.setText(sDate);
    }

    private void setAdapter(List<DateModel> dateModels) {
        mAdapter = new GridAdapter(context, dateModels, cal, resultMonth, ((Activity) context));
        calendarGridView.setAdapter(mAdapter);
    }
}
