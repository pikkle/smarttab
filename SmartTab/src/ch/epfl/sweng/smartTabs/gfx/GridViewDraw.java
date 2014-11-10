package ch.epfl.sweng.smartTabs.gfx;

import ch.epfl.sweng.smartTabs.R;
import ch.epfl.sweng.smartTabs.music.Height;
import ch.epfl.sweng.smartTabs.music.Instrument;
import ch.epfl.sweng.smartTabs.music.Tab;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author lourichard, pikkle, DrRazor
 * 
 */
public class GridViewDraw extends Drawable {
	private final Paint paint = new Paint();
	private final Height[] stantardTuning = {Height.E, Height.B, Height.G, Height.D, Height.A, Height.E};
	private int mWidth;
	private int mHeight;
	private Instrument myInstrument;
	private Tab myTab;

	private boolean displayTab = true;
	private boolean displayPartition = true;
	
	private Resources mRes;

	private static final int STANDARD_NOTATION_LINES_NUMBER = 5;

	private final Rect HEADER_RECT; // title, ..
	private final Rect LEFT_TOP_RECT; // Clef for the music sheet
	private final Rect lEFT_BOTTOM_RECT; // Tuning for the tablature
	private final Rect LEFT_CENTER_RECT; // Clef OR Tuning
	private final Rect RIGHT_TOP_RECT; // Clef for the music sheet
	private final Rect RIGHT_BOTTOM_RECT; // Tuning for the tablature
	private final Rect RIGHT_CENTER_RECT; // Clef OR Tuning

	private static final float HEADER_RATIO = 0.1f;
	private static final float TOP_CONTENT_RATIO = 0.4f;
	private static final float LEFT_SIDE_RATIO = 0.25f; 

	private static final float TITLE_WIDTH_MARGIN_DELTA = 0.99f;
	private static final float TITLE_HEIGHT_MARGIN_DELTA = 0.1f;
	private static final float CLEF_TUNING_LEFT_MARGIN = 0.5f;
	private static final float MIDDLE_SMALL_MARGIN = 0.5f;
	private static final float BIG_MARGIN = 0.25f;
	private static final float MARGIN = 0.125f;
	private static final float STRING_SHIFT = 0.5f;
	private static final float STRING_SHIFT_BOTTOM = 0.33f;

	private static final float THIN_LINE_WIDTH = 2f;
	private static final float MED_LINE_WIDTH = 5f;
	private static final float HARD_LINE_WIDTH = 15f;

	private static final float TUNING_TEXT_SIZE = 36f;
	private static final float TITLE_FONT_SIZE = 48f;
	private static final float CURSOR_WIDTH = 10f;

	private Bitmap clefDeSol;
	private Rect clefRect;
	private Rect sheetRect;
	private Rect nutRect;
	private Rect neckRect;
	private float standardLineMargin;
	private float tabLineMargin;

	public GridViewDraw(int width, int height, Instrument instru, Resources res) {
		super();
		mWidth = width;
		mHeight = height;
		myInstrument = instru;
		mRes = res;

		// Divides the screen into 2 main boxes: header and body
		Rect screenRect = new Rect(0, 0, mWidth, mHeight);
		HEADER_RECT = new Rect(screenRect.left, screenRect.top,
				screenRect.right, (int) (screenRect.height() * HEADER_RATIO));
		Rect bodyRect = new Rect(screenRect.left, HEADER_RECT.bottom,
				screenRect.right, screenRect.bottom);

		// Splits in two parts vertically
		Rect leftPartRect = new Rect(bodyRect.left, bodyRect.top,
				(int) (bodyRect.right * LEFT_SIDE_RATIO), bodyRect.bottom);
		Rect rightPartRect = new Rect(leftPartRect.right, bodyRect.top,
				bodyRect.right, bodyRect.bottom);

		// Splits the body in two parts horizontally or centers it
		Rect topContentRect = new Rect(bodyRect.left, bodyRect.top, bodyRect.right,
				(int) (bodyRect.top + (bodyRect.bottom * TOP_CONTENT_RATIO)));
		Rect bottomContentRect = new Rect(bodyRect.left, topContentRect.bottom,
				bodyRect.right, bodyRect.bottom);
		int verticalMargin = (bodyRect.height() - bottomContentRect.height()) / 2;
		Rect centerScreenRect = new Rect(bodyRect.left + verticalMargin,
				bodyRect.top, bodyRect.right - verticalMargin, bodyRect.bottom);

		// Intersects the splits to get the left parts
		LEFT_TOP_RECT = new Rect(leftPartRect.left, topContentRect.top,
				leftPartRect.right, topContentRect.bottom);
		lEFT_BOTTOM_RECT = new Rect(leftPartRect.left, bottomContentRect.top,
				leftPartRect.right, bottomContentRect.bottom);
		LEFT_CENTER_RECT = new Rect(leftPartRect.left, centerScreenRect.top,
				leftPartRect.right, centerScreenRect.bottom);

		// Intersects the splits to get the right parts
		RIGHT_TOP_RECT = new Rect(rightPartRect.left, topContentRect.top,
				rightPartRect.right, topContentRect.bottom);
		RIGHT_BOTTOM_RECT = new Rect(rightPartRect.left,
				bottomContentRect.top, rightPartRect.right,
				bottomContentRect.bottom);
		RIGHT_CENTER_RECT = new Rect(rightPartRect.left,
				centerScreenRect.top, rightPartRect.right,
				centerScreenRect.bottom);
				
		initializeBitmaps();
		invalidateBoxes();
	}

