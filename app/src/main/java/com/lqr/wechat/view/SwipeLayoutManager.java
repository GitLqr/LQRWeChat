package com.lqr.wechat.view;

/**
 * @创建者 CSDN_LQR
 * @描述 侧滑删除管理器，使用单例模式来管理listView条目的打开与关闭
 */
public class SwipeLayoutManager {

    private SwipeLayoutManager() {
    }

    // 静态内部类模式单例
    private static class LazyHolder {
        private static final SwipeLayoutManager INSTANCE = new SwipeLayoutManager();
    }

    public static SwipeLayoutManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 记录当前打开的SwipeLayout
     */
    private SwipeLayout openInstance;

    /**
     * 设置当前打开的SwipeLayout
     */
    public void setOpenInstance(SwipeLayout swipeLayout) {
        openInstance = swipeLayout;
    }

    /**
     * 判断一个条目能否侧滑
     */
    public boolean isCouldSwipe(SwipeLayout swipeLayout) {

        // 已经打开。可以侧滑
        if (isOpenInstance(swipeLayout)) {
            return true;
        }

        // 都没有打开也可以侧滑
        return openInstance == null;
    }

    /**
     * 判断是不是打开的条目
     */
    public boolean isOpenInstance(SwipeLayout swipeLayout) {

        return swipeLayout == openInstance;
    }

    /**
     * 关闭打开的条目
     */
    public void closeOpenInstance() {
        if (openInstance != null) {
            openInstance.closeDeleteMenu();
            openInstance = null;
        }
    }

}
