package tbi.com.custom_calender;

import java.util.Date;


public class DateModel {

    private Date date;
    private String isSelected = "no";

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }
}
