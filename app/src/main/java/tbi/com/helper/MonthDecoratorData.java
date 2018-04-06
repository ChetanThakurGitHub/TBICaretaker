package tbi.com.helper;

import java.util.List;


public class MonthDecoratorData {


    /**
     * status : fail
     * message : No results found right now
     * request : []
     * resultMonth : [{"date":"2017-06-28","requestStatus":"1"},{"date":"2017-06-17","requestStatus":"1"}]
     * serviceAdded : NO
     */

    private String status;
    private String message;
    private String serviceAdded;
    private List<?> request;
    private List<ResultMonthBean> resultMonth;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServiceAdded() {
        return serviceAdded;
    }

    public void setServiceAdded(String serviceAdded) {
        this.serviceAdded = serviceAdded;
    }

    public List<?> getRequest() {
        return request;
    }

    public void setRequest(List<?> request) {
        this.request = request;
    }

    public List<ResultMonthBean> getResultMonth() {
        return resultMonth;
    }

    public void setResultMonth(List<ResultMonthBean> resultMonth) {
        this.resultMonth = resultMonth;
    }

    public static class ResultMonthBean {
        /**
         * date : 2017-06-28
         * requestStatus : 1
         */

        private String date;
        private String requestStatus;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(String requestStatus) {
            this.requestStatus = requestStatus;
        }
    }
}
