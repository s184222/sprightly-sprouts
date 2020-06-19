package com.sprouts.composition.layout;

import java.util.List;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;

/**
 * @author Christian
 */
public class GridLayoutManager extends AbstractLayoutManager {

	private int rows;
	private int cols;
	private int hgap;
	private int vgap;
	
	public GridLayoutManager() {
		this(1, 0, 0, 0);
	}

	public GridLayoutManager(int rows, int cols) {
		this(rows, cols, 0, 0);
	}
	
	public GridLayoutManager(int rows, int cols, int hgap, int vgap) {
		if (rows < 0 || cols < 0)
			throw new IllegalArgumentException("rows and cols must be non-negative!");
		if (rows == 0 && cols == 0)
			throw new IllegalArgumentException("rows and cols can not both be zero!");
		if (hgap < 0 || vgap < 0)
			throw new IllegalArgumentException("hgap and vgap must be non-negative!");
		
		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
	}
	
	@Override
	public CompositionSize getMinimumLayoutSize(ParentComposition parent) {
		List<Composition> children = parent.getChildren();
		
		int numChildren = children.size();
		if (numChildren <= 0)
			return null;
		
		int cw = 0;
		int ch = 0;
		
		for (int i = 0; i < numChildren; i++) {
			Composition child = children.get(i);
			
			CompositionSize mns = child.getMinimumSize();
			Margin borderMargin = child.getBorder().getMargin();
			
			int mnw = mns.getWidth()  + borderMargin.left + borderMargin.right;
			int mnh = mns.getHeight() + borderMargin.top  + borderMargin.bottom;
			
			if (mnw > cw)
				cw = mnw;
			if (mnh > ch)
				ch = mnh;
		}
		
		int nrows = calculateNumRows(numChildren);
		int ncols = calculateNumCols(numChildren);

		int width  = ((cw + hgap) * ncols - hgap);
		int height = ((ch + vgap) * nrows - vgap);
		
		Margin padding = parent.getPadding();

		return new CompositionSize(width  + padding.left + padding.right, 
		                           height + padding.top  + padding.bottom);
	}

	@Override
	public void layoutChildren(ParentComposition parent) {
		List<Composition> children = parent.getChildren();
		
		int numChildren = children.size();
		if (numChildren <= 0)
			return;

		int nrows = calculateNumRows(numChildren);
		int ncols = calculateNumCols(numChildren);
		
		Margin padding = parent.getPadding();
		int contentWidth  = parent.getWidth()  - (ncols - 1) * hgap - padding.left - padding.right;
		int contentHeight = parent.getHeight() - (nrows - 1) * vgap - padding.top  - padding.bottom;
		
		if (contentWidth < 0)
			contentWidth = 0;
		if (contentHeight < 0)
			contentHeight = 0;
		
		int childIndex = 0;

		int y = parent.getY() + padding.top;
		int remainingHeight = contentHeight;
		for (int r = 0; r < nrows; r++) {
			int h = remainingHeight / (nrows - r);
			remainingHeight -= h;

			int x = parent.getX() + padding.left;
			int remainingWidth = contentWidth;
			for (int c = 0; c < ncols; c++) {
				int w = remainingWidth / (ncols - c);
				remainingWidth -= w;
			
				if (childIndex >= numChildren)
					return;
			
				Composition child = children.get(childIndex++);
				layoutComposition(child, x, y, w, h);
				
				x += w + hgap;
			}

			y += h + vgap;
		}
	}
	
	private int calculateNumRows(int numChildren) {
		if (cols != 0)
			return Math.max(rows, (numChildren + cols - 1) / cols);
		return rows;
	}

	private int calculateNumCols(int numChildren) {
		if (rows != 0)
			return Math.max(cols, (numChildren + rows - 1) / rows);
		return cols;
	}
	
	public int getRows() {
		return rows;
	}
	
	public void setRows(int rows) {
		if (rows < 0)
			throw new IllegalArgumentException("rows must be non-negative!");
		if (rows == 0 && cols == 0)
			throw new IllegalStateException("rows and cols can not both be zero!");
		
		this.rows = rows;
	}

	public int getColumns() {
		return cols;
	}
	
	public void setColumns(int cols) {
		if (cols < 0)
			throw new IllegalArgumentException("cols must be non-negative!");
		if (rows == 0 && cols == 0)
			throw new IllegalStateException("rows and cols can not both be zero!");
		
		this.cols = cols;
	}
	
	public int getHorizontalGap() {
		return hgap;
	}

	public void setHorizontalGap(int hgap) {
		if (hgap < 0)
			throw new IllegalArgumentException("hgap must be non-negative!");

		this.hgap = hgap;
	}
	
	public int getVerticalGap() {
		return vgap;
	}

	public void setVerticalGap(int vgap) {
		if (vgap < 0)
			throw new IllegalArgumentException("vgap must be non-negative!");
		
		this.vgap = vgap;
	}
}
