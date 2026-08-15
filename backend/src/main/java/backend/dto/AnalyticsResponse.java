package backend.dto;

import java.util.List;

public class AnalyticsResponse {

    private List<String> months;
    private List<Integer> revenueMonthly;
    private List<String> activeMonths;
    private List<Integer> activeUsers;
    private long totalRevenue;
    private int newUsersPercent;
    private int tasksCompletedPercent;
    private List<Integer> projectStatus;

    public AnalyticsResponse() {
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public List<Integer> getRevenueMonthly() {
        return revenueMonthly;
    }

    public void setRevenueMonthly(List<Integer> revenueMonthly) {
        this.revenueMonthly = revenueMonthly;
    }

    public List<String> getActiveMonths() {
        return activeMonths;
    }

    public void setActiveMonths(List<String> activeMonths) {
        this.activeMonths = activeMonths;
    }

    public List<Integer> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<Integer> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getNewUsersPercent() {
        return newUsersPercent;
    }

    public void setNewUsersPercent(int newUsersPercent) {
        this.newUsersPercent = newUsersPercent;
    }

    public int getTasksCompletedPercent() {
        return tasksCompletedPercent;
    }

    public void setTasksCompletedPercent(int tasksCompletedPercent) {
        this.tasksCompletedPercent = tasksCompletedPercent;
    }

    public List<Integer> getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(List<Integer> projectStatus) {
        this.projectStatus = projectStatus;
    }
}
