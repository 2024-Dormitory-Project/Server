package University.Dormitory.web.dto.MainPageDTO;


import lombok.Getter;
import lombok.Setter;

public class MainRequestDTO {
    @Getter
    @Setter
    public static class work {
        private String scheduleYear;
        private String scheduleMonth;
        private String scheduleDay;
        private String scheduleHour;
        private String scheduleMin;
        private String actualYear;
        private String actualMonth;
        private String actualDay;
        private String actualHour;
        private String actualMin;
    }
}
