package com.footballnewsmanager.backend.controllers;

public interface OnPresentInterface<T> {
    T onSuccess(T t);
}
