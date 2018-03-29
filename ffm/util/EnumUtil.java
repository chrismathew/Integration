package gov.hhs.cms.base.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumUtil {
    public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumClass) {
        return new ArrayList<E>(Arrays.asList(enumClass.getEnumConstants()));
    }

    public static <E extends Enum<E>> boolean isValidEnum(E enumType) {
        if (enumType == null) {
            return false;
        }

        return true;
    }

    public static <E extends Enum<E>> boolean checkEnumValue(Class<E> enumClass, String enumName) {
		try {
            Enum.valueOf(enumClass, enumName);
            return true;
        } 	catch (IllegalArgumentException ex) {
            return false;
        }
	}
}