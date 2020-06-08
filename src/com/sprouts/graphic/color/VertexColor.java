package com.sprouts.graphic.color;

import com.sprouts.math.LinMath;

public class VertexColor {

	// Color definitions are from G4mEngine. See the below link for more information:
	// https://github.com/G4me4u/g4mengine/blob/master/src/com/g4mesoft/graphic/GColor.java
	
	/* REDS */
	public static final VertexColor INDIAN_RED             = new VertexColor(0xFFCD5C5C);
	public static final VertexColor LIGHT_CORAL            = new VertexColor(0xFFF08080);
	public static final VertexColor SALMON                 = new VertexColor(0xFFFA8072);
	public static final VertexColor DARK_SALMON            = new VertexColor(0xFFE9967A);
	public static final VertexColor LIGHT_SALMON           = new VertexColor(0xFFFFA07A);
	public static final VertexColor CRIMSON_SALMON         = new VertexColor(0xFFDC143C);
	public static final VertexColor RED                    = new VertexColor(0xFFFF0000);
	public static final VertexColor FIRE_BRICK             = new VertexColor(0xFFB22222);
	public static final VertexColor DARK_RED               = new VertexColor(0xFF8B0000);

	/* PINKS */
	public static final VertexColor PINK                   = new VertexColor(0xFFFFC0CB);
	public static final VertexColor LIGHT_PINK             = new VertexColor(0xFFFFB6C1);
	public static final VertexColor HOT_PINK               = new VertexColor(0xFFFF69B4);
	public static final VertexColor DEEP_PINK              = new VertexColor(0xFFFF1493);
	public static final VertexColor MEDIUM_VIOLET_RED      = new VertexColor(0xFFC71585);
	public static final VertexColor PALE_VIOLET_RED        = new VertexColor(0xFFDB7093);
	
	/* ORANGES */
	public static final VertexColor CORAL                  = new VertexColor(0xFFFF7F50);
	public static final VertexColor TOMATO                 = new VertexColor(0xFFFF6347);
	public static final VertexColor ORANGE_RED             = new VertexColor(0xFFFF4500);
	public static final VertexColor DARK_ORANGE            = new VertexColor(0xFFFF8C00);
	public static final VertexColor ORANGE                 = new VertexColor(0xFFFFA500);
	
	/* YELLOWS */
	public static final VertexColor GOLD                   = new VertexColor(0xFFFFD700);
	public static final VertexColor YELLOW                 = new VertexColor(0xFFFFFF00);
	public static final VertexColor LIGHT_YELLOW           = new VertexColor(0xFFFFFFE0);
	public static final VertexColor LEMON_CHIFFON          = new VertexColor(0xFFFFFACD);
	public static final VertexColor LIGHT_GOLDENROD_YELLOW = new VertexColor(0xFFFAFAD2);
	public static final VertexColor PAPAYA_WHIP            = new VertexColor(0xFFFFEFD5);
	public static final VertexColor MOCCASIN               = new VertexColor(0xFFFFE4B5);
	public static final VertexColor PEACH_PUFF             = new VertexColor(0xFFFFDAB9);
	public static final VertexColor PALE_GOLDENROD         = new VertexColor(0xFFEEE8AA);
	public static final VertexColor KHAKI                  = new VertexColor(0xFFF0E68C);
	public static final VertexColor DARK_KHAKI             = new VertexColor(0xFFBDB76B);
	
	/* PURPLES */
	public static final VertexColor LAVENDER               = new VertexColor(0xFFE6E6FA);
	public static final VertexColor THISTLE                = new VertexColor(0xFFD8BFD8);
	public static final VertexColor PLUM                   = new VertexColor(0xFFDDA0DD);
	public static final VertexColor VIOLET                 = new VertexColor(0xFFEE82EE);
	public static final VertexColor ORCHID                 = new VertexColor(0xFFDA70D6);
	public static final VertexColor FUCHSIA                = new VertexColor(0xFFFF00FF);
	public static final VertexColor MAGENTA                = FUCHSIA;
	public static final VertexColor MEDIUM_ORCHID          = new VertexColor(0xFFBA55D3);
	public static final VertexColor MEDIUM_PURPLE          = new VertexColor(0xFF9370DB);
	public static final VertexColor BLUE_VIOLET            = new VertexColor(0xFF8A2BE2);
	public static final VertexColor DARK_VIOLET            = new VertexColor(0xFF9400D3);
	public static final VertexColor DARK_ORCHID            = new VertexColor(0xFF9932CC);
	public static final VertexColor DARK_MAGENTA           = new VertexColor(0xFF8B008B);
	public static final VertexColor PURPLE                 = new VertexColor(0xFF800080);
	public static final VertexColor REB_PURPLE             = new VertexColor(0xFF663399);
	public static final VertexColor INDIGO                 = new VertexColor(0xFF4B0082);
	public static final VertexColor MEDIUM_SLATE_BLUE      = new VertexColor(0xFF7B68EE);
	public static final VertexColor SLATE_BLUE             = new VertexColor(0xFF6A5ACD);
	public static final VertexColor DARK_SLATE_BLUE        = new VertexColor(0xFF483D8B);
	
