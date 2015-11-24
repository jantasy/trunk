package cn.yjt.oa.app.widget.dragdrop;

public interface DropTarget {

	boolean accept(Object dragItem, DragSource dragSource);
	void drop(Object dragItem, int x, int y);
	void onDragIn(int x, int y);
	void onDragOver(int x, int y);
	void onDragOut(int x, int y);
}
