
public enum FreeRectChoiceHeuristic {
	RectBestShortSideFit, ///< -BSSF: Positions the rectangle against the short side of a free rectangle into which it fits the best.
	RectBestLongSideFit, ///< -BLSF: Positions the rectangle against the long side of a free rectangle into which it fits the best.
	RectBestAreaFit, ///< -BAF: Positions the rectangle into the smallest free rect into which it fits.
	RectBottomLeftRule, ///< -BL: Does the Tetris placement.
	RectContactPointRule ///< -CP: Choosest the placement where the rectangle touches other rects as much as possible.
}
