package ru.winlocker.wapi.utils.other;

@FunctionalInterface
public interface IConsumer<V> {

    V apply();
}