	@Override
	public void draw(Canvas canvas) {
		clearScreen(canvas);
		drawSongName(canvas);
		if (displayTab) {
			drawTablatureGrid(canvas);
		}
		if (displayPartition) {
			drawStandardGrid(canvas);
		}
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	/**
	 * Update how the screen should draw the partitions
	 */
	public void invalidateBoxes() {
		if (displayPartition) {
			// Standard grid positions calculations
			// Selects a centered box if possible
			Rect standardLeft = !displayTab ? LEFT_CENTER_RECT : LEFT_TOP_RECT;
			Rect standardRight = !displayTab ? RIGHT_CENTER_RECT : RIGHT_TOP_RECT;
			int standardMargin = (int) (standardLeft.width() * MARGIN);
			Rect standardRect;

			// adds a margin to the box
			if (!displayTab) {
				standardLeft = Box.marginRect(standardLeft, standardMargin, standardMargin, 0, standardMargin);
				standardRight = Box.marginRect(standardRight, 0, standardMargin, 0, standardMargin);
				standardRect = Box.marginRect(
						Box.bigUnion(standardLeft, standardRight),
						(int) (standardLeft.width() * CLEF_TUNING_LEFT_MARGIN),
						(int) (standardLeft.height() * BIG_MARGIN), 
						0,
						(int) (standardLeft.height() * BIG_MARGIN));
			} else {
				standardLeft = Box.marginRect(standardLeft, standardMargin, standardMargin, 0,
						(int) (standardMargin * MIDDLE_SMALL_MARGIN));
				standardRight = Box.marginRect(standardRight, 0, standardMargin, 0,
						(int) (standardMargin * MIDDLE_SMALL_MARGIN));
				standardRect = Box.marginRect(Box.bigUnion(standardLeft, standardRight),
						(int) (standardLeft.width() * CLEF_TUNING_LEFT_MARGIN), 0, 0, 0);
			}

			// Gets the marged two boxes (left & right) to draw in
			clefRect = Box.intersection(standardRect, standardLeft);
			sheetRect = Box.intersection(standardRect, standardRight);

			// Create a bitmap that fits the rectangle
			int standardHeight = clefRect.height();
			int standardWidth = clefDeSol.getWidth() * standardHeight / clefDeSol.getHeight(); // keeps the image ratio
			clefDeSol = Bitmap.createScaledBitmap(clefDeSol, standardWidth, standardHeight, false);

			standardLineMargin = (float) (sheetRect.height() / STANDARD_NOTATION_LINES_NUMBER);
			
		} 
		if (displayTab) {
			// Tablature grid positions calculations
			// Select centered box if possible
			Rect left = !displayPartition ? LEFT_CENTER_RECT : lEFT_BOTTOM_RECT;
			Rect right = !displayPartition ? RIGHT_CENTER_RECT : RIGHT_BOTTOM_RECT;
			int margin = (int) (left.width() * MARGIN);
			Rect tabRect;
			if (!displayPartition) {
				left = Box.marginRect(left, margin, margin, 0, margin);
				right = Box.marginRect(right, 0, margin, 0, margin);
				tabRect = Box.marginRect(Box.bigUnion(left, right),
						(int) (left.width() * CLEF_TUNING_LEFT_MARGIN),
						(int) (left.height() * BIG_MARGIN), 0,
						(int) (left.height() * BIG_MARGIN));
			} else {
				left = Box.marginRect(left, margin, margin / 2, 0, margin);
				right = Box.marginRect(right, 0, margin / 2, 0, margin);
				tabRect = Box
						.marginRect(Box.bigUnion(left, right),
								(int) (left.width() * CLEF_TUNING_LEFT_MARGIN),
								0, 0, 0);
			}
			nutRect = Box.intersection(tabRect, left);
			neckRect = Box.intersection(tabRect, right);
			tabLineMargin = (float) (neckRect.height() / myInstrument
					.getNumOfStrings());
		}
	}
	private void clearScreen(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);
	}

