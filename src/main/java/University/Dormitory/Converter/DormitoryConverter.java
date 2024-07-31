package University.Dormitory.Converter;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.exception.Handler.UndefinedDormitoryException;

public class DormitoryConverter {
    public static Dormitory toDormitory(int dormitoryNumber) {
        Dormitory dormitory;
        switch (dormitoryNumber) {
            case 1:
                return Dormitory.DORMITORY1;
            case 2:
                return Dormitory.DORMITORY2;
            case 3:
                return  Dormitory.DORMITORY3;
            default:
                throw new UndefinedDormitoryException("존재하지 않는 기숙사 번호입니다.");
        }
    }
}
