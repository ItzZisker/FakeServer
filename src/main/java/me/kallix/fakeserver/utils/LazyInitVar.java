package me.kallix.fakeserver.utils;

public abstract class LazyInitVar<T> {

    private T object;
    private boolean isInitialized = false;

    public T get() {

        if (!this.isInitialized) {
            this.isInitialized = true;
            this.object = this.init();
        }
        return this.object;
    }

    protected abstract T init();
}
