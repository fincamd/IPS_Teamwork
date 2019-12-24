package ui.transportation;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import com.toedter.calendar.IDateEvaluator;

public class DateEspecificator implements IDateEvaluator {

	Calendar calendar = Calendar.getInstance();

	@Override
	public Color getInvalidBackroundColor() {
		return new Color(240, 240, 240);
	}

	@Override
	public Color getInvalidForegroundColor() {
		return Color.BLACK;
	}

	@Override
	public String getInvalidTooltip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getSpecialBackroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getSpecialForegroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSpecialTooltip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvalid(Date date) {
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	@Override
	public boolean isSpecial(Date arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