	/* GREENS */
	public static final VertexColor GREEN_YELLOW           = new VertexColor(0xFFADFF2F);
	public static final VertexColor CHARTREUSE             = new VertexColor(0xFF7FFF00);
	public static final VertexColor LAWN_GREEN             = new VertexColor(0xFF7CFC00);
	public static final VertexColor LIME                   = new VertexColor(0xFF00FF00);
	public static final VertexColor LIME_GREEN             = new VertexColor(0xFF32CD32);
	public static final VertexColor PALE_GREEN             = new VertexColor(0xFF98FB98);
	public static final VertexColor LIGHT_GREEN            = new VertexColor(0xFF90EE90);
	public static final VertexColor MEDIUM_SPRING_GREEN    = new VertexColor(0xFF00FA9A);
	public static final VertexColor SPRING_GREEN           = new VertexColor(0xFF00FF7F);
	public static final VertexColor MEDIUM_SEA_GREEN       = new VertexColor(0xFF3CB371);
	public static final VertexColor SEA_GREEN              = new VertexColor(0xFF2E8B57);
	public static final VertexColor FOREST_GREEN           = new VertexColor(0xFF228B22);
	public static final VertexColor GREEN                  = new VertexColor(0xFF008000);
	public static final VertexColor DARK_GREEN             = new VertexColor(0xFF006400);
	public static final VertexColor YELLOW_GREEN           = new VertexColor(0xFF9ACD32);
	public static final VertexColor OLIVE_DRAB             = new VertexColor(0xFF6B8E23);
	public static final VertexColor OLIVE                  = new VertexColor(0xFF808000);
	public static final VertexColor DARK_OLIVE_GREEN       = new VertexColor(0xFF556B2F);
	public static final VertexColor MEDIUM_AQUAMARINE      = new VertexColor(0xFF66CDAA);
	public static final VertexColor DARK_SEA_GREEN         = new VertexColor(0xFF8FBC8F);
	public static final VertexColor LIGHT_SEA_GREEN        = new VertexColor(0xFF20B2AA);
	public static final VertexColor DARK_CYAN              = new VertexColor(0xFF008B8B);
	public static final VertexColor TEAL                   = new VertexColor(0xFF008080);
	
	/* BLUES / CYANS */
	public static final VertexColor AQUA                   = new VertexColor(0xFF00FFFF);
	public static final VertexColor CYAN                   = AQUA;
	public static final VertexColor LIGHT_CYAN             = new VertexColor(0xFFE0FFFF);
	public static final VertexColor PALE_TURQUOISE         = new VertexColor(0xFFAFEEEE);
	public static final VertexColor AQUAMARINE             = new VertexColor(0xFF7FFFD4);
	public static final VertexColor TURQUOISE              = new VertexColor(0xFF40E0D0);
	public static final VertexColor MEDIUM_TURQUOISE       = new VertexColor(0xFF48D1CC);
	public static final VertexColor DARK_TURQUOISE         = new VertexColor(0xFF00CED1);
	public static final VertexColor CADET_BLUE             = new VertexColor(0xFF5F9EA0);
	public static final VertexColor STEEL_BLUE             = new VertexColor(0xFF4682B4);
	public static final VertexColor LIGHT_STEEL_BLUE       = new VertexColor(0xFFB0C4DE);
	public static final VertexColor POWDER_BLUE            = new VertexColor(0xFFB0E0E6);
	public static final VertexColor LIGHT_BLUE             = new VertexColor(0xFFADD8E6);
	public static final VertexColor SKY_BLUE               = new VertexColor(0xFF87CEEB);
	public static final VertexColor LIGHT_SKY_BLUE         = new VertexColor(0xFF87CEFA);
	public static final VertexColor DEEP_SKY_BLUE          = new VertexColor(0xFF00BFFF);
	public static final VertexColor DODGER_BLUE            = new VertexColor(0xFF1E90FF);
	public static final VertexColor CORNFLOWER_BLUE        = new VertexColor(0xFF6495ED);
	public static final VertexColor ROYAL_BLUE             = new VertexColor(0xFF4169E1);
	public static final VertexColor BLUE                   = new VertexColor(0xFF0000FF);
	public static final VertexColor MEDIUM_BLUE            = new VertexColor(0xFF0000CD);
	public static final VertexColor JENIFER_BLUE           = new VertexColor(0xFF06066D);
	public static final VertexColor DARK_BLUE              = new VertexColor(0xFF00008B);
	public static final VertexColor NAVY                   = new VertexColor(0xFF000080);
	public static final VertexColor MIDNIGHT_BLUE          = new VertexColor(0xFF191970);
	
