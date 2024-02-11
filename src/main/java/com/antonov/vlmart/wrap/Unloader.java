package com.antonov.vlmart.wrap;

public interface Unloader {
    boolean unloadStart(boolean includeManager);
    void unloadComplete();
}
