package tbi.org.custom_calender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tbi.org.R;
import tbi.org.custom_calender.activity.CalanderCaretakerActivity;
import tbi.org.custom_calender.activity.CalanderSuffererActivity;
import tbi.org.helper.MonthDecoratorData;
import tbi.org.session.Session;

public class GridAdapter extends ArrayAdapter {
    private String date, year, month;
    private boolean clicked;
    private List<MonthDecoratorData.ResultMonthBean> resultMonth;
    private Activity activity;
    private LayoutInflater mInflater;
    private List<DateModel> monthlyDates;
    private Calendar currentDate;


    public GridAdapter(Context context, List<DateModel> monthlyDates, Calendar currentDate, List<MonthDecoratorData.ResultMonthBean> resultMonth, Activity activity) {
        super(context, R.layout.single_cell_layout);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        mInflater = LayoutInflater.from(context);
        this.resultMonth = resultMonth;
        this.activity = activity;

    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {


       /* if (position == 1) {
            updateRequestList("");
        }*/

        Calendar c = Calendar.getInstance();
        int dateMonth = c.get(Calendar.DAY_OF_MONTH);
        int Month = c.get(Calendar.MONTH);
        int presentYear = c.get(Calendar.YEAR);

        Month = Month + 1;

        final Date mDate = monthlyDates.get(position).getDate();
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.single_cell_layout, parent, false);
        }

        final TextView cellNumber = view.findViewById(R.id.calendar_date_id);

        cellNumber.setText(String.valueOf(dayValue));
        if (monthlyDates.get(position).getIsSelected().equals("no")) {
            if (displayMonth == currentMonth && displayYear == currentYear) {
                if (dateMonth == dayValue && Month == currentMonth && displayYear == presentYear) {

                    if (!clicked) {
                        try {
                            date = String.valueOf(dayValue);
                            month = String.valueOf(Month);
                            year = String.valueOf(displayYear);
                            updateRequestList(year + "/" + month + "/" + date);

                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }

                        view.setBackgroundResource(R.drawable.selected_cell_background);
                        cellNumber.setTextColor(Color.parseColor("#ffffff"));


                    } else {
                        view.setBackgroundResource(R.drawable.calendercell_background);
                        cellNumber.setTextColor(Color.parseColor("#ffffff"));
                    }
                } else {
                    view.setBackgroundResource(R.drawable.calendercell_background);
                    cellNumber.setTextColor(Color.parseColor("#ffffff"));
                }

            } else {
                view.setBackgroundResource(R.drawable.calendercell_background);
                cellNumber.setTextColor(Color.parseColor("#a3a3a3"));


            }
        } else {
            view.setBackgroundResource(R.drawable.selected_cell_background);
            cellNumber.setTextColor(Color.parseColor("#ffffff"));

        }

        if (resultMonth != null) {
            if (resultMonth.size() > 0) {
                date = String.valueOf(dayValue);
                month = String.valueOf(displayMonth);
                year = String.valueOf(displayYear);
                if (month.length() < 1) {
                    month = "0" + month;
                }
                if (date.length() < 1) {
                    date = "0" + date;
                }
                String currentDate = year + "-" + month + "-" + date;
                for (int i = 0; i < resultMonth.size(); i++) {
                    String resultMonthDate = resultMonth.get(i).getDate();
                    String resultMonthStatus = resultMonth.get(i).getRequestStatus();
                    if (currentDate.equals(resultMonthDate)) {
                        if (resultMonthStatus.equals("1")) {
                            view.setBackground(getContext().getResources().getDrawable(R.drawable.yellow_decorator));
                            cellNumber.setTextColor(Color.parseColor("#FF0E8BD2"));

                        } else if (resultMonthStatus.equals("2")) {
                            view.setBackground(getContext().getResources().getDrawable(R.drawable.green_decorator));
                            cellNumber.setTextColor(Color.parseColor("#FF0E8BD2"));

                        } else if (resultMonthStatus.equals("8")) {
                            view.setBackground(getContext().getResources().getDrawable(R.drawable.grey_decorator));
                            cellNumber.setTextColor(Color.parseColor("#ffffff"));

                        } else if (resultMonthStatus.equals("9")) {
                            view.setBackground(getContext().getResources().getDrawable(R.drawable.grey_decorator));
                            cellNumber.setTextColor(Color.parseColor("#ffffff"));

                        } else if (resultMonthStatus.equals("10")) {
                            view.setBackground(getContext().getResources().getDrawable(R.drawable.grey_decorator));
                            cellNumber.setTextColor(Color.parseColor("#ffffff"));
                        }
                        break;
                    }
                }

            }
        }


        try {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Date mDate = monthlyDates.get(position).getDate();
                    Calendar dateCal = Calendar.getInstance();
                    dateCal.setTime(mDate);
                    int displayMonth = dateCal.get(Calendar.MONTH) + 1;
                    int displayYear = dateCal.get(Calendar.YEAR);
                    for (int i = 0; i < monthlyDates.size(); i++) {
                        clicked = true;
                        if (i == position) {
                            String is_selected = monthlyDates.get(i).getIsSelected();
                            if (!is_selected.equals("yes")) {
                                is_selected = "yes";
                                monthlyDates.get(i).setIsSelected(is_selected);
                                date = cellNumber.getText().toString();
                                updateRequestList("" + displayYear + "/" + displayMonth + "/" + date);
                            }
                        } else {
                            String is_selected = "no";
                            monthlyDates.get(i).setIsSelected(is_selected);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }

    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }


    public void updateRequestList(String date) {

        try {
            if (getContext() != null) {
                Session session = new Session(getContext());
                if (session.getUserType().equals("1")) {
                    ((CalanderSuffererActivity) activity).getAllRemindersListAPI(date);
                } else {
                    ((CalanderCaretakerActivity) activity).getAllRemindersListAPI(date);
                }
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
        }
    }


}
