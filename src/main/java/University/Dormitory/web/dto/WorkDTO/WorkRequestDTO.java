package University.Dormitory.web.dto.WorkDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

public class WorkRequestDTO {
    @Getter
    public static class giveWorkDto {
        String userName;
        String year;
        String month;
        String day;
        String hour;
        String min;
    }

    @Getter
    public static class exchangeWorkDto {
        int year1;
        int month1;
        int day1;
        int hour1;
        int min1;
        String userName2;
        int year2;
        int month2;
        int day2;
        int hour2;
        int min2;
    }

    @Getter
    public static class worker {
        @Schema(description = "이름", example = "권하림")
        String name;
        @Schema(description = "이름", example = "\"times\":[\"2023-04-18 10:30\", \"2023-04-18 10:30\", \"2023-04-18 10:30\"] OR " +
                "[\"2023-04-18 10:30\", \"2023-04-18 10:30\"]")
        List<String> times;
    }

    @Getter
    public static class reason {
        @Schema(description = "시간", example = "2024-04-18 11:30")
        String time;
        @Schema(description = "사유", example = "늦잠")
        String reason;
    }
}