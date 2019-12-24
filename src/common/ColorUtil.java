package common;

import java.awt.Color;

public class ColorUtil {

	public static Color getOpositeColor(Color colorToChange) {
		return new Color(colorToChange.getBlue(), colorToChange.getRed(), colorToChange.getGreen());
	}

}