	private void drawSongName(Canvas canvas) {
		paint.setTextSize(TITLE_FONT_SIZE);
		paint.setColor(Color.GRAY);
		Rect titleRect = Box.marginRect(HEADER_RECT, TITLE_WIDTH_MARGIN_DELTA,
				TITLE_HEIGHT_MARGIN_DELTA);
		canvas.drawText(myTab.getTabName(), titleRect.left, titleRect.top,
				paint);
	}

	private void drawStandardGrid(Canvas canvas) {
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(THIN_LINE_WIDTH);
		// Draws standard lines (5 by default)
		for (int i = 0; i < STANDARD_NOTATION_LINES_NUMBER; i++) {
			canvas.drawLine(clefRect.left, 
					clefRect.top + (i + STRING_SHIFT) * standardLineMargin, 
					sheetRect.right, 
					sheetRect.top + (i + STRING_SHIFT) * standardLineMargin, 
					paint);
		}
		paint.setStrokeWidth(HARD_LINE_WIDTH);
		// Draws vertical hard line at the left end of the standard grid
		canvas.drawLine(clefRect.left + HARD_LINE_WIDTH / 2, clefRect.top
				+ (STRING_SHIFT * standardLineMargin), clefRect.left + HARD_LINE_WIDTH
				/ 2, clefRect.bottom - (STRING_SHIFT * standardLineMargin), paint);

		// Draws G clef bitmap
		canvas.drawBitmap(clefDeSol, clefRect.left + 2 * HARD_LINE_WIDTH, clefRect.top, paint);

		paint.setColor(Color.RED);
		paint.setStrokeWidth(CURSOR_WIDTH);
		// Draws the cursor
		canvas.drawLine(clefRect.right, clefRect.top, clefRect.right,
				clefRect.bottom, paint);
	}

	private void drawTablatureGrid(Canvas canvas) {
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(THIN_LINE_WIDTH);
		paint.setTextSize(TUNING_TEXT_SIZE);
		for (int i = 0; i < myInstrument.getNumOfStrings(); i++) {
			canvas.drawLine(nutRect.left, nutRect.top + (i + STRING_SHIFT)
					* tabLineMargin, neckRect.right, neckRect.top
					+ (i + STRING_SHIFT) * tabLineMargin, paint);
			canvas.drawText(stantardTuning[i].toString(), nutRect.left
					- TUNING_TEXT_SIZE, nutRect.top
					+ ((i + STRING_SHIFT) * tabLineMargin)
					+ (TUNING_TEXT_SIZE * STRING_SHIFT_BOTTOM), paint);
		}
		paint.setStrokeWidth(MED_LINE_WIDTH);
		canvas.drawLine(nutRect.left,
				nutRect.top + (STRING_SHIFT * tabLineMargin), nutRect.left,
				nutRect.bottom - (STRING_SHIFT * tabLineMargin), paint);

		paint.setColor(Color.RED);
		paint.setStrokeWidth(CURSOR_WIDTH);
		canvas.drawLine(nutRect.right, nutRect.top, nutRect.right,
				nutRect.bottom, paint);
	}


	/**
	 * Initialize different Bitmaps for the standard notation
	 */
	private void initializeBitmaps() {
		clefDeSol = BitmapFactory.decodeResource(mRes, R.raw.cledesol);
	}
	
	/**
	 * @return Returns if the Tab is displayed or not in the drawable
	 */
	public boolean isDisplayTab() {
		return displayTab;
	}

	/**
	 * @param Sets if the drawable draws the tab
	 */
	public void setDisplayTab(boolean displayTab) {
		this.displayTab = displayTab;
	}

	/**
	 * @return Returns if the Partition is displayed or not in the drawable
	 */
	public boolean isDisplayPartition() {
		return displayPartition;
	}

	/**
	 * @param Sets if the drawable draws the partition
	 */
	public void setDisplayPartition(boolean displayPartition) {
		this.displayPartition = displayPartition;
	}

	/**
	 * @return The standard grid rectangle
	 */
	public Rect getStandardRect() {
		return sheetRect;
	}

	/**
	 * @return The tab grid rectangle
	 */
	public Rect getTabRect() {
		return neckRect;
	}

	/**
	 * @return the standardLineMargin
	 */
	public float getStandardLineMargin() {
		return standardLineMargin;
	}

	/**
	 * @return the tabLineMargin
	 */
	public float getTabLineMargin() {
		return tabLineMargin;
	}

}