	/* BROWNS */
	public static final VertexColor CORNSILK               = new VertexColor(0xFFFFF8DC);
	public static final VertexColor BLANCHED_ALMOND        = new VertexColor(0xFFFFEBCD);
	public static final VertexColor BISQUE                 = new VertexColor(0xFFFFE4C4);
	public static final VertexColor NAVAJO_WHITE           = new VertexColor(0xFFFFDEAD);
	public static final VertexColor WHEAT                  = new VertexColor(0xFFF5DEB3);
	public static final VertexColor BURLY_WOOD             = new VertexColor(0xFFDEB887);
	public static final VertexColor TAN                    = new VertexColor(0xFFD2B48C);
	public static final VertexColor ROSY_BROWN             = new VertexColor(0xFFBC8F8F);
	public static final VertexColor SANDY_BROWN            = new VertexColor(0xFFF4A460);
	public static final VertexColor GOLDENROD              = new VertexColor(0xFFDAA520);
	public static final VertexColor DARK_GOLDENROD         = new VertexColor(0xFFB8860B);
	public static final VertexColor PERU                   = new VertexColor(0xFFCD853F);
	public static final VertexColor CHOCOLATE              = new VertexColor(0xFFD2691E);
	public static final VertexColor SADDLE_BROWN           = new VertexColor(0xFF8B4513);
	public static final VertexColor SIENNA                 = new VertexColor(0xFFA0522D);
	public static final VertexColor BROWN                  = new VertexColor(0xFFA52A2A);
	public static final VertexColor MAROON                 = new VertexColor(0xFF800000);
	
	/* WHITES */
	public static final VertexColor WHITE                  = new VertexColor(0xFFFFFFFF);
	public static final VertexColor SNOW                   = new VertexColor(0xFFFFFAFA);
	public static final VertexColor HONEYDEW               = new VertexColor(0xFFF0FFF0);
	public static final VertexColor MINT_CREAM             = new VertexColor(0xFFF5FFFA);
	public static final VertexColor AZURE                  = new VertexColor(0xFFF0FFFF);
	public static final VertexColor ALICE_BLUE             = new VertexColor(0xFFF0F8FF);
	public static final VertexColor GHOST_WHITE            = new VertexColor(0xFFF8F8FF);
	public static final VertexColor WHITE_SMOKE            = new VertexColor(0xFFF5F5F5);
	public static final VertexColor SEASHELL               = new VertexColor(0xFFFFF5EE);
	public static final VertexColor BEIGE                  = new VertexColor(0xFFF5F5DC);
	public static final VertexColor OLD_LACE               = new VertexColor(0xFFFDF5E6);
	public static final VertexColor FLORAL_WHITE           = new VertexColor(0xFFFFFAF0);
	public static final VertexColor IVORY                  = new VertexColor(0xFFFFFFF0);
	public static final VertexColor ANTIQUE_WHITE          = new VertexColor(0xFFFAEBD7);
	public static final VertexColor LINEN                  = new VertexColor(0xFFFAF0E6);
	public static final VertexColor LAVENDER_BLUSH         = new VertexColor(0xFFFFF0F5);
	public static final VertexColor MISTY_ROSE             = new VertexColor(0xFFFFE4E1);
	
