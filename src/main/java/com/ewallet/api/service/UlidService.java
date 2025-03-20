package com.ewallet.api.service;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.stereotype.Service;
import com.github.f4b6a3.ulid.UlidCreator;

@Service
public class UlidService {
    public Ulid generateUlid() {
        return UlidCreator.getUlid();
    }
}
