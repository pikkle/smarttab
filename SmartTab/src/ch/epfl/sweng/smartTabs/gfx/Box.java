/**
 * 
 */
package ch.epfl.sweng.smartTabs.gfx;

import java.security.InvalidParameterException;

import android.graphics.Rect;

/**
 * Gives static functions to Rect
 * @author pikkle
 */
public enum Box{;
	
	/**
	 * Gives a smaller rectangle within a given one, leaving a margin between them of factor delta
	 * @param r The rectangle to resize
	 * @param delta The ratio of the margin (0 <= delta <= 1)
	 * @return The smaller rectangle centered at the same point
	 * @throws InvalidParameterException if delta isn't in the given bounds
	 */
	public static Rect marginRect(Rect r, float delta) throws InvalidParameterException {
		return marginRect(r, delta, delta);
	}
	
	/**
	 * Gives a smaller rectangle within a given one, leaving a margin between them of factor delta
	 * @param r The rectangle to resize
	 * @param deltaWidth The ratio of the margin on the left and right (0 <= deltaWidth <= 1)
	 * @param deltaWidth The ratio of the margin on the top and bottom (0 <= deltaHeight <= 1)
	 * @return The smaller rectangle centered at the same point
	 * @throws InvalidParameterException if delta isn't in the given bounds
	 */
	public static Rect marginRect(Rect r, float deltaWidth, float deltaHeight) throws InvalidParameterException {
		if (deltaWidth < 0 || deltaWidth > 1 || deltaHeight < 0 || deltaHeight > 1) {
			throw new InvalidParameterException("Delta should be between 0 and 1");
		}
		int newWidth = (int) (r.width() * deltaWidth);
		int newHeight = (int) (r.height() * deltaHeight);
		
		int left = (r.width()-newWidth)/2;
		int top = (r.height()-newHeight)/2;
		int right = left + newWidth;
		int bottom = top + newHeight;
		
		return new Rect(left, top, right, bottom);
	}
	
	/**
	 * Gives a smaller rectangle within a given one, leaving a margin between them of a given size
	 * @param r The rectangle to resize
	 * @param margin The size of the margin to apply
	 * @return The smaller rectangle centered at the same point
	 * @throws InvalidParameterException if the margin is negative or too big for the rectangle
	 */
	public static Rect marginRect(Rect r, int margin) throws InvalidParameterException {
		
		return marginRect(r, margin, margin, margin, margin);
	}
	
	/**
	 * Gives a smaller rectangle within a given one, leaving a margin between them of a given size
	 * @param r The rectangle to resize
	 * @param marginLeft The left margin
	 * @param marginTop The top margin
	 * @param marginRight The right margin
	 * @param marginBottom The bottom margin
	 * @return The smaller rectangle centered at the same point
	 * @throws InvalidParameterException if the margin is negative or too big for the rectangle
	 */
	public static Rect marginRect(Rect r, int marginLeft, int marginTop, int marginRight, int marginBottom) 
		throws InvalidParameterException {
		if (marginLeft < 0 || marginLeft > r.width() 
				|| marginTop < 0 || marginTop > r.height()
				|| marginRight < 0 || marginRight > r.width()
				|| marginBottom < 0 || marginBottom > r.height()
				|| marginTop + marginBottom > r.height()
				|| marginLeft + marginRight > r.width()) {
			throw new InvalidParameterException(
					"The margin should not be negative nor be bigger than the given rectangle's width or height");
		}
		return new Rect(r.left+marginLeft, r.top+marginTop, r.right+marginRight, r.bottom+marginBottom);
	}
}
