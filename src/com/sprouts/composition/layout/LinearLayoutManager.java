package com.sprouts.composition.layout;

import java.util.List;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;

/**
 * @author Christian
 */
public class LinearLayoutManager extends AbstractLayoutManager {

	private LayoutDirection direction;
	private int gap;
	
	private final LayoutSpecification defaultSpec;
	
	public LinearLayoutManager(LayoutDirection direction) {
		this(direction, 0);
	}

	public LinearLayoutManager(LayoutDirection direction, int gap) {
		if (direction == null)
			throw new IllegalArgumentException("direction is null!");
		if (gap < 0)
			throw new IllegalArgumentException("gap must be non-negative!");
		
		this.direction = direction;
		this.gap = gap;
		
		defaultSpec = new LayoutSpecification();
	}
	
	@Override
	public CompositionSize getMinimumLayoutSize(ParentComposition parent) {
		List<Composition> children = parent.getChildren();
		
		int numChildren = children.size();
		if (numChildren <= 0)
			return null;
		
		int mnw = 0;
		int mnh = 0;
		
		for (int i = 0; i < numChildren; i++) {
			Composition child = children.get(i);
			
			CompositionSize size = child.getMinimumSize();
			Margin borderMargin = child.getBorder().getMargin();
			
			int cw = size.getWidth()  + borderMargin.left + borderMargin.right;
			int ch = size.getHeight() + borderMargin.top  + borderMargin.bottom;
		
			if (direction == LayoutDirection.HORIZONTAL) {
				mnw += cw;
				
				if (ch > mnh)
					mnh = ch;
			} else {
				if (cw > mnw)
					mnw = cw;
				
				mnh += ch;
			}
		}
		
		if (direction == LayoutDirection.HORIZONTAL) {
			mnw += gap * (numChildren - 1);
		} else {
			mnh += gap * (numChildren - 1);
		}
		
		Margin padding = parent.getPadding();

		return new CompositionSize(mnw + padding.left + padding.right, 
		                           mnh + padding.top  + padding.bottom);
	}
	
	private LayoutSpecification getLayoutSpecification(Composition child) {
		LayoutSpecification spec = specs.get(child);
		return (spec == null) ? defaultSpec : spec;
	}
	
	@Override
	public void layoutChildren(ParentComposition parent) {
		List<Composition> children = parent.getChildren();
		
		int numChildren = children.size();
		if (numChildren <= 0)
			return;

		int nw = numChildren;
		int nh = numChildren;
		
		Margin padding = parent.getPadding();
		int remainingWidth  = parent.getWidth()  - (numChildren - 1) * gap - padding.left - padding.right;
		int remainingHeight = parent.getHeight() - (numChildren - 1) * gap - padding.top  - padding.bottom;
		
		for (Composition child : children) {
			CompositionSize minimumSize = child.getMinimumSize();
			Margin borderMargin = child.getBorder().getMargin();
			LayoutSpecification spec = getLayoutSpecification(child);
			
			if (spec.getHorizontalFill() == CompositionFill.FILL_MINIMUM) {
				remainingWidth -= minimumSize.getWidth() + borderMargin.left + borderMargin.right;
				nw--;
			}
			
			if (spec.getVerticalFill() == CompositionFill.FILL_MINIMUM) {
				remainingHeight -= minimumSize.getHeight() - borderMargin.top  + borderMargin.bottom;
				nh--;
			}
		}

		if (direction == LayoutDirection.HORIZONTAL) {
			int xa = parent.getX() + padding.left;
			for (Composition child : children) {
				CompositionSize minimumSize = child.getMinimumSize();
				Margin borderMargin = child.getBorder().getMargin();
				
				int width = 0;
				
				LayoutSpecification spec = getLayoutSpecification(child);
				if (spec.getHorizontalFill() != CompositionFill.FILL_MINIMUM) {
					// If the remaining width is negative (or there
					// is no remaining width), the width of the child
					// is zero.
					if (remainingWidth > 0) {
						width = remainingWidth / nw--;
						
						int mxw = child.getMaximumSize().getWidth();
						if (mxw < Integer.MAX_VALUE - borderMargin.left - borderMargin.right)
							width = Math.min(width, mxw + borderMargin.left + borderMargin.right);

						remainingWidth -= width;
					}
				} else {
					width = minimumSize.getWidth() + borderMargin.left + borderMargin.right;
					
					if (remainingWidth < 0) {
						// Remaining width is negative. We have to take
						// the width from the minimum sized children.
						int rw = remainingWidth / (numChildren - nw);
						remainingWidth -= rw;
						width += rw;
						
						// The number of children with a
						// non-preferred size has changed.
						nw++;
					}
				}

				int y = parent.getY() + padding.top;
				int height = parent.getHeight() - padding.top - padding.bottom;

				if (spec.getVerticalFill() == CompositionFill.FILL_REMAINING) {
					int mxh = child.getMaximumSize().getHeight();
					if (mxh < Integer.MAX_VALUE - borderMargin.top - borderMargin.bottom)
						height = Math.min(height, mxh + borderMargin.top + borderMargin.bottom);
				}

				layoutComposition(child, xa, y, width, height);
				
				xa += width + gap;
			}
		} else {
			int ya = parent.getY() + padding.top;
			for (Composition child : children) {
				CompositionSize minimumSize = child.getMinimumSize();
				Margin borderMargin = child.getBorder().getMargin();
				
				int height = 0;
				
				LayoutSpecification spec = getLayoutSpecification(child);
				if (spec.getVerticalFill() != CompositionFill.FILL_MINIMUM) {
					if (remainingHeight > 0) {
						height = remainingHeight / nh--;
						
						int mxh = child.getMaximumSize().getHeight();
						if (mxh < Integer.MAX_VALUE - borderMargin.top - borderMargin.bottom)
							height = Math.min(height, mxh + borderMargin.top + borderMargin.bottom);
						
						remainingHeight -= height;
					}
				} else {
					height = minimumSize.getHeight() + borderMargin.top + borderMargin.bottom;
					
					if (remainingHeight < 0) {
						int rh = remainingHeight / (numChildren - nh);
						remainingHeight -= rh;
						height += rh;
						
						nh++;
					}
				}

				int x = parent.getX() + padding.left;
				int width = parent.getWidth() - padding.left - padding.right;

				if (spec.getHorizontalFill() == CompositionFill.FILL_REMAINING) {
					int mxw = child.getMaximumSize().getWidth();
					if (mxw < Integer.MAX_VALUE - borderMargin.left - borderMargin.right)
						width = Math.min(width, mxw + borderMargin.left + borderMargin.right);
				}

				layoutComposition(child, x, ya, width, height);
				
				ya += height + gap;
			}
		}
	}

	public LayoutDirection getDirection() {
		return direction;
	}
	
	public void setDirection(LayoutDirection direction) {
		if (direction == null)
			throw new IllegalArgumentException("direction is null!");
		
		this.direction = direction;
	}
	
	public int getGap() {
		return gap;
	}

	public void setGap(int gap) {
		if (gap < 0)
			throw new IllegalArgumentException("gap must be non-negative!");

		this.gap = gap;
	}
}
