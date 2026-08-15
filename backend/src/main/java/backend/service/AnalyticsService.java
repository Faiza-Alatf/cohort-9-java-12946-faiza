package backend.service;

import backend.dto.AnalyticsResponse;
import backend.entity.Contact;
import backend.entity.User;
import backend.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ContactRepository contactRepository;

    public AnalyticsService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public AnalyticsResponse getAnalyticsForUser(User user) {
        List<Contact> contacts = contactRepository.findByUser(user);

        // compute totals and simple monthly series for the last 7 months
        ZoneId zone = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zone);

        List<LocalDate> months = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            months.add(now.minusMonths(i).withDayOfMonth(1));
        }

        List<String> monthLabels = months.stream()
                .map(m -> m.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.toList());

        // map contacts by month
        Map<String, Integer> counts = new HashMap<>();
        for (LocalDate m : months) counts.put(m.toString(), 0);

        for (Contact c : contacts) {
            Instant created = c.getCreatedAt();
            LocalDate d = created == null ? now : Instant.ofEpochMilli(created.toEpochMilli()).atZone(zone).toLocalDate();
            LocalDate monthStart = d.withDayOfMonth(1);
            String key = monthStart.toString();
            if (counts.containsKey(key)) counts.put(key, counts.get(key) + 1);
        }

        List<Integer> revenueMonthly = months.stream()
                .map(m -> counts.getOrDefault(m.toString(), 0))
                .collect(Collectors.toList());

        // active users = same as revenueMonthly for this simplified analytics
        List<Integer> activeUsers = new ArrayList<>(revenueMonthly);

        long totalContacts = contacts.size();

        int newUsersPercent = 0;
        if (revenueMonthly.size() >= 2) {
            int last = revenueMonthly.get(revenueMonthly.size() - 1);
            int prev = revenueMonthly.get(revenueMonthly.size() - 2);
            newUsersPercent = prev == 0 ? (last > 0 ? 100 : 0) : (int) ((last - prev) * 100.0 / prev);
        }

        // tasksCompletedPercent: percentage of contacts that have a title set
        long withTitle = contacts.stream().filter(c -> c.getTitle() != null && !c.getTitle().isBlank()).count();
        int tasksCompletedPercent = totalContacts == 0 ? 0 : (int) (withTitle * 100 / totalContacts);

        // projectStatus: [onTrack, atRisk] -> use withTitle vs without
        int onTrack = (int) withTitle;
        int atRisk = (int) (totalContacts - withTitle);

        AnalyticsResponse resp = new AnalyticsResponse();
        resp.setMonths(monthLabels);
        resp.setRevenueMonthly(revenueMonthly);
        resp.setActiveMonths(monthLabels.subList(Math.max(0, monthLabels.size()-6), monthLabels.size()));
        resp.setActiveUsers(activeUsers.subList(Math.max(0, activeUsers.size()-6), activeUsers.size()));
        resp.setTotalRevenue(totalContacts);
        resp.setNewUsersPercent(newUsersPercent);
        resp.setTasksCompletedPercent(tasksCompletedPercent);
        resp.setProjectStatus(Arrays.asList(onTrack, atRisk));

        return resp;
    }
}
