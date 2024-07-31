package University.Dormitory.web.dto.WorkDTO;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class WorkRequestDTO {
    @Getter
    public static class giveWorkDto {
        String userName;
        String year;
        String month;
        String Day;
        String Hour;
        String Min;
    }

    @Getter
    public static class exchangeWorkDto {
        String userName1;
        int year1;
        int month1;
        int day1;
        int hour1;
        int min1;
        String UserName2;
        int year2;
        int month2;
        int day2;
        int hour2;
        int min2;
    }

    @Getter
    public static class worker {
        String name;
        List<String> times;
    }
}