	/* GREYS */
	public static final VertexColor GAINSBORO              = new VertexColor(0xFFDCDCDC);
	public static final VertexColor LIGHT_GRAY             = new VertexColor(0xFFD3D3D3);
	public static final VertexColor LIGHT_GREY             = LIGHT_GRAY;
	public static final VertexColor SILVER                 = new VertexColor(0xFFC0C0C0);
	public static final VertexColor DARK_GRAY              = new VertexColor(0xFFA9A9A9);
	public static final VertexColor DARK_GREY              = DARK_GRAY;
	public static final VertexColor GRAY                   = new VertexColor(0xFF808080);
	public static final VertexColor GREY                   = GRAY;
	public static final VertexColor DIM_GRAY               = new VertexColor(0xFF696969);
	public static final VertexColor DIM_GREY               = DIM_GRAY;
	public static final VertexColor LIGHT_SLATE_GRAY       = new VertexColor(0xFF778899);
	public static final VertexColor LIGHT_SLATE_GREY       = LIGHT_SLATE_GRAY;
	public static final VertexColor SLATE_GRAY             = new VertexColor(0xFF708090);
	public static final VertexColor SLATE_GREY             = SLATE_GRAY;
	public static final VertexColor DARK_SLATE_GRAY        = new VertexColor(0xFF2F4F4F);
	public static final VertexColor DARK_SLATE_GREY        = DARK_SLATE_GRAY;
	public static final VertexColor BLACK                  = new VertexColor(0xFF000000);
	
	private final int alpha;
	private final int red;
	private final int green;
	private final int blue;

	public VertexColor(float red, float green, float blue) {
		this(255, deNormalize(red), deNormalize(green), deNormalize(blue));
	}

	public VertexColor(float alpha, float red, float green, float blue) {
		this(deNormalize(alpha), deNormalize(red), deNormalize(green), deNormalize(blue));
	}

	public VertexColor(int red, int green, int blue) {
		this(255, red, green, blue);
	}

	public VertexColor(int alpha, int red, int green, int blue) {
		validateARGB(alpha, red, green, blue);
		
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public VertexColor(int argb) {
		this(argb, true);
	}
	
	public VertexColor(int argb, boolean hasAlpha) {
		alpha = hasAlpha ? ((argb >>> 24) & 0xFF) : 0xFF;
		red   = (argb >>> 16) & 0xFF;
		green = (argb >>>  8) & 0xFF;
		blue  = (argb >>>  0) & 0xFF;
	}
	
	private void validateARGB(int alpha, int red, int green, int blue) {
		if ((alpha & (~0xFF)) != 0)
			throw new IllegalArgumentException("Invalid alpha value, must be 0-255: " + alpha);
		if ((red & (~0xFF)) != 0)
			throw new IllegalArgumentException("Invalid red value, must be 0-255: " + red);
		if ((green & (~0xFF)) != 0)
			throw new IllegalArgumentException("Invalid green value, must be 0-255: " + green);
		if ((blue & (~0xFF)) != 0)
			throw new IllegalArgumentException("Invalid blue value, must be 0-255: " + blue);
	}
	
	private static int deNormalize(float value) {
		return LinMath.clamp((int)(value * 255.0f), 0, 255);
	}

	private static float normalize(int value) {
		return value / 255.0f;
	}
	
	public VertexColor withAlpha(float alphaN) {
		return new VertexColor(deNormalize(alphaN), red, green, blue);
	}

	public VertexColor withRed(float redN) {
		return new VertexColor(alpha, deNormalize(redN), green, blue);
	}

	public VertexColor withGreen(float greenN) {
		return new VertexColor(alpha, red, deNormalize(greenN), blue);
	}
	
	public VertexColor withBlue(float blueN) {
		return new VertexColor(alpha, red, green, deNormalize(blueN));
	}

	public VertexColor withAlpha(int alpha) {
		return new VertexColor(alpha, red, green, blue);
	}

	public VertexColor withRed(int red) {
		return new VertexColor(alpha, red, green, blue);
	}

	public VertexColor withGreen(int green) {
		return new VertexColor(alpha, red, green, blue);
	}
	
	public VertexColor withBlue(int blue) {
		return new VertexColor(alpha, red, green, blue);
	}
	
	public VertexColor interpolate(VertexColor other, float t) {
		int a = alpha + (int)(t * (other.alpha - alpha));
		int r = red   + (int)(t * (other.red   - red  ));
		int g = green + (int)(t * (other.green - green));
		int b = blue  + (int)(t * (other.blue  - blue ));
		return new VertexColor(a, r, g, b);
	}
	
	public float getAlphaN() {
		return normalize(alpha);
	}
	
	public float getRedN() {
		return normalize(red);
	}
	
	public float getGreenN() {
		return normalize(green);
	}
	
	public float getBlueN() {
		return normalize(blue);
	}

	public int getAlpha() {
		return alpha;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}
}
