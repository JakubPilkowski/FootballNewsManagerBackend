package com.footballnewsmanager.backend.controllers;

import com.footballnewsmanager.backend.models.User;

public interface OnPresentInterface<T> {
    T onSuccess(T t);
}
